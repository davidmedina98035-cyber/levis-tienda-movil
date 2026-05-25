package com.example.levisappadmin.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val Token: String,
    val mensaje: String? = null
)