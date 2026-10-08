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
import com.example.littlegenius.ui.theme.SkyBlue200
import com.example.littlegenius.ui.theme.SkyBlue600

data class HouseItem(
    val name: String,
    val emoji: String,
    val activeEmoji: String,
    val desc: String,
    val actionWord: String
)

data class RoomData(
    val id: String,
    val title: String,
    val emoji: String,
    val bgColor: Color,
    val items: List<HouseItem>
)

val ROOMS = listOf(
    RoomData(
        "bedroom", "غرفة النوم", "🛏️",
        Color(0xFFFEF3C7),
        listOf(
            HouseItem("السرير", "🛏️", "😴", "سرير مريح للنوم والراحة", "نام بهدوء"),
            HouseItem("المصباح", "💡", "✨", "مصباح لإضاءة الغرفة", "أشعل النور"),
            HouseItem("الدبدوب", "🧸", "🤗", "لعبة دبدوب لطيفة", "عانق الدبدوب"),
            HouseItem("المنبه", "⏰", "🔔", "منبه للاستيقاظ بنشاط", "رن المنبه")
        )
    ),
    RoomData(
        "kitchen", "المطبخ", "🍳",
        Color(0xFFFFEDD5),
        listOf(
            HouseItem("الثلاجة", "🧊", "❄️", "ثلاجة لحفظ الطعام بارداً", "فتحت الثلاجة"),
            HouseItem("التفاحة", "🍎", "😋", "تفاحة صحية ولذيذة", "أكلت التفاحة"),
            HouseItem("الحليب", "🥛", "💪", "كوب حليب مفيد للعظام", "شربت الحليب"),
            HouseItem("الفرن", "🍲", "♨️", "فرن لطهي الطعام الشهي", "طبخت الطعام")
        )
    ),
    RoomData(
        "living", "غرفة الجلوس", "🛋️",
        Color(0xFFF3E8FF),
        listOf(
            HouseItem("الأريكة", "🛋️", "👨‍👩‍👧‍👦", "أريكة للجلوس مع العائلة", "جلست العائلة"),
            HouseItem("التلفاز", "📺", "🎬", "تلفاز لمشاهدة الرسوم المتحركة", "شغّلت الرسوم"),
            HouseItem("الكتاب", "📚", "📖", "كتاب مفيد لقراءة القصص", "قرأت قصة"),
            HouseItem("الزرع", "🪴", "🌺", "نبتة جميلة تنعش الغرفة", "سقيت النبتة")
        )
    ),
    RoomData(
        "bath", "الحمام", "🛁",
        Color(0xFFE0F2FE),
        listOf(
            HouseItem("حوض الاستحمام", "🛁", "🫧", "حوض للاستحمام والنظافة", "فقاعات الصابون"),
            HouseItem("الصابون", "🧼", "🧼", "صابون لغسل اليدين جيداً", "غسلت يدي"),
            HouseItem("الفرشاة", "🪥", "✨", "فرشاة لتنظيف الأسنان", "نظّفت أسناني"),
            HouseItem("المنشفة", "🧴", "🧖", "منشفة لتجفيف الجسم", "جففت جسمي")
        )
    ),
    RoomData(
        "garden", "حديقة البيت", "🏡",
        Color(0xFFDCFCE7),
        listOf(
            HouseItem("الأرجوحة", "🪅", "🎉", "أرجوحة للعب والمرح", "تأرجحت عالياً"),
            HouseItem("الشجرة", "🌳", "🍎", "شجرة خضراء وارفة الظلال", "قطفت الثمار"),
            HouseItem("العصفور", "🐦", "🎶", "عصفور يغرد بألحان عذبة", "غرّد العصفور"),
            HouseItem("الكرة", "⚽", "🥅", "كرة قدم للعب مع الأصدقاء", "سددت هدفاً")
        )
    )
)

@Composable
fun InteractiveHouseScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var selectedRoom by remember { mutableStateOf(ROOMS[0]) }
        var activeItemName by remember { mutableStateOf<String?>(null) }
        var exploredCount by remember { mutableIntStateOf(0) }

        LaunchedEffect(selectedRoom) {
            activeItemName = null
            onSpeak("${selectedRoom.title}! استكشف الأشياء بالداخل")
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(selectedRoom.bgColor)
                .testTag("interactive_house_screen")
        ) {
            GameHeader(
                title = "بيتي الجميل 🏠",
                onBack = onBack,
                starsCount = starsCount
            )

            // Room Tabs Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ROOMS) { room ->
                    val isSelected = selectedRoom.id == room.id
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSelected) SkyBlue600 else Color.White,
                        modifier = Modifier
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) SkyBlue600 else SkyBlue200,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable { selectedRoom = room }
                            .testTag("room_tab_${room.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(room.emoji, fontSize = 20.sp)
                            Text(
                                room.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFF1E293B)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Room Items Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                val rows = selectedRoom.items.chunked(2)
                rows.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        rowItems.forEach { item ->
                            val isActive = activeItemName == item.name
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1.15f)
                                    .border(
                                        3.dp,
                                        if (isActive) Emerald500 else Color.White,
                                        RoundedCornerShape(24.dp)
                                    )
                                    .clickable {
                                        activeItemName = if (isActive) null else item.name
                                        onSpeak("${item.name}: ${item.actionWord} .. ${item.desc}")
                                        exploredCount++
                                        if (exploredCount % 4 == 0) {
                                            onWin(1)
                                        }
                                    }
                                    .testTag("house_item_${item.name}"),
                                shape = RoundedCornerShape(24.dp),
                                color = if (isActive) Color(0xFFECFDF5) else Color.White,
                                shadowElevation = 3.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = if (isActive) item.activeEmoji else item.emoji,
                                        fontSize = 44.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = item.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isActive) Emerald500 else Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = if (isActive) item.actionWord else item.desc,
                                        fontSize = 11.sp,
                                        color = if (isActive) Emerald500 else Color.Gray,
                                        maxLines = 1,
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom interactive speech echo
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "المس الأشياء لرؤية ما تفعله وسماع اسمها! ✨",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
        }
    }
}
