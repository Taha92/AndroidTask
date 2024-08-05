package com.example.androidtask.model

import javax.inject.Inject
import javax.inject.Singleton

class AuthTokenProvider @Inject constructor() {
    private var token: String = ""

    fun setToken(token: String) {
        this.token = token
    }

    fun getToken(): String {
        return token
    }
}