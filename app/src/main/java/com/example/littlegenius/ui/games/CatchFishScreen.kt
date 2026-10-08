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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.littlegenius.ui.theme.Emerald500
import com.example.littlegenius.ui.theme.SkyBlue200
import com.example.littlegenius.ui.theme.SkyBlue600
import kotlinx.coroutines.delay
import kotlin.random.Random

data class SwimmingFish(
    val id: Int,
    val name: String,
    val emoji: String,
    val xRatio: Float,
    val yRatio: Float,
    val speed: Float,
    val isTarget: Boolean = false
)

data class FishLevel(
    val levelNumber: Int,
    val title: String,
    val targetName: String,
    val targetEmoji: String,
    val targetCount: Int,
    val waterColor: Color,
    val fishPool: List<Pair<String, String>>
)

val FISH_LEVELS = listOf(
    FishLevel(
        1,
        "مستوى ١: الشاطئ الذهبي",
        "سمكة برتقالية",
        "🐠",
        5,
        Color(0xFF0284C7),
        listOf(
            "سمكة برتقالية" to "🐠",
            "سمكة زرقاء" to "🐟",
            "نجم بحر" to "⭐"
        )
    ),
    FishLevel(
        2,
        "مستوى ٢: الشعاب المرجانية",
        "سلطعون سريع",
        "🦀",
        6,
        Color(0xFF0369A1),
        listOf(
            "سلطعون سريع" to "🦀",
            "سمكة منتفخة" to "🐡",
            "سمكة ملونة" to "🐠"
        )
    ),
    FishLevel(
        3,
        "مستوى ٣: الأعماق السحرية",
        "أخطبوط ذكي",
        "🐙",
        7,
        Color(0xFF075985),
        listOf(
            "أخطبوط ذكي" to "🐙",
            "حوت لطيف" to "🐳",
            "سمكة قرش مرحة" to "🦈",
            "سلحفاة بحرية" to "🐢"
        )
    ),
    FishLevel(
        4,
        "مستوى ٤: كنز الدلافين",
        "دلفين راقص",
        "🐬",
        8,
        Color(0xFF0C4A6E),
        listOf(
            "دلفين راقص" to "🐬",
            "صندوق الكنز" to "💎",
            "أخطبوط ذكي" to "🐙",
            "حورية بحر" to "🧜"
        )
    )
)

@Composable
fun CatchFishScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var levelIndex by remember { mutableIntStateOf(0) }
        val currentLevel = FISH_LEVELS[levelIndex % FISH_LEVELS.size]

        var caughtTargetCount by remember(currentLevel) { mutableIntStateOf(0) }
        val fishes = remember(currentLevel) { mutableStateListOf<SwimmingFish>() }
        var fishId by remember { mutableIntStateOf(0) }
        var feedbackMessage by remember { mutableStateOf("") }

        LaunchedEffect(currentLevel) {
            caughtTargetCount = 0
            fishes.clear()
            feedbackMessage = "مهمتك: اصطد ${currentLevel.targetCount} من ${currentLevel.targetName} ${currentLevel.targetEmoji}"
            onSpeak("مرحباً بك في ${currentLevel.title}! مهمتك اصطياد ${currentLevel.targetName} ${currentLevel.targetEmoji}")
            while (true) {
                if (fishes.size < 7) {
                    val isTargetSpawn = Random.nextFloat() < 0.45f
                    val fishPair = if (isTargetSpawn) {
                        currentLevel.targetName to currentLevel.targetEmoji
                    } else {
                        currentLevel.fishPool.random()
                    }
                    fishes.add(
                        SwimmingFish(
                            id = fishId++,
                            name = fishPair.first,
                            emoji = fishPair.second,
                            xRatio = Random.nextFloat() * 0.76f + 0.08f,
                            yRatio = Random.nextFloat() * 0.58f + 0.16f,
                            speed = Random.nextFloat() * 0.003f + 0.002f,
                            isTarget = fishPair.second == currentLevel.targetEmoji
                        )
                    )
                }
                delay(1300)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(currentLevel.waterColor)
                .testTag("catch_fish_screen")
        ) {
            Column(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)) {
                GameHeader(
                    title = "صيد السمك والبحار 🐠",
                    onBack = onBack,
                    starsCount = starsCount
                )

                // Level Tabs
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(FISH_LEVELS) { lvl ->
                        val isSel = lvl.levelNumber == currentLevel.levelNumber
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSel) SkyBlue600 else Color.White.copy(alpha = 0.9f),
                            modifier = Modifier
                                .clickable {
                                    levelIndex = lvl.levelNumber - 1
                                }
                                .border(
                                    width = if (isSel) 2.dp else 1.dp,
                                    color = if (isSel) SkyBlue600 else SkyBlue200,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .testTag("fish_lvl_${lvl.levelNumber}")
                        ) {
                            Text(
                                text = lvl.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else Color(0xFF0369A1),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Target Banner
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White.copy(alpha = 0.92f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "المطلوب: ${currentLevel.targetName} ${currentLevel.targetEmoji}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "$caughtTargetCount / ${currentLevel.targetCount} 🎯",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = if (caughtTargetCount >= currentLevel.targetCount) Emerald500 else Color(0xFF0284C7)
                        )
                    }
                }
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 135.dp, bottom = 80.dp)
            ) {
                val width = maxWidth
                val height = maxHeight

                fishes.forEach { fish ->
                    Box(
                        modifier = Modifier
                            .offset(
                                x = width * fish.xRatio,
                                y = height * fish.yRatio
                            )
                            .size(76.dp)
                            .clickable {
                                fishes.remove(fish)
                                if (fish.isTarget) {
                                    caughtTargetCount++
                                    onSpeak("رائع! اصطدت ${fish.name}")
                                    feedbackMessage = "ممتاز! ${fish.name} +١"
                                    if (caughtTargetCount >= currentLevel.targetCount) {
                                        onSpeak("مبروك! أكملت مهمة صيد ${currentLevel.targetName}")
                                        onWin(2)
                                        if (levelIndex < FISH_LEVELS.size - 1) {
                                            levelIndex++
                                        }
                                    }
                                } else {
                                    onSpeak("هذه ${fish.name}، ابحث عن ${currentLevel.targetName}")
                                    feedbackMessage = "هذه ${fish.name}! ركز على ${currentLevel.targetEmoji}"
                                }
                            }
                            .testTag("fish_${fish.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (fish.isTarget) Color.Yellow.copy(alpha = 0.25f) else Color.Transparent,
                            modifier = Modifier.size(68.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(fish.emoji, fontSize = 44.sp)
                            }
                        }
                    }
                }
            }

            // Bottom Status Toast
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 5.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
            ) {
                Text(
                    text = feedbackMessage.ifEmpty { "المس الكائنات البحرية لاصطيادها! 🌊" },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0369A1),
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp)
                )
            }
        }
    }
}
