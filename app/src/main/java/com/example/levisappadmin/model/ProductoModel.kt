package com.example.levisappadmin.model

data class Producto(
    val id_producto: Int,
    val nombreProducto: String,
    val descripcionProducto: String? = null,
    val precioProducto: Double,
    val talla: String? = null,
    val categoria: String? = null,
    val stockProducto: Int,
    val genero: String? = null,
    val imagen: String? = null
)

data class ProductoRequest(
    val nombreProducto: String,
    val descripcionProducto: String,
    val precioProducto: Double,
    val talla: String,
    val categoria: String,
    val stockProducto: Int,
    val genero: String
)