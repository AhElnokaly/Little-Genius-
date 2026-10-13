package com.example.littlegenius.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.littlegenius.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    AnimalInfo("mouse", "الفأر", "🐭", "صرير", R.raw.mouse, Color(0xFFD6D3E1)),
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
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val coroutineScope = rememberCoroutineScope()
        val tappedAnimals = remember { mutableStateListOf<String>() }
        var activePlayingAnimalId by remember { mutableStateOf<String?>(null) }

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
                text = "المس أي حيوان لتسمع صوته الحقيقي في الطبيعة! 🔊",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF166534),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(ANIMAL_LIST) { animal ->
                    AnimalCard(
                        animal = animal,
                        isPlaying = activePlayingAnimalId == animal.id,
                        onClick = {
                            activePlayingAnimalId = animal.id
                            onPlayAnimal(animal)
                            coroutineScope.launch {
                                delay(1800)
                                if (activePlayingAnimalId == animal.id) {
                                    activePlayingAnimalId = null
                                }
                            }
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
}

@Composable
fun AnimalCard(
    animal: AnimalInfo,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val cardScale = remember { Animatable(1f) }

    Surface(
        modifier = Modifier
            .aspectRatio(0.9f)
            .graphicsLayer {
                scaleX = cardScale.value
                scaleY = cardScale.value
            }
            .border(
                width = if (isPlaying) 3.5.dp else 2.5.dp,
                color = if (isPlaying) Color(0xFFFACC15) else Color.White,
                shape = RoundedCornerShape(22.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                coroutineScope.launch {
                    cardScale.animateTo(1.15f, tween(100))
                    cardScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                }
                onClick()
            }
            .testTag("animal_card_${animal.id}"),
        shape = RoundedCornerShape(22.dp),
        color = animal.color,
        shadowElevation = if (isPlaying) 8.dp else 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = animal.emoji,
                fontSize = 42.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = animal.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = animal.soundName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}
