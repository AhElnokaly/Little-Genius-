package com.example.littlegenius.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.littlegenius.ui.theme.SkyBlue200
import com.example.littlegenius.ui.theme.SkyBlue600
import kotlin.random.Random

@Composable
fun ParentalGateDialog(
    onSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    val num1 by remember { mutableIntStateOf(Random.nextInt(1, 6)) }
    val num2 by remember { mutableIntStateOf(Random.nextInt(1, 6)) }
    val correctAnswer = num1 + num2

    val options = remember(correctAnswer) {
        val list = mutableListOf(correctAnswer, correctAnswer + 1, (correctAnswer - 1).coerceAtLeast(1))
        list.distinct().shuffled()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(4.dp, SkyBlue200, RoundedCornerShape(28.dp))
                .testTag("parental_gate_dialog")
        ) {
            Box(modifier = Modifier.padding(24.dp)) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(Color(0xFFF1F5F9), CircleShape)
                        .testTag("close_gate_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.Gray)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text(
                        text = "للوالدين فقط 👨‍👩‍👧",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "الرجاء حل المسألة للمتابعة:",
                        fontSize = 16.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "$num1 + $num2 = ?",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = SkyBlue600
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        options.forEach { opt ->
                            Button(
                                onClick = {
                                    if (opt == correctAnswer) {
                                        onSuccess()
                                    } else {
                                        onDismiss()
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE0F2FE),
                                    contentColor = SkyBlue600
                                ),
                                modifier = Modifier
                                    .size(64.dp)
                                    .testTag("option_button_$opt"),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "$opt",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
