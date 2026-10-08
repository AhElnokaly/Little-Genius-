package com.example.littlegenius.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.littlegenius.ui.theme.Amber400
import kotlinx.coroutines.delay

@Composable
fun WinCelebrationOverlay(
    starsCount: Int = 1,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "stars_anim")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        delay(3000)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() }
            .testTag("win_celebration_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color.White,
            tonalElevation = 12.dp,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(0.9f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = "🌟".repeat(starsCount.coerceIn(1, 3)),
                    fontSize = 48.sp,
                    modifier = Modifier.scale(scale)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "أحسنت يا بطل! 🎉",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Amber400,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "لقد حصلت على $starsCount ${if (starsCount > 1) "نجوم" else "نجمة"} إضافية!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Amber400),
                    modifier = Modifier.testTag("celebration_continue_button")
                ) {
                    Text(
                        text = "متابعة اللعب 🚀",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF78350F)
                    )
                }
            }
        }
    }
}
