package com.example.littlegenius.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.littlegenius.ui.theme.Amber400
import com.example.littlegenius.ui.theme.Emerald500
import com.example.littlegenius.ui.theme.SkyBlue600

data class FamilyPair(
    val parentName: String,
    val parentEmoji: String,
    val babyName: String,
    val babyEmoji: String,
    val wrongBabies: List<Pair<String, String>>,
    val funFact: String
)

val FAMILY_PAIRS = listOf(
    FamilyPair("الدجاجة", "🐔", "الكتكوت", "🐥", listOf("الجرو" to "🐕", "الشبل" to "🦁"), "الكتكوت يخرج من البيضة بعد ٢١ يوماً"),
    FamilyPair("القطة", "🐱", "الهر الصغير", "🐈", listOf("الحمل" to "🐑", "العجل" to "🐮"), "القطط الصغيرة تتعلم الصيد من أمها"),
    FamilyPair("الكلب", "🐶", "الجرو", "🐕", listOf("الكتكوت" to "🐥", "المهر" to "🐴"), "الجرو كائن وفي ويحب اللعب والمرح"),
    FamilyPair("الخروف", "🐑", "الحَمَل", "🐏", listOf("الجرو" to "🐕", "الكتكوت" to "🐥"), "الحمل يتبع أمه الخروف أينما تذهب"),
    FamilyPair("الأسد", "🦁", "الشِبْل", "🐾", listOf("المهر" to "🐴", "العجل" to "🐮"), "الشبل هو ملك الغابة الصغير اللطيف"),
    FamilyPair("الحصان", "🐴", "المُهْر", "🐎", listOf("الكتكوت" to "🐥", "الهر" to "🐈"), "المهر يستطيع الوقوف بعد ساعات قليلة من ولادته"),
    FamilyPair("البقرة", "🐮", "العِجْل", "🐂", listOf("الشبل" to "🐾", "الجرو" to "🐕"), "العجل الصغير يتغذى على حليب أمه اللذيذ"),
    FamilyPair("البطة", "🦆", "البُطَيْط", "🐣", listOf("الحمل" to "🐏", "المهر" to "🐎"), "البط الصغير يجيد السباحة خلف أمه فوراً"),
    FamilyPair("الفيل", "🐘", "الدَّغْفَل", "🐘", listOf("الكتكوت" to "🐥", "العجل" to "🐂"), "صغير الفيل يتميز بخرطومه المرح"),
    FamilyPair("الكنغر", "🦘", "الجُوِي", "🦘", listOf("الهر" to "🐈", "الجرو" to "🐕"), "صغير الكنغر يختبئ في جيب أمه الدافئ")
)

@Composable
fun AnimalFamilyScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var pairIndex by remember { mutableIntStateOf(0) }
        val currentPair = FAMILY_PAIRS[pairIndex % FAMILY_PAIRS.size]
        var reunited by remember { mutableStateOf(false) }
        var pairsMatched by remember { mutableIntStateOf(0) }

        val options = remember(currentPair) {
            (listOf(currentPair.babyName to currentPair.babyEmoji) + currentPair.wrongBabies).shuffled()
        }

        LaunchedEffect(currentPair) {
            reunited = false
            onSpeak("من هو صغير ${currentPair.parentName}؟")
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF7ED))
                .testTag("animal_family_screen")
        ) {
            GameHeader(
                title = "عائلات الحيوانات 🐾",
                onBack = onBack,
                starsCount = starsCount
            )

            // Family Card
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = Color.White,
                shadowElevation = 5.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .border(4.dp, Amber400, RoundedCornerShape(32.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = currentPair.parentEmoji,
                            fontSize = 72.sp
                        )
                        if (reunited) {
                            Text("💖", fontSize = 36.sp)
                            Text(
                                text = currentPair.babyEmoji,
                                fontSize = 54.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = currentPair.parentName,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF9A3412)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (reunited) "التأم شمل العائلة! ${currentPair.funFact} ✨" else "ابحث عن صغيرها وساعدهما على اللقاء 💕",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (reunited) Emerald500 else Color(0xFFC2410C)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "اختر الصغير المناسب:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9A3412),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                options.forEach { (name, emoji) ->
                    Button(
                        onClick = {
                            if (name == currentPair.babyName) {
                                reunited = true
                                pairsMatched++
                                onSpeak("أحسنت! صغير ${currentPair.parentName} هو ${currentPair.babyName}")
                                onWin(1)
                                pairIndex++
                            } else {
                                onSpeak("فكر ثانية.. هذا ليس صغير ${currentPair.parentName}")
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                            .testTag("family_opt_$name"),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SkyBlue600,
                            contentColor = Color.White
                        )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = emoji, fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            // Progress banner
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "جمعت $pairsMatched من عائلات الحيوانات 🐾",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9A3412),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
        }
    }
}
