package com.example.androidtask.network

import com.example.androidtask.model.Task
import retrofit2.http.GET

interface TaskApi {
    @GET("v1/tasks/select")
    suspend fun getTasks(): List<Task>
}