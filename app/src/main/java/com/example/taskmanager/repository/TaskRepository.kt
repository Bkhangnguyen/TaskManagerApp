package com.example.taskmanager.repository

import com.example.taskmanager.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TaskRepository {

    // Danh sách Mock Data khởi tạo bám sát chính xác dữ liệu từ Airtable
    private val tasksList = mutableListOf(
        Task(
            id = 1,
            title = "Soạn phần tổng quan báo cáo",
            description = "Draft các nội dung: Đặt vấn đề, mục tiêu đề tài, khảo sát hiện trạng...",
            assignee = "Dương",
            category = "Báo cáo",
            status = "Cần chỉnh sửa",
            resultUrl = "https://docs.google.com"
        ),
        Task(
            id = 2,
            title = "Nghiên cứu website Digital Art",
            description = "Tìm 5 website đang bán Digital Art, Digital Download hoặc artwork online...",
            assignee = "Khôi",
            category = "Nghiên cứu",
            status = "Hoàn thành",
            resultUrl = "https://byvn.net/hs5v"
        ),
        Task(
            id = 3,
            title = "Chuẩn bị dữ liệu 15-20 Digital Art",
            description = "Chuẩn bị dữ liệu mẫu để nhập WooCommerce. Mỗi sản phẩm bắt buộc có hình ảnh và mô tả...",
            assignee = "Khôi",
            category = "Nội dung",
            status = "Hoàn thành",
            resultUrl = "https://byvn.net/cDkq"
        ),
        Task(
            id = 4,
            title = "Tạo checklist test WooCommerce",
            description = "Chuẩn bị checklist dùng để test từ tuần 2. Các nhóm cần test: đặt hàng, giỏ hàng, thanh toán...",
            assignee = "Kiệt",
            category = "Kiểm thử",
            status = "Hoàn thành",
            resultUrl = "https://docs.google.com"
        ),
        Task(
            id = 5,
            title = "Nghiên cứu UI Digital Art",
            description = "Tìm 5 website có giao diện phù hợp với concept Digital Art...",
            assignee = "Khoa",
            category = "Giao diện",
            status = "Hoàn thành",
            resultUrl = "https://docs.google.com"
        ),
        Task(
            id = 6,
            title = "Tạo Moodboard / Style Guide ban đầu",
            description = "Đề xuất style tổng thể của website: Bảng màu, typography, phong cách thiết kế...",
            assignee = "Khoa",
            category = "Giao diện",
            status = "Hoàn thành",
            resultUrl = "https://www.figma.com"
        ),
        Task(
            id = 7,
            title = "Wireframe 4 màn hình chính",
            description = "Thiết kế wireframe cơ bản: Trang chủ, Danh mục, Chi tiết sản phẩm, Giỏ hàng...",
            assignee = "Kiệt",
            category = "UI/UX",
            status = "Hoàn thành",
            resultUrl = "https://www.figma.com"
        ),
        Task(
            id = 8,
            title = "Khởi tạo GitHub Repository",
            description = "Tạo repository công khai cho đồ án trên GitHub cá nhân, mời các thành viên vào nhóm...",
            assignee = "Dương",
            category = "Quản lý / Git",
            status = "Hoàn thành",
            resultUrl = "https://github.com/Bkhangnguyen/TaskManagerApp"
        ),
        Task(
            id = 9,
            title = "Thiết lập WordPress Local",
            description = "Thiết lập môi trường WordPress dùng để phát triển đồ án...",
            assignee = "Dương",
            category = "WordPress",
            status = "Hoàn thành",
            resultUrl = "http://localhost:8080"
        )
    )

    private val _tasksFlow = MutableStateFlow<List<Task>>(tasksList.toList())
    val tasksFlow: Flow<List<Task>> = _tasksFlow.asStateFlow()

    fun addTask(task: Task) {
        tasksList.add(0, task)
        _tasksFlow.value = tasksList.toList()
    }

    fun updateTaskStatus(taskId: Int, newStatus: String) {
        val index = tasksList.indexOfFirst { it.id == taskId }
        if (index != -1) {
            val updated = tasksList[index].copy(status = newStatus)
            tasksList[index] = updated
            _tasksFlow.value = tasksList.toList()
        }
    }
}
