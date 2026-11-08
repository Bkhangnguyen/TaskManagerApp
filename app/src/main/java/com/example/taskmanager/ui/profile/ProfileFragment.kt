package com.example.taskmanager.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.taskmanager.R
import com.example.taskmanager.data.auth.AuthRepository
import com.example.taskmanager.data.security.SecurityPrefManager
import com.example.taskmanager.databinding.FragmentProfileBinding
import com.example.taskmanager.ui.auth.LoginActivity

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

        // Hiển thị email đã đăng nhập từ Firebase hoặc EncryptedSharedPreferences
        val email = authRepository.currentUser?.email ?: securityPref.getUserEmail() ?: "user@taskmanager.com"
        binding.tvProfileName.text = email.substringBefore("@")
        binding.tvProfileRole.text = email

        // Milestone 5: Tải ảnh avatar bất đồng bộ mượt mà bằng thư viện Glide
        val avatarUrl = "https://picsum.photos/300/300"
        Glide.with(this)
            .load(avatarUrl)
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .circleCrop()
            .into(binding.ivProfileAvatar)

        // Xử lý Đăng xuất
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
