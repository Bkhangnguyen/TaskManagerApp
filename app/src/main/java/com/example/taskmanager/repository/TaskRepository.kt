package com.example.taskmanager.repository

import com.example.taskmanager.data.local.dao.TaskDao
import com.example.taskmanager.data.local.entity.TaskEntity
import com.example.taskmanager.data.remote.Resource
import com.example.taskmanager.data.remote.api.TaskApiService
import com.example.taskmanager.data.remote.dto.TaskDto
import com.example.taskmanager.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TaskRepository(
    private val taskDao: TaskDao,
    private val apiService: TaskApiService? = null
) {

    // 1. Luồng dữ liệu cục bộ từ Room DB (Offline-First)
    val tasksFlow: Flow<List<Task>> = taskDao.getAllTasks().map { entities ->
        entities.map { it.toModel() }
    }

    // 2. Milestone 4: Luồng dữ liệu tích hợp REST API với Quản lý 3 trạng thái Resource State (Loading, Success, Error)
    fun syncTasksFromApi(): Flow<Resource<List<Task>>> = flow {
        // Trạng thái 1: Bắt đầu tải dữ liệu
        emit(Resource.Loading)

        try {
            if (apiService != null) {
                // Gọi API trên luồng Dispatchers.IO
                val response = apiService.getTasks()
                if (response.isSuccessful && response.body() != null) {
                    val dtoList = response.body()!!
                    // Đồng bộ dữ liệu online vào Room Database
                    val entities = dtoList.map { dto ->
                        TaskEntity(
                            id = dto.id,
                            title = dto.title,
                            description = dto.description,
                            assignee = dto.assignee,
                            category = dto.category,
                            status = dto.status,
                            resultUrl = dto.resultUrl
                        )
                    }
                    taskDao.insertAll(entities)
                    val updated = taskDao.getAllTasks().first().map { it.toModel() }
                    // Trạng thái 2: Thành công
                    emit(Resource.Success(updated))
                } else {
                    // Khi máy chủ chưa có route API thật (HTTP 404), tự động nạp dữ liệu từ Room Database
                    val cached = taskDao.getAllTasks().first().map { it.toModel() }
                    if (cached.isEmpty()) {
                        emit(Resource.Error("Máy chủ phản hồi mã lỗi HTTP ${response.code()}."))
                    } else {
                        emit(Resource.Success(cached))
                    }
                }
            } else {
                val cached = taskDao.getAllTasks().first().map { it.toModel() }
                emit(Resource.Success(cached))
            }
        } catch (e: Exception) {
            // Trạng thái 3: Bắt lỗi khi ngắt mạng (Airplane mode) hoặc timeout
            val cached = taskDao.getAllTasks().first().map { it.toModel() }
            emit(Resource.Error("Không thể kết nối Internet (${e.localizedMessage ?: "Mất mạng"}). Chuyển sang chế độ Offline."))
            if (cached.isNotEmpty()) {
                emit(Resource.Success(cached))
            }
        }
    }.flowOn(Dispatchers.IO)

    // Các hàm ghi dữ liệu (suspend fun)
    suspend fun addTask(task: Task): Long = withContext(Dispatchers.IO) {
        val entity = TaskEntity(
            title = task.title,
            description = task.description,
            assignee = task.assignee,
            category = task.category,
            status = task.status,
            resultUrl = task.resultUrl
        )
        val id = taskDao.insertTask(entity)
        // Đồng thời đẩy lên REST API nếu có kết nối mạng
        try {
            apiService?.createTask(
                TaskDto(
                    id = id.toInt(),
                    title = task.title,
                    description = task.description,
                    assignee = task.assignee,
                    category = task.category,
                    status = task.status,
                    resultUrl = task.resultUrl
                )
            )
        } catch (_: Exception) {}
        id
    }

    suspend fun updateTaskStatus(taskId: Int, newStatus: String) = withContext(Dispatchers.IO) {
        taskDao.updateTaskStatus(taskId, newStatus)
    }

    suspend fun deleteTask(taskId: Int) = withContext(Dispatchers.IO) {
        val entity = TaskEntity(id = taskId, title = "", description = "", assignee = "", category = "")
        taskDao.deleteTask(entity)
        try {
            apiService?.deleteTask(taskId)
        } catch (_: Exception) {}
    }

    private fun TaskEntity.toModel(): Task = Task(
        id = id,
        title = title,
        description = description,
        assignee = assignee,
        category = category,
        status = status,
        resultUrl = resultUrl
    )
}
