package com.example.androidtask.repository

import com.example.androidtask.model.TaskEntity
import com.example.androidtask.network.TaskApi
import com.example.androidtask.room.TaskDatabaseDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val apiService: TaskApi,
    private val taskDao: TaskDatabaseDao
) {

    suspend fun fetchTasks(): List<TaskEntity> {
        val tasksFromApi = apiService.getTasks()
        val taskEntities = tasksFromApi.map {
            TaskEntity(
                task = it.task,
                title = it.title,
                description = it.description,
                colorCode = it.colorCode,
                wageType = it.wageType,
                businessUnit = it.businessUnit
            )
        }
        taskDao.insertAll(taskEntities)
        return taskEntities
    }

    suspend fun getAllTasks(): Flow<List<TaskEntity>> {
        return taskDao.getAllTasks()
    }

    suspend fun searchTasks(query: String): Flow<List<TaskEntity>> {
        return taskDao.searchTasks("%$query%")
    }
}