package com.example.levisappadmin.network

import com.example.levisappadmin.model.LoginRequest
import com.example.levisappadmin.model.LoginResponse
import com.example.levisappadmin.model.Producto
import retrofit2.Response
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.example.levisappadmin.model.Usuario
import com.example.levisappadmin.model.CrearUsuarioRequest
import com.example.levisappadmin.model.ActualizarUsuarioRequest
import com.example.levisappadmin.model.VentaDetalleRaw
import com.example.levisappadmin.model.PerfilResponse
import com.example.levisappadmin.model.ActualizarPerfilRequest
import com.example.levisappadmin.model.RegisterRequest
import com.example.levisappadmin.model.VentaRequest
import com.example.levisappadmin.model.VentaResponse
import com.example.levisappadmin.model.Proveedor
import com.example.levisappadmin.model.ProveedorRequest
import com.example.levisappadmin.model.ItemCarrito
import com.example.levisappadmin.model.ItemVentaRequest

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

    // Activación / Desactivación lógica de productos
    @PUT("api/productos/{id}/estado")
    suspend fun cambiarEstadoProducto(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body estado: Map<String, Boolean>
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
        @Part("color") color: RequestBody,
        @Part("precioProducto") precio: RequestBody,
        @Part("categoria") categoria: RequestBody,
        @Part("genero") genero: RequestBody,
        @Part("id_proveedor") idProveedor: RequestBody?,
        @Part("tallas") tallas: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): Response<Producto>

    @GET("api/usuarios")
    suspend fun getUsuarios(
        @Header("Authorization") token: String
    ): Response<List<Usuario>>

    @POST("api/usuarios")
    suspend fun crearUsuario(
        @Header("Authorization") token: String,
        @Body body: CrearUsuarioRequest
    ): Response<Usuario>

    @PUT("api/usuarios/{id}")
    suspend fun actualizarUsuario(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body body: ActualizarUsuarioRequest
    ): Response<Usuario>

    // --- REPORTE DE VENTAS REACTIVADO ---
    @GET("api/productos/reporte-ventas")
    suspend fun getReporteVentas(
        @Header("Authorization") token: String
    ): Response<List<VentaDetalleRaw>>

    @GET("api/auth/perfil/{email}")
    suspend fun getPerfil(
        @Header("Authorization") token: String,
        @Path("email") email: String
    ): Response<PerfilResponse>

    @PUT("api/auth/perfil/actualizar")
    suspend fun actualizarPerfil(
        @Header("Authorization") token: String,
        @Body body: ActualizarPerfilRequest
    ): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(
        @Body body: RegisterRequest
    ): Response<LoginResponse>

    @POST("api/productos/finalizar-compra")
    suspend fun crearVenta(
        @Header("Authorization") token: String,
        @Body body: VentaRequest
    ): Response<VentaResponse>

    @POST("api/auth/recuperar")
    suspend fun recuperarPassword(
        @Body body: Map<String, String>
    ): Response<LoginResponse>

    // --- Endpoints de Proveedores ---

    @GET("api/proveedores")
    suspend fun getProveedores(
        @Header("Authorization") token: String
    ): Response<List<Proveedor>>

    @POST("api/proveedores")
    suspend fun crearProveedor(
        @Header("Authorization") token: String,
        @Body body: ProveedorRequest
    ): Response<Proveedor>

    @PUT("api/proveedores/{id}")
    suspend fun actualizarProveedor(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body body: ProveedorRequest
    ): Response<Proveedor>

    @DELETE("api/proveedores/{id}")
    suspend fun eliminarProveedor(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Unit>

    @DELETE("api/usuarios/{id}")
    suspend fun eliminarUsuario(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>
}