package com.example.littlegenius.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.littlegenius.ui.theme.Amber400
import com.example.littlegenius.ui.theme.SkyBlue600

@Composable
fun TimeLockOverlay(
    onUnlockRequest: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xE60F172A))
            .testTag("time_lock_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "⏰",
                fontSize = 80.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "انتهى وقت اللعب لليوم!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Amber400,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "حان وقت أخذ استراحة مفيدة واللعب مع العائلة 🌟",
                fontSize = 18.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onUnlockRequest,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SkyBlue600,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .height(56.dp)
                    .testTag("unlock_time_button")
            ) {
                Text(
                    text = "فتح القفل (للأهل)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
