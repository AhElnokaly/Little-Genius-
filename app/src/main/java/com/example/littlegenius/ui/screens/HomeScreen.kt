package com.example.littlegenius.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.littlegenius.model.GameCategory
import com.example.littlegenius.model.GameItem
import com.example.littlegenius.ui.components.TopBar
import com.example.littlegenius.ui.theme.SkyBlue50

val ALL_GAME_CATEGORIES = listOf(
    GameCategory(
        title = "الحروف والأرقام 🔢",
        games = listOf(
            GameItem("arabic", "حروف عربي", "أ", 0xFF2DD4BF),
            GameItem("tashkeel", "تشكيل الحروف", "َُِ", 0xFF818CF8),
            GameItem("wordbuilder", "تكوين الكلمات", "📝", 0xFF10B981),
            GameItem("english", "حروف English", "A", 0xFFFB7185),
            GameItem("letteranimal", "حروف وحيوانات", "🦁", 0xFFF59E0B),
            GameItem("numbers", "أرقام 123", "١", 0xFF38BDF8),
            GameItem("counting", "عد الأشياء", "🔢", 0xFFFBBF24),
            GameItem("lettermatch", "توصيل حروف", "🔤", 0xFFA78BFA),
            GameItem("simplemath", "حساب بسيط", "➕", 0xFFE879F9)
        )
    ),
    GameCategory(
        title = "عالمي الصغير 🌍",
        games = listOf(
            GameItem("house", "بيتي", "🏠", 0xFFFCD34D),
            GameItem("healthyfood", "أكل صحي", "🍎", 0xFFA3E635),
            GameItem("nature", "طبيعة", "🌿", 0xFF22C55E),
            GameItem("animalfamily", "عائلات", "🐾", 0xFFFB923C),
            GameItem("animal", "حيوانات", "🐶", 0xFF4ADE80),
            GameItem("guesssound", "خمن الصوت", "👂", 0xFFFACC15),
            GameItem("recycle", "الفرز والتدوير", "♻️", 0xFF10B981),
            GameItem("time", "الوقت", "⏰", 0xFF3B82F6),
            GameItem("moon", "القمر", "🌙", 0xFF475569)
        )
    ),
    GameCategory(
        title = "ألعاب ومهارات 🎨",
        games = listOf(
            GameItem("drumband", "عازف الإيقاع", "🥁", 0xFFEF4444),
            GameItem("colorbynum", "تلوين بالأرقام", "🖍️", 0xFFEAB308),
            GameItem("puzzle", "بازل الصور", "🧩", 0xFF10B981),
            GameItem("maze", "متاهة الأبطال", "🌀", 0xFF6366F1),
            GameItem("balloon", "بالونات", "🎈", 0xFFF87171),
            GameItem("draw", "رسم حر", "✨", 0xFFC084FC),
            GameItem("drawshapes", "ارسم شكل", "✏️", 0xFF6366F1),
            GameItem("color", "ألوان", "🎨", 0xFF60A5FA),
            GameItem("stickers", "الملصقات", "🖼️", 0xFFF59E0B),
            GameItem("coloring", "تلوين", "🖍️", 0xFFFB7185),
            GameItem("sorter", "أشكال", "🧩", 0xFFFB923C),
            GameItem("piano", "بيانو", "🎹", 0xFFF472B6),
            GameItem("fish", "سمك", "🐟", 0xFF22D3EE),
            GameItem("jigsaw", "تركيب", "🧩", 0xFF34D399),
            GameItem("memory", "ذاكرة", "🧠", 0xFF818CF8)
        )
    )
)

@Composable
fun HomeScreen(
    avatar: String,
    childName: String,
    starsCount: Int,
    onSelectGame: (GameItem) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenTrophies: () -> Unit = {}
) {
    CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
        Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SkyBlue50)
            .testTag("home_screen")
    ) {
        TopBar(
            avatar = avatar,
            childName = childName,
            starsCount = starsCount,
            onSettingsClick = onOpenSettings,
            onTrophiesClick = onOpenTrophies
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Spotlight: Game of the Day & Random Game Action
            item {
                val allGames = remember { ALL_GAME_CATEGORIES.flatMap { it.games } }
                val gameOfTheDay = remember {
                    // Stable per calendar day or default fun pick
                    val dayIndex = (System.currentTimeMillis() / (1000 * 60 * 60 * 24)).toInt()
                    allGames[Math.floorMod(dayIndex, allGames.size)]
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Game of the day card
                    Surface(
                        modifier = Modifier
                            .weight(1.3f)
                            .clickable { onSelectGame(gameOfTheDay) }
                            .testTag("game_of_the_day_card"),
                        shape = RoundedCornerShape(26.dp),
                        color = Color(0xFFFEF3C7),
                        border = androidx.compose.foundation.BorderStroke(2.5.dp, Color(0xFFF59E0B)),
                        shadowElevation = 5.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF59E0B)
                                ) {
                                    Text(
                                        text = "⭐ تحدي اليوم ⭐",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = gameOfTheDay.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF78350F)
                                )
                                Text(
                                    text = "العب الآن واكسب نجوماً!",
                                    fontSize = 11.sp,
                                    color = Color(0xFF92400E)
                                )
                            }
                            Text(
                                text = gameOfTheDay.icon,
                                fontSize = 42.sp,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }
                    }

                    // Random game roulette button
                    Surface(
                        modifier = Modifier
                            .weight(0.7f)
                            .clickable {
                                val randomGame = allGames.random()
                                onSelectGame(randomGame)
                            }
                            .testTag("random_game_card"),
                        shape = RoundedCornerShape(26.dp),
                        color = Color(0xFFEDE9FE),
                        border = androidx.compose.foundation.BorderStroke(2.5.dp, Color(0xFF8B5CF6)),
                        shadowElevation = 5.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "🎲", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "العب بالحظ!",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF5B21B6),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            items(ALL_GAME_CATEGORIES) { category ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = category.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0369A1),
                        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                    )

                    // Grid of 3 columns
                    val rows = category.games.chunked(3)
                    rows.forEach { rowGames ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowGames.forEach { game ->
                                GameCard(
                                    game = game,
                                    modifier = Modifier.weight(1f),
                                    onClick = { onSelectGame(game) }
                                )
                            }
                            // Fill remaining spaces in row if less than 3
                            repeat(3 - rowGames.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
fun GameCard(
    game: GameItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .aspectRatio(0.95f)
            .border(3.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .testTag("game_card_${game.id}"),
        shape = RoundedCornerShape(24.dp),
        color = Color(game.colorHex),
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.25f), Color.Transparent)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(6.dp)
            ) {
                Text(
                    text = game.icon,
                    fontSize = 40.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = game.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}
