package com.example.littlegenius.ui.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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

data class BlendingWord(
    val letters: List<String>,
    val fullWord: String,
    val translation: String,
    val emoji: String,
    val lang: String // "en" or "ar"
)

val ENGLISH_BLENDING_WORDS = listOf(
    BlendingWord(listOf("S", "A", "T"), "SAT", "جلس", "🪑", "en"),
    BlendingWord(listOf("P", "A", "T"), "PAT", "ربّت بحنان", "✋", "en"),
    BlendingWord(listOf("T", "I", "P"), "TIP", "قمة / إكرامية", "🏔️", "en"),
    BlendingWord(listOf("P", "I", "N"), "PIN", "دبوس", "📌", "en"),
    BlendingWord(listOf("C", "A", "T"), "CAT", "قطة", "🐱", "en"),
    BlendingWord(listOf("D", "O", "G"), "DOG", "كلب", "🐶", "en"),
    BlendingWord(listOf("S", "U", "N"), "SUN", "شمس", "☀️", "en"),
    BlendingWord(listOf("B", "U", "S"), "BUS", "حافلة", "🚌", "en"),
    BlendingWord(listOf("H", "E", "N"), "HEN", "دجاجة", "🐔", "en"),
    BlendingWord(listOf("F", "O", "X"), "FOX", "ثعلب", "🦊", "en")
)

val ARABIC_BLENDING_WORDS = listOf(
    BlendingWord(listOf("دَ", "رَ", "سَ"), "دَرَسَ", "Studied", "📚", "ar"),
    BlendingWord(listOf("كspace_place", "تَ", "بَ").map { it.replace("space_place", "َ") }, "كَتَبَ", "Wrote", "✍️", "ar"),
    BlendingWord(listOf("زَ", "رَ", "عَ"), "زَرَعَ", "Planted", "🌱", "ar"),
    BlendingWord(listOf("قَ", "رَ", "أَ"), "قَرَأَ", "Read", "📖", "ar"),
    BlendingWord(listOf("حَ", "مَ", "لَ"), "حَمَلَ", "Carried", "📦", "ar"),
    BlendingWord(listOf("رَ", "كspace_place", "ضَ").map { it.replace("space_place", "َ") }, "رَكَضَ", "Ran", "🏃", "ar"),
    BlendingWord(listOf("سَ", "بَ", "حَ"), "سَبَحَ", "Swam", "🏊", "ar"),
    BlendingWord(listOf("لَ", "عِ", "بَ"), "لَعِبَ", "Played", "⚽", "ar"),
    BlendingWord(listOf("صَ", "عِ", "دَ"), "صَعِدَ", "Climbed", "🧗", "ar"),
    BlendingWord(listOf("شَ", "رِ", "بَ"), "شَرِبَ", "Drank", "🥛", "ar")
)

@Composable
fun PhonicsBlendingTrainScreen(
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
        val wordsList = if (isArabicMode) ARABIC_BLENDING_WORDS else ENGLISH_BLENDING_WORDS
        var currentWordIndex by remember(isArabicMode) { mutableIntStateOf(0) }
        val currentWord = wordsList[currentWordIndex % wordsList.size]

        // State for highlighted carriage during blending
        var activeCarriageIndex by remember(currentWord) { mutableStateOf<Int?>(null) }
        var isBlendingInProgress by remember(currentWord) { mutableStateOf(false) }
        var showCelebration by remember(currentWord) { mutableStateOf(false) }

        // Train wheel rotation animation
        val infiniteTransition = rememberInfiniteTransition(label = "train_wheels")
        val wheelAngle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(if (isBlendingInProgress) 400 else 2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "wheelAngle"
        )

        // Train train sway
        val trainBounce by infiniteTransition.animateFloat(
            initialValue = -3f,
            targetValue = 3f,
            animationSpec = infiniteRepeatable(
                animation = tween(350, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "trainBounce"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFE0F2FE), Color(0xFFFEF3C7), Color(0xFFDCFCE7))
                    )
                )
                .testTag("blending_train_screen")
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
                        .testTag("train_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = Color(0xFF0369A1)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "قطار دمج الأصوات 🚂",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0369A1)
                    )
                    Text(
                        text = if (isArabicMode) "دمج حروف الكلمة العربية" else "Jolly Phonics Word Blending",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0284C7)
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
                    .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0xFFBAE6FD), RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            isArabicMode = false
                            currentWordIndex = 0
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = if (!isArabicMode) Color(0xFF0284C7) else Color.Transparent
                ) {
                    Text(
                        text = "🇬🇧 Jolly Phonics (3 Letters)",
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (!isArabicMode) Color.White else Color(0xFF0369A1)
                    )
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            isArabicMode = true
                            currentWordIndex = 0
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isArabicMode) Color(0xFF16A34A) else Color.Transparent
                ) {
                    Text(
                        text = "🇸🇦 كلمات عربية بالحركات",
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isArabicMode) Color.White else Color(0xFF15803D)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Instructions banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.9f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("💡", fontSize = 22.sp)
                    Text(
                        text = "المس كل عربة لسماع صوت الحرف، ثم اضغط على زر انطلاق القطار لدمج الحروف وقراءة الكلمة كاملة!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155),
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Train Scene
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { translationY = trainBounce }
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Locomotive Engine
                    LocomotiveEngine(wheelAngle = wheelAngle)

                    // Carriages with letters
                    currentWord.letters.forEachIndexed { index, letter ->
                        TrainCarriage(
                            letter = letter,
                            isActive = activeCarriageIndex == index,
                            wheelAngle = wheelAngle,
                            onClick = {
                                activeCarriageIndex = index
                                onPopSound()
                                onSpeak(letter, currentWord.lang)
                            }
                        )
                    }
                }
            }

            // Train Track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(Color(0xFF78350F))
            ) {
                // Track ties
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(14) {
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .fillMaxHeight()
                                .background(Color(0xFF451A03))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Blended Result Card / Celebration Box
            AnimatedVisibility(
                visible = showCelebration,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(3.dp, Color(0xFF10B981)),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = currentWord.emoji,
                            fontSize = 48.sp
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = currentWord.fullWord,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF047857)
                            )
                            Text(
                                text = currentWord.translation,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                        }

                        IconButton(
                            onClick = { onSpeak(currentWord.fullWord, currentWord.lang) },
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color(0xFFD1FAE5), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "استمع للكلمة",
                                tint = Color(0xFF059669)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons (Launch Train & Next Word)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Blend / Go Train Button
                Button(
                    onClick = {
                        if (!isBlendingInProgress) {
                            isBlendingInProgress = true
                            showCelebration = false
                            coroutineScope.launch {
                                // Sequentially highlight and speak each letter
                                for (i in currentWord.letters.indices) {
                                    activeCarriageIndex = i
                                    onPopSound()
                                    onSpeak(currentWord.letters[i], currentWord.lang)
                                    delay(750)
                                }
                                activeCarriageIndex = null
                                delay(300)
                                // Blend the full word!
                                onWinSound()
                                showCelebration = true
                                onSpeak(currentWord.fullWord, currentWord.lang)
                                onWin(1)
                                isBlendingInProgress = false
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(56.dp)
                        .testTag("blend_train_launch_btn"),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "انطلق",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBlendingInProgress) "القطار يدمج الأصوات... 🚂" else "انطلق وادمج الكلمة! 🚂",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // Next Word Button
                Button(
                    onClick = {
                        showCelebration = false
                        activeCarriageIndex = null
                        currentWordIndex++
                        onPopSound()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .testTag("next_blending_word_btn"),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "كلمة تالية",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "كلمة تالية",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
fun LocomotiveEngine(wheelAngle: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Smoke chimney
        Box(
            modifier = Modifier
                .width(18.dp)
                .height(14.dp)
                .background(Color(0xFF334155), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
        )

        // Engine Body
        Surface(
            modifier = Modifier
                .size(width = 82.dp, height = 80.dp),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 6.dp, bottomStart = 8.dp, bottomEnd = 8.dp),
            color = Color(0xFFDC2626),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF991B1B)),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Window
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(24.dp)
                        .background(Color(0xFFBAE6FD), RoundedCornerShape(6.dp))
                        .border(1.5.dp, Color.White, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👨‍✈️", fontSize = 16.sp)
                }

                Text(
                    text = "🚂",
                    fontSize = 24.sp
                )
            }
        }

        // Engine Wheels
        Row(
            modifier = Modifier.width(82.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            TrainWheel(size = 24.dp, angle = wheelAngle)
            TrainWheel(size = 24.dp, angle = wheelAngle)
        }
    }
}

@Composable
fun TrainCarriage(
    letter: String,
    isActive: Boolean,
    wheelAngle: Float,
    onClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isActive) {
        if (isActive) {
            scale.animateTo(1.15f, tween(120))
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier
                .size(width = 74.dp, height = 74.dp)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onClick()
                },
            shape = RoundedCornerShape(16.dp),
            color = if (isActive) Color(0xFFFEF08A) else Color.White,
            border = androidx.compose.foundation.BorderStroke(
                if (isActive) 3.5.dp else 2.dp,
                if (isActive) Color(0xFFEAB308) else Color(0xFF0284C7)
            ),
            shadowElevation = if (isActive) 8.dp else 3.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isActive) Color(0xFF854D0E) else Color(0xFF0369A1)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Carriage Wheels
        Row(
            modifier = Modifier.width(74.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            TrainWheel(size = 20.dp, angle = wheelAngle)
            TrainWheel(size = 20.dp, angle = wheelAngle)
        }
    }
}

@Composable
fun TrainWheel(size: androidx.compose.ui.unit.Dp, angle: Float) {
    Box(
        modifier = Modifier
            .size(size)
            .rotate(angle)
            .background(Color(0xFF334155), CircleShape)
            .border(2.dp, Color(0xFF94A3B8), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(Color(0xFFF1F5F9), CircleShape)
        )
    }
}
