package com.example.littlegenius.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.SoundPool
import android.speech.tts.TextToSpeech
import com.example.littlegenius.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

class SoundManager(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var soundPool: SoundPool? = null
    private val loadedSoundIds = ConcurrentHashMap<Int, Boolean>()

    // SoundPool resource IDs
    private var popSoundId = 0
    private var winSoundId = 0
    private var chimeSoundId = 0
    private var kickSoundId = 0
    private var snareSoundId = 0
    private var cymbalSoundId = 0
    private var tambourineSoundId = 0

    init {
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build()

            soundPool?.setOnLoadCompleteListener { _, sampleId, status ->
                if (status == 0) {
                    loadedSoundIds[sampleId] = true
                }
            }

            soundPool?.let { sp ->
                try {
                    popSoundId = sp.load(context, R.raw.pop, 1)
                    winSoundId = sp.load(context, R.raw.win, 1)
                    chimeSoundId = sp.load(context, R.raw.chime, 1)
                    kickSoundId = sp.load(context, R.raw.drum_kick, 1)
                    snareSoundId = sp.load(context, R.raw.drum_snare, 1)
                    cymbalSoundId = sp.load(context, R.raw.drum_cymbal, 1)
                    tambourineSoundId = sp.load(context, R.raw.drum_tambourine, 1)
                } catch (_: Exception) { }
            }
        } catch (_: Exception) { }

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

    fun playRawSound(resId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val player = MediaPlayer.create(context.applicationContext, resId)
                if (player != null) {
                    player.setVolume(1.0f, 1.0f)
                    player.setOnCompletionListener { mp ->
                        try { mp.release() } catch (_: Exception) { }
                    }
                    player.start()
                } else {
                    playPopTone()
                }
            } catch (_: Exception) {
                playPopTone()
            }
        }
    }

    fun playPopSound() {
        if (popSoundId != 0 && loadedSoundIds[popSoundId] == true) {
            val stream = soundPool?.play(popSoundId, 1.0f, 1.0f, 1, 0, 1.0f) ?: 0
            if (stream == 0) playPopTone()
        } else {
            playRawSound(R.raw.pop)
        }
    }

    fun playWinFanfare() {
        if (winSoundId != 0 && loadedSoundIds[winSoundId] == true) {
            val stream = soundPool?.play(winSoundId, 1.0f, 1.0f, 1, 0, 1.0f) ?: 0
            if (stream == 0) playWinTone()
        } else {
            playRawSound(R.raw.win)
        }
    }

    fun playChime() {
        if (chimeSoundId != 0 && loadedSoundIds[chimeSoundId] == true) {
            val stream = soundPool?.play(chimeSoundId, 1.0f, 1.0f, 1, 0, 1.0f) ?: 0
            if (stream == 0) playChimeTone()
        } else {
            playRawSound(R.raw.chime)
        }
    }

    fun playDrum(type: String) {
        val soundId = when (type) {
            "kick" -> kickSoundId
            "snare" -> snareSoundId
            "cymbal" -> cymbalSoundId
            "tambourine" -> tambourineSoundId
            else -> kickSoundId
        }
        if (soundId != 0 && loadedSoundIds[soundId] == true) {
            val stream = soundPool?.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f) ?: 0
            if (stream == 0) playDrumSynth(type)
        } else {
            playDrumSynth(type)
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
        CoroutineScope(Dispatchers.Default).launch {
            val notes = listOf(523.25, 659.25, 783.99, 1046.50)
            for (freq in notes) {
                synthesizeTone(freq, 160, type = "piano")
                kotlinx.coroutines.delay(120)
            }
        }
    }

    private fun playDrumSynth(type: String) {
        when (type) {
            "kick" -> synthesizeTone(120.0, 180, type = "kick")
            "snare" -> synthesizeTone(320.0, 140, type = "snare")
            "cymbal" -> synthesizeTone(1200.0, 220, type = "cymbal")
            "tambourine" -> synthesizeTone(950.0, 150, type = "chime")
            else -> synthesizeTone(150.0, 150, type = "kick")
        }
    }

    private fun synthesizeTone(freqHz: Double, durationMs: Int, type: String = "piano") {
        CoroutineScope(Dispatchers.Default).launch {
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
                            // Descending frequency sweep
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

                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()

                val audioFormat = AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()

                val track = AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(audioFormat)
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.play()
                kotlinx.coroutines.delay(durationMs.toLong() + 40)
                try {
                    track.stop()
                    track.release()
                } catch (_: Exception) { }
            } catch (_: Exception) { }
        }
    }

    fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            soundPool?.release()
            soundPool = null
        } catch (_: Exception) { }
    }
}
