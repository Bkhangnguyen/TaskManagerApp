package com.example.taskmanager.model

data class Category(
    val id: Int,
    val name: String,
    val colorHex: String,
    val taskCount: Int = 0
)
