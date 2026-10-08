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
import com.example.littlegenius.ui.theme.SkyBlue200
import com.example.littlegenius.ui.theme.SkyBlue600

data class NumberColor(val num: Int, val name: String, val color: Color)
data class Sector(val id: Int, val requiredNum: Int, val label: String)

data class ColorByNumLevel(
    val levelNumber: Int,
    val title: String,
    val emoji: String,
    val colors: List<NumberColor>,
    val sectors: List<Sector>
)

val COLOR_BY_NUM_LEVELS = listOf(
    ColorByNumLevel(
        1, "القطة اللطيفة 🐱", "🐱",
        listOf(
            NumberColor(1, "أصفر", Color(0xFFFDE047)),
            NumberColor(2, "وردي", Color(0xFFF472B6)),
            NumberColor(3, "أزرق", Color(0xFF60A5FA))
        ),
        listOf(
            Sector(1, 1, "الوجه"),
            Sector(2, 2, "الأذن اليمنى"),
            Sector(3, 2, "الأذن اليسرى"),
            Sector(4, 3, "الفيونكة")
        )
    ),
    ColorByNumLevel(
        2, "الصاروخ السريع 🚀", "🚀",
        listOf(
            NumberColor(1, "أحمر", Color(0xFFEF4444)),
            NumberColor(2, "أبيض", Color(0xFFE2E8F0)),
            NumberColor(3, "برتقالي", Color(0xFFF97316)),
            NumberColor(4, "أزرق", Color(0xFF3B82F6))
        ),
        listOf(
            Sector(1, 1, "مقدمة الصاروخ"),
            Sector(2, 2, "جسم الصاروخ"),
            Sector(3, 4, "الأجنحة"),
            Sector(4, 3, "لهب المحرك")
        )
    ),
    ColorByNumLevel(
        3, "الفراشة الزاهية 🦋", "🦋",
        listOf(
            NumberColor(1, "بنفسجي", Color(0xFF8B5CF6)),
            NumberColor(2, "وردي", Color(0xFFEC4899)),
            NumberColor(3, "أخضر", Color(0xFF10B981))
        ),
        listOf(
            Sector(1, 1, "الجناح العلوي الأيمن"),
            Sector(2, 1, "الجناح العلوي الأيسر"),
            Sector(3, 2, "الجناح السفلي الأيمن"),
            Sector(4, 2, "الجناح السفلي الأيسر"),
            Sector(5, 3, "جسم الفراشة")
        )
    ),
    ColorByNumLevel(
        4, "التفاحة اللذيذة 🍎", "🍎",
        listOf(
            NumberColor(1, "أحمر", Color(0xFFDC2626)),
            NumberColor(2, "أخضر", Color(0xFF16A34A)),
            NumberColor(3, "بني", Color(0xFF78350F))
        ),
        listOf(
            Sector(1, 1, "الثمرة"),
            Sector(2, 2, "الورقة"),
            Sector(3, 3, "الغصن")
        )
    ),
    ColorByNumLevel(
        5, "الشمس المشرقة ☀️", "☀️",
        listOf(
            NumberColor(1, "أصفر", Color(0xFFEAB308)),
            NumberColor(2, "برتقالي", Color(0xFFEA580C)),
            NumberColor(3, "أزرق", Color(0xFF38BDF8))
        ),
        listOf(
            Sector(1, 1, "قرص الشمس"),
            Sector(2, 2, "الأشعة"),
            Sector(3, 3, "السماء")
        )
    )
)

@Composable
fun ColorByNumbersScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onPlayChime: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var levelIndex by remember { mutableIntStateOf(0) }
        val currentLevel = COLOR_BY_NUM_LEVELS[levelIndex % COLOR_BY_NUM_LEVELS.size]

        var selectedColorNum by remember(currentLevel) { mutableIntStateOf(currentLevel.colors[0].num) }
        val paintedSectors = remember(currentLevel) { mutableStateMapOf<Int, Color>() }
        val isComplete = currentLevel.sectors.all { paintedSectors.containsKey(it.id) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFEFCE8))
                .testTag("color_by_numbers_screen")
        ) {
            GameHeader(
                title = "تلوين بالأرقام 🖍️",
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
                items(COLOR_BY_NUM_LEVELS) { lvl ->
                    val isSel = lvl.levelNumber == currentLevel.levelNumber
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSel) SkyBlue600 else Color.White,
                        modifier = Modifier
                            .clickable {
                                levelIndex = lvl.levelNumber - 1
                            }
                            .border(
                                width = if (isSel) 2.dp else 1.dp,
                                color = if (isSel) SkyBlue600 else SkyBlue200,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .testTag("color_num_lvl_${lvl.levelNumber}")
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

            // Painting Canvas Area
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
                    .border(3.dp, SkyBlue200, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Text(currentLevel.emoji, fontSize = 64.sp)

                    Text(
                        text = "المس الجزء ذو الرقم ($selectedColorNum) لتلوينه بالفرشاة المختارة",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    // Sector Tiles to color
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        currentLevel.sectors.forEach { sector ->
                            val paintedColor = paintedSectors[sector.id]
                            val isPainted = paintedColor != null

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = paintedColor ?: Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .border(
                                        2.dp,
                                        if (isPainted) Color.Transparent else Color.LightGray,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        if (sector.requiredNum == selectedColorNum) {
                                            val c = currentLevel.colors.first { it.num == selectedColorNum }.color
                                            paintedSectors[sector.id] = c
                                            onPlayChime()

                                            if (currentLevel.sectors.all { paintedSectors.containsKey(it.id) }) {
                                                onWin(2)
                                            }
                                        }
                                    }
                                    .testTag("paint_sector_${sector.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = sector.label,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPainted) Color.White else Color(0xFF334155)
                                    )
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isPainted) Color.White.copy(alpha = 0.3f) else Color.White
                                    ) {
                                        Text(
                                            text = if (isPainted) "✓" else "${sector.requiredNum}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (isPainted) Color.White else Color(0xFF0369A1),
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Color Palette Picker
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .border(2.dp, SkyBlue200, RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    currentLevel.colors.forEach { nc ->
                        val isSel = selectedColorNum == nc.num
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { selectedColorNum = nc.num }
                                .testTag("color_palette_${nc.num}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(nc.color, CircleShape)
                                    .border(
                                        width = if (isSel) 4.dp else 1.dp,
                                        color = if (isSel) Color.Black else Color.White,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${nc.num}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                            Text(nc.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                    }
                }
            }

            if (isComplete && levelIndex < COLOR_BY_NUM_LEVELS.size - 1) {
                Button(
                    onClick = { levelIndex++ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .height(50.dp)
                        .testTag("next_color_num_lvl"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500)
                ) {
                    Text("المرحلة التالية 🚀", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}
