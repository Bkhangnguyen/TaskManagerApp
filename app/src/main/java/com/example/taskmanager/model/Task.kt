package com.example.taskmanager.model

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val assignee: String,
    val category: String,
    val status: String, // "Cần làm", "Đang làm", "Cần chỉnh sửa", "Hoàn thành"
    val resultUrl: String? = null
)
