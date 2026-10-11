package com.example.littlegenius.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.littlegenius.ui.theme.Emerald500
import com.example.littlegenius.ui.theme.SkyBlue200
import com.example.littlegenius.ui.theme.SkyBlue600
import java.util.ArrayDeque

data class MazeLevel(
    val levelNumber: Int,
    val title: String,
    val playerEmoji: String,
    val goalEmoji: String,
    val rows: Int,
    val cols: Int,
    // 0 = open, 1 = wall
    val layout: List<List<Int>>,
    val startRow: Int,
    val startCol: Int,
    val goalRow: Int,
    val goalCol: Int,
    val color: Color
)

val MAZE_10_LEVELS = listOf(
    // Level 1: 3x3
    MazeLevel(
        1, "الأرنب والجزرة", "🐰", "🥕", 3, 3,
        listOf(
            listOf(0, 0, 1),
            listOf(1, 0, 0),
            listOf(1, 1, 0)
        ), 0, 0, 2, 2, Color(0xFFFED7AA)
    ),
    // Level 2: 4x4
    MazeLevel(
        2, "القطة والحليب", "🐱", "🥛", 4, 4,
        listOf(
            listOf(0, 0, 1, 1),
            listOf(1, 0, 0, 1),
            listOf(1, 1, 0, 0),
            listOf(1, 1, 1, 0)
        ), 0, 0, 3, 3, Color(0xFFFDBA74)
    ),
    // Level 3: 4x4 with turns
    MazeLevel(
        3, "الجرو والعظمة", "🐶", "🦴", 4, 4,
        listOf(
            listOf(0, 1, 0, 0),
            listOf(0, 1, 0, 1),
            listOf(0, 0, 0, 1),
            listOf(1, 1, 0, 0)
        ), 0, 0, 3, 3, Color(0xFF93C5FD)
    ),
    // Level 4: 5x5
    MazeLevel(
        4, "البطة والبركة", "🦆", "🌊", 5, 5,
        listOf(
            listOf(0, 0, 0, 1, 1),
            listOf(1, 1, 0, 0, 1),
            listOf(1, 0, 0, 0, 1),
            listOf(1, 0, 1, 0, 0),
            listOf(1, 0, 1, 1, 0)
        ), 0, 0, 4, 4, Color(0xFF86EFAC)
    ),
    // Level 5: 5x5
    MazeLevel(
        5, "النحلة والزهرة", "🐝", "🌸", 5, 5,
        listOf(
            listOf(0, 1, 0, 0, 0),
            listOf(0, 1, 0, 1, 0),
            listOf(0, 0, 0, 1, 0),
            listOf(1, 1, 0, 0, 0),
            listOf(1, 1, 1, 1, 0)
        ), 0, 0, 4, 4, Color(0xFFFDE047)
    ),
    // Level 6: 5x5
    MazeLevel(
        6, "الفأر والجبنة", "🐭", "🧀", 5, 5,
        listOf(
            listOf(0, 0, 1, 0, 0),
            listOf(1, 0, 1, 0, 1),
            listOf(0, 0, 0, 0, 1),
            listOf(0, 1, 1, 0, 0),
            listOf(0, 0, 0, 1, 0)
        ), 0, 0, 4, 4, Color(0xFFD6D3D1)
    ),
    // Level 7: 6x6
    MazeLevel(
        7, "الصاروخ والقمر", "🚀", "🌙", 6, 6,
        listOf(
            listOf(0, 0, 0, 1, 1, 1),
            listOf(1, 1, 0, 0, 0, 1),
            listOf(1, 0, 0, 1, 0, 1),
            listOf(1, 0, 1, 1, 0, 0),
            listOf(1, 0, 0, 0, 1, 0),
            listOf(1, 1, 1, 0, 0, 0)
        ), 0, 0, 5, 5, Color(0xFFC7D2FE)
    ),
    // Level 8: 6x6
    MazeLevel(
        8, "القرد والموز", "🐵", "🍌", 6, 6,
        listOf(
            listOf(0, 1, 0, 0, 0, 0),
            listOf(0, 1, 0, 1, 1, 0),
            listOf(0, 0, 0, 1, 0, 0),
            listOf(1, 1, 0, 0, 0, 1),
            listOf(0, 0, 0, 1, 0, 0),
            listOf(0, 1, 1, 1, 1, 0)
        ), 0, 0, 5, 5, Color(0xFFFDE68A)
    ),
    // Level 9: 6x6
    MazeLevel(
        9, "الفراشة والبستان", "🦋", "🌺", 6, 6,
        listOf(
            listOf(0, 0, 0, 0, 1, 1),
            listOf(1, 1, 1, 0, 0, 1),
            listOf(0, 0, 0, 0, 1, 1),
            listOf(0, 1, 1, 0, 0, 0),
            listOf(0, 1, 0, 0, 1, 0),
            listOf(0, 0, 0, 1, 1, 0)
        ), 0, 0, 5, 5, Color(0xFFFBCFE8)
    ),
    // Level 10: 6x6
    MazeLevel(
        10, "البطل والكأس", "👦", "🏆", 6, 6,
        listOf(
            listOf(0, 0, 1, 0, 0, 0),
            listOf(1, 0, 1, 0, 1, 0),
            listOf(0, 0, 0, 0, 1, 0),
            listOf(0, 1, 1, 0, 0, 0),
            listOf(0, 0, 1, 1, 1, 0),
            listOf(1, 0, 0, 0, 0, 0)
        ), 0, 0, 5, 5, Color(0xFFBAE6FD)
    )
)

// BFS algorithm to find next step towards the goal
fun findNextStep(
    startR: Int, startC: Int,
    goalR: Int, goalC: Int,
    rows: Int, cols: Int,
    layout: List<List<Int>>
): Pair<Int, Int>? {
    if (startR == goalR && startC == goalC) return null
    val queue = ArrayDeque<Pair<Int, Int>>()
    val visited = mutableSetOf<Pair<Int, Int>>()
    val parent = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()

    val start = Pair(startR, startC)
    queue.add(start)
    visited.add(start)

    val directions = listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))

    while (queue.isNotEmpty()) {
        val curr = queue.poll()
        if (curr.first == goalR && curr.second == goalC) {
            // Backtrack to find the node immediately after start
            var step = curr
            while (parent[step] != null && parent[step] != start) {
                step = parent[step]!!
            }
            return step
        }

        for (d in directions) {
            val nr = curr.first + d.first
            val nc = curr.second + d.second
            val next = Pair(nr, nc)
            if (nr in 0 until rows && nc in 0 until cols && layout[nr][nc] == 0 && next !in visited) {
                visited.add(next)
                parent[next] = curr
                queue.add(next)
            }
        }
    }
    return null
}

@Composable
fun MazeGameScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    var levelIndex by remember { mutableIntStateOf(0) }
    val level = MAZE_10_LEVELS[levelIndex % MAZE_10_LEVELS.size]

    var playerRow by remember(level) { mutableIntStateOf(level.startRow) }
    var playerCol by remember(level) { mutableIntStateOf(level.startCol) }
    var movesCount by remember(level) { mutableIntStateOf(0) }

    val visitedTrail = remember(level) { mutableStateListOf<Pair<Int, Int>>() }
    var hintedTile by remember(level) { mutableStateOf<Pair<Int, Int>?>(null) }

    val isWon = playerRow == level.goalRow && playerCol == level.goalCol

    // Pulsating hint light animation
    val infiniteTransition = rememberInfiniteTransition(label = "maze_hint")
    val hintScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hintPulse"
    )

    LaunchedEffect(level) {
        visitedTrail.clear()
        visitedTrail.add(Pair(level.startRow, level.startCol))
        hintedTile = null
        onSpeak("متاهة ${level.title}")
    }

    LaunchedEffect(isWon) {
        if (isWon) {
            onSpeak("أحسنت! وصل ${level.playerEmoji} إلى ${level.goalEmoji}")
            onWin(2)
        }
    }

    val tryMove: (Int, Int) -> Unit = { dRow, dCol ->
        if (!isWon) {
            val newR = playerRow + dRow
            val newC = playerCol + dCol
            if (newR in 0 until level.rows && newC in 0 until level.cols) {
                if (level.layout[newR][newC] == 0) {
                    playerRow = newR
                    playerCol = newC
                    movesCount++
                    visitedTrail.add(Pair(newR, newC))
                    hintedTile = null
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEFCE8))
            .testTag("maze_game_screen")
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            GameHeader(
                title = "متاهة الأبطال 🌀",
                onBack = onBack,
                starsCount = starsCount,
                trailingAction = {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Hint Lightbulb Button
                        IconButton(
                            onClick = {
                                val next = findNextStep(
                                    playerRow, playerCol,
                                    level.goalRow, level.goalCol,
                                    level.rows, level.cols,
                                    level.layout
                                )
                                hintedTile = next
                                if (next != null) {
                                    onSpeak("انظر إلى النجمة المضيئة!")
                                }
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color(0xFFFEF08A), CircleShape)
                                .testTag("hint_maze_btn")
                        ) {
                            Icon(Icons.Default.Lightbulb, contentDescription = "تلميح", tint = Color(0xFFD97706))
                        }

                        // Restart Button
                        IconButton(
                            onClick = {
                                playerRow = level.startRow
                                playerCol = level.startCol
                                movesCount = 0
                                visitedTrail.clear()
                                visitedTrail.add(Pair(level.startRow, level.startCol))
                                hintedTile = null
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color.White, CircleShape)
                                .testTag("reset_maze_btn")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "إعادة البدء", tint = SkyBlue600)
                        }
                    }
                }
            )

            // Levels horizontal selector
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(MAZE_10_LEVELS) { l ->
                    val isCurrent = l.levelNumber == level.levelNumber
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isCurrent) SkyBlue600 else Color.White,
                        modifier = Modifier
                            .clickable {
                                levelIndex = l.levelNumber - 1
                            }
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (isCurrent) SkyBlue600 else SkyBlue200,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .testTag("maze_chip_${l.levelNumber}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(l.playerEmoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "مستوى ${l.levelNumber}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrent) Color.White else Color(0xFF334155)
                            )
                        }
                    }
                }
            }

            // Objective Banner
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = level.color.copy(alpha = 0.4f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .border(2.dp, level.color, RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${level.playerEmoji} ➡️ ${level.goalEmoji}", fontSize = 26.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ساعد ${level.playerEmoji} للوصول!",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B)
                        )
                    }
                    Surface(shape = CircleShape, color = Color.White) {
                        Text(
                            text = "الخطوات: $movesCount",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F766E),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Maze Grid Board
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .border(4.dp, if (isWon) Emerald500 else SkyBlue200, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                ) {
                    for (r in 0 until level.rows) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            for (c in 0 until level.cols) {
                                val isWall = level.layout[r][c] == 1
                                val isPlayer = r == playerRow && c == playerCol
                                val isGoal = r == level.goalRow && c == level.goalCol
                                val isHinted = hintedTile?.first == r && hintedTile?.second == c
                                val isVisited = Pair(r, c) in visitedTrail && !isPlayer && !isGoal

                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(2.dp)
                                        .clickable {
                                            // Tap adjacent cell directly to move!
                                            if (Math.abs(r - playerRow) + Math.abs(c - playerCol) == 1) {
                                                tryMove(r - playerRow, c - playerCol)
                                            }
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                    color = when {
                                        isWall -> Color(0xFF475569) // Wall
                                        isGoal -> Color(0xFFFEF08A) // Goal
                                        isHinted -> Color(0xFFFEF9C3) // Hint highlight
                                        isVisited -> Color(0xFFF1F5F9) // Breadcrumb open path
                                        else -> Color(0xFFF8FAFC) // Open path
                                    }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        when {
                                            isPlayer -> Text(level.playerEmoji, fontSize = 28.sp)
                                            isGoal -> Text(level.goalEmoji, fontSize = 26.sp)
                                            isWall -> Text("🧱", fontSize = 16.sp)
                                            isHinted -> Text(
                                                "⭐",
                                                fontSize = 22.sp,
                                                modifier = Modifier.graphicsLayer {
                                                    scaleX = hintScale
                                                    scaleY = hintScale
                                                }
                                            )
                                            isVisited -> Text("🐾", fontSize = 12.sp, modifier = Modifier.graphicsLayer { alpha = 0.4f })
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // PHYSICAL LTR D-PAD:
        // Enforcing LTR coordinates for the directional pad guarantees the Left arrow is on the left
        // and the Right arrow is on the right, matching physical direction on any device!
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Up Button
                FilledIconButton(
                    onClick = { tryMove(-1, 0) },
                    modifier = Modifier.size(54.dp).testTag("maze_btn_up"),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = SkyBlue600)
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "أعلى", tint = Color.White)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Button (Physically on the left)
                    FilledIconButton(
                        onClick = { tryMove(0, -1) },
                        modifier = Modifier.size(54.dp).testTag("maze_btn_left"),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = SkyBlue600)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "يسار", tint = Color.White)
                    }

                    // Down Button (Middle bottom)
                    FilledIconButton(
                        onClick = { tryMove(1, 0) },
                        modifier = Modifier.size(54.dp).testTag("maze_btn_down"),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = SkyBlue600)
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "أسفل", tint = Color.White)
                    }

                    // Right Button (Physically on the right)
                    FilledIconButton(
                        onClick = { tryMove(0, 1) },
                        modifier = Modifier.size(54.dp).testTag("maze_btn_right"),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = SkyBlue600)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "يمين", tint = Color.White)
                    }
                }
            }
        }
    }
}
