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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.littlegenius.ui.theme.SkyBlue200
import com.example.littlegenius.ui.theme.SkyBlue600

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
    ShapeData("معين", "🔷", Color(0xFF8B5CF6))
)

data class SorterLevel(
    val levelNumber: Int,
    val title: String,
    val shapes: List<ShapeData>
)

val SORTER_LEVELS = listOf(
    SorterLevel(1, "المرحلة ١ (٣ أشكال)", ALL_SHAPES.subList(0, 3)),
    SorterLevel(2, "المرحلة ٢ (٤ أشكال)", ALL_SHAPES.subList(0, 4)),
    SorterLevel(3, "المرحلة ٣ (٥ أشكال)", ALL_SHAPES.subList(0, 5)),
    SorterLevel(4, "المرحلة ٤ (جميع الأشكال)", ALL_SHAPES)
)

@Composable
fun ShapeSorterScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var levelIndex by remember { mutableIntStateOf(0) }
        val currentLevel = SORTER_LEVELS[levelIndex % SORTER_LEVELS.size]
        var roundIndex by remember { mutableIntStateOf(0) }

        val targetShape = currentLevel.shapes[roundIndex % currentLevel.shapes.size]

        val options = remember(targetShape, currentLevel) {
            val list = mutableListOf(targetShape)
            val others = currentLevel.shapes.filter { it != targetShape }.shuffled()
            if (others.isNotEmpty()) list.add(others[0])
            if (others.size > 1) list.add(others[1])
            list.shuffled()
        }

        LaunchedEffect(targetShape) {
            onSpeak("أين شكل ${targetShape.name}؟")
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF7ED))
                .testTag("shape_sorter_screen")
        ) {
            GameHeader(
                title = "الأشكال الهندسية 🧩",
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

            Spacer(modifier = Modifier.height(14.dp))

            // Silhouette / Cutout Slot Card
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = Color.White,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .border(4.dp, SkyBlue200, RoundedCornerShape(32.dp))
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "طابق الشكل مع قالبه",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(24.dp))
                            .border(3.dp, Color(0xFF94A3B8), RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = targetShape.emoji,
                            fontSize = 72.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = targetShape.name,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = SkyBlue600
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                options.forEach { shape ->
                    Button(
                        onClick = {
                            if (shape.name == targetShape.name) {
                                onSpeak("ممتاز! هذا ${targetShape.name}")
                                onWin(1)
                                roundIndex++
                                if (roundIndex >= currentLevel.shapes.size && levelIndex < SORTER_LEVELS.size - 1) {
                                    levelIndex++
                                    roundIndex = 0
                                }
                            } else {
                                onSpeak("حاول مرة أخرى")
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(96.dp)
                            .testTag("shape_btn_${shape.name}"),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = shape.color)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = shape.emoji, fontSize = 40.sp)
                            Text(text = shape.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
