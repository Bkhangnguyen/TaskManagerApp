package com.example.taskmanager.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TaskDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("assignee")
    val assignee: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("resultUrl")
    val resultUrl: String? = null
)
