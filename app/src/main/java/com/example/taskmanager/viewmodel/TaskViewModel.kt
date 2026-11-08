package com.example.taskmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.model.Task
import com.example.taskmanager.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository = TaskRepository()
) : ViewModel() {

    // Bộ lọc trạng thái hiện tại (Tất cả, Cần làm, Đang làm, Cần chỉnh sửa, Hoàn thành)
    private val _selectedStatus = MutableStateFlow("Tất cả")
    val selectedStatus: StateFlow<String> = _selectedStatus

    // Từ khóa tìm kiếm
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // StateFlow kết hợp danh sách từ Repository với bộ lọc và tìm kiếm
    val filteredTasks: StateFlow<List<Task>> = combine(
        repository.tasksFlow,
        _selectedStatus,
        _searchQuery
    ) { tasks, status, query ->
        tasks.filter { task ->
            val matchStatus = if (status == "Tất cả") true else task.status.equals(status, ignoreCase = true)
            val matchQuery = if (query.isBlank()) true else {
                task.title.contains(query, ignoreCase = true) ||
                task.description.contains(query, ignoreCase = true) ||
                task.assignee.contains(query, ignoreCase = true)
            }
            matchStatus && matchQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun filterByStatus(status: String) {
        _selectedStatus.value = status
    }

    fun searchTasks(query: String) {
        _searchQuery.value = query
    }

    fun addTask(title: String, description: String, assignee: String, category: String, resultUrl: String?) {
        viewModelScope.launch {
            val newTask = Task(
                id = System.currentTimeMillis().toInt(),
                title = title,
                description = description,
                assignee = assignee.ifBlank { "Chưa phân công" },
                category = category.ifBlank { "Khác" },
                status = "Cần làm",
                resultUrl = resultUrl
            )
            repository.addTask(newTask)
        }
    }

    fun toggleTaskStatus(taskId: Int, currentStatus: String) {
        viewModelScope.launch {
            val nextStatus = when (currentStatus) {
                "Cần làm" -> "Đang làm"
                "Đang làm" -> "Hoàn thành"
                "Cần chỉnh sửa" -> "Hoàn thành"
                else -> "Cần làm"
            }
            repository.updateTaskStatus(taskId, nextStatus)
        }
    }
}
