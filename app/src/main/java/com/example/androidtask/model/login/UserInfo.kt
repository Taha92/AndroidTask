package com.example.androidtask.model.login

data class UserInfo(
    val personalNo: Int,
    val firstName: String,
    val lastName: String,
    val displayName: String,
    val active: Boolean,
    val businessUnit: String,

)
