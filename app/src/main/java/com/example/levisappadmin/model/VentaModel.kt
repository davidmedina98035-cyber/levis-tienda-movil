package com.example.levisappadmin.model

data class ItemCarrito(
    val producto: Producto, // Cambiado de ProductoModel a Producto
    val cantidad: Int
)

data class ItemVentaRequest(
    val id_producto: Int,
    val cantidad: Int,
    val talla: String?
)

data class VentaRequest(
    val total_venta: Double,
    val productos: List<ItemVentaRequest>
)

data class VentaResponse(
    val mensaje: String,
    val id_venta: Int?
)

data class VentaDetalleRaw(
    val id_venta: Int,
    val fecha: String?,
    val total_venta: Double,
    val nombreProducto: String?,
    val cantidad: Int,
    val precioUnitario: Double,
    val imagen: String?,
    val talla: String?,
    val nombre_usuario: String?,
    val email_usuario: String?
)

data class ClienteConVentas(
    val nombre_usuario: String,
    val email_usuario: String,
    val ventas: List<VentaAgrupada>
)

data class VentaAgrupada(
    val id_venta: Int,
    val fecha: String,
    val total_venta: Double,
    val productos: List<DetalleProducto>
)

data class DetalleProducto(
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double,
    val imagen: String?,
    val talla: String
)