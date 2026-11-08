package com.example.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey
    val email: String,
    val fullName: String,
    val role: String = "MEMBER", // "ADMIN" hoặc "MEMBER"
    val registeredAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis(),
    val currentProjectName: String = "Chưa tham gia dự án"
)
