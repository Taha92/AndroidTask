package com.example.androidtask.model

import javax.inject.Inject

class AuthTokenProvider @Inject constructor() {
    private var token: String = ""

    fun setToken(token: String) {
        this.token = token
    }

    fun getToken(): String {
        return token
    }
}