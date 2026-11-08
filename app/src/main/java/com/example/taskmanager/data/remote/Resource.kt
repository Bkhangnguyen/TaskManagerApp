package com.example.taskmanager.data.remote

// Sealed Class chuẩn mực quản lý trọn vẹn 3 trạng thái giao diện theo yêu cầu Milestone 4:
// Loading (hiển thị ProgressBar), Success (nạp dữ liệu), Error (báo lỗi khi ngắt mạng / HTTP 4xx, 5xx)
sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val cause: Throwable? = null) : Resource<Nothing>()
}
