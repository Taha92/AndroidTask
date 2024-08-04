package com.example.androidtask.Room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.androidtask.model.TaskEntity
import dagger.Provides
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskEntity>)

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE task LIKE :query OR task_title LIKE :query OR task_description LIKE :query")
    fun searchTasks(query: String): Flow<List<TaskEntity>>
}