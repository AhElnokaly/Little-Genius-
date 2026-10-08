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
import kotlin.random.Random

data class TashkeelOption(
    val name: String,
    val mark: String,
    val soundSuffix: String,
    val exampleWord: String,
    val color: Color
)

val TASHKEEL_LIST = listOf(
    TashkeelOption("فَتْحَة", "َ", "فَتْحَة", "بَـ قَرَة 🐄", Color(0xFFFCA5A5)),
    TashkeelOption("ضَمَّة", "ُ", "ضَمَّة", "بُـ رْتُقَال 🍊", Color(0xFF93C5FD)),
    TashkeelOption("كَسْرَة", "ِ", "كَسْرَة", "بِـ نْت 👧", Color(0xFF86EFAC)),
    TashkeelOption("سُكُون", "ْ", "سُكُون", "حَبْـ ل 🪢", Color(0xFFFDE047)),
    TashkeelOption("تَنْوِين فَتْح", "ً", "تَنْوِين فَتْح", "بَيْتاً 🏠", Color(0xFFFDBA74)),
    TashkeelOption("تَنْوِين ضَمّ", "ٌ", "تَنْوِين ضَمّ", "كِتَابٌ 📖", Color(0xFFC4B5FD))
)

val PRACTICE_LETTERS = listOf("ب", "ت", "ج", "د", "ر", "س", "م", "ن", "ف", "ك")

@Composable
fun ArabicTashkeelScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var selectedLetter by remember { mutableStateOf("ب") }
        var selectedTashkeel by remember { mutableStateOf(TASHKEEL_LIST[0]) }
        var isChallengeMode by remember { mutableStateOf(false) }
        var challengeTarget by remember(isChallengeMode) {
            mutableStateOf(if (isChallengeMode) TASHKEEL_LIST.random() else null)
        }
        var feedbackMsg by remember { mutableStateOf("") }
        var practicedCount by remember { mutableIntStateOf(0) }

        LaunchedEffect(selectedTashkeel, isChallengeMode) {
            if (isChallengeMode && challengeTarget != null) {
                onSpeak("أين حركة ${challengeTarget?.name}؟ اضغط عليها!")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEEF2FF))
                .testTag("arabic_tashkeel_screen")
        ) {
            GameHeader(
                title = "تشكيل الحركات َُِ",
                onBack = onBack,
                starsCount = starsCount
            )

            // Mode Selector Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isChallengeMode) "🎯 وضع التحدي واختبار الحركات" else "📖 وضع التعلّم والاستكشاف",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3730A3)
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isChallengeMode) Emerald500 else Color(0xFF4338CA),
                    modifier = Modifier.clickable {
                        isChallengeMode = !isChallengeMode
                        if (isChallengeMode) {
                            challengeTarget = TASHKEEL_LIST.random()
                            onSpeak("تحدي الحركات! ابحث عن الحركة المطلوبة")
                        } else {
                            challengeTarget = null
                            feedbackMsg = ""
                        }
                    }
                ) {
                    Text(
                        text = if (isChallengeMode) "🎯 اختبار" else "📖 تعلّم",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Challenge Prompt Banner
            if (isChallengeMode && challengeTarget != null) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .border(2.dp, Emerald500, RoundedCornerShape(20.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "المطلوب: حركة ${challengeTarget?.name} 🎯",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E1B4B)
                        )
                        if (feedbackMsg.isNotEmpty()) {
                            Text(
                                text = feedbackMsg,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Emerald500
                            )
                        }
                    }
                }
            }

            // Main Display Card
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 5.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .border(4.dp, selectedTashkeel.color, RoundedCornerShape(28.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$selectedLetter${selectedTashkeel.mark}",
                        fontSize = 82.sp,
                        fontWeight = FontWeight.Black,
                        color = SkyBlue600
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = selectedTashkeel.color
                        ) {
                            Text(
                                text = selectedTashkeel.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.clickable {
                                onSpeak("$selectedLetter ${selectedTashkeel.soundSuffix}")
                            }
                        ) {
                            Text(
                                text = "🔊 انطق",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "مثال: ${selectedTashkeel.exampleWord}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Tashkeel Selector Buttons (Grid / Flow)
            Text(
                text = "الحركات والتنوين:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3730A3),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(TASHKEEL_LIST) { t ->
                    Surface(
                        modifier = Modifier
                            .height(60.dp)
                            .border(
                                width = if (selectedTashkeel == t) 3.dp else 1.dp,
                                color = if (selectedTashkeel == t) SkyBlue600 else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                selectedTashkeel = t
                                if (isChallengeMode && challengeTarget != null) {
                                    if (t.name == challengeTarget?.name) {
                                        feedbackMsg = "صحيح! أحسنت 🎉"
                                        onSpeak("صحيح! هذه ${t.name}")
                                        onWin(1)
                                        challengeTarget = TASHKEEL_LIST.random()
                                    } else {
                                        feedbackMsg = "هذه ${t.name}، حاول ثانية!"
                                        onSpeak("هذه حركة ${t.name}، ابحث عن ${challengeTarget?.name}")
                                    }
                                } else {
                                    onSpeak("$selectedLetter ${t.soundSuffix}")
                                    practicedCount++
                                    if (practicedCount % 4 == 0) {
                                        onWin(1)
                                    }
                                }
                            }
                            .testTag("tashkeel_${t.mark}"),
                        shape = RoundedCornerShape(16.dp),
                        color = t.color,
                        shadowElevation = 2.dp
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        ) {
                            Text(
                                text = "${t.mark} ${t.name}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Letter Selector Row
            Text(
                text = "غيّر الحرف المجرّب:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3730A3),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(PRACTICE_LETTERS) { l ->
                    Surface(
                        modifier = Modifier
                            .size(54.dp)
                            .border(
                                width = if (selectedLetter == l) 3.dp else 1.dp,
                                color = if (selectedLetter == l) SkyBlue600 else SkyBlue200,
                                shape = CircleShape
                            )
                            .clickable {
                                selectedLetter = l
                                onSpeak("$l ${selectedTashkeel.soundSuffix}")
                            }
                            .testTag("letter_pick_$l"),
                        shape = CircleShape,
                        color = if (selectedLetter == l) Color(0xFFBAE6FD) else Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = l,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = SkyBlue600
                            )
                        }
                    }
                }
            }
        }
    }
}
