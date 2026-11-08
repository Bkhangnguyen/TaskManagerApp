package com.example.taskmanager

import android.app.Application
import com.example.taskmanager.data.local.AppDatabase
import com.example.taskmanager.repository.CategoryRepository
import com.example.taskmanager.repository.MemberRepository
import com.example.taskmanager.repository.TaskRepository

class TaskManagerApp : Application() {

    // Khởi tạo Database Singleton
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    // Khởi tạo các Repository dùng chung tầng ứng dụng (kết nối Room DB + Retrofit API)
    val taskRepository: TaskRepository by lazy {
        TaskRepository(database.taskDao(), com.example.taskmanager.data.remote.RetrofitClient.taskApiService)
    }
    val categoryRepository: CategoryRepository by lazy { CategoryRepository(database.categoryDao()) }
    val memberRepository: MemberRepository by lazy { MemberRepository(database.memberDao()) }
}
