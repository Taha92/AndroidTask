package com.example.androidtask.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.androidtask.model.TaskEntity

@Dao
interface TaskDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskEntity>)

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE task LIKE :query OR task_title LIKE :query OR task_description LIKE :query")
    suspend fun searchTasks(query: String): List<TaskEntity>
}