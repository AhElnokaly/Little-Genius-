package com.example.littlegenius.model

data class GameStats(
    val played: Int = 0,
    val stars: Int = 0
)

data class UserProfile(
    val name: String = "",
    val dob: String = "",
    val lockEnabled: Boolean = false,
    val playTimeLimit: Int = 0, // in minutes, 0 means no limit
    val avatar: String = "👦",
    val difficulty: String = "medium", // "easy", "medium", "hard"
    val stats: Map<String, GameStats> = emptyMap()
)

data class GameCategory(
    val title: String,
    val games: List<GameItem>
)

data class GameItem(
    val id: String,
    val title: String,
    val icon: String,
    val colorHex: Long
)
