package com.raihanardila.bapps.core.data.local.prefrences

import android.content.Context
import android.content.SharedPreferences

class AuthPreferences (
    context: Context
) {

    companion object {
        private const val PREFS_NAME = "auth_prefs"
        private const val TOKEN_KEY = "auth_token"
    }

    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Menyimpan token
    fun saveToken(token: String) {
        val editor = preferences.edit()
        editor.putString(TOKEN_KEY, token)
        editor.apply()
    }

    // Mengambil token
    fun getToken(): String? {
        return preferences.getString(TOKEN_KEY, null)
    }

    // Menghapus token
    fun clearToken() {
        val editor = preferences.edit()
        editor.remove(TOKEN_KEY)
        editor.apply()
    }
}
