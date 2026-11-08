package com.example.taskmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taskmanager.data.local.entity.TaskResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskResultDao {

    @Query("SELECT * FROM task_results WHERE taskId = :taskId")
    fun getResultsByTaskId(taskId: Int): Flow<List<TaskResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: TaskResultEntity): Long

    @Delete
    suspend fun deleteResult(result: TaskResultEntity)
}
