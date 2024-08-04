package com.example.androidtask.repository

import com.example.androidtask.Room.TaskDatabaseDao
import com.example.androidtask.model.TaskEntity
import com.example.androidtask.network.TaskApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/*
class TaskRepository @Inject constructor(private val taskDatabaseDao: TaskDatabaseDao) {
    suspend fun searchTasks(query: String): Flow<List<TaskEntity>> = taskDatabaseDao.searchTasks(query)
    suspend fun getAllTasks(): Flow<List<TaskEntity>> = taskDatabaseDao.getAllTasks().flowOn(Dispatchers.IO)
        .conflate()

}
*/

class TaskRepository @Inject constructor(
    private val apiService: TaskApi,
    private val taskDao: TaskDatabaseDao
) {

    suspend fun fetchTasks(): List<TaskEntity> {
        val tasksFromApi = apiService.getTasks()
        val taskEntities = tasksFromApi.map {
            TaskEntity(task = it.task, title = it.title, description = it.description, colorCode = it.colorCode)
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