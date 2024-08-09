package com.example.androidtask.network

import com.example.androidtask.model.Task
import com.example.androidtask.model.login.LoginResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface TaskApi {
    @GET("v1/tasks/select")
    suspend fun getTasks(): List<Task>

    @POST("login")
    suspend fun login(@Header("Authorization") auth: String, @Body credentials: RequestBody): LoginResponse

}