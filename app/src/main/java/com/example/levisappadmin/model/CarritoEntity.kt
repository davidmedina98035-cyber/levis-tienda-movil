package com.example.levisappadmin.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carrito")
data class CarritoEntity(
    @PrimaryKey
    val productoId: Int,
    val nombre: String,
    val precio: Double,
    val cantidad: Int,
    val talla: String? = null,
    val categoria: String? = null
)