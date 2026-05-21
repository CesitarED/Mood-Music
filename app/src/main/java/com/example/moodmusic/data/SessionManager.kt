package com.example.moodmusic.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("mood_music_prefs", Context.MODE_PRIVATE)

    companion object {
        const val USER_NAME = "user_name"
        const val IS_LOGGED_IN = "is_logged_in"
        const val DARK_MODE = "dark_mode"
    }

    fun getPrefs(): SharedPreferences = prefs

    fun saveSession(username: String) {
        val editor = prefs.edit()
        editor.putString(USER_NAME, username)
        editor.putBoolean(IS_LOGGED_IN, true)
        editor.apply()
    }

    fun getUsername(): String? {
        return prefs.getString(USER_NAME, null)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGGED_IN, false)
    }

    fun logout() {
        val editor = prefs.edit()
        editor.remove(USER_NAME)
        editor.remove(IS_LOGGED_IN)
        editor.apply()
    }

    fun isDarkMode(): Boolean {
        return prefs.getBoolean(DARK_MODE, false)
    }

    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(DARK_MODE, enabled).apply()
    }
}
