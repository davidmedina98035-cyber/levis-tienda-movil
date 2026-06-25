package com.example.levisappadmin.model

data class ItemCarrito(
    val producto: Producto,
    var cantidad: Int
)

data class ItemVentaRequest(
    val id_producto: Int,
    val cantidad: Int
)

data class VentaRequest(
    val id_usuario: Int,
    val items: List<ItemVentaRequest>
)

data class VentaResponse(
    val message: String,
    val id_venta: Int,
    val total: Double
)