package com.example.littlegenius.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.littlegenius.R
import com.example.littlegenius.ui.theme.Amber400
import com.example.littlegenius.ui.theme.SkyBlue600

data class MysterySound(
    val name: String,
    val emoji: String,
    val soundResId: Int,
    val wrongAnimals: List<Pair<String, String>>
)

val MYSTERY_SOUNDS = listOf(
    MysterySound("القطة", "🐱", R.raw.cat, listOf("الكلب" to "🐶", "البقرة" to "🐮")),
    MysterySound("الكلب", "🐶", R.raw.dog, listOf("البطة" to "🦆", "الخروف" to "🐑")),
    MysterySound("البقرة", "🐮", R.raw.cow, listOf("الأسد" to "🦁", "الحصان" to "🐴")),
    MysterySound("البطة", "🦆", R.raw.duck, listOf("العصفور" to "🐦", "الديك" to "🐔")),
    MysterySound("الأسد", "🦁", R.raw.lion, listOf("القرد" to "🐵", "الفيل" to "🐘")),
    MysterySound("الخروف", "🐑", R.raw.sheep, listOf("الماعز" to "🐐", "الذئب" to "🐺"))
)

@Composable
fun GuessSoundScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onPlayRaw: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    var roundIndex by remember { mutableIntStateOf(0) }
    val currentMystery = MYSTERY_SOUNDS[roundIndex % MYSTERY_SOUNDS.size]

    val options = remember(currentMystery) {
        (listOf(currentMystery.name to currentMystery.emoji) + currentMystery.wrongAnimals).shuffled()
    }

    LaunchedEffect(currentMystery) {
        onPlayRaw(currentMystery.soundResId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEFCE8))
            .testTag("guess_sound_screen")
    ) {
        GameHeader(
            title = "خمن الصوت 👂",
            onBack = onBack,
            starsCount = starsCount
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Big Audio Speaker Button
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = { onPlayRaw(currentMystery.soundResId) },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Amber400),
                modifier = Modifier
                    .size(140.dp)
                    .border(6.dp, Color.White, CircleShape)
                    .testTag("replay_sound_btn")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "تشغيل الصوت",
                        tint = Color(0xFF78350F),
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "اسمع مجدداً",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF78350F)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "صوت من هذا الحيوان؟ 🤔",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF854D0E),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEach { (name, emoji) ->
                Button(
                    onClick = {
                        if (name == currentMystery.name) {
                            onSpeak("أحسنت! هذا صوت $name")
                            onWin(1)
                            roundIndex++
                        } else {
                            onSpeak("لا، اسمع الصوت جيداً")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(96.dp)
                        .testTag("sound_guess_$name"),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SkyBlue600,
                        contentColor = Color.White
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = emoji, fontSize = 38.sp)
                        Text(text = name, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}
