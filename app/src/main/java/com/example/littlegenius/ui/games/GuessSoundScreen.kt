package com.example.littlegenius.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.littlegenius.R
import com.example.littlegenius.ui.theme.Amber400
import com.example.littlegenius.ui.theme.Emerald500
import com.example.littlegenius.ui.theme.SkyBlue200
import com.example.littlegenius.ui.theme.SkyBlue600
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MysterySound(
    val name: String,
    val emoji: String,
    val soundResId: Int,
    val wrongAnimals: List<Pair<String, String>>
)

data class GuessSoundLevel(
    val levelNumber: Int,
    val title: String,
    val sounds: List<MysterySound>
)

val GUESS_SOUND_LEVELS = listOf(
    GuessSoundLevel(
        1,
        "حيوانات المزرعة 🚜",
        listOf(
            MysterySound("القطة", "🐱", R.raw.cat, listOf("الكلب" to "🐶", "البقرة" to "🐮")),
            MysterySound("الكلب", "🐶", R.raw.dog, listOf("البطة" to "🦆", "الخروف" to "🐑")),
            MysterySound("البقرة", "🐮", R.raw.cow, listOf("الحصان" to "🐴", "الماعز" to "🐐")),
            MysterySound("الخروف", "🐑", R.raw.sheep, listOf("الديك" to "🐔", "القطة" to "🐱")),
            MysterySound("البطة", "🦆", R.raw.duck, listOf("العصفور" to "🐦", "الديك" to "🐔")),
            MysterySound("الديك", "🐔", R.raw.rooster, listOf("البطة" to "🦆", "البقرة" to "🐮"))
        )
    ),
    GuessSoundLevel(
        2,
        "حيوانات الغابة 🌴",
        listOf(
            MysterySound("الأسد", "🦁", R.raw.lion, listOf("القرد" to "🐵", "الفيل" to "🐘")),
            MysterySound("الفيل", "🐘", R.raw.elephant, listOf("الأسد" to "🦁", "الذئب" to "🐺")),
            MysterySound("القرد", "🐵", R.raw.monkey, listOf("الضفدع" to "🐸", "الفيل" to "🐘")),
            MysterySound("الحصان", "🐴", R.raw.horse, listOf("البقرة" to "🐮", "الخروف" to "🐑")),
            MysterySound("الذئب", "🐺", R.raw.wolf, listOf("الكلب" to "🐶", "الأسد" to "🦁"))
        )
    ),
    GuessSoundLevel(
        3,
        "الطيور والمخلوقات اللطيفة 🐦",
        listOf(
            MysterySound("العصفور", "🐦", R.raw.bird, listOf("البطة" to "🦆", "البومة" to "🦉")),
            MysterySound("الضفدع", "🐸", R.raw.frog, listOf("النحلة" to "🐝", "البطة" to "🦆")),
            MysterySound("البومة", "🦉", R.raw.owl, listOf("العصفور" to "🐦", "الذئب" to "🐺")),
            MysterySound("النحلة", "🐝", R.raw.bee, listOf("الضفدع" to "🐸", "العصفور" to "🐦")),
            MysterySound("الماعز", "🐐", R.raw.goat, listOf("الخروف" to "🐑", "البقرة" to "🐮")),
            MysterySound("الدلفين", "🐬", R.raw.dolphin, listOf("الضفدع" to "🐸", "البطة" to "🦆"))
        )
    ),
    GuessSoundLevel(
        4,
        "التحدي الكبير 🏆",
        listOf(
            MysterySound("الأسد", "🦁", R.raw.lion, listOf("الذئب" to "🐺", "الفيل" to "🐘")),
            MysterySound("الحصان", "🐴", R.raw.horse, listOf("الماعز" to "🐐", "الكلب" to "🐶")),
            MysterySound("الفيل", "🐘", R.raw.elephant, listOf("البقرة" to "🐮", "الأسد" to "🦁")),
            MysterySound("القرد", "🐵", R.raw.monkey, listOf("القطة" to "🐱", "العصفور" to "🐦")),
            MysterySound("الديك", "🐔", R.raw.rooster, listOf("البومة" to "🦉", "البطة" to "🦆")),
            MysterySound("الكلب", "🐶", R.raw.dog, listOf("الذئب" to "🐺", "الخروف" to "🐑"))
        )
    )
)

@Composable
fun GuessSoundScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onPlayRaw: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val coroutineScope = rememberCoroutineScope()
        var levelIndex by remember { mutableIntStateOf(0) }
        val currentLevel = GUESS_SOUND_LEVELS[levelIndex % GUESS_SOUND_LEVELS.size]

        var roundIndex by remember(currentLevel) { mutableIntStateOf(0) }
        val currentMystery = currentLevel.sounds[roundIndex % currentLevel.sounds.size]

        var selectedAnswer by remember(currentMystery) { mutableStateOf<String?>(null) }
        var isAnswerCorrect by remember(currentMystery) { mutableStateOf(false) }

        // Sound waves pulsing animation
        val infiniteTransition = rememberInfiniteTransition(label = "sound_waves")
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.25f,
            animationSpec = infiniteRepeatable(
                animation = tween(650, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        )

        val options = remember(currentMystery) {
            (listOf(currentMystery.name to currentMystery.emoji) + currentMystery.wrongAnimals).shuffled()
        }

        LaunchedEffect(currentMystery) {
            selectedAnswer = null
            isAnswerCorrect = false
            delay(150)
            onPlayRaw(currentMystery.soundResId)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFEFCE8))
                .testTag("guess_sound_screen")
        ) {
            GameHeader(
                title = "خمن صوت الحيوان 👂",
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
                items(GUESS_SOUND_LEVELS) { lvl ->
                    val isCurrent = lvl.levelNumber == currentLevel.levelNumber
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isCurrent) SkyBlue600 else Color.White,
                        modifier = Modifier
                            .clickable {
                                levelIndex = lvl.levelNumber - 1
                                roundIndex = 0
                            }
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (isCurrent) SkyBlue600 else SkyBlue200,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .testTag("guess_lvl_${lvl.levelNumber}")
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

            Spacer(modifier = Modifier.height(16.dp))

            // Big Audio Speaker with Interactive Rippling Waves
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer Sound Wave Ring
                Box(
                    modifier = Modifier
                        .size(165.dp)
                        .graphicsLayer {
                            scaleX = pulseScale
                            scaleY = pulseScale
                            alpha = 0.35f
                        }
                        .background(Color(0xFFFDE68A), CircleShape)
                )

                // Main Speaker Button
                Button(
                    onClick = { onPlayRaw(currentMystery.soundResId) },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Amber400),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp, pressedElevation = 2.dp),
                    modifier = Modifier
                        .size(136.dp)
                        .border(6.dp, Color.White, CircleShape)
                        .testTag("replay_sound_btn")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "تشغيل الصوت الحقيقي",
                            tint = Color(0xFF78350F),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "اسمع الصوت 🔊",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF78350F)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Prompt Question
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 24.dp)
                    .border(2.dp, SkyBlue200, RoundedCornerShape(20.dp))
            ) {
                Text(
                    text = if (isAnswerCorrect) "ممتاز! هذا صوت ${currentMystery.name} 🎉" else "صوت أي حيوان تسمعه الآن؟ 🤔",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isAnswerCorrect) Emerald500 else Color(0xFF854D0E),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Interactive Animal Answer Options
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                options.forEach { (name, emoji) ->
                    val isSelected = selectedAnswer == name
                    val isCorrect = name == currentMystery.name

                    val btnColor = when {
                        isSelected && isCorrect -> Emerald500
                        isSelected && !isCorrect -> Color(0xFFEF4444)
                        else -> SkyBlue600
                    }

                    Button(
                        onClick = {
                            selectedAnswer = name
                            if (isCorrect) {
                                isAnswerCorrect = true
                                onSpeak("أحسنت رائع! هذا صوت $name الحقيقي")
                                coroutineScope.launch {
                                    delay(700)
                                    onWin(1)
                                    roundIndex++
                                    if (roundIndex >= currentLevel.sounds.size && levelIndex < GUESS_SOUND_LEVELS.size - 1) {
                                        levelIndex++
                                        roundIndex = 0
                                    }
                                }
                            } else {
                                onSpeak("لا، هذا $name، اسمع الصوت مرة أخرى!")
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(104.dp)
                            .testTag("sound_guess_$name"),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = btnColor,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = emoji, fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Bottom Progress Info
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 20.dp)
                    .border(1.dp, SkyBlue200, CircleShape)
            ) {
                Text(
                    text = "السؤال ${(roundIndex % currentLevel.sounds.size) + 1} من ${currentLevel.sounds.size} ⭐",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0369A1),
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)
                )
            }
        }
    }
}
