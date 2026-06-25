package com.example.levisappadmin.model

data class Usuario(
    val id_usuario: Int = 0,
    val nombre: String = "",
    val email: String = "",
    val rol: String = "",
    val telefono: String? = null,
    val direccion: String? = null
)

data class CrearUsuarioRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val rol: String,
    val telefono: String? = null,
    val direccion: String? = null
)

data class ActualizarUsuarioRequest(
    val nombre: String,
    val email: String,
    val rol: String,
    val telefono: String? = null,
    val direccion: String? = null
)