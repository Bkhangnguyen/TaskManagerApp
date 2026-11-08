package com.example.taskmanager.model

data class Member(
    val id: Int,
    val name: String,
    val role: String,
    val assignedTasksCount: Int = 0,
    val tagColorHex: String = "#3B82F6"
)
