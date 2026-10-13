package com.example.littlegenius.ui.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class SoundHuntItem(
    val word: String,
    val translation: String,
    val emoji: String,
    val isCorrect: Boolean
)

data class SoundHuntQuestion(
    val soundPromptText: String,
    val spokenPrompt: String,
    val lang: String,
    val options: List<SoundHuntItem>
)

val ENGLISH_SOUND_HUNT_QUESTIONS = listOf(
    SoundHuntQuestion(
        "Find the word starting with /s/ sound! 🐍",
        "Which word starts with sssss?",
        "en",
        listOf(
            SoundHuntItem("Snake", "ثعبان", "🐍", true),
            SoundHuntItem("Apple", "تفاحة", "🍎", false),
            SoundHuntItem("Tiger", "نمر", "🐯", false)
        )
    ),
    SoundHuntQuestion(
        "Find the word starting with /æ/ sound! 🐜",
        "Which word starts with a-a-ants?",
        "en",
        listOf(
            SoundHuntItem("Pencil", "قلم", "✏️", false),
            SoundHuntItem("Apple", "تفاحة", "🍎", true),
            SoundHuntItem("Cat", "قطة", "🐱", false)
        )
    ),
    SoundHuntQuestion(
        "Find the word starting with /t/ sound! 🎾",
        "Which word starts with t-t-tennis?",
        "en",
        listOf(
            SoundHuntItem("Train", "قطار", "🚂", true),
            SoundHuntItem("Sun", "شمس", "☀️", false),
            SoundHuntItem("Dog", "كلب", "🐶", false)
        )
    ),
    SoundHuntQuestion(
        "Find the word starting with /p/ sound! 🕯️",
        "Which word starts with p-p-puff?",
        "en",
        listOf(
            SoundHuntItem("Fish", "سمكة", "🐟", false),
            SoundHuntItem("Panda", "باندا", "🐼", true),
            SoundHuntItem("Nest", "عش", "🪺", false)
        )
    ),
    SoundHuntQuestion(
        "Find the word starting with /k/ sound! 🐱",
        "Which word starts with k-k-castanets?",
        "en",
        listOf(
            SoundHuntItem("Cat", "قطة", "🐱", true),
            SoundHuntItem("Elephant", "فيل", "🐘", false),
            SoundHuntItem("Rabbit", "أرنب", "🐰", false)
        )
    ),
    SoundHuntQuestion(
        "Find the word starting with /m/ sound! 😋",
        "Which word starts with mmmmm yummy?",
        "en",
        listOf(
            SoundHuntItem("Duck", "بطة", "🦆", false),
            SoundHuntItem("Moon", "قمر", "🌙", true),
            SoundHuntItem("Horse", "حصان", "🐴", false)
        )
    ),
    SoundHuntQuestion(
        "Find the word starting with /d/ sound! 🥁",
        "Which word starts with d-d-drum?",
        "en",
        listOf(
            SoundHuntItem("Dog", "كلب", "🐶", true),
            SoundHuntItem("Banana", "موز", "🍌", false),
            SoundHuntItem("Lion", "أسد", "🦁", false)
        )
    ),
    SoundHuntQuestion(
        "Find the word starting with /b/ sound! ⚾",
        "Which word starts with b-b-ball?",
        "en",
        listOf(
            SoundHuntItem("Bear", "دب", "🐻", true),
            SoundHuntItem("Juice", "عصير", "🧃", false),
            SoundHuntItem("Zebra", "حمار وحشي", "🦓", false)
        )
    )
)

val ARABIC_SOUND_HUNT_QUESTIONS = listOf(
    SoundHuntQuestion(
        "ابحث عن الكلمة التي تبدأ بصوت (أَ)! 🦁",
        "أين الكلمة التي تبدأ بصوت أَ؟",
        "ar",
        listOf(
            SoundHuntItem("أَسَد", "Lion", "🦁", true),
            SoundHuntItem("بَطَّة", "Duck", "🦆", false),
            SoundHuntItem("فِيل", "Elephant", "🐘", false)
        )
    ),
    SoundHuntQuestion(
        "ابحث عن الكلمة التي تبدأ بصوت (بَ)! 🦆",
        "أين الكلمة التي تبدأ بصوت بَ؟",
        "ar",
        listOf(
            SoundHuntItem("تُفَّاح", "Apple", "🍎", false),
            SoundHuntItem("بُرْتُقَال", "Orange", "🍊", true),
            SoundHuntItem("جَمَل", "Camel", "🐪", false)
        )
    ),
    SoundHuntQuestion(
        "ابحث عن الكلمة التي تبدأ بصوت (تَ)! 🍎",
        "أين الكلمة التي تبدأ بصوت تَ؟",
        "ar",
        listOf(
            SoundHuntItem("تُفَّاح", "Apple", "🍎", true),
            SoundHuntItem("خَرُوف", "Sheep", "🐑", false),
            SoundHuntItem("دُبّ", "Bear", "🐻", false)
        )
    ),
    SoundHuntQuestion(
        "ابحث عن الكلمة التي تبدأ بصوت (جَ)! 🐪",
        "أين الكلمة التي تبدأ بصوت جَ؟",
        "ar",
        listOf(
            SoundHuntItem("حِصَان", "Horse", "🐴", false),
            SoundHuntItem("جَزَر", "Carrot", "🥕", true),
            SoundHuntItem("زَرَافَة", "Giraffe", "🦒", false)
        )
    ),
    SoundHuntQuestion(
        "ابحث عن الكلمة التي تبدأ بصوت (حَ)! 🐴",
        "أين الكلمة التي تبدأ بصوت حَ؟",
        "ar",
        listOf(
            SoundHuntItem("حِصَان", "Horse", "🐴", true),
            SoundHuntItem("سَمَكَة", "Fish", "🐟", false),
            SoundHuntItem("شَمْس", "Sun", "☀️", false)
        )
    ),
    SoundHuntQuestion(
        "ابحث عن الكلمة التي تبدأ بصوت (سَ)! 🐟",
        "أين الكلمة التي تبدأ بصوت سَ؟",
        "ar",
        listOf(
            SoundHuntItem("صَقْر", "Falcon", "🦅", false),
            SoundHuntItem("سَمَكَة", "Fish", "🐟", true),
            SoundHuntItem("طَائِرَة", "Airplane", "✈️", false)
        )
    ),
    SoundHuntQuestion(
        "ابحث عن الكلمة التي تبدأ بصوت (قَ)! 🐱",
        "أين الكلمة التي تبدأ بصوت قَ؟",
        "ar",
        listOf(
            SoundHuntItem("قِطَّة", "Cat", "🐱", true),
            SoundHuntItem("كَلْب", "Dog", "🐶", false),
            SoundHuntItem("لَيْمُون", "Lemon", "🍋", false)
        )
    ),
    SoundHuntQuestion(
        "ابحث عن الكلمة التي تبدأ بصوت (مَ)! 🍌",
        "أين الكلمة التي تبدأ بصوت مَ؟",
        "ar",
        listOf(
            SoundHuntItem("نَحْلَة", "Bee", "🐝", false),
            SoundHuntItem("مَوْز", "Banana", "🍌", true),
            SoundHuntItem("وَرْدَة", "Rose", "🌹", false)
        )
    )
)

@Composable
fun PhonicsSoundHuntScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onPopSound: () -> Unit,
    onWinSound: () -> Unit,
    onSpeak: (String, String) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val coroutineScope = rememberCoroutineScope()
        var isArabicMode by remember { mutableStateOf(false) }
        val questionList = if (isArabicMode) ARABIC_SOUND_HUNT_QUESTIONS else ENGLISH_SOUND_HUNT_QUESTIONS
        var questionIndex by remember(isArabicMode) { mutableIntStateOf(0) }
        val currentQuestion = questionList[questionIndex % questionList.size]

        var selectedAnswer by remember(currentQuestion) { mutableStateOf<SoundHuntItem?>(null) }
        var isAnswerCorrect by remember(currentQuestion) { mutableStateOf(false) }
        var showShakeAnimation by remember(currentQuestion) { mutableStateOf(false) }

        // Pulsing speaker animation
        val infiniteTransition = rememberInfiniteTransition(label = "speaker_pulse")
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.18f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        )

        // Shake offset
        val shakeOffset = remember { Animatable(0f) }

        // Play prompt on question load
        LaunchedEffect(currentQuestion) {
            selectedAnswer = null
            isAnswerCorrect = false
            delay(200)
            onSpeak(currentQuestion.spokenPrompt, currentQuestion.lang)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFEF9C3), Color(0xFFF0FDF4), Color(0xFFE0F2FE))
                    )
                )
                .testTag("sound_hunt_screen")
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color.White, CircleShape)
                        .testTag("sound_hunt_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = Color(0xFFB45309)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "صائد الأصوات 🎯",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFB45309)
                    )
                    Text(
                        text = if (isArabicMode) "تحدي مخارج وأصوات الحروف" else "Phonics Listening Challenge",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD97706)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFFEF08A),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFEAB308))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "نجوم",
                            tint = Color(0xFFCA8A04),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "$starsCount",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF854D0E)
                        )
                    }
                }
            }

            // Language Switcher Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0xFFFDE68A), RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            isArabicMode = false
                            questionIndex = 0
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = if (!isArabicMode) Color(0xFFF59E0B) else Color.Transparent
                ) {
                    Text(
                        text = "🇬🇧 Jolly Phonics Hunt",
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (!isArabicMode) Color.White else Color(0xFF92400E)
                    )
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            isArabicMode = true
                            questionIndex = 0
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isArabicMode) Color(0xFF10B981) else Color.Transparent
                ) {
                    Text(
                        text = "🇸🇦 صائد الحروف العربية",
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isArabicMode) Color.White else Color(0xFF065F46)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Question Card with Pulsing Speaker
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(26.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(2.5.dp, Color(0xFFFDE047)),
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Pulsing Speaker Button
                    IconButton(
                        onClick = {
                            onPopSound()
                            onSpeak(currentQuestion.spokenPrompt, currentQuestion.lang)
                        },
                        modifier = Modifier
                            .size(72.dp)
                            .graphicsLayer {
                                scaleX = pulseScale
                                scaleY = pulseScale
                            }
                            .background(Color(0xFFFEF9C3), CircleShape)
                            .border(3.dp, Color(0xFFEAB308), CircleShape)
                            .testTag("sound_hunt_speaker_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "استمع للصوت مجدداً",
                            tint = Color(0xFFCA8A04),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentQuestion.soundPromptText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "المس السماعة لسماع الصوت مجدداً 🔊",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 3 Option Cards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .graphicsLayer { translationX = shakeOffset.value },
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                currentQuestion.options.forEach { item ->
                    val isSelected = selectedAnswer == item
                    val isRightAnswer = isSelected && item.isCorrect
                    val isWrongAnswer = isSelected && !item.isCorrect

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isAnswerCorrect) {
                                selectedAnswer = item
                                if (item.isCorrect) {
                                    isAnswerCorrect = true
                                    onWinSound()
                                    onWin(1)
                                    onSpeak(item.word, currentQuestion.lang)
                                    coroutineScope.launch {
                                        delay(1500)
                                        questionIndex++
                                    }
                                } else {
                                    onPopSound()
                                    onSpeak("حاول مرة أخرى", "ar")
                                    coroutineScope.launch {
                                        shakeOffset.animateTo(18f, tween(50))
                                        shakeOffset.animateTo(-18f, tween(50))
                                        shakeOffset.animateTo(12f, tween(50))
                                        shakeOffset.animateTo(0f, tween(50))
                                    }
                                }
                            }
                            .testTag("sound_hunt_option_${item.word}"),
                        shape = RoundedCornerShape(20.dp),
                        color = when {
                            isRightAnswer -> Color(0xFFD1FAE5)
                            isWrongAnswer -> Color(0xFFFEE2E2)
                            else -> Color.White
                        },
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 3.5.dp else 2.dp,
                            color = when {
                                isRightAnswer -> Color(0xFF10B981)
                                isWrongAnswer -> Color(0xFFEF4444)
                                else -> Color(0xFFCBD5E1)
                            }
                        ),
                        shadowElevation = if (isSelected) 6.dp else 2.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(
                                    text = item.emoji,
                                    fontSize = 42.sp
                                )

                                Column {
                                    Text(
                                        text = item.word,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = item.translation,
                                        fontSize = 13.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            if (isRightAnswer) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(0xFF10B981), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "صحيح",
                                        tint = Color.White
                                    )
                                }
                            } else if (isWrongAnswer) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(0xFFEF4444), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "خطأ",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}
