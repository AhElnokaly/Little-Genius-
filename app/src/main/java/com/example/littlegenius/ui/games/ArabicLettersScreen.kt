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
import kotlin.random.Random

data class ArabicLetterItem(
    val letter: String,
    val word: String,
    val emoji: String,
    val color: Color
)

val ARABIC_LETTERS = listOf(
    ArabicLetterItem("أ", "أَسَد", "🦁", Color(0xFFFCA5A5)),
    ArabicLetterItem("ب", "بَطَّة", "🦆", Color(0xFFFDBA74)),
    ArabicLetterItem("ت", "تُفَّاح", "🍎", Color(0xFFFDE047)),
    ArabicLetterItem("ث", "ثَعْلَب", "🦊", Color(0xFF86EFAC)),
    ArabicLetterItem("ج", "جَمَل", "🐪", Color(0xFF67E8F9)),
    ArabicLetterItem("ح", "حِصَان", "🐴", Color(0xFF93C5FD)),
    ArabicLetterItem("خ", "خَرُوف", "🐑", Color(0xFFA5B4FC)),
    ArabicLetterItem("د", "دُبّ", "🐻", Color(0xFFC4B5FD)),
    ArabicLetterItem("ذ", "ذِئْب", "🐺", Color(0xFFF472B6)),
    ArabicLetterItem("ر", "رُمَّان", "🫐", Color(0xFFFB7185)),
    ArabicLetterItem("ز", "زَرَافَة", "🦒", Color(0xFFFCD34D)),
    ArabicLetterItem("س", "سَمَكَة", "🐟", Color(0xFF4ADE80)),
    ArabicLetterItem("ش", "شَمْس", "☀️", Color(0xFF38BDF8)),
    ArabicLetterItem("ص", "صَقْر", "🦅", Color(0xFF818CF8)),
    ArabicLetterItem("ض", "ضِفْدَع", "🐸", Color(0xFF34D399)),
    ArabicLetterItem("ط", "طَائِرَة", "✈️", Color(0xFFFBBF24)),
    ArabicLetterItem("ظ", "ظَبْي", "🦌", Color(0xFFF43F5E)),
    ArabicLetterItem("ع", "عُصْفُور", "🐦", Color(0xFF06B6D4)),
    ArabicLetterItem("غ", "غَزَال", "🦌", Color(0xFFA855F7)),
    ArabicLetterItem("ف", "فِيل", "🐘", Color(0xFF64748B)),
    ArabicLetterItem("ق", "قِطَّة", "🐱", Color(0xFFFB923C)),
    ArabicLetterItem("ك", "كَلْب", "🐶", Color(0xFF22C55E)),
    ArabicLetterItem("ل", "لَيْمُون", "🍋", Color(0xFFEAB308)),
    ArabicLetterItem("م", "مَوْز", "🍌", Color(0xFFEC4899)),
    ArabicLetterItem("ن", "نَحْلَة", "🐝", Color(0xFF14B8A6)),
    ArabicLetterItem("هـ", "هِلال", "🌙", Color(0xFF6366F1)),
    ArabicLetterItem("و", "وَرْدَة", "🌹", Color(0xFFE11D48)),
    ArabicLetterItem("ي", "يَد", "✋", Color(0xFF0284C7))
)

val ARABIC_LEVELS = listOf(
    "مرحلة ١ (أ - ث)" to ARABIC_LETTERS.subList(0, 4),
    "مرحلة ٢ (ج - خ)" to ARABIC_LETTERS.subList(4, 7),
    "مرحلة ٣ (د - ز)" to ARABIC_LETTERS.subList(7, 11),
    "مرحلة ٤ (س - ض)" to ARABIC_LETTERS.subList(11, 15),
    "مرحلة ٥ (ط - غ)" to ARABIC_LETTERS.subList(15, 19),
    "مرحلة ٦ (ف - ل)" to ARABIC_LETTERS.subList(19, 23),
    "مرحلة ٧ (م - ي)" to ARABIC_LETTERS.subList(23, 28),
    "مرحلة ٨ (كل الحروف)" to ARABIC_LETTERS
)

@Composable
fun ArabicLettersScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var levelIndex by remember { mutableIntStateOf(0) }
        val currentLevel = ARABIC_LEVELS[levelIndex]
        val displayedLetters = currentLevel.second

        var selectedItem by remember(levelIndex) { mutableStateOf<ArabicLetterItem?>(displayedLetters.firstOrNull()) }
        var isQuizMode by remember { mutableStateOf(false) }
        var quizTarget by remember(levelIndex, isQuizMode) {
            mutableStateOf(if (isQuizMode) displayedLetters.random() else null)
        }
        var quizScore by remember { mutableIntStateOf(0) }
        var feedbackMessage by remember { mutableStateOf("") }

        LaunchedEffect(selectedItem, isQuizMode) {
            if (isQuizMode && quizTarget != null) {
                onSpeak("أين حرف ${quizTarget?.letter}؟ ابحث عنه واضغط عليه!")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0FDFA))
                .testTag("arabic_letters_screen")
        ) {
            GameHeader(
                title = "حروف الهجاء 🔤",
                onBack = onBack,
                starsCount = starsCount
            )

            // Mode & Level Controls
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
                    items(ARABIC_LEVELS.indices.toList()) { idx ->
                        val isCurrent = idx == levelIndex
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isCurrent) SkyBlue600 else Color.White,
                            modifier = Modifier
                                .clickable {
                                    levelIndex = idx
                                    selectedItem = ARABIC_LEVELS[idx].second.firstOrNull()
                                    if (isQuizMode) {
                                        quizTarget = ARABIC_LEVELS[idx].second.random()
                                    }
                                }
                                .border(
                                    width = if (isCurrent) 2.dp else 1.dp,
                                    color = if (isCurrent) SkyBlue600 else SkyBlue200,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .testTag("arabic_lvl_$idx")
                        ) {
                            Text(
                                text = ARABIC_LEVELS[idx].first,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrent) Color.White else Color(0xFF334155),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Toggle Quiz Mode Button
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isQuizMode) Emerald500 else Color(0xFF0F766E),
                    modifier = Modifier
                        .clickable {
                            isQuizMode = !isQuizMode
                            if (isQuizMode) {
                                quizTarget = displayedLetters.random()
                                onSpeak("بدأ التحدي! ابحث عن الحرف المطلوب")
                            } else {
                                quizTarget = null
                                feedbackMessage = ""
                            }
                        }
                        .testTag("arabic_quiz_toggle")
                ) {
                    Text(
                        text = if (isQuizMode) "🎯 اختبار" else "📖 استكشاف",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Quiz Banner or Letter Card
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
                            Text(
                                text = "ابحث عن حرف: ${quizTarget?.letter} 🔍",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F766E)
                            )
                            if (feedbackMessage.isNotEmpty()) {
                                Text(
                                    text = feedbackMessage,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald500
                                )
                            }
                        }
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFCCFBF1),
                            modifier = Modifier
                                .clickable {
                                    onSpeak("أين حرف ${quizTarget?.letter}؟ مثل كلمة ${quizTarget?.word}")
                                }
                                .padding(4.dp)
                        ) {
                            Text(
                                text = "🔊 اسمع",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F766E),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            } else {
                // Selected Letter Showcase Card
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = selectedItem?.color ?: Color(0xFFCCFBF1),
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
                        Text(
                            text = selectedItem?.letter ?: "أ",
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0F766E)
                        )
                        Text(
                            text = selectedItem?.emoji ?: "🦁",
                            fontSize = 44.sp
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = selectedItem?.word ?: "أَسَد",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F766E)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.clickable {
                                    selectedItem?.let { onSpeak("${it.letter} .. ${it.word}") }
                                }
                            ) {
                                Text(
                                    text = "🔊 انطق",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F766E),
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
                    val isQuizCorrect = isQuizMode && item.letter == quizTarget?.letter
                    Surface(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .border(
                                width = if (item.letter == selectedItem?.letter) 3.dp else 2.dp,
                                color = if (item.letter == selectedItem?.letter) Color(0xFF0F766E) else Color.White,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                selectedItem = item
                                if (isQuizMode && quizTarget != null) {
                                    if (item.letter == quizTarget?.letter) {
                                        quizScore++
                                        feedbackMessage = "إجابة صحيحة! أحسنت 🎉"
                                        onSpeak("صحيح! هذا حرف ${item.letter} مثل ${item.word}")
                                        onWin(1)
                                        // Pick next target
                                        quizTarget = displayedLetters.random()
                                    } else {
                                        feedbackMessage = "هذا حرف ${item.letter}، حاول ثانية!"
                                        onSpeak("هذا حرف ${item.letter}، ابحث عن ${quizTarget?.letter}")
                                    }
                                } else {
                                    onSpeak("${item.letter} .. ${item.word}")
                                }
                            }
                            .testTag("arabic_letter_${item.letter}"),
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
                                text = item.letter,
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
