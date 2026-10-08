package com.example.littlegenius.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.littlegenius.ui.theme.Amber400
import com.example.littlegenius.ui.theme.Emerald500
import com.example.littlegenius.ui.theme.SkyBlue200
import com.example.littlegenius.ui.theme.SkyBlue600
import kotlinx.coroutines.delay

data class MemoryCard(
    val id: Int,
    val emoji: String,
    val name: String,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)

data class MemoryLevel(
    val levelNumber: Int,
    val title: String,
    val columns: Int,
    val pairsCount: Int,
    val themeName: String,
    val items: List<Pair<String, String>>
)

val MEMORY_LEVELS = listOf(
    MemoryLevel(
        1,
        "مستوى ١ (٤ بطاقات)",
        2,
        2,
        "حيوانات الغابة",
        listOf("🦁" to "أسد", "🐼" to "باندا", "🐰" to "أرنب", "🦊" to "ثعلب")
    ),
    MemoryLevel(
        2,
        "مستوى ٢ (٦ بطاقات)",
        3,
        3,
        "سلة الفواكه",
        listOf("🍎" to "تفاح", "🍌" to "موز", "🍓" to "فراولة", "🍇" to "عنب", "🍊" to "برتقال")
    ),
    MemoryLevel(
        3,
        "مستوى ٣ (٨ بطاقات)",
        4,
        4,
        "المركبات السريعة",
        listOf("🚗" to "سيارة", "🚀" to "صاروخ", "✈️" to "طائرة", "🚢" to "سفينة", "🚁" to "مروحية")
    ),
    MemoryLevel(
        4,
        "مستوى ٤ (١٢ بطاقة)",
        4,
        6,
        "أبطال الفضاء والطبيعة",
        listOf("⭐" to "نجمة", "🌙" to "قمر", "☀️" to "شمس", "🌈" to "قوس قزح", "🌸" to "زهرة", "🍄" to "فطر")
    )
)

@Composable
fun MemoryMatchScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var currentLevelIndex by remember { mutableIntStateOf(0) }
        val currentLevel = MEMORY_LEVELS[currentLevelIndex % MEMORY_LEVELS.size]

        var cards by remember(currentLevel) {
            val selectedPairs = currentLevel.items.shuffled().take(currentLevel.pairsCount)
            val doubled = (selectedPairs + selectedPairs).shuffled()
            mutableStateOf(
                doubled.mapIndexed { index, pair ->
                    MemoryCard(index, pair.first, pair.second)
                }
            )
        }

        var selectedFirst by remember(currentLevel) { mutableStateOf<Int?>(null) }
        var selectedSecond by remember(currentLevel) { mutableStateOf<Int?>(null) }
        var isChecking by remember(currentLevel) { mutableStateOf(false) }
        var movesCount by remember(currentLevel) { mutableIntStateOf(0) }

        LaunchedEffect(currentLevel) {
            onSpeak("${currentLevel.title}: ${currentLevel.themeName}! اقلب البطاقات وابحث عن المتشابهين")
        }

        LaunchedEffect(selectedFirst, selectedSecond) {
            if (selectedFirst != null && selectedSecond != null) {
                isChecking = true
                movesCount++
                delay(700)
                val c1 = cards.firstOrNull { it.id == selectedFirst }
                val c2 = cards.firstOrNull { it.id == selectedSecond }

                if (c1 != null && c2 != null) {
                    if (c1.emoji == c2.emoji) {
                        cards = cards.map {
                            if (it.id == c1.id || it.id == c2.id) it.copy(isMatched = true) else it
                        }
                        onSpeak("تطابق رائع! ${c1.name}")
                        if (cards.all { it.isMatched }) {
                            onSpeak("ممتاز جداً! أكملت كل بطاقات ${currentLevel.themeName}")
                            onWin(2)
                            if (currentLevelIndex < MEMORY_LEVELS.size - 1) {
                                currentLevelIndex++
                            }
                        }
                    } else {
                        cards = cards.map {
                            if (it.id == c1.id || it.id == c2.id) it.copy(isFlipped = false) else it
                        }
                    }
                }

                selectedFirst = null
                selectedSecond = null
                isChecking = false
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F3FF))
                .testTag("memory_match_screen")
        ) {
            GameHeader(
                title = "لعبة الذاكرة 🧠",
                onBack = onBack,
                starsCount = starsCount
            )

            // Level Selector Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(MEMORY_LEVELS) { lvl ->
                    val isSel = lvl.levelNumber == currentLevel.levelNumber
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSel) SkyBlue600 else Color.White,
                        modifier = Modifier
                            .clickable {
                                currentLevelIndex = lvl.levelNumber - 1
                            }
                            .border(
                                width = if (isSel) 2.dp else 1.dp,
                                color = if (isSel) SkyBlue600 else SkyBlue200,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .testTag("memory_lvl_${lvl.levelNumber}")
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

            // Subtitle & Moves
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الموضوع: ${currentLevel.themeName} ✨",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B21A8)
                )
                Text(
                    text = "المحاولات: $movesCount 🎯",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Emerald500
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(currentLevel.columns),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(cards) { card ->
                    val isRevealed = card.isFlipped || card.isMatched
                    Surface(
                        modifier = Modifier
                            .aspectRatio(0.9f)
                            .border(
                                3.dp,
                                if (card.isMatched) Emerald500 else Color.White,
                                RoundedCornerShape(18.dp)
                            )
                            .clickable(enabled = !isRevealed && !isChecking) {
                                cards = cards.map {
                                    if (it.id == card.id) it.copy(isFlipped = true) else it
                                }
                                onSpeak(card.name)
                                if (selectedFirst == null) {
                                    selectedFirst = card.id
                                } else {
                                    selectedSecond = card.id
                                }
                            }
                            .testTag("memory_card_${card.id}"),
                        shape = RoundedCornerShape(18.dp),
                        color = if (card.isMatched) Color(0xFFD1FAE5) else if (card.isFlipped) Color.White else SkyBlue600,
                        shadowElevation = 3.dp
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isRevealed) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(card.emoji, fontSize = if (currentLevel.columns >= 4) 36.sp else 44.sp)
                                    Text(
                                        text = card.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF334155)
                                    )
                                }
                            } else {
                                Text("❓", fontSize = 32.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
