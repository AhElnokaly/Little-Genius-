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
import androidx.compose.ui.graphics.Brush
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
import kotlinx.coroutines.isActive
import kotlin.math.sin
import kotlin.random.Random

data class SwimmingFish(
    val id: Int,
    val name: String,
    val emoji: String,
    var xRatio: Float,
    var yRatio: Float,
    val speed: Float,
    var movingRight: Boolean,
    val isTarget: Boolean = false,
    val baseVerticalRatio: Float = yRatio,
    var wiggleOffset: Float = 0f
)

data class WaterBubble(
    val id: Int,
    var xRatio: Float,
    var yRatio: Float,
    val sizeDp: Float,
    val speed: Float,
    val driftAmp: Float
)

data class CatchSplash(
    val id: Int,
    val xRatio: Float,
    val yRatio: Float,
    val emoji: String
)

data class FishLevel(
    val levelNumber: Int,
    val title: String,
    val targetName: String,
    val targetEmoji: String,
    val targetCount: Int,
    val topWaterColor: Color,
    val bottomWaterColor: Color,
    val fishPool: List<Pair<String, String>>
)

val FISH_LEVELS = listOf(
    FishLevel(
        1,
        "مستوى ١: الشاطئ الذهبي",
        "سمكة برتقالية",
        "🐠",
        5,
        Color(0xFF38BDF8),
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
        Color(0xFF0284C7),
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
        Color(0xFF0369A1),
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
        Color(0xFF075985),
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
    var levelIndex by remember { mutableIntStateOf(0) }
    val currentLevel = FISH_LEVELS[levelIndex % FISH_LEVELS.size]

    var caughtTargetCount by remember(currentLevel) { mutableIntStateOf(0) }
    val fishes = remember(currentLevel) { mutableStateListOf<SwimmingFish>() }
    val bubbles = remember { mutableStateListOf<WaterBubble>() }
    val splashes = remember { mutableStateListOf<CatchSplash>() }
    var fishIdCounter by remember { mutableIntStateOf(0) }
    var bubbleIdCounter by remember { mutableIntStateOf(0) }
    var splashIdCounter by remember { mutableIntStateOf(0) }
    var feedbackMessage by remember { mutableStateOf("") }

    // Wave transition for decorative sea surface
    val infiniteTransition = rememberInfiniteTransition(label = "sea")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    // Initialize initial fishes and ambient bubbles
    LaunchedEffect(currentLevel) {
        caughtTargetCount = 0
        fishes.clear()
        feedbackMessage = "مهمتك: اصطد ${currentLevel.targetCount} من ${currentLevel.targetName} ${currentLevel.targetEmoji}"
        onSpeak("مرحباً بك في ${currentLevel.title}! مهمتك اصطياد ${currentLevel.targetName} ${currentLevel.targetEmoji}")

        // Seed 6 initial fishes swimming horizontally in various directions
        repeat(6) {
            val isTarget = (it % 2 == 0)
            val pair = if (isTarget) {
                currentLevel.targetName to currentLevel.targetEmoji
            } else {
                currentLevel.fishPool.random()
            }
            val movingRight = Random.nextBoolean()
            fishes.add(
                SwimmingFish(
                    id = fishIdCounter++,
                    name = pair.first,
                    emoji = pair.second,
                    xRatio = Random.nextFloat() * 0.8f + 0.1f,
                    yRatio = Random.nextFloat() * 0.62f + 0.12f,
                    speed = Random.nextFloat() * 0.0035f + 0.002f,
                    movingRight = movingRight,
                    isTarget = pair.second == currentLevel.targetEmoji
                )
            )
        }

        // Seed bubbles
        bubbles.clear()
        repeat(12) {
            bubbles.add(
                WaterBubble(
                    id = bubbleIdCounter++,
                    xRatio = Random.nextFloat(),
                    yRatio = Random.nextFloat(),
                    sizeDp = Random.nextFloat() * 14f + 8f,
                    speed = Random.nextFloat() * 0.004f + 0.002f,
                    driftAmp = Random.nextFloat() * 0.03f + 0.01f
                )
            )
        }
    }

    // Physics Engine Loop: Moves fishes and animates bubbles smoothly
    LaunchedEffect(currentLevel) {
        var tick = 0L
        while (isActive) {
            delay(20) // ~50 fps update
            tick++

            // Update fishes
            val iterator = fishes.listIterator()
            while (iterator.hasNext()) {
                val f = iterator.next()
                // Move fish horizontally according to direction
                if (f.movingRight) {
                    f.xRatio += f.speed
                    if (f.xRatio > 1.15f) {
                        // Reached right edge -> respawn on the left or turn around
                        f.xRatio = -0.15f
                        f.yRatio = (Random.nextFloat() * 0.65f + 0.1f).coerceIn(0.1f, 0.85f)
                    }
                } else {
                    f.xRatio -= f.speed
                    if (f.xRatio < -0.15f) {
                        // Reached left edge -> respawn on the right or turn around
                        f.xRatio = 1.15f
                        f.yRatio = (Random.nextFloat() * 0.65f + 0.1f).coerceIn(0.1f, 0.85f)
                    }
                }
                // Gentle sinusoidal swimming wave (up and down bobbing)
                f.wiggleOffset = sin((tick * 0.1f) + f.id.toFloat()) * 0.015f
            }

            // Spawn extra fishes if some were caught
            if (fishes.size < 6 && tick % 50 == 0L) {
                val needTarget = fishes.count { it.isTarget } < 2
                val pair = if (needTarget || Random.nextFloat() < 0.45f) {
                    currentLevel.targetName to currentLevel.targetEmoji
                } else {
                    currentLevel.fishPool.random()
                }
                val movingRight = Random.nextBoolean()
                fishes.add(
                    SwimmingFish(
                        id = fishIdCounter++,
                        name = pair.first,
                        emoji = pair.second,
                        xRatio = if (movingRight) -0.12f else 1.12f,
                        yRatio = Random.nextFloat() * 0.65f + 0.1f,
                        speed = Random.nextFloat() * 0.0035f + 0.002f,
                        movingRight = movingRight,
                        isTarget = pair.second == currentLevel.targetEmoji
                    )
                )
            }

            // Update rising bubbles
            bubbles.forEach { b ->
                b.yRatio -= b.speed
                if (b.yRatio < -0.05f) {
                    b.yRatio = 1.05f
                    b.xRatio = Random.nextFloat()
                }
            }

            // Clean up old splashes
            if (splashes.isNotEmpty() && tick % 25 == 0L) {
                splashes.clear()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(currentLevel.topWaterColor, currentLevel.bottomWaterColor)
                )
            )
            .testTag("catch_fish_screen")
    ) {
        // Decorative Animated Sunbeams & Sea Floor
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw floating ambient bubbles
            bubbles.forEach { b ->
                val bx = size.width * (b.xRatio + sin(b.yRatio * 10f) * b.driftAmp)
                val by = size.height * b.yRatio
                drawCircle(
                    color = Color.White.copy(alpha = 0.28f),
                    radius = b.sizeDp,
                    center = Offset(bx, by)
                )
                // Bubble shine highlight
                drawCircle(
                    color = Color.White.copy(alpha = 0.55f),
                    radius = b.sizeDp * 0.35f,
                    center = Offset(bx - b.sizeDp * 0.3f, by - b.sizeDp * 0.3f)
                )
            }

            // Draw seabed sand gradient at bottom
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(Color.Transparent, Color(0xFFEAB308).copy(alpha = 0.35f))
                ),
                topLeft = Offset(0f, size.height * 0.88f),
                size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.12f)
            )
        }

        // Header and Controls (Arabic RTL for UI text and buttons)
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                GameHeader(
                    title = "صيد السمك والبحار 🐠",
                    onBack = onBack,
                    starsCount = starsCount
                )

                // Level Selection Tabs
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

                // Mission Goal Banner
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White.copy(alpha = 0.94f),
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "المطلوب صيده: ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "${currentLevel.targetName} ${currentLevel.targetEmoji}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0284C7)
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = if (caughtTargetCount >= currentLevel.targetCount) Emerald500 else Color(0xFFE0F2FE),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = "$caughtTargetCount / ${currentLevel.targetCount} ⭐",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = if (caughtTargetCount >= currentLevel.targetCount) Color.White else Color(0xFF0369A1),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // AQUARIUM PHYSICS VIEWPORT:
        // CRITICAL: We enforce LTR for the coordinate system so x=0 is screen-left and x=width is screen-right.
        // This eliminates coordinate reversal in RTL layouts and ensures accurate swimming direction physics!
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 140.dp, bottom = 80.dp)
            ) {
                val screenWidth = maxWidth
                val screenHeight = maxHeight

                // Render Active Swimming Fishes
                fishes.forEach { fish ->
                    val fishEffectiveY = (fish.yRatio + fish.wiggleOffset).coerceIn(0.05f, 0.9f)
                    
                    // Head Direction Correction:
                    // Standard aquatic emojis (🐠, 🐟, 🐡, 🐬, 🐳, 🦈) face LEFT by default in standard fonts.
                    // When swimming right (movingRight = true): flip horizontally with scaleX = -1f to face RIGHT!
                    // When swimming left (movingRight = false): keep scaleX = 1f to face LEFT!
                    // For symmetrical emoji like star ⭐ or crab 🦀, flip is natural.
                    val scaleXFactor = if (fish.movingRight) -1f else 1f

                    Box(
                        modifier = Modifier
                            .offset(
                                x = screenWidth * fish.xRatio,
                                y = screenHeight * fishEffectiveY
                            )
                            .size(80.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                // Add splash animation at caught position
                                splashes.add(
                                    CatchSplash(
                                        id = splashIdCounter++,
                                        xRatio = fish.xRatio,
                                        yRatio = fishEffectiveY,
                                        emoji = "💦"
                                    )
                                )
                                fishes.remove(fish)

                                if (fish.isTarget) {
                                    caughtTargetCount++
                                    onSpeak("ممتاز! اصطدت ${fish.name}")
                                    feedbackMessage = "رائع جداً! اصطدت ${fish.name} 🎯"
                                    if (caughtTargetCount >= currentLevel.targetCount) {
                                        onSpeak("مبروك! أكملت مهمة صيد ${currentLevel.targetName} بنجاح!")
                                        onWin(3)
                                        if (levelIndex < FISH_LEVELS.size - 1) {
                                            levelIndex++
                                        }
                                    }
                                } else {
                                    onSpeak("هذه ${fish.name}، ابحث عن ${currentLevel.targetName}")
                                    feedbackMessage = "هذه ${fish.name}! المطلوب: ${currentLevel.targetEmoji}"
                                }
                            }
                            .testTag("fish_${fish.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        // Target glow indicator
                        if (fish.isTarget) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFEF08A).copy(alpha = 0.35f),
                                modifier = Modifier
                                    .size(72.dp)
                                    .border(
                                        width = 1.5.dp,
                                        color = Color(0xFFFDE047).copy(alpha = 0.5f),
                                        shape = CircleShape
                                    )
                            ) {}
                        }

                        // Fish Emoji with realistic head orientation & swimming rotation
                        Text(
                            text = fish.emoji,
                            fontSize = 46.sp,
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = scaleXFactor
                                    // Slight sinusoidal pitch angle during swimming
                                    rotationZ = sin((fish.xRatio * 15f)) * 6f
                                }
                        )
                    }
                }

                // Render Water Splashes
                splashes.forEach { splash ->
                    Box(
                        modifier = Modifier
                            .offset(
                                x = screenWidth * splash.xRatio,
                                y = screenHeight * splash.yRatio
                            )
                            .size(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(splash.emoji, fontSize = 38.sp)
                    }
                }
            }
        }

        // Bottom Feedback Toast (Arabic RTL)
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.96f),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
                    .border(1.5.dp, SkyBlue200, CircleShape)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = feedbackMessage.ifEmpty { "المس الكائنات المطلوبة لاصطيادها بالشبكة! 🌊" },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0369A1)
                    )
                }
            }
        }
    }
}
