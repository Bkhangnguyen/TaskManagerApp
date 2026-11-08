package com.example.taskmanager.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.taskmanager.R
import com.example.taskmanager.data.auth.AuthRepository
import com.example.taskmanager.data.security.SecurityPrefManager
import com.example.taskmanager.databinding.FragmentProfileBinding
import com.example.taskmanager.ui.admin.AdminAccountsActivity
import com.example.taskmanager.ui.auth.LoginActivity
import com.example.taskmanager.ui.project.ProjectManageDialog

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var securityPref: SecurityPrefManager
    private lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        securityPref = SecurityPrefManager(requireContext())
        authRepository = AuthRepository(securityPref)

        // 1. Hiển thị thông tin người dùng và vai trò
        val email = authRepository.currentUser?.email ?: securityPref.getUserEmail() ?: "user@taskmanager.com"
        val roleLabel = if (securityPref.isAdmin()) "👑 Quản trị viên (Trưởng nhóm / PM)" else "👤 Thành viên dự án (Member)"

        binding.tvProfileName.text = email.substringBefore("@")
        binding.tvProfileRole.text = "$roleLabel\n$email"
        binding.tvCurrentProjectProfile.text = "Dự án: ${securityPref.getCurrentProject()}"

        // 2. Tải ảnh avatar bằng Glide
        val avatarUrl = "https://picsum.photos/300/300"
        Glide.with(this)
            .load(avatarUrl)
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .circleCrop()
            .into(binding.ivProfileAvatar)

        // 3. Quản lý Dark Mode (Giao diện tối)
        binding.switchDarkMode.isChecked = securityPref.isDarkMode()
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            securityPref.setDarkMode(isChecked)
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // 4. Mở hộp thoại Quản lý Dự án & Mã mời
        binding.btnOpenProjectManage.setOnClickListener {
            val dialog = ProjectManageDialog { updatedProjectName, _ ->
                binding.tvCurrentProjectProfile.text = "Dự án: $updatedProjectName"
            }
            dialog.show(childFragmentManager, "ProjectManageDialog")
        }

        // 5. Phân quyền Admin: Nút xem tài khoản & xuất file CSV
        if (securityPref.isAdmin()) {
            binding.btnAdminAccounts.visibility = View.VISIBLE
            binding.btnAdminAccounts.setOnClickListener {
                val intent = Intent(requireContext(), AdminAccountsActivity::class.java)
                startActivity(intent)
            }
        } else {
            binding.btnAdminAccounts.visibility = View.GONE
        }

        // 6. Xử lý Đăng xuất
        binding.btnLogout.setOnClickListener {
            authRepository.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
