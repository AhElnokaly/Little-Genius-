package com.example.littlegenius.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import kotlin.math.sin
import kotlin.random.Random

data class BalloonData(
    val id: Int,
    val text: String,
    val speakWord: String,
    val xRatio: Float,
    val color: Color,
    val initialY: Float = 1.15f,
    val speed: Float = 0.003f
)

data class PopSpark(
    val id: Int,
    val xRatio: Float,
    val yRatio: Float,
    val color: Color,
    var age: Int = 0
)

data class BalloonLevel(
    val levelNumber: Int,
    val title: String,
    val description: String,
    val targetPops: Int,
    val spawnDelayMs: Long,
    val speedBase: Float,
    val items: List<Pair<String, String>>
)

val BALLOON_LEVELS = listOf(
    BalloonLevel(
        1,
        "المرحلة ١ (ألوان)",
        "فرقع البالونات الملونة الجميلة!",
        6,
        1400L,
        0.0032f,
        listOf(
            "🔴" to "أحمر",
            "🔵" to "أزرق",
            "🟢" to "أخضر",
            "🟡" to "أصفر",
            "🟣" to "بنفسجي",
            "🌸" to "وردي"
        )
    ),
    BalloonLevel(
        2,
        "المرحلة ٢ (أرقام)",
        "فرقع بالونات الأرقام وتعلم عدها!",
        8,
        1100L,
        0.0042f,
        listOf(
            "١" to "واحد",
            "٢" to "اثنان",
            "٣" to "ثلاثة",
            "٤" to "أربعة",
            "٥" to "خمسة",
            "٦" to "ستة",
            "٧" to "سبعة",
            "٨" to "ثمانية",
            "٩" to "تسعة",
            "١٠" to "عشرة"
        )
    ),
    BalloonLevel(
        3,
        "المرحلة ٣ (حروف)",
        "فرقع الحروف الهجائية الطائرة!",
        10,
        900L,
        0.0055f,
        listOf(
            "أ" to "ألف",
            "ب" to "باء",
            "ت" to "تاء",
            "ث" to "ثاء",
            "ج" to "جيم",
            "ح" to "حاء",
            "خ" to "خاء",
            "د" to "دال",
            "ر" to "راء",
            "س" to "سين",
            "م" to "ميم",
            "ن" to "نون"
        )
    ),
    BalloonLevel(
        4,
        "المرحلة ٤ (أشكال وحيوانات)",
        "فرقع بالونات الأبطال السريعة!",
        12,
        700L,
        0.007f,
        listOf(
            "⭐" to "نجمة",
            "❤️" to "قلب",
            "🌙" to "هلال",
            "🦁" to "أسد",
            "🐰" to "أرنب",
            "🐱" to "قطة",
            "🐼" to "باندا",
            "🦋" to "فراشة"
        )
    )
)

private val BALLOON_COLORS = listOf(
    Color(0xFFEF4444),
    Color(0xFF3B82F6),
    Color(0xFF10B981),
    Color(0xFFF59E0B),
    Color(0xFF8B5CF6),
    Color(0xFFEC4899),
    Color(0xFF06B6D4)
)

@Composable
fun BalloonPopScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onPopSound: () -> Unit,
    onSpeak: (String) -> Unit = {}
) {
    var currentLevelIndex by remember { mutableIntStateOf(0) }
    val currentLevel = BALLOON_LEVELS[currentLevelIndex % BALLOON_LEVELS.size]

    var poppedInLevel by remember(currentLevel) { mutableIntStateOf(0) }
    val balloons = remember(currentLevel) { mutableStateListOf<BalloonData>() }
    val sparks = remember { mutableStateListOf<PopSpark>() }
    var nextId by remember { mutableIntStateOf(0) }
    var sparkCounter by remember { mutableIntStateOf(0) }
    var lastPoppedWord by remember { mutableStateOf("") }

    LaunchedEffect(currentLevel) {
        poppedInLevel = 0
        balloons.clear()
        onSpeak(currentLevel.description)
        while (true) {
            if (balloons.size < 7) {
                val pair = currentLevel.items.random()
                balloons.add(
                    BalloonData(
                        id = nextId++,
                        text = pair.first,
                        speakWord = pair.second,
                        xRatio = Random.nextFloat() * 0.72f + 0.1f,
                        color = BALLOON_COLORS.random(),
                        speed = Random.nextFloat() * 0.002f + currentLevel.speedBase
                    )
                )
            }
            delay(currentLevel.spawnDelayMs)
        }
    }

    // Animation frame for balloon physics & pop sparks
    var tick by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            tick++
            // Age and clean up pop sparks
            if (sparks.isNotEmpty()) {
                val sparkIter = sparks.listIterator()
                while (sparkIter.hasNext()) {
                    val s = sparkIter.next()
                    s.age++
                    if (s.age > 20) {
                        sparkIter.remove()
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F2FE))
            .testTag("balloon_pop_screen")
    ) {
        // Arabic Header and Controls
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                GameHeader(
                    title = "فرقع البالونات 🎈",
                    onBack = onBack,
                    starsCount = starsCount
                )

                // Level Select Row
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(BALLOON_LEVELS) { lvl ->
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
                                .testTag("balloon_lvl_${lvl.levelNumber}")
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

                // Subtitle helper
                Text(
                    text = currentLevel.description,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF0369A1),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }
        }

        // Sky Physics Container: Enforce LTR coordinates so balloons float identically regardless of system locale
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 130.dp, bottom = 80.dp)
            ) {
                val screenWidth = maxWidth
                val screenHeight = maxHeight
                val toRemove = mutableListOf<BalloonData>()

                // Render Balloons
                balloons.forEach { b ->
                    val elapsedTicks = (tick - b.id * 10).coerceAtLeast(0)
                    val currentYRatio = b.initialY - (elapsedTicks * b.speed)

                    if (currentYRatio < -0.2f) {
                        toRemove.add(b)
                    } else {
                        // Horizontal sinusoidal wobble as balloon floats upward
                        val swayOffset = sin((tick + b.id * 15) * 0.06f) * 0.02f
                        val effectiveXRatio = (b.xRatio + swayOffset).coerceIn(0.04f, 0.88f)

                        Box(
                            modifier = Modifier
                                .offset(
                                    x = screenWidth * effectiveXRatio,
                                    y = screenHeight * currentYRatio
                                )
                                .size(78.dp)
                                .clip(CircleShape)
                                .background(b.color)
                                .border(2.dp, Color.White.copy(alpha = 0.85f), CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    onPopSound()
                                    onSpeak(b.speakWord)
                                    lastPoppedWord = b.speakWord
                                    // Spawn pop spark particles
                                    sparks.add(
                                        PopSpark(
                                            id = sparkCounter++,
                                            xRatio = effectiveXRatio,
                                            yRatio = currentYRatio,
                                            color = b.color
                                        )
                                    )
                                    balloons.remove(b)
                                    poppedInLevel++
                                    if (poppedInLevel >= currentLevel.targetPops) {
                                        onSpeak("أحسنت! أكملت المستوى بنجاح")
                                        onWin(2)
                                        if (currentLevelIndex < BALLOON_LEVELS.size - 1) {
                                            currentLevelIndex++
                                        }
                                    }
                                }
                                .testTag("balloon_${b.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                // Balloon specular shine highlight
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.5f),
                                    radius = size.minDimension * 0.18f,
                                    center = Offset(size.width * 0.32f, size.height * 0.3f)
                                )
                            }
                            Text(
                                text = b.text,
                                fontSize = if (b.text.length > 2) 22.sp else 30.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }

                balloons.removeAll(toRemove)

                // Render Pop Spark Particles
                sparks.forEach { spark ->
                    val sparkScale = 1f + (spark.age * 0.1f)
                    val sparkAlpha = (1f - (spark.age / 20f)).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .offset(
                                x = screenWidth * spark.xRatio,
                                y = screenHeight * spark.yRatio
                            )
                            .size(78.dp)
                            .graphicsLayer {
                                scaleX = sparkScale
                                scaleY = sparkScale
                                alpha = sparkAlpha
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✨", fontSize = 42.sp)
                    }
                }
            }
        }

        // Bottom progress bar with word echo (Arabic RTL)
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.96f),
                shadowElevation = 5.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
                    .border(1.5.dp, SkyBlue200, CircleShape)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "المستوى ${currentLevel.levelNumber}: $poppedInLevel من ${currentLevel.targetPops} 🎯",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0C4A6E)
                    )
                    if (lastPoppedWord.isNotEmpty()) {
                        Text(
                            text = "($lastPoppedWord ✨)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald500
                        )
                    }
                }
            }
        }
    }
}
