package com.example.levisappadmin.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    @SerializedName("token", alternate = ["Token"])
    val Token: String? = null,

    @SerializedName("rol", alternate = ["Rol"])
    val Rol: String? = null,

    @SerializedName("id_usuario", alternate = ["id", "userId"])
    val id_usuario: Int? = null,

    val mensaje: String? = null
)

data class RegisterRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val rol: String = "cliente",
    val telefono: String? = null,
    val direccion: String? = null
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