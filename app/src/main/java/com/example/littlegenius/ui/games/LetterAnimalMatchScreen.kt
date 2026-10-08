package com.example.littlegenius.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.littlegenius.ui.theme.Amber400
import com.example.littlegenius.ui.theme.SkyBlue600

data class AnimalMatchQuestion(
    val letter: String,
    val animalName: String,
    val emoji: String,
    val wrongLetters: List<String>
)

val MATCH_QUESTIONS = listOf(
    AnimalMatchQuestion("أ", "أَسَد", "🦁", listOf("ب", "ت")),
    AnimalMatchQuestion("ب", "بَطَّة", "🦆", listOf("أ", "س")),
    AnimalMatchQuestion("ف", "فِيل", "🐘", listOf("ق", "م")),
    AnimalMatchQuestion("ق", "قِطَّة", "🐱", listOf("ك", "ن")),
    AnimalMatchQuestion("ح", "حِصَان", "🐴", listOf("ج", "خ")),
    AnimalMatchQuestion("ك", "كَلْب", "🐶", listOf("ف", "ل")),
    AnimalMatchQuestion("خ", "خَرُوف", "🐑", listOf("ع", "هـ")),
    AnimalMatchQuestion("ع", "عُصْفُور", "🐦", listOf("ش", "ص"))
)

@Composable
fun LetterAnimalMatchScreen(
    starsCount: Int,
    onBack: () -> Unit,
    onWin: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    var questionIndex by remember { mutableIntStateOf(0) }
    val currentQuestion = MATCH_QUESTIONS[questionIndex % MATCH_QUESTIONS.size]

    val options = remember(currentQuestion) {
        (listOf(currentQuestion.letter) + currentQuestion.wrongLetters).shuffled()
    }

    LaunchedEffect(currentQuestion) {
        onSpeak("ما هو الحرف الأول لـ ${currentQuestion.animalName}؟")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBEB))
            .testTag("letter_animal_match_screen")
    ) {
        GameHeader(
            title = "حروف وحيوانات 🦁",
            onBack = onBack,
            starsCount = starsCount
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Animal prompt card
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color.White,
            shadowElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .border(4.dp, Amber400, RoundedCornerShape(32.dp))
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentQuestion.emoji,
                    fontSize = 80.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = currentQuestion.animalName,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF92400E)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "يبدأ بحرف ماذا؟ 🤔",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB45309)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Options Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEach { letter ->
                Button(
                    onClick = {
                        if (letter == currentQuestion.letter) {
                            onSpeak("أحسنت! ${currentQuestion.letter} لـ ${currentQuestion.animalName}")
                            onWin(1)
                            questionIndex++
                        } else {
                            onSpeak("حاول مرة ثانية")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .testTag("match_option_$letter"),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SkyBlue600,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = letter,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
