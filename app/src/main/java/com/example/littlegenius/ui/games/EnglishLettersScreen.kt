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
import com.example.littlegenius.ui.theme.Emerald500
import com.example.littlegenius.ui.theme.SkyBlue200
import com.example.littlegenius.ui.theme.SkyBlue600

data class EnglishLetter(
    val letter: String,
    val word: String,
    val emoji: String,
    val color: Color
)

val ENGLISH_LETTERS = listOf(
    EnglishLetter("A", "Apple", "🍎", Color(0xFFFCA5A5)),
    EnglishLetter("B", "Ball", "⚽", Color(0xFF93C5FD)),
    EnglishLetter("C", "Cat", "🐱", Color(0xFFFDBA74)),
    EnglishLetter("D", "Dog", "🐶", Color(0xFFFDE047)),
    EnglishLetter("E", "Elephant", "🐘", Color(0xFFCBD5E1)),
    EnglishLetter("F", "Fish", "🐟", Color(0xFF67E8F9)),
    EnglishLetter("G", "Giraffe", "🦒", Color(0xFFFCD34D)),
    EnglishLetter("H", "Horse", "🐴", Color(0xFFFB923C)),
    EnglishLetter("I", "Ice cream", "🍦", Color(0xFFF472B6)),
    EnglishLetter("J", "Juice", "🧃", Color(0xFFA78BFA)),
    EnglishLetter("K", "Kite", "🪁", Color(0xFF34D399)),
    EnglishLetter("L", "Lion", "🦁", Color(0xFFFBBF24)),
    EnglishLetter("M", "Monkey", "🐵", Color(0xFFD97706)),
    EnglishLetter("N", "Nest", "🪺", Color(0xFF86EFAC)),
    EnglishLetter("O", "Orange", "🍊", Color(0xFFFB923C)),
    EnglishLetter("P", "Pencil", "✏️", Color(0xFF38BDF8)),
    EnglishLetter("Q", "Queen", "👑", Color(0xFFE879F9)),
    EnglishLetter("R", "Rabbit", "🐰", Color(0xFFF43F5E)),
    EnglishLetter("S", "Sun", "☀️", Color(0xFFFACC15)),
    EnglishLetter("T", "Tree", "🌳", Color(0xFF22C55E)),
    EnglishLetter("U", "Umbrella", "☂️", Color(0xFF818CF8)),
    EnglishLetter("V", "Violin", "🎻", Color(0xFFC084FC)),
    EnglishLetter("W", "Water", "💧", Color(0xFF0284C7)),
    EnglishLetter("X", "Xylophone", "🎼", Color(0xFFEC4899)),
    EnglishLetter("Y", "Yacht", "⛵", Color(0xFF14B8A6)),
    EnglishLetter("Z", "Zebra", "🦓", Color(0xFF64748B))
)

val ENGLISH_LEVELS = listOf(
    "Level 1 (A-G)" to ENGLISH_LETTERS.subList(0, 7),
    "Level 2 (H-N)" to ENGLISH_LETTERS.subList(7, 14),
    "Level 3 (O-U)" to ENGLISH_LETTERS.subList(14, 21),
    "Level 4 (V-Z)" to ENGLISH_LETTERS.subList(21, 26),
    "Level 5 (All A-Z)" to ENGLISH_LETTERS
)

@Composable
fun EnglishLettersScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onSpeakEnglish: (String) -> Unit
) {
    // English games are explicitly set to LTR as requested
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        var levelIndex by remember { mutableIntStateOf(0) }
        val currentLevel = ENGLISH_LEVELS[levelIndex]
        val displayedLetters = currentLevel.second

        var selectedLetter by remember(levelIndex) { mutableStateOf<EnglishLetter?>(displayedLetters.firstOrNull()) }
        var isLowercaseMode by remember { mutableStateOf(false) }
        var isQuizMode by remember { mutableStateOf(false) }
        var quizTarget by remember(levelIndex, isQuizMode) {
            mutableStateOf(if (isQuizMode) displayedLetters.random() else null)
        }
        var feedbackText by remember { mutableStateOf("") }

        LaunchedEffect(selectedLetter, isQuizMode) {
            if (isQuizMode && quizTarget != null) {
                onSpeakEnglish("Where is the letter ${quizTarget?.letter}? Tap on it!")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF1F2))
                .testTag("english_letters_screen")
        ) {
            GameHeader(
                title = "English Letters 🔤",
                onBack = onBack,
                starsCount = starsCount
            )

            // Mode & Level Selector Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(ENGLISH_LEVELS.indices.toList()) { idx ->
                        val isCurrent = idx == levelIndex
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isCurrent) SkyBlue600 else Color.White,
                            modifier = Modifier
                                .clickable {
                                    levelIndex = idx
                                    selectedLetter = ENGLISH_LEVELS[idx].second.firstOrNull()
                                    if (isQuizMode) {
                                        quizTarget = ENGLISH_LEVELS[idx].second.random()
                                    }
                                }
                                .border(
                                    width = if (isCurrent) 2.dp else 1.dp,
                                    color = if (isCurrent) SkyBlue600 else SkyBlue200,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .testTag("english_level_$idx")
                        ) {
                            Text(
                                text = ENGLISH_LEVELS[idx].first,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrent) Color.White else Color(0xFF334155),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Case toggle button
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SkyBlue600),
                    modifier = Modifier.clickable { isLowercaseMode = !isLowercaseMode }
                ) {
                    Text(
                        text = if (isLowercaseMode) "a→A" else "A→a",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SkyBlue600,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Quiz Mode Toggle
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isQuizMode) Emerald500 else Color(0xFF9F1239),
                    modifier = Modifier
                        .clickable {
                            isQuizMode = !isQuizMode
                            if (isQuizMode) {
                                quizTarget = displayedLetters.random()
                                onSpeakEnglish("Quiz started! Find the letter!")
                            } else {
                                quizTarget = null
                                feedbackText = ""
                            }
                        }
                        .testTag("english_quiz_toggle")
                ) {
                    Text(
                        text = if (isQuizMode) "🎯 Quiz" else "📖 Learn",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            // Quiz Banner or Showcase Card
            if (isQuizMode && quizTarget != null) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = Color.White,
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .border(2.dp, Emerald500, RoundedCornerShape(22.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            val targetDisplay = if (isLowercaseMode) quizTarget?.letter?.lowercase() else quizTarget?.letter
                            Text(
                                text = "Find Letter: $targetDisplay 🔍",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF9F1239)
                            )
                            if (feedbackText.isNotEmpty()) {
                                Text(
                                    text = feedbackText,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald500
                                )
                            }
                        }
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFE4E6),
                            modifier = Modifier.clickable {
                                onSpeakEnglish("Find the letter ${quizTarget?.letter}, like ${quizTarget?.word}")
                            }
                        ) {
                            Text(
                                text = "🔊 Listen",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9F1239),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            } else {
                // Selected Letter Showcase Card
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = selectedLetter?.color ?: Color(0xFFFECDD3),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .border(3.dp, Color.White, RoundedCornerShape(22.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val bigChar = if (isLowercaseMode) selectedLetter?.letter?.lowercase() ?: "a" else selectedLetter?.letter ?: "A"
                        Text(
                            text = bigChar,
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF9F1239)
                        )
                        Text(
                            text = selectedLetter?.emoji ?: "🍎",
                            fontSize = 44.sp
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = selectedLetter?.word ?: "Apple",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9F1239)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.clickable {
                                    selectedLetter?.let { onSpeakEnglish("${it.letter}. ${it.word}") }
                                }
                            ) {
                                Text(
                                    text = "🔊 Speak",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9F1239),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(displayedLetters) { item ->
                    val charToDisplay = if (isLowercaseMode) item.letter.lowercase() else item.letter
                    Surface(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .border(
                                width = if (item.letter == selectedLetter?.letter) 3.dp else 2.dp,
                                color = if (item.letter == selectedLetter?.letter) Color(0xFF9F1239) else Color.White,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                selectedLetter = item
                                if (isQuizMode && quizTarget != null) {
                                    if (item.letter == quizTarget?.letter) {
                                        feedbackText = "Awesome! Correct answer 🎉"
                                        onSpeakEnglish("Correct! Letter ${item.letter} for ${item.word}")
                                        onWin(1)
                                        quizTarget = displayedLetters.random()
                                    } else {
                                        feedbackText = "This is ${item.letter}, try again!"
                                        onSpeakEnglish("This is ${item.letter}. Find ${quizTarget?.letter}")
                                    }
                                } else {
                                    onSpeakEnglish("${item.letter}. ${item.word}")
                                }
                            }
                            .testTag("english_letter_${item.letter}"),
                        shape = RoundedCornerShape(16.dp),
                        color = item.color,
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = charToDisplay,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = item.emoji,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
