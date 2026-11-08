package com.example.taskmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.remote.Resource
import com.example.taskmanager.model.Task
import com.example.taskmanager.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    // Milestone 4: Quản lý 3 trạng thái Resource State (Loading, Success, Error)
    private val _resourceState = MutableStateFlow<Resource<List<Task>>>(Resource.Success(emptyList()))
    val resourceState: StateFlow<Resource<List<Task>>> = _resourceState.asStateFlow()

    // Bộ lọc trạng thái hiện tại (Tất cả, Cần làm, Đang làm, Cần chỉnh sửa, Hoàn thành)
    private val _selectedStatus = MutableStateFlow("Tất cả")
    val selectedStatus: StateFlow<String> = _selectedStatus

    // Bộ lọc theo Hạng mục (Báo cáo, Giao diện, Kiểm thử...)
    private val _selectedCategory = MutableStateFlow("Tất cả")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Từ khóa tìm kiếm theo thời gian thực
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // StateFlow kết hợp luồng dữ liệu từ Room Database với bộ lọc trạng thái, hạng mục và tìm kiếm
    val filteredTasks: StateFlow<List<Task>> = combine(
        repository.tasksFlow,
        _selectedStatus,
        _selectedCategory,
        _searchQuery
    ) { tasks, status, category, query ->
        tasks.filter { task ->
            val matchStatus = if (status == "Tất cả") true else task.status.equals(status, ignoreCase = true)
            val matchCategory = if (category == "Tất cả" || category.isNullOrBlank()) true else task.category.equals(category, ignoreCase = true)
            val matchQuery = if (query.isBlank()) true else {
                task.title.contains(query, ignoreCase = true) ||
                task.description.contains(query, ignoreCase = true) ||
                task.assignee.contains(query, ignoreCase = true) ||
                task.category.contains(query, ignoreCase = true)
            }
            matchStatus && matchCategory && matchQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun filterByStatus(status: String) {
        _selectedStatus.value = status
    }

    fun filterByCategory(category: String?) {
        _selectedCategory.value = category ?: "Tất cả"
    }

    fun clearCategoryFilter() {
        _selectedCategory.value = "Tất cả"
    }

    // Milestone 4: Kéo dữ liệu qua Retrofit 2 với Coroutines Dispatchers.IO và phát trạng thái Resource
    fun fetchTasksFromApi() {
        viewModelScope.launch {
            repository.syncTasksFromApi().collect { resource ->
                _resourceState.value = resource
            }
        }
    }

    fun searchTasks(query: String) {
        _searchQuery.value = query
    }

    // Thêm công việc mới: Thao tác ghi bất đồng bộ vào Room DB bằng Coroutines
    fun addTask(title: String, description: String, assignee: String, category: String, resultUrl: String?) {
        viewModelScope.launch {
            val newTask = Task(
                id = 0, // Room DB tự động sinh id tăng dần (@PrimaryKey(autoGenerate = true))
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

    // Cập nhật trạng thái công việc: UI tự động phản ứng đổi màu qua Flow
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

    // Xóa công việc (Quyền Admin)
    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    class Factory(private val repository: TaskRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TaskViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
