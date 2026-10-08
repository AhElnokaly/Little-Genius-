package com.example.littlegenius.data

import android.content.Context
import android.content.SharedPreferences
import com.example.littlegenius.model.GameStats
import com.example.littlegenius.model.UserProfile
import org.json.JSONObject

class ProfileRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("little_genius_prefs", Context.MODE_PRIVATE)

    fun getProfile(): UserProfile {
        val name = prefs.getString("name", "") ?: ""
        val dob = prefs.getString("dob", "") ?: ""
        val lockEnabled = prefs.getBoolean("lockEnabled", false)
        val playTimeLimit = prefs.getInt("playTimeLimit", 0)
        val avatar = prefs.getString("avatar", "👦") ?: "👦"
        val difficulty = prefs.getString("difficulty", "medium") ?: "medium"

        val statsJsonStr = prefs.getString("stats", "{}") ?: "{}"
        val statsMap = mutableMapOf<String, GameStats>()
        try {
            val json = JSONObject(statsJsonStr)
            val keys = json.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val item = json.getJSONObject(key)
                statsMap[key] = GameStats(
                    played = item.optInt("played", 0),
                    stars = item.optInt("stars", 0)
                )
            }
        } catch (_: Exception) { }

        return UserProfile(
            name = name,
            dob = dob,
            lockEnabled = lockEnabled,
            playTimeLimit = playTimeLimit,
            avatar = avatar,
            difficulty = difficulty,
            stats = statsMap
        )
    }

    fun saveProfile(profile: UserProfile) {
        val statsJson = JSONObject()
        profile.stats.forEach { (k, v) ->
            val obj = JSONObject()
            obj.put("played", v.played)
            obj.put("stars", v.stars)
            statsJson.put(k, obj)
        }

        prefs.edit()
            .putString("name", profile.name)
            .putString("dob", profile.dob)
            .putBoolean("lockEnabled", profile.lockEnabled)
            .putInt("playTimeLimit", profile.playTimeLimit)
            .putString("avatar", profile.avatar)
            .putString("difficulty", profile.difficulty)
            .putString("stats", statsJson.toString())
            .apply()
    }

    fun recordGameWin(gameId: String, starsEarned: Int) {
        val currentProfile = getProfile()
        val currentStats = currentProfile.stats.toMutableMap()
        val existing = currentStats[gameId] ?: GameStats()
        currentStats[gameId] = existing.copy(
            played = existing.played + 1,
            stars = existing.stars + starsEarned
        )
        saveProfile(currentProfile.copy(stats = currentStats))
    }

    fun getTotalStars(): Int {
        val profile = getProfile()
        return profile.stats.values.sumOf { it.stars }
    }
}
