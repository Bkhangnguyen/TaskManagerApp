package com.example.taskmanager.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.taskmanager.MainActivity
import com.example.taskmanager.TaskManagerApp
import com.example.taskmanager.data.auth.AuthRepository
import com.example.taskmanager.data.remote.Resource
import com.example.taskmanager.data.security.SecurityPrefManager
import com.example.taskmanager.databinding.ActivityLoginBinding
import com.example.taskmanager.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val authViewModel: AuthViewModel by viewModels {
        val securityPref = SecurityPrefManager(applicationContext)
        val authRepo = AuthRepository(securityPref)
        AuthViewModel.Factory(authRepo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Kiểm tra phiên đăng nhập đã tồn tại chưa
        if (authViewModel.isLoggedIn()) {
            navigateToMain()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeAuthState()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text?.toString()?.trim().orEmpty()
            val pass = binding.etPassword.text?.toString()?.trim().orEmpty()
            authViewModel.login(email, pass)
        }

        binding.btnRegister.setOnClickListener {
            // Mở màn hình Đăng ký tài khoản riêng biệt
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Đăng nhập nhanh quyền Admin (Trưởng nhóm)
        binding.btnQuickAdmin.setOnClickListener {
            val securityPref = SecurityPrefManager(applicationContext)
            securityPref.saveUserSession("admin@taskmanager.com", "admin_uid_001", "ADMIN")
            Toast.makeText(this, "Đã đăng nhập với tư cách: Quản trị viên (Admin)", Toast.LENGTH_SHORT).show()
            navigateToMain()
        }

        // Đăng nhập nhanh quyền Thành viên (Member)
        binding.btnQuickMember.setOnClickListener {
            val securityPref = SecurityPrefManager(applicationContext)
            securityPref.saveUserSession("member@taskmanager.com", "member_uid_002", "MEMBER")
            Toast.makeText(this, "Đã đăng nhập với tư cách: Thành viên dự án", Toast.LENGTH_SHORT).show()
            navigateToMain()
        }

        // Hỗ trợ kiểm thử Offline Mode (Chế độ máy bay) khi không có mạng Internet để kết nối Firebase
        binding.btnBypassOffline.setOnClickListener {
            val securityPref = SecurityPrefManager(applicationContext)
            securityPref.saveUserSession("offline_user@taskmanager.local", "offline_uid", "ADMIN")
            navigateToMain()
        }
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.authState.collect { state ->
                    when (state) {
                        is Resource.Loading -> {
                            binding.progressBarAuth.visibility = View.VISIBLE
                            binding.btnLogin.isEnabled = false
                            binding.btnRegister.isEnabled = false
                        }
                        is Resource.Success -> {
                            binding.progressBarAuth.visibility = View.GONE
                            binding.btnLogin.isEnabled = true
                            binding.btnRegister.isEnabled = true

                            // Cập nhật thời gian đăng nhập cuối
                            val app = application as TaskManagerApp
                            val email = binding.etEmail.text?.toString()?.trim().orEmpty()
                            lifecycleScope.launch {
                                app.userAccountDao.updateLastLogin(email, System.currentTimeMillis())
                            }

                            Toast.makeText(this@LoginActivity, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                            navigateToMain()
                        }
                        is Resource.Error -> {
                            binding.progressBarAuth.visibility = View.GONE
                            binding.btnLogin.isEnabled = true
                            binding.btnRegister.isEnabled = true
                            Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_LONG).show()
                        }
                        null -> {
                            binding.progressBarAuth.visibility = View.GONE
                            binding.btnLogin.isEnabled = true
                            binding.btnRegister.isEnabled = true
                        }
                    }
                }
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
