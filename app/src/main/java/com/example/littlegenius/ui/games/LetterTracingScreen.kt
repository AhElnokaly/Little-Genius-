package com.example.littlegenius.ui.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sqrt

data class TracingLetter(
    val letter: String,
    val word: String,
    val emoji: String,
    val lang: String,
    val guidePoints: List<Offset> // normalized 0..1 coordinates representing stroke path
)

val ENGLISH_TRACING_LETTERS = listOf(
    TracingLetter(
        "A", "Apple", "🍎", "en",
        listOf(
            Offset(0.25f, 0.85f), Offset(0.35f, 0.50f), Offset(0.50f, 0.15f),
            Offset(0.65f, 0.50f), Offset(0.75f, 0.85f),
            Offset(0.35f, 0.60f), Offset(0.65f, 0.60f)
        )
    ),
    TracingLetter(
        "B", "Ball", "⚽", "en",
        listOf(
            Offset(0.30f, 0.15f), Offset(0.30f, 0.50f), Offset(0.30f, 0.85f),
            Offset(0.55f, 0.15f), Offset(0.65f, 0.32f), Offset(0.45f, 0.50f),
            Offset(0.68f, 0.68f), Offset(0.50f, 0.85f), Offset(0.30f, 0.85f)
        )
    ),
    TracingLetter(
        "C", "Cat", "🐱", "en",
        listOf(
            Offset(0.70f, 0.25f), Offset(0.50f, 0.15f), Offset(0.30f, 0.35f),
            Offset(0.28f, 0.65f), Offset(0.50f, 0.85f), Offset(0.70f, 0.75f)
        )
    ),
    TracingLetter(
        "S", "Sun", "☀️", "en",
        listOf(
            Offset(0.70f, 0.25f), Offset(0.50f, 0.15f), Offset(0.35f, 0.32f),
            Offset(0.50f, 0.50f), Offset(0.68f, 0.68f), Offset(0.50f, 0.85f),
            Offset(0.30f, 0.75f)
        )
    ),
    TracingLetter(
        "T", "Tiger", "🐯", "en",
        listOf(
            Offset(0.20f, 0.15f), Offset(0.50f, 0.15f), Offset(0.80f, 0.15f),
            Offset(0.50f, 0.15f), Offset(0.50f, 0.50f), Offset(0.50f, 0.85f)
        )
    )
)

val ARABIC_TRACING_LETTERS = listOf(
    TracingLetter(
        "أ", "أَسَد", "🦁", "ar",
        listOf(
            Offset(0.50f, 0.15f), Offset(0.50f, 0.40f), Offset(0.50f, 0.70f), Offset(0.50f, 0.85f),
            Offset(0.48f, 0.18f), Offset(0.54f, 0.15f), Offset(0.50f, 0.22f)
        )
    ),
    TracingLetter(
        "ب", "بَطَّة", "🦆", "ar",
        listOf(
            Offset(0.25f, 0.45f), Offset(0.25f, 0.65f), Offset(0.50f, 0.68f),
            Offset(0.75f, 0.65f), Offset(0.75f, 0.45f),
            Offset(0.50f, 0.82f) // Dot
        )
    ),
    TracingLetter(
        "ت", "تُفَّاح", "🍎", "ar",
        listOf(
            Offset(0.25f, 0.45f), Offset(0.25f, 0.65f), Offset(0.50f, 0.68f),
            Offset(0.75f, 0.65f), Offset(0.75f, 0.45f),
            Offset(0.45f, 0.42f), Offset(0.55f, 0.42f) // Two dots
        )
    ),
    TracingLetter(
        "د", "دُبّ", "🐻", "ar",
        listOf(
            Offset(0.55f, 0.35f), Offset(0.40f, 0.50f), Offset(0.45f, 0.65f), Offset(0.65f, 0.65f)
        )
    ),
    TracingLetter(
        "ر", "رُمَّان", "🫐", "ar",
        listOf(
            Offset(0.55f, 0.38f), Offset(0.52f, 0.55f), Offset(0.42f, 0.72f), Offset(0.32f, 0.80f)
        )
    )
)

data class DrawnStroke(
    val points: List<Offset>,
    val color: Color
)

@Composable
fun LetterTracingScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onPopSound: () -> Unit,
    onWinSound: () -> Unit,
    onSpeak: (String, String) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val coroutineScope = rememberCoroutineScope()
        var isArabicMode by remember { mutableStateOf(true) }
        val letterList = if (isArabicMode) ARABIC_TRACING_LETTERS else ENGLISH_TRACING_LETTERS
        var letterIndex by remember(isArabicMode) { mutableIntStateOf(0) }
        val currentLetter = letterList[letterIndex % letterList.size]

        // Tracing state
        val drawnStrokes = remember(currentLetter) { mutableStateListOf<DrawnStroke>() }
        var currentStrokePoints by remember(currentLetter) { mutableStateOf(listOf<Offset>()) }
        var isCompleted by remember(currentLetter) { mutableStateOf(false) }

        // Guide star pulsing animation
        val infiniteTransition = rememberInfiniteTransition(label = "star_guide")
        val starPulse by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.35f,
            animationSpec = infiniteRepeatable(
                animation = tween(500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "starPulse"
        )

        // Pronounce letter when loaded
        LaunchedEffect(currentLetter) {
            isCompleted = false
            drawnStrokes.clear()
            currentStrokePoints = emptyList()
            delay(200)
            onSpeak(currentLetter.word, currentLetter.lang)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A)) // Night chalkboard theme
                .testTag("letter_tracing_screen")
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
                        .background(Color(0xFF1E293B), CircleShape)
                        .testTag("tracing_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = Color(0xFFFACC15)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "سبورة تتبع الحروف المضيئة ✨",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFDE047)
                    )
                    Text(
                        text = if (isArabicMode) "تتبع مسار كتابة الحرف العربي" else "Trace & Write English Letters",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF854D0E),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFACC15))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "نجوم",
                            tint = Color(0xFFFDE047),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "$starsCount",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }

            // Language Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .background(Color(0xFF1E293B), RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            isArabicMode = true
                            letterIndex = 0
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isArabicMode) Color(0xFF10B981) else Color.Transparent
                ) {
                    Text(
                        text = "🇸🇦 الحروف العربية",
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isArabicMode) Color.White else Color(0xFF94A3B8)
                    )
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            isArabicMode = false
                            letterIndex = 0
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = if (!isArabicMode) Color(0xFF6366F1) else Color.Transparent
                ) {
                    Text(
                        text = "🇬🇧 English Letters",
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (!isArabicMode) Color.White else Color(0xFF94A3B8)
                    )
                }
            }

            // Letter & Hint Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = currentLetter.emoji,
                        fontSize = 36.sp
                    )
                    Column {
                        Text(
                            text = currentLetter.word,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "مرر إصبعك فوق النقاط المضيئة لترسم الحرف!",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                IconButton(
                    onClick = { onSpeak(currentLetter.word, currentLetter.lang) },
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color(0xFF334155), CircleShape)
                        .border(1.5.dp, Color(0xFF64748B), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "استمع للكلمة",
                        tint = Color(0xFFFDE047)
                    )
                }
            }

            // Tracing Canvas Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .background(Color(0xFF020617), RoundedCornerShape(28.dp))
                    .border(3.dp, if (isCompleted) Color(0xFF10B981) else Color(0xFF1E293B), RoundedCornerShape(28.dp))
                    .clip(RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(currentLetter) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentStrokePoints = listOf(offset)
                                    onPopSound()
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    currentStrokePoints = currentStrokePoints + change.position
                                },
                                onDragEnd = {
                                    if (currentStrokePoints.isNotEmpty()) {
                                        drawnStrokes.add(
                                            DrawnStroke(
                                                points = currentStrokePoints,
                                                color = Color(0xFF38BDF8)
                                            )
                                        )
                                        currentStrokePoints = emptyList()

                                        // Check if child traced enough points
                                        val totalDrawn = drawnStrokes.sumOf { it.points.size }
                                        if (totalDrawn > 35 && !isCompleted) {
                                            isCompleted = true
                                            onWinSound()
                                            onWin(1)
                                            onSpeak("أحسنت! ${currentLetter.word}", currentLetter.lang)
                                        }
                                    }
                                }
                            )
                        }
                        .testTag("tracing_canvas")
                ) {
                    val w = size.width
                    val h = size.height

                    // 1. Draw dashed template guide path
                    val guidePts = currentLetter.guidePoints.map { Offset(it.x * w, it.y * h) }
                    for (i in 0 until guidePts.size - 1) {
                        drawLine(
                            color = Color(0xFF334155),
                            start = guidePts[i],
                            end = guidePts[i + 1],
                            strokeWidth = 36f,
                            cap = StrokeCap.Round
                        )
                    }

                    // Draw guide dots
                    guidePts.forEachIndexed { idx, pt ->
                        drawCircle(
                            color = if (idx == 0) Color(0xFFFACC15) else Color(0xFF64748B),
                            radius = if (idx == 0) 14f * starPulse else 10f,
                            center = pt
                        )
                    }

                    // 2. Draw child's saved strokes with neon glowing trail
                    drawnStrokes.forEach { stroke ->
                        for (i in 0 until stroke.points.size - 1) {
                            drawLine(
                                color = Color(0xFF38BDF8),
                                start = stroke.points[i],
                                end = stroke.points[i + 1],
                                strokeWidth = 28f,
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = Color(0xFFBAE6FD),
                                start = stroke.points[i],
                                end = stroke.points[i + 1],
                                strokeWidth = 14f,
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    // 3. Draw active drawing stroke
                    for (i in 0 until currentStrokePoints.size - 1) {
                        drawLine(
                            color = Color(0xFFF472B6),
                            start = currentStrokePoints[i],
                            end = currentStrokePoints[i + 1],
                            strokeWidth = 30f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = Color(0xFFFDE047),
                            start = currentStrokePoints[i],
                            end = currentStrokePoints[i + 1],
                            strokeWidth = 16f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                // Completion celebration overlay
                if (isCompleted) {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = Color(0xFF065F46).copy(alpha = 0.95f),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF34D399)),
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🎉 ممتاز يا بطل! رسمت الحرف بنجاح! 🌟", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Text(currentLetter.word, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFDE047))
                        }
                    }
                }
            }

            // Controls Bar (Clear, Repeat, Next Letter)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Clear Canvas Button
                Button(
                    onClick = {
                        drawnStrokes.clear()
                        currentStrokePoints = emptyList()
                        isCompleted = false
                        onPopSound()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("tracing_clear_btn"),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "مسح", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("مسح", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                // Next Letter Button
                Button(
                    onClick = {
                        drawnStrokes.clear()
                        currentStrokePoints = emptyList()
                        isCompleted = false
                        letterIndex++
                        onPopSound()
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(52.dp)
                        .testTag("tracing_next_btn"),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("الحرف التالي", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "تالي", modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}
