package com.example.androidtask.model.login

data class OAuth(
    val access_token: String,
    val expires_in: Int,
    val token_type: String,
    val scope: String,
    val refresh_token: String,
)
