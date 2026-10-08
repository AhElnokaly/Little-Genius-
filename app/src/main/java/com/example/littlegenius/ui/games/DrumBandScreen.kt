package com.example.littlegenius.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.littlegenius.ui.theme.Amber400
import com.example.littlegenius.ui.theme.SkyBlue200
import com.example.littlegenius.ui.theme.SkyBlue600
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class DrumInstrument(
    val id: String,
    val name: String,
    val emoji: String,
    val color: Color
)

val DRUM_INSTRUMENTS = listOf(
    DrumInstrument("kick", "طبلة كبيرة (بوم)", "🥁", Color(0xFFEF4444)),
    DrumInstrument("snare", "طبلة حادة (تك)", "🪘", Color(0xFFF97316)),
    DrumInstrument("cymbal", "صنج ذهبي (تشش)", "🔔", Color(0xFFFBBF24)),
    DrumInstrument("tambourine", "دف رنان (شخشيخة)", "🪇", Color(0xFF10B981))
)

data class RhythmLevel(
    val levelNumber: Int,
    val title: String,
    val targetPattern: List<String>
)

val RHYTHM_LEVELS = listOf(
    RhythmLevel(1, "العزف الحر 🥁", emptyList()),
    RhythmLevel(2, "إيقاع الدبابة (بوم - تك)", listOf("kick", "snare", "kick", "snare")),
    RhythmLevel(3, "إيقاع الفرح (بوم - تك - تشش)", listOf("kick", "snare", "cymbal", "kick", "snare", "cymbal")),
    RhythmLevel(4, "مهرجان الإيقاع الشامل", listOf("kick", "tambourine", "snare", "cymbal", "kick", "tambourine"))
)

@Composable
fun DrumBandScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onPlayDrum: (String) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var levelIndex by remember { mutableIntStateOf(0) }
        val currentLevel = RHYTHM_LEVELS[levelIndex % RHYTHM_LEVELS.size]
        var patternStep by remember(currentLevel) { mutableIntStateOf(0) }
        var totalHits by remember { mutableIntStateOf(0) }
        val coroutineScope = rememberCoroutineScope()
        var lastHitInstrument by remember { mutableStateOf<String?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFEF2F2))
                .testTag("drum_band_screen")
        ) {
            GameHeader(
                title = "عازف الإيقاع 🥁",
                onBack = onBack,
                starsCount = starsCount
            )

            // Rhythm Level Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(RHYTHM_LEVELS) { lvl ->
                    val isSel = lvl.levelNumber == currentLevel.levelNumber
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSel) SkyBlue600 else Color.White,
                        modifier = Modifier
                            .clickable {
                                levelIndex = lvl.levelNumber - 1
                                patternStep = 0
                            }
                            .border(
                                width = if (isSel) 2.dp else 1.dp,
                                color = if (isSel) SkyBlue600 else SkyBlue200,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .testTag("drum_lvl_${lvl.levelNumber}")
                    ) {
                        Text(
                            text = lvl.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) Color.White else Color(0xFF334155),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Rhythm Guide Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(2.dp, Amber400, RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (currentLevel.targetPattern.isNotEmpty() && patternStep < currentLevel.targetPattern.size) {
                        val nextTargetId = currentLevel.targetPattern[patternStep]
                        val nextInst = DRUM_INSTRUMENTS.firstOrNull { it.id == nextTargetId }
                        Text(
                            text = "المس: ${nextInst?.name ?: ""} ${nextInst?.emoji ?: ""}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF991B1B)
                        )
                        Text(
                            text = "الخطوة ${patternStep + 1} من ${currentLevel.targetPattern.size}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    } else {
                        Text(
                            text = "المس الطبول واعزف إيقاعات حماسية!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF991B1B)
                        )
                        Text(
                            text = "مجموع الضربات: $totalHits",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Drums Grid (2x2)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val rows = DRUM_INSTRUMENTS.chunked(2)
                rows.forEach { rowInsts ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowInsts.forEach { inst ->
                            val isHit = lastHitInstrument == inst.id
                            val scale by animateFloatAsState(
                                targetValue = if (isHit) 1.15f else 1.0f,
                                animationSpec = spring(stiffness = Spring.StiffnessHigh),
                                label = "drum_scale"
                            )

                            Surface(
                                shape = CircleShape,
                                color = inst.color,
                                shadowElevation = 8.dp,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .scale(scale)
                                    .border(4.dp, Color.White, CircleShape)
                                    .clickable {
                                        onPlayDrum(inst.id)
                                        totalHits++
                                        lastHitInstrument = inst.id
                                        coroutineScope.launch {
                                            delay(150)
                                            lastHitInstrument = null
                                        }

                                        if (currentLevel.targetPattern.isNotEmpty()) {
                                            if (patternStep < currentLevel.targetPattern.size && currentLevel.targetPattern[patternStep] == inst.id) {
                                                patternStep++
                                                if (patternStep >= currentLevel.targetPattern.size) {
                                                    onWin(2)
                                                    if (levelIndex < RHYTHM_LEVELS.size - 1) {
                                                        levelIndex++
                                                        patternStep = 0
                                                    }
                                                }
                                            }
                                        } else if (totalHits % 10 == 0) {
                                            onWin(1)
                                        }
                                    }
                                    .testTag("drum_pad_${inst.id}")
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(inst.emoji, fontSize = 48.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = inst.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
