package com.example.levisappadmin.model

data class Producto(
    val id_producto: Int? = null,
    val id: Int? = null,
    val nombreProducto: String? = null,
    val nombre: String? = null,
    val descripcionProducto: String? = null,
    val descripcion: String? = null,
    val precioProducto: Double? = null,
    val precio: Double = 0.0,
    val talla: String? = null,
    val categoria: String? = null,
    val stockProducto: Int? = null,
    val stock: Int = 0,
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