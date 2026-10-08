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

data class NumberItem(
    val arNum: String,
    val enNum: String,
    val arWord: String,
    val countEmoji: String,
    val count: Int,
    val color: Color
)

val BILINGUAL_NUMBERS = listOf(
    NumberItem("١", "1", "وَاحِد", "🍎", 1, Color(0xFFFCA5A5)),
    NumberItem("٢", "2", "اِثْنَان", "🚗", 2, Color(0xFFFDBA74)),
    NumberItem("٣", "3", "ثَلاثَة", "⭐", 3, Color(0xFFFDE047)),
    NumberItem("٤", "4", "أَرْبَعَة", "🌸", 4, Color(0xFF86EFAC)),
    NumberItem("٥", "5", "خَمْسَة", "🎈", 5, Color(0xFF67E8F9)),
    NumberItem("٦", "6", "سِتَّة", "🐟", 6, Color(0xFF93C5FD)),
    NumberItem("٧", "7", "سَبْعَة", "🦋", 7, Color(0xFFA5B4FC)),
    NumberItem("٨", "8", "ثَمَانِيَة", "🐥", 8, Color(0xFFC4B5FD)),
    NumberItem("٩", "9", "تِسْعَة", "⚽", 9, Color(0xFFF472B6)),
    NumberItem("١٠", "10", "عَشَرَة", "🚀", 10, Color(0xFFFB7185))
)

@Composable
fun NumberBilingualScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    var selectedNumber by remember { mutableStateOf(BILINGUAL_NUMBERS[0]) }
    var practicedCount by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F9FF))
            .testTag("number_bilingual_screen")
    ) {
        GameHeader(
            title = "أرقام 123 🔢",
            onBack = onBack,
            starsCount = starsCount
        )

        // Showcase Card
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = selectedNumber.color,
            shadowElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .border(3.dp, Color.White, RoundedCornerShape(28.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedNumber.arNum,
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0C4A6E)
                    )
                    Text(
                        text = selectedNumber.enNum,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0C4A6E).copy(alpha = 0.8f)
                    )
                    Text(
                        text = selectedNumber.arWord,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0C4A6E)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Visual items
                Text(
                    text = selectedNumber.countEmoji.repeat(selectedNumber.count),
                    fontSize = 26.sp,
                    lineHeight = 32.sp
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(BILINGUAL_NUMBERS) { item ->
                Surface(
                    modifier = Modifier
                        .aspectRatio(0.85f)
                        .border(
                            width = if (selectedNumber == item) 3.dp else 1.dp,
                            color = if (selectedNumber == item) SkyBlue600 else Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            selectedNumber = item
                            onSpeak("${item.arWord} .. ${item.arNum}")
                            practicedCount++
                            if (practicedCount % 4 == 0) {
                                onWin(1)
                            }
                        }
                        .testTag("number_item_${item.enNum}"),
                    shape = RoundedCornerShape(16.dp),
                    color = item.color,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = item.arNum,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = item.enNum,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                    }
                }
            }
        }
    }
}
