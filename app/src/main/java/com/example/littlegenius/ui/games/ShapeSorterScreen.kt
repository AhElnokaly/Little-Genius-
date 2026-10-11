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

data class ShapeData(
    val name: String,
    val emoji: String,
    val color: Color
)

val ALL_SHAPES = listOf(
    ShapeData("دائرة", "⭕", Color(0xFFEF4444)),
    ShapeData("مربع", "⏹️", Color(0xFF3B82F6)),
    ShapeData("مثلث", "🔺", Color(0xFF10B981)),
    ShapeData("نجمة", "⭐", Color(0xFFF59E0B)),
    ShapeData("قلب", "💖", Color(0xFFEC4899)),
    ShapeData("معين", "🔷", Color(0xFF8B5CF6)),
    ShapeData("هلال", "🌙", Color(0xFFEAB308)),
    ShapeData("بيضاوي", "🥚", Color(0xFF14B8A6))
)

data class SorterLevel(
    val levelNumber: Int,
    val title: String,
    val shapes: List<ShapeData>
)

val SORTER_LEVELS = listOf(
    SorterLevel(1, "المرحلة ١ (الأشكال الأساسية)", ALL_SHAPES.subList(0, 3)),
    SorterLevel(2, "المرحلة ٢ (أشكال ممتعة)", ALL_SHAPES.subList(0, 5)),
    SorterLevel(3, "المرحلة ٣ (تحدي النجوم)", ALL_SHAPES.subList(0, 7)),
    SorterLevel(4, "المرحلة ٤ (خبير الأشكال)", ALL_SHAPES)
)

@Composable
fun ShapeSorterScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val coroutineScope = rememberCoroutineScope()
        var levelIndex by remember { mutableIntStateOf(0) }
        val currentLevel = SORTER_LEVELS[levelIndex % SORTER_LEVELS.size]
        var roundIndex by remember { mutableIntStateOf(0) }

        val targetShape = currentLevel.shapes[roundIndex % currentLevel.shapes.size]
        var isMatchedInSlot by remember(targetShape) { mutableStateOf(false) }
        var wrongShapeTapped by remember { mutableStateOf<String?>(null) }
        var feedbackMessage by remember(targetShape) { mutableStateOf("طابق شكل ${targetShape.name} مع قالبه") }

        // Shake animation for incorrect choices
        val shakeOffset = remember { Animatable(0f) }
        // Snap celebration scale for slot
        val slotScale = remember { Animatable(1f) }

        val options = remember(targetShape, currentLevel) {
            val list = mutableListOf(targetShape)
            val others = currentLevel.shapes.filter { it != targetShape }.shuffled()
            if (others.isNotEmpty()) list.add(others[0])
            if (others.size > 1) list.add(others[1])
            list.shuffled()
        }

        LaunchedEffect(targetShape) {
            isMatchedInSlot = false
            slotScale.snapTo(1f)
            onSpeak("أين شكل ${targetShape.name}؟")
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF7ED))
                .testTag("shape_sorter_screen")
        ) {
            GameHeader(
                title = "فارز الأشكال الهندسية 🧩",
                onBack = onBack,
                starsCount = starsCount
            )

            // Level Selector Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SORTER_LEVELS) { lvl ->
                    val isCurrent = lvl.levelNumber == currentLevel.levelNumber
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isCurrent) SkyBlue600 else Color.White,
                        modifier = Modifier
                            .clickable {
                                levelIndex = lvl.levelNumber - 1
                                roundIndex = 0
                            }
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (isCurrent) SkyBlue600 else SkyBlue200,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .testTag("sorter_lvl_${lvl.levelNumber}")
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

            Spacer(modifier = Modifier.height(10.dp))

            // Silhouette / Cutout Slot Card
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = Color.White,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .border(
                        width = 4.dp,
                        color = if (isMatchedInSlot) Emerald500 else SkyBlue200,
                        shape = RoundedCornerShape(32.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = feedbackMessage,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isMatchedInSlot) Emerald500 else Color(0xFF475569)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // The Cutout Slot with Silhouette Outline or Snapped Shape
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .graphicsLayer {
                                scaleX = slotScale.value
                                scaleY = slotScale.value
                            }
                            .background(
                                color = if (isMatchedInSlot) targetShape.color.copy(alpha = 0.2f) else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(28.dp)
                            )
                            .border(
                                width = 3.dp,
                                color = if (isMatchedInSlot) targetShape.color else Color(0xFF94A3B8),
                                shape = RoundedCornerShape(28.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isMatchedInSlot) {
                            Text(
                                text = targetShape.emoji,
                                fontSize = 80.sp
                            )
                        } else {
                            // Ghost Silhouette Representation
                            Text(
                                text = targetShape.emoji,
                                fontSize = 76.sp,
                                modifier = Modifier.graphicsLayer { alpha = 0.25f }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = targetShape.name,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isMatchedInSlot) Emerald500 else SkyBlue600
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Interactive Options Row with Shake on Wrong & Snap on Correct
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                options.forEach { shape ->
                    val isWrongTapped = wrongShapeTapped == shape.name
                    Button(
                        onClick = {
                            if (isMatchedInSlot) return@Button

                            if (shape.name == targetShape.name) {
                                isMatchedInSlot = true
                                feedbackMessage = "ممتاز! هذا ${targetShape.name} ✨"
                                onSpeak("ممتاز! هذا ${targetShape.name}")
                                coroutineScope.launch {
                                    slotScale.animateTo(1.22f, tween(160))
                                    slotScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                    delay(750)
                                    onWin(1)
                                    roundIndex++
                                    if (roundIndex >= currentLevel.shapes.size && levelIndex < SORTER_LEVELS.size - 1) {
                                        levelIndex++
                                        roundIndex = 0
                                    }
                                }
                            } else {
                                wrongShapeTapped = shape.name
                                feedbackMessage = "هذا ${shape.name}، ابحث عن ${targetShape.name}"
                                onSpeak("هذا ${shape.name}، حاول مرة أخرى!")
                                coroutineScope.launch {
                                    shakeOffset.animateTo(14f, tween(50))
                                    shakeOffset.animateTo(-14f, tween(50))
                                    shakeOffset.animateTo(8f, tween(40))
                                    shakeOffset.animateTo(0f, tween(40))
                                    delay(600)
                                    wrongShapeTapped = null
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                            .graphicsLayer {
                                if (isWrongTapped) {
                                    translationX = shakeOffset.value
                                }
                            }
                            .testTag("shape_btn_${shape.name}"),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = shape.color),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp, pressedElevation = 2.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = shape.emoji, fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = shape.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Bottom Round Progress Counter
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 20.dp)
                    .border(1.dp, SkyBlue200, CircleShape)
            ) {
                Text(
                    text = "الجولة ${(roundIndex % currentLevel.shapes.size) + 1} من ${currentLevel.shapes.size} 🎯",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0369A1),
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)
                )
            }
        }
    }
}
