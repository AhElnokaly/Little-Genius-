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
import com.example.littlegenius.R

data class AnimalInfo(
    val id: String,
    val name: String,
    val emoji: String,
    val soundName: String,
    val rawResId: Int,
    val color: Color
)

val ANIMAL_LIST = listOf(
    AnimalInfo("cat", "القطة", "🐱", "مياو", R.raw.cat, Color(0xFFFDBA74)),
    AnimalInfo("dog", "الكلب", "🐶", "هو هو", R.raw.dog, Color(0xFF93C5FD)),
    AnimalInfo("cow", "البقرة", "🐮", "موو", R.raw.cow, Color(0xFF86EFAC)),
    AnimalInfo("duck", "البطة", "🦆", "واك واك", R.raw.duck, Color(0xFFFDE047)),
    AnimalInfo("sheep", "الخروف", "🐑", "باع باع", R.raw.sheep, Color(0xFFCBD5E1)),
    AnimalInfo("frog", "الضفدع", "🐸", "نقيق", R.raw.frog, Color(0xFF4ADE80)),
    AnimalInfo("bird", "العصفور", "🐦", "تويت تويت", R.raw.bird, Color(0xFF7DD3FC)),
    AnimalInfo("monkey", "القرد", "🐵", "أو أو", R.raw.monkey, Color(0xFFFCD34D)),
    AnimalInfo("horse", "الحصان", "🐴", "صهيل", R.raw.horse, Color(0xFFF59E0B)),
    AnimalInfo("elephant", "الفيل", "🐘", "نهيم", R.raw.elephant, Color(0xFF94A3B8)),
    AnimalInfo("lion", "الأسد", "🦁", "زئير", R.raw.lion, Color(0xFFFB923C)),
    AnimalInfo("rooster", "الديك", "🐔", "كوكو كوكو", R.raw.rooster, Color(0xFFFCA5A5)),
    AnimalInfo("owl", "البومة", "🦉", "هو هو", R.raw.owl, Color(0xFFA5B4FC)),
    AnimalInfo("bee", "النحلة", "🐝", "بززز", R.raw.bee, Color(0xFFFEF08A)),
    AnimalInfo("wolf", "الذئب", "🐺", "عواء", R.raw.wolf, Color(0xFF64748B)),
    AnimalInfo("turkey", "الديك الرومي", "🦃", "قرقرة", R.raw.turkey, Color(0xFFEA580C)),
    AnimalInfo("mouse", "الفأر", "🐭", "صرير", R.raw.mouse, Color(0xFFD6D3D1)),
    AnimalInfo("dolphin", "الدلفين", "🐬", "صفير", R.raw.dolphin, Color(0xFF67E8F9)),
    AnimalInfo("goat", "الماعز", "🐐", "ماع ماع", R.raw.goat, Color(0xFFA8A29E)),
    AnimalInfo("penguin", "البطريق", "🐧", "صياح", R.raw.penguin, Color(0xFFBAE6FD))
)

@Composable
fun AnimalFriendsScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onPlayAnimal: (AnimalInfo) -> Unit
) {
    val tappedAnimals = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0FDF4))
            .testTag("animal_friends_screen")
    ) {
        GameHeader(
            title = "أصدقاء الحيوانات 🐶",
            onBack = onBack,
            starsCount = starsCount
        )

        Text(
            text = "المس الحيوان لتسمع صوته الجميل 🎵",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF166534),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(ANIMAL_LIST) { animal ->
                AnimalCard(
                    animal = animal,
                    onClick = {
                        onPlayAnimal(animal)
                        if (!tappedAnimals.contains(animal.id)) {
                            tappedAnimals.add(animal.id)
                            if (tappedAnimals.size % 4 == 0) {
                                onWin(1)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AnimalCard(
    animal: AnimalInfo,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .aspectRatio(1f)
            .border(3.dp, Color.White, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .testTag("animal_card_${animal.id}"),
        shape = RoundedCornerShape(20.dp),
        color = animal.color,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = animal.emoji,
                fontSize = 44.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = animal.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B)
            )
        }
    }
}
