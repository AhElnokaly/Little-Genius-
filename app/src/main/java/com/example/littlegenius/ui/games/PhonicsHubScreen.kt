package com.example.littlegenius.ui.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.littlegenius.data.ArabicLetterPhonics
import com.example.littlegenius.data.CategorizedExample
import com.example.littlegenius.data.JollyPhonicsLetter
import com.example.littlegenius.data.PhonicsCategory
import com.example.littlegenius.data.PhonicsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class PhonicsLanguageMode {
    ENGLISH_JOLLY,
    ARABIC_MAKHARIJ
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhonicsHubScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onSpeak: (String, String) -> Unit // text, langCode ("en" or "ar")
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val coroutineScope = rememberCoroutineScope()
        var currentMode by remember { mutableStateOf(PhonicsLanguageMode.ENGLISH_JOLLY) }
        var selectedEnglishLetter by remember { mutableStateOf<JollyPhonicsLetter?>(null) }
        var selectedArabicLetter by remember { mutableStateOf<ArabicLetterPhonics?>(null) }
        val exploredLetters = remember { mutableStateListOf<String>() }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFFBEB)) // Warm buttery background
                .testTag("phonics_hub_screen")
        ) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFFEF3C7),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color.White, CircleShape)
                            .testTag("phonics_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color(0xFFD97706)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "مركز الحروف والأصوات 🔤",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF92400E)
                        )
                        Text(
                            text = if (currentMode == PhonicsLanguageMode.ENGLISH_JOLLY)
                                "Jolly Phonics (الأصوات والأفعال)"
                            else
                                "مخارج الحروف مع الحركات والأمثلة",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFFDE68A),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF59E0B))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "نجوم",
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "$starsCount",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF92400E)
                            )
                        }
                    }
                }
            }

            // Language Segment Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .background(Color(0xFFFEF3C7), RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0xFFFDE68A), RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // English Jolly Phonics Tab
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { currentMode = PhonicsLanguageMode.ENGLISH_JOLLY }
                        .testTag("tab_jolly_phonics"),
                    shape = RoundedCornerShape(12.dp),
                    color = if (currentMode == PhonicsLanguageMode.ENGLISH_JOLLY) Color(0xFF6366F1) else Color.Transparent,
                    shadowElevation = if (currentMode == PhonicsLanguageMode.ENGLISH_JOLLY) 3.dp else 0.dp
                ) {
                    Text(
                        text = "🇬🇧 Jolly Phonics",
                        modifier = Modifier.padding(vertical = 10.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (currentMode == PhonicsLanguageMode.ENGLISH_JOLLY) Color.White else Color(0xFF78350F)
                    )
                }

                // Arabic Makharij Tab
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { currentMode = PhonicsLanguageMode.ARABIC_MAKHARIJ }
                        .testTag("tab_arabic_makharij"),
                    shape = RoundedCornerShape(12.dp),
                    color = if (currentMode == PhonicsLanguageMode.ARABIC_MAKHARIJ) Color(0xFF10B981) else Color.Transparent,
                    shadowElevation = if (currentMode == PhonicsLanguageMode.ARABIC_MAKHARIJ) 3.dp else 0.dp
                ) {
                    Text(
                        text = "🇸🇦 الحروف العربية",
                        modifier = Modifier.padding(vertical = 10.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (currentMode == PhonicsLanguageMode.ARABIC_MAKHARIJ) Color.White else Color(0xFF78350F)
                    )
                }
            }

            // Single-Page Unified Grid
            if (currentMode == PhonicsLanguageMode.ENGLISH_JOLLY) {
                EnglishJollyPhonicsBoard(
                    letters = PhonicsRepository.JOLLY_PHONICS_LETTERS,
                    onSelectLetter = { letter ->
                        selectedEnglishLetter = letter
                        onSpeak(letter.letter, "en")
                        if (!exploredLetters.contains("en_${letter.letter}")) {
                            exploredLetters.add("en_${letter.letter}")
                            if (exploredLetters.size % 4 == 0) {
                                onWin(1)
                            }
                        }
                    }
                )
            } else {
                ArabicMakharijBoard(
                    letters = PhonicsRepository.ARABIC_LETTERS_DATA,
                    onSelectLetter = { letter ->
                        selectedArabicLetter = letter
                        onSpeak(letter.name, "ar")
                        if (!exploredLetters.contains("ar_${letter.letter}")) {
                            exploredLetters.add("ar_${letter.letter}")
                            if (exploredLetters.size % 4 == 0) {
                                onWin(1)
                            }
                        }
                    }
                )
            }
        }

        // English Detail Bottom Sheet
        if (selectedEnglishLetter != null) {
            val letter = selectedEnglishLetter!!
            ModalBottomSheet(
                onDismissRequest = { selectedEnglishLetter = null },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = Color(0xFFFEFCE8),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                JollyLetterDetailContent(
                    letter = letter,
                    onClose = { selectedEnglishLetter = null },
                    onSpeak = onSpeak
                )
            }
        }

        // Arabic Detail Bottom Sheet
        if (selectedArabicLetter != null) {
            val letter = selectedArabicLetter!!
            ModalBottomSheet(
                onDismissRequest = { selectedArabicLetter = null },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = Color(0xFFF0FDF4),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                ArabicLetterDetailContent(
                    letter = letter,
                    onClose = { selectedArabicLetter = null },
                    onSpeak = onSpeak
                )
            }
        }
    }
}

// ==================== ENGLISH JOLLY PHONICS BOARD ====================
@Composable
fun EnglishJollyPhonicsBoard(
    letters: List<JollyPhonicsLetter>,
    onSelectLetter: (JollyPhonicsLetter) -> Unit
) {
    val groups = remember(letters) { letters.groupBy { it.groupNumber } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFEEF2FF),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFC7D2FE))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("💡", fontSize = 24.sp)
                    Column {
                        Text(
                            text = "طريقة Jolly Phonics البريطانية",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF3730A3)
                        )
                        Text(
                            text = "تعلّم صوت الحرف والحركة الجسدية (Action) بدلاً من اسم الحرف المجرد لتسهيل القراءة السريعة!",
                            fontSize = 11.sp,
                            color = Color(0xFF4338CA),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        groups.forEach { (groupNum, groupLetters) ->
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Group Header Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = when (groupNum) {
                            1 -> Color(0xFFF59E0B)
                            2 -> Color(0xFF0284C7)
                            3 -> Color(0xFF10B981)
                            4 -> Color(0xFF8B5CF6)
                            else -> Color(0xFFEC4899)
                        },
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "المجموعة $groupNum (Group $groupNum)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Letters Row / Grid for this group
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        groupLetters.forEach { letterItem ->
                            Box(modifier = Modifier.weight(1f)) {
                                JollyLetterCard(
                                    letter = letterItem,
                                    onClick = { onSelectLetter(letterItem) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JollyLetterCard(
    letter: JollyPhonicsLetter,
    onClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    Surface(
        modifier = Modifier
            .aspectRatio(0.85f)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                coroutineScope.launch {
                    scale.animateTo(1.15f, tween(100))
                    scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                }
                onClick()
            }
            .testTag("jolly_letter_${letter.letter}"),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE0E7FF)),
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            // Upper and Lower case
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = letter.letter,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF4338CA)
                )
                Text(
                    text = letter.lowerLetter,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6366F1),
                    modifier = Modifier.padding(start = 2.dp)
                )
            }

            // Primary Emoji
            Text(
                text = letter.examples.firstOrNull()?.emoji ?: "⭐",
                fontSize = 22.sp
            )

            // Phonic sound tag
            Surface(
                shape = CircleShape,
                color = Color(0xFFEEF2FF)
            ) {
                Text(
                    text = letter.phonicSoundText.take(6),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3730A3),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

// ==================== ARABIC MAKHARIJ BOARD ====================
@Composable
fun ArabicMakharijBoard(
    letters: List<ArabicLetterPhonics>,
    onSelectLetter: (ArabicLetterPhonics) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFECFDF5),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFA7F3D0))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("📖", fontSize = 24.sp)
                    Column {
                        Text(
                            text = "نطق الحروف العربية ومخارجها",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF065F46)
                        )
                        Text(
                            text = "اضغط على أي حرف لتسمع صوته بالحركات القصيرة (الفتحة، الضمة، الكسرة) وتتعرف على مخرجه وأمثلة متنوعة من بيئتك!",
                            fontSize = 11.sp,
                            color = Color(0xFF047857),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        item {
            // 4-column grid for Arabic letters
            val chunkedLetters = remember(letters) { letters.chunked(4) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                chunkedLetters.forEach { rowLetters ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowLetters.forEach { letterItem ->
                            Box(modifier = Modifier.weight(1f)) {
                                ArabicLetterCard(
                                    letter = letterItem,
                                    onClick = { onSelectLetter(letterItem) }
                                )
                            }
                        }
                        // Fill remaining spaces if chunk is < 4
                        for (i in 0 until (4 - rowLetters.size)) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArabicLetterCard(
    letter: ArabicLetterPhonics,
    onClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    Surface(
        modifier = Modifier
            .aspectRatio(0.88f)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                coroutineScope.launch {
                    scale.animateTo(1.15f, tween(100))
                    scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                }
                onClick()
            }
            .testTag("arabic_letter_${letter.letter}"),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFA7F3D0)),
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = letter.letter,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF065F46)
            )

            Text(
                text = letter.examples.firstOrNull()?.emoji ?: "🍎",
                fontSize = 22.sp
            )

            Surface(
                shape = CircleShape,
                color = Color(0xFFECFDF5)
            ) {
                Text(
                    text = letter.fatHa.take(5),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF047857),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

// ==================== BOTTOM SHEET: JOLLY PHONICS DETAIL ====================
@Composable
fun JollyLetterDetailContent(
    letter: JollyPhonicsLetter,
    onClose: () -> Unit,
    onSpeak: (String, String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(PhonicsCategory.ALL) }

    val filteredExamples = remember(letter, selectedCategory) {
        if (selectedCategory == PhonicsCategory.ALL)
            letter.examples
        else
            letter.examples.filter { it.category == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Drag handle line
        Box(
            modifier = Modifier
                .width(42.dp)
                .height(4.dp)
                .background(Color(0xFFCBD5E1), CircleShape)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Big Letter Card with Pronounce sound button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF6366F1),
                    modifier = Modifier.size(68.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${letter.letter}${letter.lowerLetter}",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Letter ${letter.letter} (حرف ${letter.letter})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = letter.phonicSoundText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4F46E5)
                    )
                }
            }

            // Hear letter sound button
            IconButton(
                onClick = { onSpeak(letter.letter, "en") },
                modifier = Modifier
                    .size(52.dp)
                    .background(Color(0xFFEEF2FF), CircleShape)
                    .border(2.dp, Color(0xFF6366F1), CircleShape)
                    .testTag("hear_phonic_sound_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "استمع للصوت",
                    tint = Color(0xFF4F46E5),
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Jolly Phonics Action & Story Box
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFFEF3C7),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFDE68A))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎬", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "حركة وقصة الحرف (Jolly Phonics Action):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF92400E)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = letter.actionHintAr,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF78350F)
                )
                Text(
                    text = letter.actionHintEn,
                    fontSize = 11.sp,
                    color = Color(0xFF92400E),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category Filter Chips
        Text(
            text = "أمثلة مصنفة على الحرف (اضغط لسماع الكلمة):",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF334155),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(PhonicsCategory.values()) { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) Color(0xFF6366F1) else Color(0xFFF1F5F9),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) Color(0xFF4F46E5) else Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier.clickable { selectedCategory = cat }
                ) {
                    Text(
                        text = "${cat.emoji} ${cat.titleAr}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color(0xFF475569),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Categorized Examples Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 280.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredExamples) { item ->
                ExampleCard(
                    example = item,
                    primaryLang = "en",
                    onSpeak = onSpeak
                )
            }
        }
    }
}

// ==================== BOTTOM SHEET: ARABIC DETAIL ====================
@Composable
fun ArabicLetterDetailContent(
    letter: ArabicLetterPhonics,
    onClose: () -> Unit,
    onSpeak: (String, String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(PhonicsCategory.ALL) }

    val filteredExamples = remember(letter, selectedCategory) {
        if (selectedCategory == PhonicsCategory.ALL)
            letter.examples
        else
            letter.examples.filter { it.category == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Drag handle line
        Box(
            modifier = Modifier
                .width(42.dp)
                .height(4.dp)
                .background(Color(0xFFCBD5E1), CircleShape)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Big Letter Card with Pronounce name button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF10B981),
                    modifier = Modifier.size(68.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = letter.letter,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "حرف ${letter.name}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF065F46)
                    )
                    Text(
                        text = letter.makhrajAr,
                        fontSize = 12.sp,
                        color = Color(0xFF047857),
                        lineHeight = 16.sp
                    )
                }
            }

            // Hear letter name button
            IconButton(
                onClick = { onSpeak(letter.name, "ar") },
                modifier = Modifier
                    .size(52.dp)
                    .background(Color(0xFFECFDF5), CircleShape)
                    .border(2.dp, Color(0xFF10B981), CircleShape)
                    .testTag("hear_arabic_sound_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "استمع للاسم",
                    tint = Color(0xFF059669),
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tashkeel / Short Vowels interactive row
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFF0FDF4),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFA7F3D0))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "نطق الحرف بالحركات الثلاث والسكون (المس لتسمع):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF065F46)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val vowels = listOf(letter.fatHa, letter.damma, letter.kasra, letter.sukun)
                    vowels.forEach { vowel ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSpeak(vowel, "ar") },
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF34D399)),
                            shadowElevation = 2.dp
                        ) {
                            Text(
                                text = vowel,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category Filter Chips
        Text(
            text = "أمثلة مصنفة على الحرف (خضار، فاكهة، حيوان، شيء):",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF334155),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(PhonicsCategory.values()) { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) Color(0xFF10B981) else Color(0xFFF1F5F9),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) Color(0xFF059669) else Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier.clickable { selectedCategory = cat }
                ) {
                    Text(
                        text = "${cat.emoji} ${cat.titleAr}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color(0xFF475569),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Categorized Examples Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 280.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredExamples) { item ->
                ExampleCard(
                    example = item,
                    primaryLang = "ar",
                    onSpeak = onSpeak
                )
            }
        }
    }
}

@Composable
fun ExampleCard(
    example: CategorizedExample,
    primaryLang: String,
    onSpeak: (String, String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                coroutineScope.launch {
                    scale.animateTo(1.1f, tween(80))
                    scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                }
                if (primaryLang == "ar") {
                    onSpeak(example.wordAr, "ar")
                } else {
                    onSpeak(example.wordEn, "en")
                }
            },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE2E8F0)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = example.emoji,
                fontSize = 32.sp
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (primaryLang == "ar") example.wordAr else example.wordEn,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = if (primaryLang == "ar") example.wordEn else example.wordAr,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "استمع",
                tint = if (primaryLang == "ar") Color(0xFF10B981) else Color(0xFF6366F1),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
