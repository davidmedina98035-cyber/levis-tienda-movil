package com.example.levisappadmin.network

import com.example.levisappadmin.model.LoginRequest
import com.example.levisappadmin.model.LoginResponse
import com.example.levisappadmin.model.Producto
import retrofit2.Response
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.example.levisappadmin.model.ProductoRequest

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @GET("api/productos")
    suspend fun getProductos(
        @Header("Authorization") token: String
    ): Response<List<Producto>>

    @DELETE("api/productos/{id}")
    suspend fun deleteProducto(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    @Multipart
    @POST("api/productos")
    suspend fun crearProducto(
        @Header("Authorization") token: String,
        @Part("nombreProducto") nombre: RequestBody,
        @Part("descripcionProducto") descripcion: RequestBody,
        @Part("precioProducto") precio: RequestBody,
        @Part("talla") talla: RequestBody,
        @Part("categoria") categoria: RequestBody,
        @Part("stockProducto") stock: RequestBody,
        @Part("genero") genero: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): Response<Producto>

    @Multipart
    @PUT("api/productos/{id}")
    suspend fun actualizarProducto(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Part("nombreProducto") nombre: RequestBody,
        @Part("descripcionProducto") descripcion: RequestBody,
        @Part("precioProducto") precio: RequestBody,
        @Part("talla") talla: RequestBody,
        @Part("categoria") categoria: RequestBody,
        @Part("stockProducto") stock: RequestBody,
        @Part("genero") genero: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): Response<Producto>
}
