package com.example.littlegenius

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.littlegenius.audio.SoundManager
import com.example.littlegenius.data.ProfileRepository
import com.example.littlegenius.model.GameItem
import com.example.littlegenius.model.UserProfile
import com.example.littlegenius.ui.components.*
import com.example.littlegenius.ui.games.*
import com.example.littlegenius.ui.screens.HomeScreen
import com.example.littlegenius.ui.theme.LittleGeniusTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private lateinit var soundManager: SoundManager
    private lateinit var profileRepository: ProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        soundManager = SoundManager(this)
        profileRepository = ProfileRepository(this)

        setContent {
            LittleGeniusTheme {
                MainAppContent(
                    soundManager = soundManager,
                    profileRepository = profileRepository,
                    onExitApp = { finish() }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}

@Composable
fun MainAppContent(
    soundManager: SoundManager,
    profileRepository: ProfileRepository,
    onExitApp: () -> Unit
) {
    var profile by remember { mutableStateOf(profileRepository.getProfile()) }
    var starsCount by remember { mutableIntStateOf(profileRepository.getTotalStars()) }
    var activeGameId by remember { mutableStateOf<String?>(null) }

    // Overlays state
    var showParentalGate by remember { mutableStateOf(false) }
    var pendingGateAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    var isTimeLocked by remember { mutableStateOf(false) }
    var timeRemainingSeconds by remember { mutableStateOf<Int?>(null) }

    var showCelebration by remember { mutableStateOf(false) }
    var celebrationStars by remember { mutableIntStateOf(1) }

    // Play time timer
    LaunchedEffect(profile.playTimeLimit) {
        if (profile.playTimeLimit > 0) {
            timeRemainingSeconds = profile.playTimeLimit * 60
            isTimeLocked = false
        } else {
            timeRemainingSeconds = null
            isTimeLocked = false
        }
    }

    LaunchedEffect(timeRemainingSeconds, isTimeLocked) {
        if (timeRemainingSeconds != null && !isTimeLocked) {
            while (timeRemainingSeconds != null && timeRemainingSeconds!! > 0) {
                delay(1000)
                timeRemainingSeconds = timeRemainingSeconds!! - 1
                if (timeRemainingSeconds!! <= 0) {
                    isTimeLocked = true
                    break
                }
            }
        }
    }

    val handleWin: (Int) -> Unit = { earnedStars ->
        starsCount += earnedStars
        celebrationStars = earnedStars
        showCelebration = true
        soundManager.playWinFanfare()
        soundManager.speak("أحسنت يا بطل!")

        activeGameId?.let { gameId ->
            profileRepository.recordGameWin(gameId, earnedStars)
            profile = profileRepository.getProfile()
        }
    }

    val requestExitOrBack = {
        if (profile.lockEnabled) {
            pendingGateAction = {
                if (activeGameId != null) {
                    activeGameId = null
                } else {
                    onExitApp()
                }
            }
            showParentalGate = true
        } else {
            if (activeGameId != null) {
                activeGameId = null
            } else {
                onExitApp()
            }
        }
    }

    BackHandler {
        requestExitOrBack()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (activeGameId) {
            null -> {
                HomeScreen(
                    avatar = profile.avatar,
                    childName = profile.name,
                    starsCount = starsCount,
                    onSelectGame = { game ->
                        soundManager.speak(game.title)
                        activeGameId = game.id
                    },
                    onOpenSettings = {
                        if (profile.lockEnabled) {
                            pendingGateAction = { activeGameId = "settings" }
                            showParentalGate = true
                        } else {
                            activeGameId = "settings"
                        }
                    },
                    onOpenTrophies = {
                        soundManager.speak("مملكة الأوسمة والجوائز")
                        activeGameId = "trophies"
                    }
                )
            }
            "trophies" -> {
                CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
                    BadgesShowcaseScreen(
                        profile = profile,
                        starsCount = starsCount,
                        onBack = { activeGameId = null },
                        onSpeak = { soundManager.speak(it) }
                    )
                }
            }
            "settings" -> {
                SettingsScreen(
                    profile = profile,
                    onSave = { updated ->
                        profileRepository.saveProfile(updated)
                        profile = updated
                        activeGameId = null
                    },
                    onBack = { activeGameId = null },
                    onOpenDashboard = { activeGameId = "dashboard" }
                )
            }
            "dashboard" -> {
                ParentsDashboardScreen(
                    profile = profile,
                    onBack = { activeGameId = "settings" }
                )
            }
            "balloon" -> {
                BalloonPopScreen(
                    starsCount = starsCount,
                    onBack = { requestExitOrBack() },
                    onWin = handleWin,
                    onPopSound = { soundManager.playPopSound() },
                    onSpeak = { soundManager.speak(it) }
                )
            }
            "animal" -> {
                AnimalFriendsScreen(
                    starsCount = starsCount,
                    onBack = { requestExitOrBack() },
                    onWin = handleWin,
                    onPlayAnimal = { animal ->
                        soundManager.playRawSound(animal.rawResId)
                    }
                )
            }
            "draw" -> {
                MagicGlowDrawScreen(
                    starsCount = starsCount,
                    onBack = { requestExitOrBack() },
                    onWin = handleWin
                )
            }
            "piano" -> {
                ColorPianoScreen(
                    starsCount = starsCount,
                    onBack = { requestExitOrBack() },
                    onWin = handleWin,
                    onPlayTone = { freq -> soundManager.playPianoTone(freq) }
                )
            }
            "drumband" -> {
                DrumBandScreen(
                    starsCount = starsCount,
                    onBack = { requestExitOrBack() },
                    onWin = handleWin,
                    onPlayDrum = { type -> soundManager.playDrum(type) }
                )
            }
            "colorbynum" -> {
                ColorByNumbersScreen(
                    starsCount = starsCount,
                    onBack = { requestExitOrBack() },
                    onWin = handleWin,
                    onPlayChime = { soundManager.playChime() }
                )
            }
            "recycle" -> {
                RecycleSortScreen(
                    starsCount = starsCount,
                    onBack = { requestExitOrBack() },
                    onWin = handleWin,
                    onSpeak = { soundManager.speak(it) }
                )
            }
            "arabic" -> {
                ArabicLettersScreen(
                    starsCount = starsCount,
                    onBack = { requestExitOrBack() },
                    onWin = handleWin,
                    onSpeak = { soundManager.speak(it) }
                )
            }
            "tashkeel" -> {
                ArabicTashkeelScreen(
                    starsCount = starsCount,
                    onBack = { requestExitOrBack() },
                    onWin = handleWin,
                    onSpeak = { soundManager.speak(it) }
                )
            }
            "wordbuilder" -> {
                WordBuilderScreen(
                    starsCount = starsCount,
                    onBack = { requestExitOrBack() },
                    onWin = handleWin,
                    onSpeak = { soundManager.speak(it) }
                )
            }
            "english" -> {
                EnglishLettersScreen(
                    starsCount = starsCount,
                    onBack = { requestExitOrBack() },
                    onWin = handleWin,
                    onSpeakEnglish = { soundManager.speak(it, "en") }
                )
            }
            "letteranimal" -> {
                LetterAnimalMatchScreen(
                    starsCount = starsCount,
                    onBack = { requestExitOrBack() },
                    onWin = handleWin,
                    onSpeak = { soundManager.speak(it) }
                )
            }
            "numbers" -> {
                NumberBilingualScreen(
                    starsCount = starsCount,
                    onBack = { requestExitOrBack() },
                    onWin = handleWin,
                    onSpeak = { soundManager.speak(it) }
                )
            }
            "counting" -> {
                CountingGameScreen(
                    starsCount = starsCount,
                    onBack = { requestExitOrBack() },
                    onWin = handleWin,
                    onSpeak = { soundManager.speak(it) }
                )
            }
            "simplemath" -> {
                SimpleMathScreen(
                    starsCount = starsCount,
                    onBack = { requestExitOrBack() },
                    onWin = handleWin,
                    onSpeak = { soundManager.speak(it) }
                )
            }
            "house" -> {
                InteractiveHouseScreen(
                    starsCount = starsCount,
                    onBack = { requestExitOrBack() },
                    onWin = handleWin,
                    onSpeak = { soundManager.speak(it) }
                )
            }
            "healthyfood" -> {
                HealthyFoodScreen(
                    starsCount = starsCount,
                    onBack = { requestExitOrBack() },
                    onWin = handleWin,
                    onSpeak = { soundManager.speak(it) }
                )
            }
            "animalfamily" -> {
                AnimalFamilyScreen(
                    starsCount = starsCount,
                    onBack = { requestExitOrBack() },
                    onWin = handleWin,
                    onSpeak = { soundManager.speak(it) }
                )
            }
            "guesssound" -> {
                GuessSoundScreen(
                    starsCount = starsCount,
                    onBack = { requestExitOrBack() },
                    onWin = handleWin,
                    onPlayRaw = { soundManager.playRawSound(it) },
                    onSpeak = { soundManager.speak(it) }
                )
            }
            "sorter" -> {
                ShapeSorterScreen(
                    starsCount = starsCount,
                    onBack = { requestExitOrBack() },
                    onWin = handleWin,
                    onSpeak = { soundManager.speak(it) }
                )
            }
            "stickers" -> {
                StickerBookScreen(
                    starsCount = starsCount,
                    onBack = { requestExitOrBack() },
                    onWin = handleWin,
                    onSpeak = { soundManager.speak(it) }
                )
            }
            "fish" -> {
                CatchFishScreen(
                    starsCount = starsCount,
                    onBack = { requestExitOrBack() },
                    onWin = handleWin,
                    onSpeak = { soundManager.speak(it) }
                )
            }
            "puzzle", "jigsaw" -> {
                CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
                    PuzzleGameScreen(
                        starsCount = starsCount,
                        onBack = { requestExitOrBack() },
                        onWin = handleWin,
                        onSpeak = { soundManager.speak(it) }
                    )
                }
            }
            "maze" -> {
                CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
                    MazeGameScreen(
                        starsCount = starsCount,
                        onBack = { requestExitOrBack() },
                        onWin = handleWin,
                        onSpeak = { soundManager.speak(it) }
                    )
                }
            }
            "memory" -> {
                CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
                    MemoryMatchScreen(
                        starsCount = starsCount,
                        onBack = { requestExitOrBack() },
                        onWin = handleWin,
                        onSpeak = { soundManager.speak(it) }
                    )
                }
            }
            else -> {
                CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
                    GenericMiniGameScreen(
                        gameId = activeGameId!!,
                        starsCount = starsCount,
                        onBack = { requestExitOrBack() },
                        onWin = handleWin,
                        onSpeak = { soundManager.speak(it) }
                    )
                }
            }
        }

        // Overlays
        if (showCelebration) {
            WinCelebrationOverlay(
                starsCount = celebrationStars,
                onDismiss = { showCelebration = false }
            )
        }

        if (isTimeLocked) {
            TimeLockOverlay(
                onUnlockRequest = {
                    pendingGateAction = {
                        isTimeLocked = false
                        timeRemainingSeconds = null
                        activeGameId = "settings"
                    }
                    showParentalGate = true
                }
            )
        }

        if (showParentalGate) {
            ParentalGateDialog(
                onSuccess = {
                    showParentalGate = false
                    pendingGateAction?.invoke()
                    pendingGateAction = null
                },
                onDismiss = {
                    showParentalGate = false
                    pendingGateAction = null
                }
            )
        }
    }
}
