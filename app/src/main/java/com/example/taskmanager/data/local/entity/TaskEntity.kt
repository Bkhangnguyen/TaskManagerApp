package com.example.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val assignee: String,
    val category: String,
    val status: String = "Cần làm", // "Cần làm", "Đang làm", "Cần chỉnh sửa", "Hoàn thành"
    val resultUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
