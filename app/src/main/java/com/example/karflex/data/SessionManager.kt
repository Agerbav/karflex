package com.example.karflex.data

import android.content.Context

// Nyimpen user yang lagi login pakai SharedPreferences
class SessionManager(context: Context) {

    private val pref = context.getSharedPreferences("karflex_session", Context.MODE_PRIVATE)

    fun saveLogin(userId: Int) {
        pref.edit().putInt("user_id", userId).apply()
    }

    fun getUserId(): Int {
        return pref.getInt("user_id", -1)
    }

    fun isLoggedIn(): Boolean {
        return getUserId() != -1
    }

    fun logout() {
        pref.edit().clear().apply()
    }
}
