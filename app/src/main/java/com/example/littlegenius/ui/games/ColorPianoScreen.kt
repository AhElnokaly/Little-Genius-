package com.example.littlegenius.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.littlegenius.ui.theme.Emerald500
import com.example.littlegenius.ui.theme.SkyBlue200
import com.example.littlegenius.ui.theme.SkyBlue600
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class PianoKey(
    val note: String,
    val arabicNote: String,
    val freqHz: Double,
    val color: Color
)

val PIANO_KEYS = listOf(
    PianoKey("C", "دو", 261.63, Color(0xFFEF4444)),
    PianoKey("D", "ري", 293.66, Color(0xFFF97316)),
    PianoKey("E", "مي", 329.63, Color(0xFFFBBF24)),
    PianoKey("F", "فا", 349.23, Color(0xFF22C55E)),
    PianoKey("G", "صول", 392.00, Color(0xFF06B6D4)),
    PianoKey("A", "لا", 440.00, Color(0xFF3B82F6)),
    PianoKey("B", "سي", 493.88, Color(0xFF8B5CF6)),
    PianoKey("C2", "دو", 523.25, Color(0xFFEC4899))
)

data class PianoLevel(
    val levelNumber: Int,
    val title: String,
    val guideNotes: List<String>
)

val PIANO_LEVELS = listOf(
    PianoLevel(1, "العزف الحر 🎶", emptyList()),
    PianoLevel(2, "السلم الموسيقي", listOf("دو", "ري", "مي", "فا", "صول", "لا", "سي", "دو")),
    PianoLevel(3, "لحن الفراشة", listOf("دو", "دو", "صول", "صول", "لا", "لا", "صول")),
    PianoLevel(4, "لحن الأبطال", listOf("مي", "مي", "فا", "صول", "صول", "فا", "مي", "ري"))
)

data class FloatingMusicNote(
    val id: Int,
    val text: String,
    val keyIndex: Int,
    var age: Int = 0
)

@Composable
fun ColorPianoScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onPlayTone: (Double) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val coroutineScope = rememberCoroutineScope()
        var levelIndex by remember { mutableIntStateOf(0) }
        val currentLevel = PIANO_LEVELS[levelIndex % PIANO_LEVELS.size]

        var playedNotesCount by remember { mutableIntStateOf(0) }
        var activeNote by remember { mutableStateOf<String?>(null) }
        var activeKeyIndex by remember { mutableIntStateOf(-1) }
        var melodyStep by remember(currentLevel) { mutableIntStateOf(0) }

        val floatingNotes = remember { mutableStateListOf<FloatingMusicNote>() }
        var noteCounter by remember { mutableIntStateOf(0) }

        // Bouncing animation for guide key
        val infiniteTransition = rememberInfiniteTransition(label = "piano_bounce")
        val guideBounceOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -12f,
            animationSpec = infiniteRepeatable(
                animation = tween(450, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bounce"
        )

        // Float notes animation loop
        LaunchedEffect(Unit) {
            while (true) {
                delay(30)
                if (floatingNotes.isNotEmpty()) {
                    val iter = floatingNotes.listIterator()
                    while (iter.hasNext()) {
                        val n = iter.next()
                        n.age++
                        if (n.age > 25) {
                            iter.remove()
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFDF4FF))
                .testTag("color_piano_screen")
        ) {
            GameHeader(
                title = "بيانو الألوان الموسيقي 🎹",
                onBack = onBack,
                starsCount = starsCount
            )

            // Melody Level Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(PIANO_LEVELS) { lvl ->
                    val isCurrent = lvl.levelNumber == currentLevel.levelNumber
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isCurrent) SkyBlue600 else Color.White,
                        modifier = Modifier
                            .clickable {
                                levelIndex = lvl.levelNumber - 1
                                melodyStep = 0
                            }
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (isCurrent) SkyBlue600 else SkyBlue200,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .testTag("piano_lvl_${lvl.levelNumber}")
                    ) {
                        Text(
                            text = lvl.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) Color.White else Color(0xFF334155),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Note prompt card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier.border(2.dp, Color(0xFFF0ABFC), RoundedCornerShape(22.dp))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
                    ) {
                        if (currentLevel.guideNotes.isNotEmpty() && melodyStep < currentLevel.guideNotes.size) {
                            val nextExpected = currentLevel.guideNotes[melodyStep]
                            Text(
                                text = "اعزف نغمة: $nextExpected 🎵",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF86198F)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "الخطوة ${melodyStep + 1} من ${currentLevel.guideNotes.size}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFA21CAF)
                            )
                        } else {
                            Text(
                                text = if (activeNote != null) "نغمة رائعة: $activeNote 🎶" else "المس مفاتيح البيانو واستمتع بأعذب الألحان! 🎼",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF86198F)
                            )
                        }
                    }
                }
            }

            // Piano Keyboard with 3D tactile keys and floating notes
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PIANO_KEYS.forEachIndexed { idx, key ->
                        val isPressed = activeKeyIndex == idx
                        val isTargetNote = currentLevel.guideNotes.isNotEmpty() &&
                                melodyStep < currentLevel.guideNotes.size &&
                                currentLevel.guideNotes[melodyStep] == key.arabicNote

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            // Guide bouncing indicator
                            if (isTargetNote) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .graphicsLayer { translationY = guideBounceOffset }
                                        .padding(top = 4.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color.Yellow,
                                        shadowElevation = 3.dp
                                    ) {
                                        Text(
                                            text = "👇",
                                            fontSize = 20.sp,
                                            modifier = Modifier.padding(2.dp)
                                        )
                                    }
                                }
                            }

                            // 3D Key Body
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.92f)
                                    .graphicsLayer {
                                        // 3D Key Press displacement downwards
                                        translationY = if (isPressed) 14f else 0f
                                    }
                                    .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                                    .border(
                                        width = if (isTargetNote) 3.5.dp else 1.5.dp,
                                        color = if (isTargetNote) Color.White else Color.Black.copy(alpha = 0.25f),
                                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        activeNote = key.arabicNote
                                        activeKeyIndex = idx
                                        onPlayTone(key.freqHz)
                                        playedNotesCount++

                                        // Spawn floating musical note
                                        floatingNotes.add(
                                            FloatingMusicNote(
                                                id = noteCounter++,
                                                text = listOf("🎵", "🎶", "⭐", "✨").random(),
                                                keyIndex = idx
                                            )
                                        )

                                        // Handle melody game logic
                                        if (currentLevel.guideNotes.isNotEmpty()) {
                                            if (melodyStep < currentLevel.guideNotes.size && currentLevel.guideNotes[melodyStep] == key.arabicNote) {
                                                melodyStep++
                                                if (melodyStep >= currentLevel.guideNotes.size) {
                                                    onWin(2)
                                                    if (levelIndex < PIANO_LEVELS.size - 1) {
                                                        levelIndex++
                                                        melodyStep = 0
                                                    }
                                                }
                                            }
                                        } else if (playedNotesCount % 10 == 0) {
                                            onWin(1)
                                        }

                                        // Auto-release key after 250ms
                                        coroutineScope.launch {
                                            delay(250)
                                            if (activeKeyIndex == idx) {
                                                activeKeyIndex = -1
                                            }
                                        }
                                    }
                                    .testTag("piano_key_${key.note}"),
                                color = key.color,
                                shadowElevation = if (isPressed) 1.dp else 8.dp
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(
                                                    Color.White.copy(alpha = if (isPressed) 0.35f else 0.2f),
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.22f)
                                                )
                                            )
                                        )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(bottom = 24.dp),
                                        verticalArrangement = Arrangement.Bottom,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = key.arabicNote,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = key.note,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White.copy(alpha = 0.9f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Render floating music notes
                floatingNotes.forEach { fn ->
                    val noteY = -(fn.age * 8f)
                    val noteAlpha = (1f - (fn.age / 25f)).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = (fn.keyIndex * 44).dp)
                            .graphicsLayer {
                                translationY = noteY
                                alpha = noteAlpha
                            }
                    ) {
                        Text(fn.text, fontSize = 28.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
