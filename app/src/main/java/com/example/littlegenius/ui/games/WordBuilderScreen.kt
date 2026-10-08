package com.example.littlegenius.ui.games

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

data class WordTarget(
    val word: String,
    val letters: List<String>,
    val emoji: String,
    val color: Color
)

data class WordLevel(
    val levelNumber: Int,
    val title: String,
    val targets: List<WordTarget>
)

val WORD_LEVELS = listOf(
    WordLevel(
        1, "المرحلة ١ (كلمات ٣ أحرف)",
        listOf(
            WordTarget("شَمْس", listOf("ش", "م", "س"), "☀️", Color(0xFFFED7AA)),
            WordTarget("بَيْت", listOf("ب", "ي", "ت"), "🏠", Color(0xFFBBF7D0)),
            WordTarget("قَلَم", listOf("ق", "ل", "م"), "✏️", Color(0xFFBAE6FD))
        )
    ),
    WordLevel(
        2, "المرحلة ٢ (حيوانات أليفة)",
        listOf(
            WordTarget("كَلْب", listOf("ك", "ل", "ب"), "🐶", Color(0xFFE9D5FF)),
            WordTarget("قِطّ", listOf("ق", "ط", "ة"), "🐱", Color(0xFFFDE68A)),
            WordTarget("نَجْم", listOf("ن", "ج", "م"), "🌟", Color(0xFFFECDD3))
        )
    ),
    WordLevel(
        3, "المرحلة ٣ (كلمات ٤ أحرف)",
        listOf(
            WordTarget("كِتَاب", listOf("ك", "ت", "ا", "ب"), "📖", Color(0xFF93C5FD)),
            WordTarget("حِصَان", listOf("ح", "ص", "ا", "ن"), "🐴", Color(0xFFFDE047)),
            WordTarget("طَائِر", listOf("ط", "ا", "ئ", "ر"), "🕊️", Color(0xFFA78BFA))
        )
    ),
    WordLevel(
        4, "المرحلة ٤ (طبيعة وحياة)",
        listOf(
            WordTarget("سَمَكَة", listOf("س", "م", "ك", "ة"), "🐟", Color(0xFF67E8F9)),
            WordTarget("زَهْرَة", listOf("ز", "ه", "ر", "ة"), "🌸", Color(0xFFFBCFE8)),
            WordTarget("سَفِينَة", listOf("س", "ف", "ي", "ن"), "⛵", Color(0xFF86EFAC))
        )
    )
)

@Composable
fun WordBuilderScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var levelIndex by remember { mutableIntStateOf(0) }
        val currentLevel = WORD_LEVELS[levelIndex % WORD_LEVELS.size]

        var wordIndex by remember { mutableIntStateOf(0) }
        val currentWord = currentLevel.targets[wordIndex % currentLevel.targets.size]

        val enteredLetters = remember { mutableStateListOf<String>() }
        val scrambledLetters = remember(currentWord) {
            currentWord.letters.shuffled()
        }

        LaunchedEffect(currentWord) {
            enteredLetters.clear()
            onSpeak(currentWord.word)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFECFDF5))
                .testTag("word_builder_screen")
        ) {
            GameHeader(
                title = "تكوين الكلمات 📝",
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
                items(WORD_LEVELS) { lvl ->
                    val isCurrent = lvl.levelNumber == currentLevel.levelNumber
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isCurrent) SkyBlue600 else Color.White,
                        modifier = Modifier
                            .clickable {
                                levelIndex = lvl.levelNumber - 1
                                wordIndex = 0
                            }
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (isCurrent) SkyBlue600 else SkyBlue200,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .testTag("word_lvl_${lvl.levelNumber}")
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

            // Target Picture Card
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = currentWord.color,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .border(3.dp, Color.White, RoundedCornerShape(28.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentWord.emoji,
                        fontSize = 68.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = currentWord.word,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF065F46)
                        )
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.clickable {
                                onSpeak(currentWord.word)
                            }
                        ) {
                            Text(
                                text = "🔊 اسمع",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF065F46),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Slots for letters to place
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                currentWord.letters.forEachIndexed { index, _ ->
                    val placedChar = enteredLetters.getOrNull(index) ?: ""
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (placedChar.isNotEmpty()) Color.White else Color(0xFFD1FAE5),
                        shadowElevation = if (placedChar.isNotEmpty()) 4.dp else 0.dp,
                        modifier = Modifier
                            .size(64.dp)
                            .padding(4.dp)
                            .border(2.dp, Emerald500, RoundedCornerShape(16.dp))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = placedChar,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Letter selection pool
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "المس الحروف بالترتيب الصحيح:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF065F46),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    scrambledLetters.forEach { letter ->
                        Button(
                            onClick = {
                                if (enteredLetters.size < currentWord.letters.size) {
                                    enteredLetters.add(letter)
                                    onSpeak(letter)

                                    if (enteredLetters.size == currentWord.letters.size) {
                                        val isCorrect = enteredLetters.toList() == currentWord.letters
                                        if (isCorrect) {
                                            onSpeak("ممتاز! ${currentWord.word}")
                                            onWin(1)
                                            wordIndex++
                                            if (wordIndex >= currentLevel.targets.size && levelIndex < WORD_LEVELS.size - 1) {
                                                levelIndex++
                                                wordIndex = 0
                                            }
                                        } else {
                                            onSpeak("حاول مرة أخرى")
                                            enteredLetters.clear()
                                        }
                                    }
                                }
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                            modifier = Modifier
                                .size(60.dp)
                                .testTag("letter_pool_$letter")
                        ) {
                            Text(
                                text = letter,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                TextButton(
                    onClick = { enteredLetters.clear() },
                    modifier = Modifier.testTag("clear_letters_btn")
                ) {
                    Text("إعادة المحاولة 🔄", color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
