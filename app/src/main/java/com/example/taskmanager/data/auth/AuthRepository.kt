package com.example.taskmanager.data.auth

import com.example.taskmanager.data.remote.Resource
import com.example.taskmanager.data.security.SecurityPrefManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// Milestone 5: Xác thực tài khoản thật bằng Firebase Authentication
class AuthRepository(
    private val securityPrefManager: SecurityPrefManager
) {
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    fun isLoggedIn(): Boolean = firebaseAuth.currentUser != null || securityPrefManager.isLoggedIn()

    suspend fun login(email: String, pass: String): Resource<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            val user = result.user
            if (user != null) {
                val role = if (email.startsWith("admin", ignoreCase = true)) "ADMIN" else "MEMBER"
                securityPrefManager.saveUserSession(user.email.orEmpty(), user.uid, role)
                Resource.Success(user)
            } else {
                Resource.Error("Đăng nhập thất bại. Người dùng không tồn tại.")
            }
        } catch (e: Exception) {
            Resource.Error("Lỗi đăng nhập: ${e.localizedMessage ?: "Vui lòng kiểm tra lại tài khoản mật khẩu"}")
        }
    }

    suspend fun register(email: String, pass: String): Resource<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            val user = result.user
            if (user != null) {
                val role = if (email.startsWith("admin", ignoreCase = true)) "ADMIN" else "MEMBER"
                securityPrefManager.saveUserSession(user.email.orEmpty(), user.uid, role)
                Resource.Success(user)
            } else {
                Resource.Error("Đăng ký thất bại.")
            }
        } catch (e: Exception) {
            Resource.Error("Lỗi đăng ký: ${e.localizedMessage ?: "Email đã được sử dụng hoặc mật khẩu quá ngắn"}")
        }
    }

    fun logout() {
        firebaseAuth.signOut()
        securityPrefManager.clearSession()
    }
}
