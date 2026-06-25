package com.example.levisappadmin.model

data class User(
    val id_usuario: Int,
    val nombre: String,
    val email: String,
    val rol: String,
    val telefono: String?,
    val direccion: String?
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val telefono: String,
    val direccion: String
)

data class LoginResponse(
    val message: String,
    val user: User
)

data class RegisterResponse(
    val message: String,
    val user: User
)