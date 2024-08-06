package com.example.androidtask.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.androidtask.model.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskEntity>)

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE task LIKE :query OR task_title LIKE :query OR task_description LIKE :query OR task_color_code LIKE :query OR wage_type LIKE :query OR business_unit LIKE :query")
    fun searchTasks(query: String): Flow<List<TaskEntity>>
}