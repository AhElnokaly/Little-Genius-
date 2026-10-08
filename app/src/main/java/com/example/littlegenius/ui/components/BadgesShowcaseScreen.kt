package com.example.littlegenius.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.littlegenius.model.UserProfile
import com.example.littlegenius.ui.theme.Amber400
import com.example.littlegenius.ui.theme.Emerald500

data class BadgeItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: (UserProfile, Int) -> Boolean
)

val ALL_BADGES = listOf(
    BadgeItem(
        "explorer", "المستكشف الصغير", "لعب 3 ألعاب مختلفة", "🌟",
        { p, _ -> p.stats.size >= 3 }
    ),
    BadgeItem(
        "adventurer", "المغامر الشجاع", "تجربة 10 ألعاب مختلفة", "🧭",
        { p, _ -> p.stats.size >= 10 }
    ),
    BadgeItem(
        "arabic_scholar", "فصيح لغتي", "تعلم الحروف العربية وتشكيلها", "📖",
        { p, _ -> (p.stats["arabic"]?.played ?: 0) + (p.stats["tashkeel"]?.played ?: 0) + (p.stats["wordbuilder"]?.played ?: 0) >= 1 }
    ),
    BadgeItem(
        "english_explorer", "English Star", "إتقان الحروف الإنجليزية", "🔤",
        { p, _ -> (p.stats["english"]?.played ?: 0) >= 1 }
    ),
    BadgeItem(
        "math_star", "نجم الحساب والعد", "حل مسائل الجمع والعد", "🔢",
        { p, _ -> (p.stats["counting"]?.played ?: 0) + (p.stats["simplemath"]?.played ?: 0) + (p.stats["numbers"]?.played ?: 0) >= 1 }
    ),
    BadgeItem(
        "puzzle_master", "بطل البازل", "إكمال بازل الصور والتركيب", "🧩",
        { p, _ -> (p.stats["puzzle"]?.played ?: 0) + (p.stats["jigsaw"]?.played ?: 0) >= 1 }
    ),
    BadgeItem(
        "maze_champ", "عبقري المتاهة", "اجتياز متاهة الأبطال", "🌀",
        { p, _ -> (p.stats["maze"]?.played ?: 0) >= 1 }
    ),
    BadgeItem(
        "memory_genius", "قوة الذاكرة", "الفوز في لعبة الذاكرة", "🧠",
        { p, _ -> (p.stats["memory"]?.played ?: 0) >= 1 }
    ),
    BadgeItem(
        "musician", "الموسيقي المبدع", "عزف البيانو أو الطبول", "🎵",
        { p, _ -> (p.stats["piano"]?.played ?: 0) + (p.stats["drumband"]?.played ?: 0) >= 1 }
    ),
    BadgeItem(
        "artist", "الفنان الصغير", "تلوين ورسم بالألوان", "🎨",
        { p, _ -> (p.stats["draw"]?.played ?: 0) + (p.stats["colorbynum"]?.played ?: 0) + (p.stats["coloring"]?.played ?: 0) >= 1 }
    ),
    BadgeItem(
        "nature_friend", "صديق البيئة", "فرز النفايات والتدوير وحماية البيئة", "🌱",
        { p, _ -> (p.stats["recycle"]?.played ?: 0) >= 1 }
    ),
    BadgeItem(
        "healthy_kid", "الصحي النشيط", "اختيار الأطعمة المفيدة للجسم", "🍎",
        { p, _ -> (p.stats["healthyfood"]?.played ?: 0) >= 1 }
    ),
    BadgeItem(
        "fisherman", "صياد البحار", "اصطياد الأسماك والكنوز", "🐟",
        { p, _ -> (p.stats["fish"]?.played ?: 0) >= 1 }
    ),
    BadgeItem(
        "balloon_popper", "ملك البالونات", "فرقعة البالونات التعليمية", "🎈",
        { p, _ -> (p.stats["balloon"]?.played ?: 0) >= 1 }
    ),
    BadgeItem(
        "champion_bronze", "نجم البداية 🥉", "جمع 10 نجوم ذهبية", "🥉",
        { _, stars -> stars >= 10 }
    ),
    BadgeItem(
        "champion_silver", "بطل النجوم 🥈", "جمع 25 نجمة ذهبية", "🥈",
        { _, stars -> stars >= 25 }
    ),
    BadgeItem(
        "champion_gold", "كأس العبقري الكبير 🥇", "جمع 50 نجمة أو أكثر", "🏆",
        { _, stars -> stars >= 50 }
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgesShowcaseScreen(
    profile: UserProfile,
    starsCount: Int,
    onBack: () -> Unit,
    onSpeak: (String) -> Unit = {}
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val unlockedCount = ALL_BADGES.count { it.isUnlocked(profile, starsCount) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("لوحة الأوسمة والإنجازات 🏆", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("badges_back_btn")) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFFEFCE8))
                    .padding(16.dp)
            ) {
                // Header Trophy Progress Banner
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(3.dp, Amber400, RoundedCornerShape(24.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "أوسمة البطل ${profile.name.ifBlank { "الصغير" }} 🌟",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF78350F)
                            )
                            Text(
                                text = "فتحت $unlockedCount من أصل ${ALL_BADGES.size} أوسمة",
                                fontSize = 13.sp,
                                color = Color(0xFF92400E)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFFFEF3C7), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏆", fontSize = 32.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(ALL_BADGES) { badge ->
                        val unlocked = badge.isUnlocked(profile, starsCount)
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (unlocked) Color.White else Color(0xFFE2E8F0),
                            shadowElevation = if (unlocked) 4.dp else 1.dp,
                            modifier = Modifier
                                .aspectRatio(0.95f)
                                .border(
                                    width = if (unlocked) 2.dp else 1.dp,
                                    color = if (unlocked) Amber400 else Color.LightGray,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    if (unlocked) {
                                        onSpeak("وسام: ${badge.title}! ${badge.description}. أحسنت!")
                                    } else {
                                        onSpeak("وسام ${badge.title}. لتفتحه: ${badge.description}")
                                    }
                                }
                                .testTag("badge_${badge.id}")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (unlocked) badge.icon else "🔒",
                                    fontSize = 42.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = badge.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (unlocked) Color(0xFF0F172A) else Color(0xFF64748B),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = badge.description,
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = if (unlocked) Emerald500.copy(alpha = 0.15f) else Color.Transparent
                                ) {
                                    Text(
                                        text = if (unlocked) "مُكتسب ✨" else "قيد الإنجاز",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (unlocked) Emerald500 else Color.Gray,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
