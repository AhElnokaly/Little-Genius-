package com.example.littlegenius.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.littlegenius.model.UserProfile
import com.example.littlegenius.ui.theme.Amber400
import com.example.littlegenius.ui.theme.SkyBlue600

private val GAME_NAMES = mapOf(
    "balloon" to "بالونات",
    "animal" to "حيوانات",
    "draw" to "رسم حر",
    "color" to "ألوان",
    "sorter" to "أشكال",
    "piano" to "بيانو",
    "fish" to "سمك",
    "jigsaw" to "تركيب",
    "memory" to "ذاكرة",
    "arabic" to "حروف عربي",
    "english" to "حروف English",
    "nature" to "طبيعة",
    "counting" to "عد الأشياء",
    "lettermatch" to "توصيل حروف",
    "healthyfood" to "أكل صحي",
    "house" to "بيتي",
    "animalfamily" to "عائلات",
    "numbers" to "أرقام 123",
    "time" to "الوقت",
    "drawshapes" to "ارسم شكل",
    "moon" to "القمر",
    "coloring" to "تلوين",
    "guesssound" to "خمن الصوت",
    "simplemath" to "حساب بسيط",
    "tashkeel" to "تشكيل الحروف",
    "letteranimal" to "حروف وحيوانات",
    "wordbuilder" to "تكوين الكلمات",
    "stickers" to "الملصقات"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentsDashboardScreen(
    profile: UserProfile,
    onBack: () -> Unit
) {
    val totalGamesPlayed = profile.stats.values.sumOf { it.played }
    val totalStarsEarned = profile.stats.values.sumOf { it.stars }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📊 لوحة متابعة الآباء", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("dashboard_back_button")) {
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
                .background(Color(0xFFF8FAFC))
                .padding(16.dp)
        ) {
            // Stats summary row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Games played card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp).background(Color(0xFFBAE6FD), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Gamepad, contentDescription = null, tint = SkyBlue600)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("إجمالي الألعاب", fontSize = 12.sp, color = Color(0xFF0369A1), fontWeight = FontWeight.Bold)
                            Text("$totalGamesPlayed", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFF0C4A6E))
                        }
                    }
                }

                // Stars earned card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp).background(Color(0xFFFDE68A), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Amber400)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("النجوم المكتسبة", fontSize = 12.sp, color = Color(0xFFB45309), fontWeight = FontWeight.Bold)
                            Text("$totalStarsEarned", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFF78350F))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Domain Skills Breakdown
            val langPlayed = (profile.stats["arabic"]?.played ?: 0) + (profile.stats["tashkeel"]?.played ?: 0) +
                    (profile.stats["wordbuilder"]?.played ?: 0) + (profile.stats["english"]?.played ?: 0) + (profile.stats["lettermatch"]?.played ?: 0)
            val mathPlayed = (profile.stats["counting"]?.played ?: 0) + (profile.stats["simplemath"]?.played ?: 0) +
                    (profile.stats["numbers"]?.played ?: 0) + (profile.stats["sorter"]?.played ?: 0)
            val logicPlayed = (profile.stats["puzzle"]?.played ?: 0) + (profile.stats["maze"]?.played ?: 0) +
                    (profile.stats["memory"]?.played ?: 0) + (profile.stats["jigsaw"]?.played ?: 0)
            val creativePlayed = (profile.stats["draw"]?.played ?: 0) + (profile.stats["colorbynum"]?.played ?: 0) +
                    (profile.stats["coloring"]?.played ?: 0) + (profile.stats["piano"]?.played ?: 0) + (profile.stats["drumband"]?.played ?: 0)

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("💡 مجالات المهارات المكتسبة", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📖 لغات", fontSize = 12.sp, color = Color.Gray)
                            Text("$langPlayed", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFF0284C7))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔢 حساب", fontSize = 12.sp, color = Color.Gray)
                            Text("$mathPlayed", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFFD97706))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🧩 منطق", fontSize = 12.sp, color = Color.Gray)
                            Text("$logicPlayed", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFF059669))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎨 إبداع", fontSize = 12.sp, color = Color.Gray)
                            Text("$creativePlayed", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFF9333EA))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("تفاصيل نشاط الطفل", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF334155))
            Spacer(modifier = Modifier.height(10.dp))

            if (profile.stats.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("لم يتم لعب أي ألعاب بعد.", color = Color.Gray, fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val entries = profile.stats.entries.sortedByDescending { it.value.played }
                    items(entries) { entry ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = GAME_NAMES[entry.key] ?: entry.key,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("لعب", fontSize = 11.sp, color = Color.Gray)
                                        Text("${entry.value.played}", fontWeight = FontWeight.Bold, color = SkyBlue600)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("نجوم", fontSize = 11.sp, color = Color.Gray)
                                        Text("${entry.value.stars}", fontWeight = FontWeight.Bold, color = Amber400)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
