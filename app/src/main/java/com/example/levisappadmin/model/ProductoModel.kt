package com.example.levisappadmin.model

import com.google.gson.annotations.SerializedName

data class Producto(
    @SerializedName("id_producto")
    val id: Int? = null,

    @SerializedName("nombreProducto")
    val nombre: String,

    @SerializedName("descripcionProducto")
    val descripcion: String? = null,

    @SerializedName("precioProducto")
    val precio: Double,

    val talla: String? = null,

    val categoria: String? = null,

    @SerializedName("stockProducto")
    val stock: Int,

    val genero: String? = null,

    val imagen: String? = null
)

data class ProductoRequest(
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val talla: String,
    val categoria: String,
    val stock: Int,
    val genero: String
)