package com.example.taskmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.taskmanager.data.security.SecurityPrefManager
import com.example.taskmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Áp dụng Dark Mode theo cài đặt đã lưu
        val securityPref = SecurityPrefManager(this)
        if (securityPref.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hiển thị tên dự án hiện tại trên thanh tiêu đề
        binding.topAppBar.subtitle = securityPref.getCurrentProject()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Liên kết BottomNavigationView với NavController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    fun updateProjectSubtitle(projectName: String) {
        binding.topAppBar.subtitle = projectName
    }
}
