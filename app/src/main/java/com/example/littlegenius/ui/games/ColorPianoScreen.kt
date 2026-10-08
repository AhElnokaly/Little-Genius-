package com.example.littlegenius.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.littlegenius.ui.theme.SkyBlue200
import com.example.littlegenius.ui.theme.SkyBlue600

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
    PianoLevel(2, "نغمة السلم الموسيقي", listOf("دو", "ري", "مي", "فا", "صول", "لا", "سي", "دو")),
    PianoLevel(3, "لحن الفراشة", listOf("دو", "دو", "صول", "صول", "لا", "لا", "صول")),
    PianoLevel(4, "لحن الأبطال", listOf("مي", "مي", "فا", "صول", "صول", "فا", "مي", "ري"))
)

@Composable
fun ColorPianoScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onPlayTone: (Double) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var levelIndex by remember { mutableIntStateOf(0) }
        val currentLevel = PIANO_LEVELS[levelIndex % PIANO_LEVELS.size]

        var playedNotesCount by remember { mutableIntStateOf(0) }
        var activeNote by remember { mutableStateOf<String?>(null) }
        var melodyStep by remember(currentLevel) { mutableIntStateOf(0) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFDF4FF))
                .testTag("color_piano_screen")
        ) {
            GameHeader(
                title = "بيانو الألوان 🎹",
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
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 3.dp,
                    modifier = Modifier.border(2.dp, Color(0xFFF0ABFC), RoundedCornerShape(20.dp))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
                    ) {
                        if (currentLevel.guideNotes.isNotEmpty() && melodyStep < currentLevel.guideNotes.size) {
                            val nextExpected = currentLevel.guideNotes[melodyStep]
                            Text(
                                text = "اضغط على: $nextExpected 🎵",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF86198F)
                            )
                            Text(
                                text = "الخطوة ${melodyStep + 1} من ${currentLevel.guideNotes.size}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        } else {
                            Text(
                                text = if (activeNote != null) "النغمة: $activeNote 🎵" else "المس المفاتيح واعزف ألحانك الجميلة!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF86198F)
                            )
                        }
                    }
                }
            }

            // Piano keyboard layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 10.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PIANO_KEYS.forEach { key ->
                    val isPressed = activeNote == key.arabicNote
                    val isTargetNote = currentLevel.guideNotes.isNotEmpty() &&
                            melodyStep < currentLevel.guideNotes.size &&
                            currentLevel.guideNotes[melodyStep] == key.arabicNote

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                            .background(key.color)
                            .border(
                                width = if (isPressed || isTargetNote) 4.dp else 2.dp,
                                color = if (isTargetNote) Color.White else if (isPressed) Color.Yellow else Color.Black.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                            )
                            .clickable {
                                activeNote = key.arabicNote
                                onPlayTone(key.freqHz)
                                playedNotesCount++

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
                                } else if (playedNotesCount % 8 == 0) {
                                    onWin(1)
                                }
                            }
                            .testTag("piano_key_${key.note}"),
                        color = key.color,
                        shadowElevation = if (isPressed) 8.dp else 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 20.dp),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = key.arabicNote,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = key.note,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }
    }
}
