package com.example.littlegenius.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.littlegenius.ui.theme.SkyBlue600

data class RecycleItem(
    val name: String,
    val emoji: String,
    val binType: String, // "paper", "plastic", "organic", "glass", "metal"
    val tip: String
)

data class RecycleBin(
    val type: String,
    val name: String,
    val emoji: String,
    val color: Color
)

val BINS = listOf(
    RecycleBin("paper", "الورق", "📦", Color(0xFF38BDF8)),
    RecycleBin("plastic", "البلاستيك", "🥤", Color(0xFFFBBF24)),
    RecycleBin("organic", "عضوي", "🍏", Color(0xFF4ADE80)),
    RecycleBin("glass", "الزجاج", "🍾", Color(0xFFA78BFA)),
    RecycleBin("metal", "المعادن", "🥫", Color(0xFFF87171))
)

val RECYCLE_ITEMS = listOf(
    RecycleItem("الجريدة القديمة", "📰", "paper", "الورق يمكن إعادة تصنيعه لإنقاذ الأشجار الجميلة"),
    RecycleItem("قارورة العصير البلاستيكية", "🥤", "plastic", "البلاستيك يوضع في السلة الصفراء لإعادة تدويره"),
    RecycleItem("قشرة الموز", "🍌", "organic", "بقايا الفواكه تتحول لسماد طبيعي مغذي للنباتات"),
    RecycleItem("علبة الكرتون", "📦", "paper", "الكرتون يصنع منه صناديق ودفاتر جديدة"),
    RecycleItem("اللعبة البلاستيكية", "🪀", "plastic", "الألعاب التالفة تصنف مع البلاستيك"),
    RecycleItem("تفاحة مأكولة", "🍏", "organic", "بقايا الطعام تذهب للسماد العضوي للأشجار"),
    RecycleItem("قارورة العطر الزجاجية", "🍾", "glass", "الزجاج مادة نقية يمكن صهرها وتشكيلها مجدداً"),
    RecycleItem("علبة المشروب المعدنية", "🥫", "metal", "المعادن والألومنيوم تصنع منها علب جديدة"),
    RecycleItem("البرطمان الزجاجي", "🫙", "glass", "البرطمانات الزجاجية توضع في سلة الزجاج بحذر"),
    RecycleItem("كتاب قديم", "📚", "paper", "الكتب والكراريس تذهب لسلة الورق")
)

@Composable
fun RecycleSortScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var itemIndex by remember { mutableIntStateOf(0) }
        val currentItem = RECYCLE_ITEMS[itemIndex % RECYCLE_ITEMS.size]
        var sortedCount by remember { mutableIntStateOf(0) }
        var feedbackMsg by remember { mutableStateOf("") }

        LaunchedEffect(currentItem) {
            onSpeak("أين نضع ${currentItem.name}؟")
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0FDF4))
                .testTag("recycle_sort_screen")
        ) {
            GameHeader(
                title = "حماة البيئة والتدوير ♻️",
                onBack = onBack,
                starsCount = starsCount
            )

            // Current Item Card
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 5.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .border(3.dp, Emerald500, RoundedCornerShape(28.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(currentItem.emoji, fontSize = 68.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = currentItem.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF166534)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = feedbackMsg.ifEmpty { "اختر السلة المناسبة لإعادة التدوير يا بطل البيئة 🌱" },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (feedbackMsg.isNotEmpty()) Emerald500 else Color(0xFF15803D),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Recycle Bins Horizontal Row
            Text(
                text = "سلات الفرز والتصنيف:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF166534),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(BINS) { bin ->
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = bin.color,
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .width(105.dp)
                            .height(130.dp)
                            .border(2.dp, Color.White, RoundedCornerShape(22.dp))
                            .clickable {
                                if (bin.type == currentItem.binType) {
                                    feedbackMsg = "إجابة ممتازة! 🎉"
                                    onSpeak("ممتاز! ${currentItem.tip}")
                                    onWin(1)
                                    sortedCount++
                                    itemIndex++
                                } else {
                                    feedbackMsg = "فكر ثانية.. ما خامة هذا الشيء؟"
                                    onSpeak("فكر ثانية.. هل ${currentItem.name} مصنوعة من ${bin.name}؟")
                                }
                            }
                            .testTag("bin_${bin.type}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(bin.emoji, fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = bin.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }
                }
            }

            // Bottom Progress
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "صنفت $sortedCount من العناصر لحماية الطبيعة 🌍",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF166534),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
        }
    }
}
