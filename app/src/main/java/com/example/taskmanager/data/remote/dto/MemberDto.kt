package com.example.taskmanager.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MemberDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("tagColorHex")
    val tagColorHex: String
)
