package com.example.androidtask.model

//data class TokenResponse()

data class BaseResponse<T: TokenResponse>(
    val version: String?,
    val success: Boolean,
    val payload: T?,
    val error: ErrorModel?
)

data class ErrorModel(
    val name: String?,
    val message: String?
)

data class TokenResponse(
    val accessToken: String?
)

