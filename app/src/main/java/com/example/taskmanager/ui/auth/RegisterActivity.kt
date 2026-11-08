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
import com.example.taskmanager.databinding.ActivityRegisterBinding
import com.example.taskmanager.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val authViewModel: AuthViewModel by viewModels {
        val securityPref = SecurityPrefManager(applicationContext)
        val authRepo = AuthRepository(securityPref)
        AuthViewModel.Factory(authRepo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeAuthState()
    }

    private fun setupListeners() {
        binding.btnBackToLogin.setOnClickListener {
            finish() // Quay lại màn hình Login
        }

        binding.btnSubmitRegister.setOnClickListener {
            val fullName = binding.etFullName.text?.toString()?.trim().orEmpty()
            val email = binding.etRegisterEmail.text?.toString()?.trim().orEmpty()
            val pass = binding.etRegisterPassword.text?.toString()?.trim().orEmpty()
            val confirmPass = binding.etConfirmPassword.text?.toString()?.trim().orEmpty()

            if (fullName.isBlank()) {
                binding.etFullName.error = "Vui lòng nhập họ và tên"
                return@setOnClickListener
            }
            if (email.isBlank()) {
                binding.etRegisterEmail.error = "Vui lòng nhập email"
                return@setOnClickListener
            }
            if (pass.length < 6) {
                binding.etRegisterPassword.error = "Mật khẩu phải từ 6 ký tự trở lên"
                return@setOnClickListener
            }
            if (pass != confirmPass) {
                binding.etConfirmPassword.error = "Mật khẩu xác nhận không khớp"
                return@setOnClickListener
            }

            authViewModel.register(email, pass)
        }
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.authState.collect { state ->
                    when (state) {
                        is Resource.Loading -> {
                            binding.progressBarRegister.visibility = View.VISIBLE
                            binding.btnSubmitRegister.isEnabled = false
                        }
                        is Resource.Success -> {
                            binding.progressBarRegister.visibility = View.GONE
                            binding.btnSubmitRegister.isEnabled = true

                            // Ghi nhận tài khoản vào CSDL người dùng (Hỗ trợ Admin theo dõi & Xuất CSV)
                            val app = application as TaskManagerApp
                            val now = System.currentTimeMillis()
                            val fullName = binding.etFullName.text?.toString()?.trim().orEmpty()
                            val email = binding.etRegisterEmail.text?.toString()?.trim().orEmpty()
                            lifecycleScope.launch {
                                app.userAccountDao.insertOrUpdate(
                                    com.example.taskmanager.data.local.entity.UserAccountEntity(
                                        email = email,
                                        fullName = fullName,
                                        role = if (email.startsWith("admin", ignoreCase = true)) "ADMIN" else "MEMBER",
                                        registeredAt = now,
                                        lastLoginAt = now,
                                        currentProjectName = "Chưa tham gia dự án (Cần nhập mã mời)"
                                    )
                                )
                            }

                            Toast.makeText(this@RegisterActivity, "Đăng ký tài khoản thành công!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@RegisterActivity, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                            finish()
                        }
                        is Resource.Error -> {
                            binding.progressBarRegister.visibility = View.GONE
                            binding.btnSubmitRegister.isEnabled = true
                            Toast.makeText(this@RegisterActivity, state.message, Toast.LENGTH_LONG).show()
                        }
                        null -> {
                            binding.progressBarRegister.visibility = View.GONE
                            binding.btnSubmitRegister.isEnabled = true
                        }
                    }
                }
            }
        }
    }
}
