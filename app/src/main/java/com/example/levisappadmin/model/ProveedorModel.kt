package com.example.levisappadmin.model

import com.google.gson.annotations.SerializedName

data class Proveedor(
    @SerializedName("id_proveedor")
    val id: Long,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("correo")
    val correo: String,

    @SerializedName("rol_proveedor")
    val rol: String?
)

data class ProveedorRequest(
    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("correo")
    val correo: String,

    @SerializedName("rol_proveedor")
    val rol: String
)