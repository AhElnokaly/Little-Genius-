package com.example.littlegenius.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import com.example.littlegenius.R
import kotlinx.coroutines.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

class SoundManager(private val context: Context) : TextToSpeech.OnInitListener {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    // Cache of decoded PCM ShortArrays for instant, zero-latency playback without CCodec/SoundPool overhead
    private val pcmCache = ConcurrentHashMap<Int, ShortArray>()

    // Track pool management to prevent AudioTrack leakage and keep memory minimal
    private val activeTracks = ConcurrentLinkedQueue<AudioTrack>()
    private val maxActiveTracks = 8

    init {
        // Pre-load common short UI and drum sound PCM data on background thread
        scope.launch(Dispatchers.IO) {
            val commonSounds = listOf(
                R.raw.pop,
                R.raw.chime,
                R.raw.win,
                R.raw.drum_kick,
                R.raw.drum_snare,
                R.raw.drum_cymbal,
                R.raw.drum_tambourine
            )
            for (resId in commonSounds) {
                getPcm(resId)
            }
        }

        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (_: Exception) { }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS && tts != null) {
            val arabicLocales = listOf(
                Locale("ar", "SA"),
                Locale("ar", "EG"),
                Locale("ar")
            )
            for (loc in arabicLocales) {
                val res = tts?.setLanguage(loc)
                if (res != TextToSpeech.LANG_MISSING_DATA && res != TextToSpeech.LANG_NOT_SUPPORTED) {
                    break
                }
            }
            tts?.setSpeechRate(0.85f)
            tts?.setPitch(1.15f)
            isTtsReady = true
        }
    }

    fun speak(text: String, langCode: String = "ar") {
        if (isTtsReady && tts != null) {
            try {
                if (langCode == "en") {
                    tts?.setLanguage(Locale.US)
                } else {
                    val res = tts?.setLanguage(Locale("ar", "SA"))
                    if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts?.setLanguage(Locale("ar"))
                    }
                }
                val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "Utterance_${System.currentTimeMillis()}")
                if (result == TextToSpeech.SUCCESS) {
                    return
                }
            } catch (_: Exception) { }
        }
        // Fallback: If TTS is unavailable, always provide pleasant playful audio feedback
        playChimeTone()
    }

    private fun getPcm(resId: Int): ShortArray? {
        pcmCache[resId]?.let { return it }
        return try {
            context.resources.openRawResource(resId).use { input ->
                val bytes = input.readBytes()
                if (bytes.size < 44) return null
                var dataOffset = -1
                var dataSize = 0
                for (i in 0..bytes.size - 8) {
                    if (bytes[i] == 'd'.code.toByte() &&
                        bytes[i + 1] == 'a'.code.toByte() &&
                        bytes[i + 2] == 't'.code.toByte() &&
                        bytes[i + 3] == 'a'.code.toByte()
                    ) {
                        dataOffset = i + 8
                        dataSize = (bytes[i + 4].toInt() and 0xFF) or
                                ((bytes[i + 5].toInt() and 0xFF) shl 8) or
                                ((bytes[i + 6].toInt() and 0xFF) shl 16) or
                                ((bytes[i + 7].toInt() and 0xFF) shl 24)
                        break
                    }
                }
                if (dataOffset == -1 || dataOffset >= bytes.size) return null
                val actualBytes = minOf(dataSize, bytes.size - dataOffset)
                val numShorts = actualBytes / 2
                val shortArray = ShortArray(numShorts)
                ByteBuffer.wrap(bytes, dataOffset, numShorts * 2)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .asShortBuffer()
                    .get(shortArray)
                pcmCache[resId] = shortArray
                shortArray
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun playPcm(
        pcm: ShortArray,
        sampleRate: Int = 44100,
        volume: Float = 1.0f
    ) {
        scope.launch {
            try {
                // Clean up excess active tracks to prevent resource exhaustion
                while (activeTracks.size >= maxActiveTracks) {
                    val oldTrack = activeTracks.poll()
                    try {
                        oldTrack?.stop()
                        oldTrack?.release()
                    } catch (_: Exception) { }
                }

                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                val audioFormat = AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()

                val bufferSize = pcm.size * 2
                val track = AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(audioFormat)
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(pcm, 0, pcm.size)
                track.setVolume(volume.coerceIn(0.0f, 1.0f))
                activeTracks.add(track)
                track.play()

                // Calculate duration in ms + small margin
                val durationMs = ((pcm.size.toDouble() / sampleRate) * 1000).toLong() + 50
                delay(durationMs)
                try {
                    track.stop()
                    track.release()
                } catch (_: Exception) { }
                activeTracks.remove(track)
            } catch (_: Exception) { }
        }
    }

    fun playRawSound(resId: Int) {
        scope.launch {
            val pcm = getPcm(resId)
            if (pcm != null) {
                playPcm(pcm)
            } else {
                playPopTone()
            }
        }
    }

    fun playPopSound() {
        val pcm = pcmCache[R.raw.pop]
        if (pcm != null) {
            playPcm(pcm)
        } else {
            playRawSound(R.raw.pop)
        }
    }

    fun playWinFanfare() {
        val pcm = pcmCache[R.raw.win]
        if (pcm != null) {
            playPcm(pcm)
        } else {
            playRawSound(R.raw.win)
        }
    }

    fun playChime() {
        val pcm = pcmCache[R.raw.chime]
        if (pcm != null) {
            playPcm(pcm)
        } else {
            playRawSound(R.raw.chime)
        }
    }

    fun playDrum(type: String) {
        val resId = when (type) {
            "kick" -> R.raw.drum_kick
            "snare" -> R.raw.drum_snare
            "cymbal" -> R.raw.drum_cymbal
            "tambourine" -> R.raw.drum_tambourine
            else -> R.raw.drum_kick
        }
        val pcm = pcmCache[resId]
        if (pcm != null) {
            playPcm(pcm)
        } else {
            playRawSound(resId)
        }
    }

    fun playPianoTone(freqHz: Double, durationMs: Int = 400) {
        synthesizeTone(freqHz, durationMs, type = "piano")
    }

    fun playPopTone() {
        synthesizeTone(520.0, 120, type = "pop")
    }

    fun playChimeTone() {
        synthesizeTone(659.25, 250, type = "chime")
    }

    fun playWinTone() {
        scope.launch {
            val notes = listOf(523.25, 659.25, 783.99, 1046.50)
            for (freq in notes) {
                synthesizeTone(freq, 160, type = "piano")
                delay(120)
            }
        }
    }

    private fun synthesizeTone(freqHz: Double, durationMs: Int, type: String = "piano") {
        scope.launch {
            try {
                val sampleRate = 44100
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    val envelope = when {
                        progress < 0.1 -> progress / 0.1
                        else -> 1.0 - progress
                    }
                    val sample = when (type) {
                        "pop" -> {
                            val curFreq = freqHz * (1.2 - progress * 0.5)
                            Math.sin(2.0 * Math.PI * curFreq * time) * envelope * 0.8
                        }
                        "kick" -> {
                            val curFreq = freqHz * (1.5 - progress * 0.9)
                            Math.sin(2.0 * Math.PI * curFreq * time) * envelope * 0.9
                        }
                        "snare", "cymbal" -> {
                            val tone = Math.sin(2.0 * Math.PI * freqHz * time) * 0.5
                            val noise = (Math.random() * 2.0 - 1.0) * 0.5
                            (tone + noise) * envelope * 0.75
                        }
                        else -> { // piano / chime
                            (Math.sin(2.0 * Math.PI * freqHz * time) +
                             0.35 * Math.sin(2.0 * Math.PI * (freqHz * 2) * time) +
                             0.15 * Math.sin(2.0 * Math.PI * (freqHz * 3) * time)) * envelope * 0.8
                        }
                    }
                    buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playPcm(buffer, sampleRate = sampleRate)
            } catch (_: Exception) { }
        }
    }

    fun release() {
        try {
            scope.cancel()
            tts?.stop()
            tts?.shutdown()
            tts = null
            while (activeTracks.isNotEmpty()) {
                val track = activeTracks.poll()
                try {
                    track?.stop()
                    track?.release()
                } catch (_: Exception) { }
            }
            pcmCache.clear()
        } catch (_: Exception) { }
    }
}
