package com.example.taskmanager.repository

import com.example.taskmanager.data.local.dao.TaskDao
import com.example.taskmanager.data.local.entity.TaskEntity
import com.example.taskmanager.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(
    private val taskDao: TaskDao
) {

    // Trả về Flow tự động phát tín hiệu cập nhật UI khi dữ liệu trong Room DB thay đổi
    val tasksFlow: Flow<List<Task>> = taskDao.getAllTasks().map { entities ->
        entities.map { entity ->
            Task(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                assignee = entity.assignee,
                category = entity.category,
                status = entity.status,
                resultUrl = entity.resultUrl
            )
        }
    }

    // Các hàm ghi dữ liệu được khai báo dưới dạng suspend fun chạy trong Coroutine
    suspend fun addTask(task: Task): Long {
        val entity = TaskEntity(
            title = task.title,
            description = task.description,
            assignee = task.assignee,
            category = task.category,
            status = task.status,
            resultUrl = task.resultUrl
        )
        return taskDao.insertTask(entity)
    }

    suspend fun updateTaskStatus(taskId: Int, newStatus: String) {
        taskDao.updateTaskStatus(taskId, newStatus)
    }

    suspend fun updateTask(task: Task) {
        val entity = TaskEntity(
            id = task.id,
            title = task.title,
            description = task.description,
            assignee = task.assignee,
            category = task.category,
            status = task.status,
            resultUrl = task.resultUrl
        )
        taskDao.updateTask(entity)
    }

    suspend fun deleteTask(taskId: Int) {
        val entity = TaskEntity(id = taskId, title = "", description = "", assignee = "", category = "")
        taskDao.deleteTask(entity)
    }
}
