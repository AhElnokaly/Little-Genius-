package com.example.littlegenius.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.littlegenius.model.UserProfile
import com.example.littlegenius.ui.theme.SkyBlue200
import com.example.littlegenius.ui.theme.SkyBlue600

private val AVATAR_LIST = listOf("👦", "👧", "🐶", "🐱", "🦁", "🐯", "🐰", "🐼", "🦊", "🐸")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    profile: UserProfile,
    onSave: (UserProfile) -> Unit,
    onBack: () -> Unit,
    onOpenDashboard: () -> Unit
) {
    var name by remember { mutableStateOf(profile.name) }
    var dob by remember { mutableStateOf(profile.dob) }
    var lockEnabled by remember { mutableStateOf(profile.lockEnabled) }
    var playTimeLimit by remember { mutableIntStateOf(profile.playTimeLimit) }
    var avatar by remember { mutableStateOf(profile.avatar) }
    var difficulty by remember { mutableStateOf(profile.difficulty) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "الإعدادات ⚙️",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    IconButton(onClick = onOpenDashboard, modifier = Modifier.testTag("dashboard_nav_button")) {
                        Icon(Icons.Default.BarChart, contentDescription = "لوحة المتابعة", tint = SkyBlue600)
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar Selector
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("اختر شخصيتك", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF334155))
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(AVATAR_LIST) { av ->
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(
                                        if (avatar == av) Color(0xFFBAE6FD) else Color(0xFFF1F5F9),
                                        CircleShape
                                    )
                                    .border(
                                        if (avatar == av) 2.dp else 0.dp,
                                        SkyBlue600,
                                        CircleShape
                                    )
                                    .clickable { avatar = av }
                                    .testTag("avatar_$av"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(av, fontSize = 28.sp)
                            }
                        }
                    }
                }
            }

            // Child Name Input
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("اسم الطفل", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF334155))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth().testTag("child_name_input"),
                        placeholder = { Text("مثال: أحمد") },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Lock Exit Toggle
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("قفل الخروج من الألعاب", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("يتطلب حل مسألة حسابية للمغادرة", fontSize = 12.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = lockEnabled,
                        onCheckedChange = { lockEnabled = it },
                        modifier = Modifier.testTag("lock_exit_switch")
                    )
                }
            }

            // Time Limit in Minutes
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("وقت اللعب المسموح (بالدقائق)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("ضع 0 لإلغاء المؤقت", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(0, 15, 30, 45, 60).forEach { limit ->
                            FilterChip(
                                selected = playTimeLimit == limit,
                                onClick = { playTimeLimit = limit },
                                label = { Text(if (limit == 0) "بدون حد" else "$limit د") }
                            )
                        }
                    }
                }
            }

            // Difficulty Setting
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("مستوى الصعوبة", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            "easy" to "سهل (٣ سنوات)",
                            "medium" to "متوسط (٤-٥)",
                            "hard" to "متقدم (٦+)"
                        ).forEach { (key, label) ->
                            FilterChip(
                                selected = difficulty == key,
                                onClick = { difficulty = key },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }

            // Parents Dashboard Link Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF)),
                modifier = Modifier.fillMaxWidth().clickable { onOpenDashboard() }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("📊 لوحة متابعة الآباء", fontWeight = FontWeight.Bold, color = Color(0xFF4338CA))
                        Text("استعراض الألعاب الملعوبة والنجوم", fontSize = 12.sp, color = Color(0xFF6366F1))
                    }
                    Button(
                        onClick = onOpenDashboard,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                    ) {
                        Text("عرض")
                    }
                }
            }

            // Save Button
            Button(
                onClick = {
                    onSave(
                        profile.copy(
                            name = name,
                            dob = dob,
                            lockEnabled = lockEnabled,
                            playTimeLimit = playTimeLimit,
                            avatar = avatar,
                            difficulty = difficulty
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("save_settings_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue600)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("حفظ التغييرات", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
