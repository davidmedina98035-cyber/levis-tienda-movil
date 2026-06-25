package com.example.levisappadmin.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val Token: String,
    val Rol: String? = null,
    val id_usuario: Int? = null,
    val mensaje: String? = null
)
data class PerfilResponse(
    val nombre: String,
    val email: String,
    val rol: String,
    val telefono: String?,
    val direccion: String?
)

data class ActualizarPerfilRequest(
    val nombre: String,
    val email: String,
    val password: String?,
    val telefono: String?,
    val direccion: String?
)