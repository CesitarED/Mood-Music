package com.example.moodmusic.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("mood_music_prefs", Context.MODE_PRIVATE)

    companion object {
        const val USER_NAME = "user_name"
        const val IS_LOGGED_IN = "is_logged_in"
    }

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
        editor.clear()
        editor.apply()
    }
}
