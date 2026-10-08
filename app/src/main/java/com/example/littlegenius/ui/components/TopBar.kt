package com.example.littlegenius.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
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
import com.example.littlegenius.ui.theme.Amber400
import com.example.littlegenius.ui.theme.SkyBlue200

@Composable
fun TopBar(
    avatar: String,
    childName: String,
    starsCount: Int,
    onSettingsClick: () -> Unit,
    onTrophiesClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("app_top_bar"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            // Settings Button
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(46.dp)
                    .background(Color.White, CircleShape)
                    .border(2.dp, SkyBlue200, CircleShape)
                    .testTag("settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "الإعدادات",
                    tint = Color(0xFF64748B)
                )
            }

            // Trophies / Badges Button
            IconButton(
                onClick = onTrophiesClick,
                modifier = Modifier
                    .size(46.dp)
                    .background(Color(0xFFFEF3C7), CircleShape)
                    .border(2.dp, Amber400, CircleShape)
                    .testTag("trophies_button")
            ) {
                Text("🏆", fontSize = 22.sp)
            }
        }

        // Child Greeting with Avatar
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.border(2.dp, SkyBlue200, RoundedCornerShape(24.dp))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = avatar,
                    fontSize = 28.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = if (childName.isNotBlank()) "أهلاً $childName! 🚀" else "أهلاً يا بطل! 🚀",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0369A1)
                )
            }
        }

        // Stars counter badge
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier
                .border(2.dp, Amber400, RoundedCornerShape(20.dp))
                .testTag("stars_badge")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "النجوم",
                    tint = Amber400,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$starsCount",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFB45309)
                )
            }
        }
    }
}
