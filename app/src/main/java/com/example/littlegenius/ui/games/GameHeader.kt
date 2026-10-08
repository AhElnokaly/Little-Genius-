package com.example.littlegenius.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun GameHeader(
    title: String,
    onBack: () -> Unit,
    starsCount: Int,
    modifier: Modifier = Modifier,
    trailingAction: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(48.dp)
                .background(Color.White, CircleShape)
                .border(2.dp, SkyBlue200, CircleShape)
                .testTag("game_back_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "رجوع",
                tint = Color(0xFF334155)
            )
        }

        Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.95f),
            shadowElevation = 2.dp,
            modifier = Modifier.border(2.dp, SkyBlue200, CircleShape)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0369A1),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            trailingAction?.invoke()

            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.border(2.dp, Amber400, CircleShape)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "نجوم",
                        tint = Amber400,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$starsCount",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
