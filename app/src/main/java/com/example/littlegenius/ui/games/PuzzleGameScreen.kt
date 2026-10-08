package com.example.littlegenius.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.littlegenius.ui.theme.Amber400
import com.example.littlegenius.ui.theme.Emerald500
import com.example.littlegenius.ui.theme.SkyBlue200
import com.example.littlegenius.ui.theme.SkyBlue600

data class PuzzleLevel(
    val levelNumber: Int,
    val title: String,
    val mainEmoji: String,
    val gridDimension: Int, // 2 for 2x2, 3 for 3x3
    val pieceLabels: List<String>,
    val color: Color
)

val PUZZLE_10_LEVELS = listOf(
    PuzzleLevel(1, "القطة اللطيفة", "🐱", 2, listOf("🐱 أذن", "🐱 عين", "🐱 أنف", "🐱 فم"), Color(0xFFFDBA74)),
    PuzzleLevel(2, "الجرو الوفي", "🐶", 2, listOf("🐶 رأس", "🐶 ذيل", "🐶 جسم", "🐶 قدم"), Color(0xFF93C5FD)),
    PuzzleLevel(3, "الأسد الشجاع", "🦁", 2, listOf("🦁 عرف", "🦁 وجه", "🦁 كف", "🦁 ظهر"), Color(0xFFFDE047)),
    PuzzleLevel(4, "البطة السابحة", "🦆", 2, listOf("🦆 منقار", "🦆 جناح", "🦆 ريش", "🦆 ماء"), Color(0xFF86EFAC)),
    PuzzleLevel(5, "صاروخ الفضاء", "🚀", 2, listOf("🚀 قمة", "🚀 نافذة", "🚀 جناح", "🚀 شعلة"), Color(0xFFF472B6)),
    PuzzleLevel(6, "الفراشة الملونة", "🦋", 3, listOf("🦋 جناح ١", "🦋 رأس", "🦋 جناح ٢", "🦋 جسم", "🦋 قلب", "🦋 ذيل", "🦋 زهرة ١", "🦋 عشب", "🦋 زهرة ٢"), Color(0xFFC084FC)),
    PuzzleLevel(7, "الديناصور الصغير", "🦖", 3, listOf("🦖 رأس", "🦖 عنق", "🦖 ظهر", "🦖 يد", "🦖 بطن", "🦖 ذيل", "🦖 قدم ١", "🦖 أرض", "🦖 قدم ٢"), Color(0xFF34D399)),
    PuzzleLevel(8, "الفيل الحكيم", "🐘", 3, listOf("🐘 أذن ١", "🐘 خرطوم", "🐘 أذن ٢", "🐘 ظهر", "🐘 بطن", "🐘 عاج", "🐘 قدم ١", "🐘 عشب", "🐘 قدم ٢"), Color(0xFF94A3B8)),
    PuzzleLevel(9, "السمكة الذهبية", "🐠", 3, listOf("🐠 فم", "🐠 عين", "🐠 زعانف", "🐠 قشور", "🐠 جسم", "🐠 ماء", "🐠 ذيل", "🐠 فقاعات", "🐠 بحر"), Color(0xFF38BDF8)),
    PuzzleLevel(10, "الحديقة السعيدة", "🌸", 3, listOf("🌸 شمس", "🌸 سحاب", "🌸 طائر", "🌸 وردة ١", "🌸 شجرة", "🌸 وردة ٢", "🌸 فراشة", "🌸 عشب", "🌸 نهر"), Color(0xFFF43F5E))
)

@Composable
fun PuzzleGameScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    var currentLevelIndex by remember { mutableIntStateOf(0) }
    val level = PUZZLE_10_LEVELS[currentLevelIndex % PUZZLE_10_LEVELS.size]

    val totalPieces = level.gridDimension * level.gridDimension
    var tiles by remember(level) {
        // Correct order is 0 until totalPieces
        val shuffled = (0 until totalPieces).shuffled()
        mutableStateOf(if (shuffled == (0 until totalPieces).toList()) shuffled.reversed() else shuffled)
    }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var movesCount by remember { mutableIntStateOf(0) }
    val isSolved = remember(tiles) { tiles == (0 until totalPieces).toList() }

    LaunchedEffect(level) {
        onSpeak("المرحلة ${level.levelNumber}: ${level.title}")
    }

    LaunchedEffect(isSolved) {
        if (isSolved && movesCount > 0) {
            onSpeak("أحسنت! أكملت بازل ${level.title}")
            onWin(2)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0FDF4))
            .testTag("puzzle_game_screen")
    ) {
        GameHeader(
            title = "بازل الصور 🧩",
            onBack = onBack,
            starsCount = starsCount,
            trailingAction = {
                IconButton(
                    onClick = {
                        tiles = (0 until totalPieces).shuffled()
                        selectedIndex = null
                        movesCount = 0
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White, CircleShape)
                        .testTag("reset_puzzle_btn")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "إعادة الترتيب", tint = SkyBlue600)
                }
            }
        )

        // Level Selector Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(PUZZLE_10_LEVELS) { l ->
                val isCurrent = l.levelNumber == level.levelNumber
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isCurrent) SkyBlue600 else Color.White,
                    modifier = Modifier
                        .clickable {
                            currentLevelIndex = l.levelNumber - 1
                            movesCount = 0
                            selectedIndex = null
                        }
                        .border(
                            width = if (isCurrent) 2.dp else 1.dp,
                            color = if (isCurrent) SkyBlue600 else SkyBlue200,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .testTag("level_chip_${l.levelNumber}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(l.mainEmoji, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "مستوى ${l.levelNumber}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) Color.White else Color(0xFF334155)
                        )
                    }
                }
            }
        }

        // Header info card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = level.color.copy(alpha = 0.35f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .border(2.dp, level.color, RoundedCornerShape(20.dp))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(level.mainEmoji, fontSize = 36.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "${level.title} (${level.gridDimension}×${level.gridDimension})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = if (isSolved) "🎉 تم حل البازل بنجاح!" else "بدّل القطع لتصل للصورة الصحيحة",
                            fontSize = 12.sp,
                            color = Color(0xFF475569)
                        )
                    }
                }
                Surface(
                    shape = CircleShape,
                    color = Color.White
                ) {
                    Text(
                        text = "الحركات: $movesCount",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F766E),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Puzzle Board Grid
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .border(4.dp, if (isSolved) Emerald500 else SkyBlue200, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    val rows = tiles.chunked(level.gridDimension)
                    rows.forEachIndexed { rowIndex, rowTiles ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            rowTiles.forEachIndexed { colIndex, pieceIndex ->
                                val tilePosition = rowIndex * level.gridDimension + colIndex
                                val isSelected = selectedIndex == tilePosition
                                val isCorrectSpot = pieceIndex == tilePosition

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isCorrectSpot) level.color.copy(alpha = 0.5f) else Color(0xFFF1F5F9),
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(4.dp)
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) Amber400 else if (isCorrectSpot) Emerald500 else Color.LightGray,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            if (!isSolved) {
                                                if (selectedIndex == null) {
                                                    selectedIndex = tilePosition
                                                } else {
                                                    // Swap tiles
                                                    val newTiles = tiles.toMutableList()
                                                    val temp = newTiles[selectedIndex!!]
                                                    newTiles[selectedIndex!!] = newTiles[tilePosition]
                                                    newTiles[tilePosition] = temp
                                                    tiles = newTiles
                                                    movesCount++
                                                    selectedIndex = null
                                                }
                                            }
                                        }
                                        .testTag("puzzle_tile_$tilePosition")
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = level.pieceLabels.getOrElse(pieceIndex) { level.mainEmoji },
                                            fontSize = if (level.gridDimension == 2) 20.sp else 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            color = Color(0xFF1E293B)
                                        )
                                        if (isCorrectSpot) {
                                            Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Emerald500,
                                                modifier = Modifier.size(16.dp)
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

        // Bottom next level button if solved
        if (isSolved && currentLevelIndex < PUZZLE_10_LEVELS.size - 1) {
            Button(
                onClick = {
                    currentLevelIndex++
                    movesCount = 0
                    selectedIndex = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .height(54.dp)
                    .testTag("next_puzzle_level_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Emerald500)
            ) {
                Text(
                    text = "الانتقال إلى المرحلة التالية (${currentLevelIndex + 2}) 🚀",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
