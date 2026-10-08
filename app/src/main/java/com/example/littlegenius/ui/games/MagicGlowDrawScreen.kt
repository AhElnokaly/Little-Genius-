package com.example.littlegenius.ui.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DrawPoint(val offset: Offset, val color: Color, val strokeWidth: Float)

private val GLOW_COLORS = listOf(
    Color(0xFFFF007F), // Neon Pink
    Color(0xFF00F5FF), // Neon Cyan
    Color(0xFF39FF14), // Neon Green
    Color(0xFFFFE600), // Neon Yellow
    Color(0xFFFF5722), // Neon Orange
    Color(0xFFB026FF), // Neon Purple
    Color(0xFFFFFFFF)  // Bright White
)

@Composable
fun MagicGlowDrawScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit
) {
    var currentColor by remember { mutableStateOf(GLOW_COLORS[0]) }
    val paths = remember { mutableStateListOf<List<DrawPoint>>() }
    var currentPath by remember { mutableStateOf<List<DrawPoint>>(emptyList()) }
    var strokeCount by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .testTag("magic_glow_draw_screen")
    ) {
        GameHeader(
            title = "الرسم السحري ✨",
            onBack = onBack,
            starsCount = starsCount,
            trailingAction = {
                IconButton(
                    onClick = {
                        paths.clear()
                        currentPath = emptyList()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White, CircleShape)
                        .testTag("clear_draw_button")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "مسح", tint = Color.Red)
                }
            }
        )

        // Drawing Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, bottom = 90.dp)
                .pointerInput(currentColor) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPath = listOf(DrawPoint(offset, currentColor, 16f))
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            currentPath = currentPath + DrawPoint(change.position, currentColor, 16f)
                        },
                        onDragEnd = {
                            if (currentPath.isNotEmpty()) {
                                paths.add(currentPath)
                                currentPath = emptyList()
                                strokeCount++
                                if (strokeCount % 6 == 0) {
                                    onWin(1)
                                }
                            }
                        }
                    )
                }
                .testTag("drawing_canvas")
        ) {
            val allPaths = paths + listOfNotNull(currentPath.takeIf { it.isNotEmpty() })
            allPaths.forEach { stroke ->
                for (i in 0 until stroke.size - 1) {
                    val p1 = stroke[i]
                    val p2 = stroke[i + 1]

                    // Outer glow
                    drawLine(
                        color = p1.color.copy(alpha = 0.35f),
                        start = p1.offset,
                        end = p2.offset,
                        strokeWidth = p1.strokeWidth * 2.2f,
                        cap = StrokeCap.Round
                    )
                    // Inner bright stroke
                    drawLine(
                        color = p1.color,
                        start = p1.offset,
                        end = p2.offset,
                        strokeWidth = p1.strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        // Color Palette at bottom
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1E293B).copy(alpha = 0.9f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
                .border(2.dp, Color(0xFF334155), RoundedCornerShape(24.dp))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GLOW_COLORS.forEach { c ->
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(c, CircleShape)
                            .border(
                                width = if (currentColor == c) 3.dp else 1.dp,
                                color = if (currentColor == c) Color.White else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { currentColor = c }
                            .testTag("color_btn_${c.value}")
                    )
                }
            }
        }
    }
}
