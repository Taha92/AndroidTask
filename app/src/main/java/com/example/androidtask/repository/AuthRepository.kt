package com.example.androidtask.repository

import com.example.androidtask.model.Login
import com.example.androidtask.network.TaskApi
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import javax.inject.Inject

class AuthRepository @Inject constructor(private val apiService: TaskApi) {

    suspend fun getAccessToken(username: String, password: String): String {
        val credentials = Login(username, password)
        val json = Gson().toJson(credentials)
        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json)
        val response = apiService.login("Basic QVBJX0V4cGxvcmVyOjEyMzQ1NmlzQUxhbWVQYXNz",body)
        return response.accessToken.toString()
    }
}