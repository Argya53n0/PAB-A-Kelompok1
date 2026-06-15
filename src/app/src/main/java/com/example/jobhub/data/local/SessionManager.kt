package com.example.jobhub.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        const val PREF_NAME = "jobhub_prefs"
        const val USER_TOKEN = "user_token"
    }

    /**
     * Function to save auth token
     */
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    /**
     * Function to clear session/token (logout)
     */
    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}
