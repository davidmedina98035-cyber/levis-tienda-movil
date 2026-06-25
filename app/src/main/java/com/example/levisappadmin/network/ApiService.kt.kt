package com.example.levisappadmin.network

import com.example.levisappadmin.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface `ApiService` {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("productos")
    suspend fun getProductos(@Header("Authorization") token: String): Response<List<Producto>>

    @POST("productos")
    suspend fun addProducto(
        @Header("Authorization") token: String,
        @Body producto: Producto
    ): Response<Producto>

    @PUT("productos/{id}")
    suspend fun updateProducto(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body producto: Producto
    ): Response<Producto>

    @DELETE("productos/{id}")
    suspend fun deleteProducto(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    @POST("ventas")
    suspend fun crearVenta(
        @Header("Authorization") token: String,
        @Body venta: VentaRequest
    ): Response<VentaResponse>
}