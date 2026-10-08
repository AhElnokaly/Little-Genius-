package com.example.littlegenius.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.littlegenius.ui.theme.Amber400
import com.example.littlegenius.ui.theme.Emerald500
import com.example.littlegenius.ui.theme.Indigo600
import com.example.littlegenius.ui.theme.SkyBlue600
import kotlin.math.roundToInt
import kotlin.random.Random

data class StickerItem(val emoji: String, val name: String)
data class PlacedSticker(val id: Long, val emoji: String, var x: Float, var y: Float)

val STICKER_LIST = listOf(
    StickerItem("🌟", "نجمة"),
    StickerItem("🚗", "سيارة"),
    StickerItem("🦖", "ديناصور"),
    StickerItem("🦄", "وحيد القرن"),
    StickerItem("🚀", "صاروخ"),
    StickerItem("🐱", "قطة"),
    StickerItem("🎈", "بالون"),
    StickerItem("🍎", "تفاحة"),
    StickerItem("⚽", "كرة"),
    StickerItem("🎨", "ألوان"),
    StickerItem("🌳", "شجرة"),
    StickerItem("☀️", "شمس"),
    StickerItem("☁️", "سحابة"),
    StickerItem("🏠", "بيت"),
    StickerItem("🦋", "فراشة")
)

@Composable
fun StickerBookScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    var mode by remember { mutableStateOf("builder") } // "builder", "shadow", "sound"
    val placedStickers = remember { mutableStateListOf<PlacedSticker>() }

    // Shadow & Sound targets
    var shadowTarget by remember { mutableStateOf(STICKER_LIST[0]) }
    val shadowOptions = remember(shadowTarget) {
        val list = mutableListOf(shadowTarget)
        val others = STICKER_LIST.filter { it != shadowTarget }.shuffled()
        list.add(others[0])
        list.add(others[1])
        list.shuffled()
    }

    var soundTarget by remember { mutableStateOf(STICKER_LIST[2]) }
    val soundOptions = remember(soundTarget) {
        val list = mutableListOf(soundTarget)
        val others = STICKER_LIST.filter { it != soundTarget }.shuffled()
        list.add(others[0])
        list.add(others[1])
        list.shuffled()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F2FE))
            .testTag("sticker_book_screen")
    ) {
        GameHeader(
            title = "كتاب الملصقات 🖼️",
            onBack = onBack,
            starsCount = starsCount,
            trailingAction = {
                if (mode == "builder" && placedStickers.isNotEmpty()) {
                    IconButton(
                        onClick = { placedStickers.clear() },
                        modifier = Modifier.size(44.dp).background(Color.White, CircleShape).testTag("clear_stickers_btn")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "مسح", tint = Color.Red)
                    }
                }
            }
        )

        // Mode switch tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 2.dp
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    listOf(
                        "builder" to "لوحتي 🖼️",
                        "shadow" to "طابق الظل 🧩",
                        "sound" to "اسمع واختار 👂"
                    ).forEach { (m, title) ->
                        val isSel = mode == m
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSel) Amber400 else Color.Transparent,
                            modifier = Modifier
                                .clickable { mode = m }
                                .testTag("sticker_mode_$m")
                        ) {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color(0xFF78350F) else Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        when (mode) {
            "builder" -> {
                // Sticker Palette
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(STICKER_LIST) { s ->
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier
                                .size(56.dp)
                                .clickable {
                                    placedStickers.add(
                                        PlacedSticker(
                                            System.currentTimeMillis(),
                                            s.emoji,
                                            Random.nextFloat() * 400f + 100f,
                                            Random.nextFloat() * 400f + 150f
                                        )
                                    )
                                    onSpeak(s.name)
                                    if (placedStickers.size % 4 == 0) {
                                        onWin(1)
                                    }
                                }
                                .testTag("add_sticker_${s.emoji}"),
                            shadowElevation = 2.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(s.emoji, fontSize = 32.sp)
                            }
                        }
                    }
                }

                // Interactive Scenery Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
                        .background(
                            Color(0xFFBAE6FD),
                            RoundedCornerShape(24.dp)
                        )
                        .border(3.dp, Color.White, RoundedCornerShape(24.dp))
                ) {
                    // Nature background decorative layers
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.35f)
                            .align(Alignment.BottomCenter)
                            .background(Color(0xFF4ADE80), RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    )

                    Text(
                        text = "المس الملصق بالأعلى لإضافته إلى لوحتك الجميلة 🎨",
                        fontSize = 14.sp,
                        color = Color(0xFF0369A1),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)
                    )

                    // Placed draggable stickers
                    placedStickers.forEach { sticker ->
                        var offset by remember { mutableStateOf(Offset(sticker.x, sticker.y)) }
                        Box(
                            modifier = Modifier
                                .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        offset = Offset(offset.x + dragAmount.x, offset.y + dragAmount.y)
                                    }
                                }
                        ) {
                            Text(sticker.emoji, fontSize = 54.sp)
                        }
                    }
                }
            }

            "shadow" -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "طابق الملصق مع شكله",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = Color(0xFFE2E8F0),
                        modifier = Modifier
                            .size(160.dp)
                            .border(3.dp, Color(0xFF94A3B8), RoundedCornerShape(28.dp)),
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(shadowTarget.emoji, fontSize = 80.sp)
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        shadowOptions.forEach { opt ->
                            Button(
                                onClick = {
                                    if (opt.emoji == shadowTarget.emoji) {
                                        onSpeak("أحسنت! هذا ${shadowTarget.name}")
                                        onWin(1)
                                        shadowTarget = STICKER_LIST.random()
                                    } else {
                                        onSpeak("حاول ثانية")
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(84.dp)
                                    .testTag("shadow_opt_${opt.emoji}"),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                            ) {
                                Text(opt.emoji, fontSize = 44.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            "sound" -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ابحث عن الملصق الصحيح",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { onSpeak(soundTarget.name) },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                        modifier = Modifier.size(120.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
                            Text("اسمع الصوت", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        soundOptions.forEach { opt ->
                            Button(
                                onClick = {
                                    if (opt.emoji == soundTarget.emoji) {
                                        onSpeak("ممتاز! هذا ${soundTarget.name}")
                                        onWin(1)
                                        soundTarget = STICKER_LIST.random()
                                    } else {
                                        onSpeak("حاول ثانية")
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(84.dp)
                                    .testTag("sound_opt_${opt.emoji}"),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                            ) {
                                Text(opt.emoji, fontSize = 44.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
