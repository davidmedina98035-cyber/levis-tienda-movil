package com.example.levisappadmin.model

import com.google.gson.annotations.SerializedName

data class TallaStock(
    @SerializedName("id_talla") val idTalla: Int? = null,
    @SerializedName("talla") val talla: String? = null,
    @SerializedName("stock") val stock: Int? = null
)

data class Producto(
    @SerializedName("id_producto") val id_producto: Int? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("nombreProducto") val nombreProducto: String? = null,
    @SerializedName("descripcionProducto") val descripcionProducto: String? = null,
    @SerializedName("precioProducto") val precioProducto: Double? = null,
    @SerializedName("id_proveedor") val id_proveedor: Int? = null,
    @SerializedName("genero") val genero: String? = null,
    @SerializedName("color") val color: String? = null,
    @SerializedName("imagen") val imagen: String? = null,
    @SerializedName("id_categoria") val id_categoria: Int? = null,
    @SerializedName("categoria") val categoria: String? = null,
    @SerializedName("activo") val activo: Int? = 1, // <--- Recibe el 1 o 0 de la base de datos sin errores
    @SerializedName("tallas") val tallas: List<TallaStock>? = emptyList()
) {
    val idReal: Int
        get() = id_producto ?: id ?: 0

    val totalStock: Int
        get() = tallas?.sumOf { it.stock ?: 0 } ?: 0

    // Propiedad limpia para saber si está activo (1 = true, 0 = false)
    val estaActivo: Boolean
        get() = activo == 1
}

data class ProductoRequest(
    @SerializedName("nombreProducto") val nombreProducto: String?,
    @SerializedName("descripcionProducto") val descripcionProducto: String?,
    @SerializedName("precioProducto") val precioProducto: Double?,
    @SerializedName("categoria") val categoria: String?,
    @SerializedName("color") val color: String?,
    @SerializedName("genero") val genero: String?,
    @SerializedName("id_proveedor") val idProveedor: Int?,
    @SerializedName("imagen") val imagen: String?,
    @SerializedName("tallas") val tallas: List<TallaStockRequest>?
)

data class TallaStockRequest(
    @SerializedName("talla") val talla: String?,
    @SerializedName("stock") val stock: Int?
)