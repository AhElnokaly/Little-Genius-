package com.example.littlegenius.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.littlegenius.ui.theme.SkyBlue600

data class MiniGameTile(
    val title: String,
    val icon: String,
    val color: Color,
    val description: String
)

fun getMiniGameData(gameId: String): Pair<String, List<MiniGameTile>> {
    return when (gameId) {
        "lettermatch" -> "توصيل الحروف بالكلمات 🔤" to listOf(
            MiniGameTile("أ - أرنب", "🐰", Color(0xFFFCA5A5), "ألف أرنب يقفز في الحقل"),
            MiniGameTile("ب - بطة", "🦆", Color(0xFFFDE047), "باء بطة تسبح في الماء"),
            MiniGameTile("ت - تفاحة", "🍎", Color(0xFF86EFAC), "تاء تفاحة حمراء لذيذة ومفيدة"),
            MiniGameTile("ث - ثعلب", "🦊", Color(0xFFFED7AA), "ثاء ثعلب ذكي وسريع"),
            MiniGameTile("ج - جمل", "🐪", Color(0xFFBAE6FD), "جيم جمل سفينة الصحراء"),
            MiniGameTile("ح - حصان", "🐴", Color(0xFFDDD6FE), "حاء حصان وفي ورشيق")
        )
        "drawshapes" -> "تعلّم الأشكال الهندسية 📐" to listOf(
            MiniGameTile("الدائرة", "⭕", Color(0xFFFCA5A5), "دائرة مستديرة تشبه الشمس والكرة"),
            MiniGameTile("المربع", "⏹️", Color(0xFF93C5FD), "مربع له ٤ أضلاع متساوية مثل الصندوق"),
            MiniGameTile("المثلث", "🔺", Color(0xFFFDE047), "مثلث له ٣ زوايا مثل قطعة البيتزا"),
            MiniGameTile("المستطيل", "🚪", Color(0xFF86EFAC), "مستطيل مثل الباب والنافذة"),
            MiniGameTile("النجمة", "⭐", Color(0xFFFDE68A), "نجمة ساطعة بـ ٥ رؤوس جميلة"),
            MiniGameTile("القلب", "❤️", Color(0xFFFECDD3), "رمز المحبة واللطف")
        )
        "color" -> "عالم الألوان 🎨" to listOf(
            MiniGameTile("أَحْمَر", "🔴", Color(0xFFEF4444), "لون التفاح والفراولة والوردة"),
            MiniGameTile("أَزْرَق", "🔵", Color(0xFF3B82F6), "لون السماء الصافية والبحر"),
            MiniGameTile("أَصْفَر", "🟡", Color(0xFFFBBF24), "لون الشمس والموز المنعش"),
            MiniGameTile("أَخْضَر", "🟢", Color(0xFF10B981), "لون الأشجار والعشب والطبيعة"),
            MiniGameTile("بُرْتُقَالِي", "🟠", Color(0xFFF97316), "لون البرتقال والغروب الساحر"),
            MiniGameTile("بَنَفْسَجِي", "🟣", Color(0xFF8B5CF6), "لون العنب وزهور اللافندر")
        )
        "time" -> "الوقت والتقويم ⏰" to listOf(
            MiniGameTile("الصباح الباكر", "🌅", Color(0xFFFDE68A), "وقت الاستيقاظ وصلاة الفجر والفطور"),
            MiniGameTile("الظهيرة", "☀️", Color(0xFFFED7AA), "الشمس في كبد السماء وقت الغداء والمذاكرة"),
            MiniGameTile("المساء والعصر", "🌆", Color(0xFFFBCFE8), "وقت اللعب والأنشطة والاجتماع الأسري"),
            MiniGameTile("الليل الهادئ", "🌙", Color(0xFFC7D2FE), "وقت النوم الهانئ وأحلام الطفولة السعيدة")
        )
        "nature" -> "مستكشف الطبيعة 🌿" to listOf(
            MiniGameTile("الشجرة الخضراء", "🌳", Color(0xFFBBF7D0), "تعطينا الظل والثمار والأكسجين النقي"),
            MiniGameTile("الزهرة الفواحة", "🌸", Color(0xFFFBCFE8), "رائحتها عطرة ومنظرها يسر الناظرين"),
            MiniGameTile("المطر المبارك", "🌧️", Color(0xFFBAE6FD), "يسقي الأرض العطشى والنباتات"),
            MiniGameTile("الجبل الشامخ", "⛰️", Color(0xFFE2E8F0), "شامخ وعالي يثبت الأرض"),
            MiniGameTile("الشمس الذهبية", "☀️", Color(0xFFFEF08A), "تمدنا بالدفء والنور وتساعد النبات"),
            MiniGameTile("البحر الواسع", "🌊", Color(0xFF93C5FD), "موطن الأسماك الجميلة والدلافين")
        )
        "moon" -> "أطوار القمر 🌙" to listOf(
            MiniGameTile("الهلال الجديد", "🌙", Color(0xFFE2E8F0), "يبدأ به الشهر الهجري الجديد بالبركة"),
            MiniGameTile("التربيع الأول", "🌓", Color(0xFFCBD5E1), "نصف القمر مضيء في السماء"),
            MiniGameTile("البدر المكتمل", "🌕", Color(0xFFFEF08A), "قمر مكتمل بدر منير وجميل في منتصف الشهر"),
            MiniGameTile("الهلال الأخير", "🌘", Color(0xFFE2E8F0), "يختم به الشهر الهجري بهدوء")
        )
        "coloring" -> "دفتر التلوين 🖍️" to listOf(
            MiniGameTile("فراشة بديعة", "🦋", Color(0xFFFBCFE8), "لوّن أجنحة الفراشة بأجمل الألوان"),
            MiniGameTile("سيارة سباق", "🚗", Color(0xFFBAE6FD), "سيارة سريعة باللون الأحمر والأزرق"),
            MiniGameTile("نجمة ذهبية", "⭐", Color(0xFFFEF08A), "نجمة ساطعة في سماء الليل"),
            MiniGameTile("شجرة تفاح", "🌳", Color(0xFFBBF7D0), "شجرة خضراء وارفة مع ثمار تفاح أحمر")
        )
        "jigsaw" -> "تركيب الصور 🧩" to listOf(
            MiniGameTile("قطعة ١: الرأس", "🦁", Color(0xFFFED7AA), "رأس الأسد الشجاع ولبدته الجميلة"),
            MiniGameTile("قطعة ٢: الجسم", "🐾", Color(0xFFBAE6FD), "جسم الأسد القوي الرشيق"),
            MiniGameTile("قطعة ٣: الأقدام", "🦶", Color(0xFFBBF7D0), "أقدام الأسد الثابتة في الغابة"),
            MiniGameTile("قطعة ٤: الذيل", "✨", Color(0xFFFBCFE8), "ذيل الأسد اللطيف يكتمل به البازل")
        )
        else -> "لعبة ممتعة 🎮" to listOf(
            MiniGameTile("المرحلة ١", "🌟", Color(0xFFBAE6FD), "خطوة أولى"),
            MiniGameTile("المرحلة ٢", "🚀", Color(0xFFFDE68A), "خطوة ثانية"),
            MiniGameTile("المرحلة ٣", "🏆", Color(0xFFBBF7D0), "خطوة الفوز")
        )
    }
}

@Composable
fun GenericMiniGameScreen(
    gameId: String,
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    val (title, tiles) = remember(gameId) { getMiniGameData(gameId) }
    var selectedTile by remember { mutableStateOf<MiniGameTile?>(null) }
    var tappedCount by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("generic_game_$gameId")
    ) {
        GameHeader(
            title = title,
            onBack = onBack,
            starsCount = starsCount
        )

        // Details card if selected
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = selectedTile?.color ?: Color(0xFFE2E8F0),
            shadowElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(3.dp, Color.White, RoundedCornerShape(24.dp))
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(selectedTile?.icon ?: "✨", fontSize = 48.sp)
                Column {
                    Text(
                        selectedTile?.title ?: "المس أي عنصر لاكتشافه!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        selectedTile?.description ?: "استمتع بالتعلم يا بطل",
                        fontSize = 14.sp,
                        color = Color(0xFF334155)
                    )
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(tiles) { tile ->
                Surface(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .border(3.dp, Color.White, RoundedCornerShape(20.dp))
                        .clickable {
                            selectedTile = tile
                            onSpeak("${tile.title} .. ${tile.description}")
                            tappedCount++
                            if (tappedCount % 3 == 0) {
                                onWin(1)
                            }
                        }
                        .testTag("tile_${tile.title}"),
                    shape = RoundedCornerShape(20.dp),
                    color = tile.color,
                    shadowElevation = 3.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(tile.icon, fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            tile.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B)
                        )
                    }
                }
            }
        }
    }
}
