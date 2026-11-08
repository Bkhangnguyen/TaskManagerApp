package com.example.taskmanager.data.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

// Milestone 5: Quản lý lưu trữ bảo mật bằng EncryptedSharedPreferences (chuẩn mã hóa AES-256)
class SecurityPrefManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_task_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveUserSession(email: String, uid: String, role: String = "MEMBER") {
        sharedPreferences.edit()
            .putString("USER_EMAIL", email)
            .putString("USER_UID", uid)
            .putString("USER_ROLE", role)
            .putBoolean("IS_LOGGED_IN", true)
            .apply()
    }

    fun getUserEmail(): String? = sharedPreferences.getString("USER_EMAIL", null)

    fun getUserRole(): String = sharedPreferences.getString("USER_ROLE", "MEMBER") ?: "MEMBER"

    fun isAdmin(): Boolean = getUserRole().equals("ADMIN", ignoreCase = true)

    fun isLoggedIn(): Boolean = sharedPreferences.getBoolean("IS_LOGGED_IN", false)

    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}
