package com.example.littlegenius.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.littlegenius.ui.theme.Emerald500
import com.example.littlegenius.ui.theme.Rose500

data class FoodItem(
    val name: String,
    val emoji: String,
    val isHealthy: Boolean,
    val category: String,
    val benefit: String
)

val FOOD_LIST = listOf(
    FoodItem("التفاح", "🍎", true, "فواكه", "غني بالفيتامينات ويقوي الجسم"),
    FoodItem("الجزر", "🥕", true, "خضروات", "يقوي النظر ومفيد للبشرة"),
    FoodItem("الحليب", "🥛", true, "ألبان", "يقوي العظام والأسنان بالبروتين"),
    FoodItem("الموز", "🍌", true, "فواكه", "يمد الجسم بالطاقة والنشاط"),
    FoodItem("البيض", "🥚", true, "بروتين", "يبني العضلات ويساعد على النمو"),
    FoodItem("السمك", "🐟", true, "بروتين", "يقوي الذاكرة والذكاء"),
    FoodItem("الحلوى", "🍬", false, "سكريات", "كثرتها تضر الأسنان وتسبب التسوس"),
    FoodItem("المصاصة", "🍭", false, "سكريات", "تحتوي على سكريات زائدة وتفتقر للفيتامينات"),
    FoodItem("الدونات", "🍩", false, "حلويات", "لذيذة لكن تؤكل باعتدال في المناسبات"),
    FoodItem("البروكلي", "🥦", true, "خضروات", "خضار رائع ومغذي ومقوي للمناعة"),
    FoodItem("المشروبات الغازية", "🥤", false, "مشروبات", "تضر بالمعدة والأسنان"),
    FoodItem("البرتقال", "🍊", true, "فواكه", "غني بفيتامين سي ويحمي من البرد"),
    FoodItem("البطاطس المقلية", "🍟", false, "وجبات سريعة", "تحتوي على زيوت كثيرة"),
    FoodItem("الماء النقي", "💧", true, "سوائل صحية", "يروي العطش وينظف الجسم")
)

@Composable
fun HealthyFoodScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var currentIndex by remember { mutableIntStateOf(0) }
        val currentFood = FOOD_LIST[currentIndex % FOOD_LIST.size]
        var healthyScore by remember { mutableIntStateOf(0) }
        var feedbackMessage by remember { mutableStateOf("") }

        LaunchedEffect(currentFood) {
            onSpeak("هل ${currentFood.name} طعام صحي أم حلويات؟")
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7FEE7))
                .testTag("healthy_food_screen")
        ) {
            GameHeader(
                title = "طعامي الصحي 🥗",
                onBack = onBack,
                starsCount = starsCount
            )

            // Category tag
            Surface(
                shape = CircleShape,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = "التصنيف: ${currentFood.category} ✨",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF365314),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // Food Card
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = Color.White,
                shadowElevation = 5.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .border(4.dp, Color(0xFFA3E635), RoundedCornerShape(32.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentFood.emoji,
                        fontSize = 76.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = currentFood.name,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF365314)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = feedbackMessage.ifEmpty { "هل هذا مفيد وصحي لجسمك؟ 🧐" },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (feedbackMessage.isNotEmpty()) Emerald500 else Color(0xFF65A30D)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Choice Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Healthy Button
                Button(
                    onClick = {
                        if (currentFood.isHealthy) {
                            healthyScore++
                            feedbackMessage = "إجابة صحيحة ومفيدة! 🎉"
                            onSpeak("ممتاز! ${currentFood.name} ${currentFood.benefit}")
                            onWin(1)
                            currentIndex++
                        } else {
                            feedbackMessage = "فكر جيداً.. هذا طعام غير صحي!"
                            onSpeak("لا، ${currentFood.name} طعام غير صحي ويجب التقليل منه")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .testTag("healthy_choice_btn"),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("صحّي ومفيد 🥗", fontSize = 16.sp, fontWeight = FontWeight.Black)
                        Text("يقوي جسمي", fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
                    }
                }

                // Treat Button
                Button(
                    onClick = {
                        if (!currentFood.isHealthy) {
                            healthyScore++
                            feedbackMessage = "صحيح! نتناوله بحذر واعتدال 🎉"
                            onSpeak("صحيح! ${currentFood.name} ${currentFood.benefit}")
                            onWin(1)
                            currentIndex++
                        } else {
                            feedbackMessage = "بل هو طعام صحي وممتاز!"
                            onSpeak("لا، ${currentFood.name} طعام صحي ومفيد جداً للجسم")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .testTag("treat_choice_btn"),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Rose500)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("غير صحي / حلويات 🍭", fontSize = 14.sp, fontWeight = FontWeight.Black)
                        Text("يؤكل باعتدال", fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
                    }
                }
            }

            // Bottom Plate score
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 20.dp)
            ) {
                Text(
                    text = "طبق الصحة: عرفت $healthyScore من الأطعمة المفيدة 🍽️",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF365314),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
        }
    }
}
