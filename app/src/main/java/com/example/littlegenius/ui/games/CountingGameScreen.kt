package com.example.littlegenius.ui.games

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
import com.example.littlegenius.ui.theme.SkyBlue200
import com.example.littlegenius.ui.theme.SkyBlue600
import kotlin.random.Random

data class CountRound(
    val emoji: String,
    val count: Int,
    val name: String
)

val COUNT_ITEMS = listOf(
    "🍎" to "تفاحات",
    "🦆" to "بطات",
    "🚗" to "سيارات",
    "⭐" to "نجوم",
    "🎈" to "بالونات",
    "🐟" to "سمكات"
)

data class CountingLevel(
    val levelNumber: Int,
    val title: String,
    val minCount: Int,
    val maxCount: Int
)

val COUNTING_LEVELS = listOf(
    CountingLevel(1, "المرحلة ١ (١-٣)", 1, 3),
    CountingLevel(2, "المرحلة ٢ (١-٥)", 1, 5),
    CountingLevel(3, "المرحلة ٣ (٣-٧)", 3, 7),
    CountingLevel(4, "المرحلة ٤ (٥-١٠)", 5, 10),
    CountingLevel(5, "المرحلة ٥ (التحدي الكبير)", 1, 10)
)

@Composable
fun CountingGameScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var levelIndex by remember { mutableIntStateOf(0) }
        val currentLevel = COUNTING_LEVELS[levelIndex % COUNTING_LEVELS.size]
        var roundSeed by remember { mutableIntStateOf(1) }

        val currentRound = remember(roundSeed, currentLevel) {
            val item = COUNT_ITEMS.random()
            val count = Random.nextInt(currentLevel.minCount, currentLevel.maxCount + 1)
            CountRound(item.first, count, item.second)
        }

        val options = remember(currentRound) {
            val opts = mutableListOf(currentRound.count)
            while (opts.size < 3) {
                val n = Random.nextInt(currentLevel.minCount, (currentLevel.maxCount + 2).coerceAtLeast(4))
                if (!opts.contains(n)) opts.add(n)
            }
            opts.shuffled()
        }

        LaunchedEffect(currentRound) {
            onSpeak("كم عدد الـ ${currentRound.name}؟")
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFEF3C7))
                .testTag("counting_game_screen")
        ) {
            GameHeader(
                title = "عد الأشياء 🔢",
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
                items(COUNTING_LEVELS) { lvl ->
                    val isCurrent = lvl.levelNumber == currentLevel.levelNumber
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isCurrent) SkyBlue600 else Color.White,
                        modifier = Modifier
                            .clickable {
                                levelIndex = lvl.levelNumber - 1
                                roundSeed++
                            }
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (isCurrent) SkyBlue600 else SkyBlue200,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .testTag("counting_lvl_${lvl.levelNumber}")
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

            // Display card showing interactive items to count
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = Color.White,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .border(4.dp, Amber400, RoundedCornerShape(32.dp))
            ) {
                var tappedIndices by remember(currentRound) { mutableStateOf(setOf<Int>()) }

                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "المس الأشياء لعدّها واحداً تلو الآخر! 👆",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // FlowRow or wrapped items for tap-to-count
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 64.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items((1..currentRound.count).toList()) { index ->
                            val isTapped = tappedIndices.contains(index)
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(
                                        if (isTapped) Color(0xFFFEF3C7) else Color(0xFFF1F5F9),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .border(
                                        2.dp,
                                        if (isTapped) Amber400 else Color(0xFFCBD5E1),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        tappedIndices = tappedIndices + index
                                        val countNames = listOf("واحد", "اثنان", "ثلاثة", "أربعة", "خمسة", "ستة", "سبعة", "ثمانية", "تسعة", "عشرة")
                                        val spokenNumber = if (index <= countNames.size) countNames[index - 1] else "$index"
                                        onSpeak(spokenNumber)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentRound.emoji,
                                    fontSize = 32.sp
                                )
                                if (isTapped) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Amber400,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = 4.dp, y = (-4).dp)
                                            .size(22.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "$index",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF78350F)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "عدد ما لمسته: ${tappedIndices.size} من ${currentRound.count} ✨",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (tappedIndices.size == currentRound.count) com.example.littlegenius.ui.theme.Emerald500 else Color(0xFFB45309)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Numbers buttons
            Text(
                text = "اختر الرقم الصحيح:",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF78350F),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                options.forEach { opt ->
                    Button(
                        onClick = {
                            if (opt == currentRound.count) {
                                onSpeak("صحيح! $opt ${currentRound.name}")
                                onWin(1)
                                roundSeed++
                            } else {
                                onSpeak("حاول عدها مرة أخرى")
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(72.dp)
                            .testTag("count_btn_$opt"),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SkyBlue600,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "$opt",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
