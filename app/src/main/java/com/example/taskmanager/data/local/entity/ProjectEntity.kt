package com.example.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val inviteCode: String, // Mã mời để thành viên nhập vào tham gia
    val ownerEmail: String,
    val isPersonal: Boolean = false, // true: Dự án cá nhân của chính mình, false: Dự án nhóm
    val createdAt: Long = System.currentTimeMillis()
)
