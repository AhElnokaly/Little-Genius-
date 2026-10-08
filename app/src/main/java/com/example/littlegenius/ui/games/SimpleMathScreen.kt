package com.example.littlegenius.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.littlegenius.ui.theme.SkyBlue200
import com.example.littlegenius.ui.theme.SkyBlue600
import kotlin.random.Random

data class MathLevel(
    val levelNumber: Int,
    val title: String,
    val isPlusOnly: Boolean?, // true for plus, false for minus, null for mix
    val maxOperand: Int
)

val MATH_LEVELS = listOf(
    MathLevel(1, "المرحلة ١ (جمع سهل)", true, 3),
    MathLevel(2, "المرحلة ٢ (جمع حتى ٥)", true, 5),
    MathLevel(3, "المرحلة ٣ (طرح سهل)", false, 4),
    MathLevel(4, "المرحلة ٤ (طرح حتى ٧)", false, 7),
    MathLevel(5, "المرحلة ٥ (التحدي الشامل)", null, 6)
)

@Composable
fun SimpleMathScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var levelIndex by remember { mutableIntStateOf(0) }
        val currentLevel = MATH_LEVELS[levelIndex % MATH_LEVELS.size]
        var roundSeed by remember { mutableIntStateOf(1) }

        val (n1, n2, isPlus) = remember(roundSeed, currentLevel) {
            val op = when (currentLevel.isPlusOnly) {
                true -> true
                false -> false
                null -> Random.nextBoolean()
            }
            if (op) {
                val a = Random.nextInt(1, currentLevel.maxOperand)
                val b = Random.nextInt(1, currentLevel.maxOperand)
                Triple(a, b, true)
            } else {
                val a = Random.nextInt(2, currentLevel.maxOperand + 1)
                val b = Random.nextInt(1, a)
                Triple(a, b, false)
            }
        }

        val answer = if (isPlus) n1 + n2 else n1 - n2

        val options = remember(answer) {
            val list = mutableListOf(answer)
            while (list.size < 3) {
                val fake = (answer + listOf(-1, 1, 2, -2).random()).coerceAtLeast(0)
                if (!list.contains(fake)) list.add(fake)
            }
            list.shuffled()
        }

        LaunchedEffect(n1, n2, isPlus) {
            val opText = if (isPlus) "زائد" else "ناقص"
            onSpeak("$n1 $opText $n2 يساوي كم؟")
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFDF4FF))
                .testTag("simple_math_screen")
        ) {
            GameHeader(
                title = "حساب بسيط ➕",
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
                items(MATH_LEVELS) { lvl ->
                    val isCurrent = lvl.levelNumber == currentLevel.levelNumber
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isCurrent) SkyBlue600 else Color.White,
                        modifier = Modifier
                            .clickable {
                                levelIndex = lvl.levelNumber - 1
                                roundSeed++
                            }
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (isCurrent) SkyBlue600 else SkyBlue200,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .testTag("math_lvl_${lvl.levelNumber}")
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

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(32.dp),
                color = Color.White,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .border(4.dp, Color(0xFFF0ABFC), RoundedCornerShape(32.dp))
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$n1 ${if (isPlus) "+" else "−"} $n2 = ?",
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF86198F)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val tokenEmoji = remember(roundSeed) { listOf("🍎", "⭐", "🚗", "🎈", "🍪", "🐱").random() }

                    // Visual token groups with nice cards
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFDF2F8),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF472B6)),
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(
                                text = tokenEmoji.repeat(n1),
                                fontSize = if (n1 > 4) 20.sp else 26.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        Text(
                            text = if (isPlus) "➕" else "➖",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFDF2F8),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF472B6)),
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(
                                text = tokenEmoji.repeat(n2),
                                fontSize = if (n2 > 4) 20.sp else 26.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                options.forEach { opt ->
                    Button(
                        onClick = {
                            if (opt == answer) {
                                onSpeak("ممتاز! الإجابة هي $answer")
                                onWin(1)
                                roundSeed++
                            } else {
                                onSpeak("حاول مرة ثانية يا بطل")
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(76.dp)
                            .testTag("math_opt_$opt"),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SkyBlue600,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "$opt",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
