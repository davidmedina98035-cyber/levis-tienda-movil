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
    import com.example.levisappadmin.model.ProductoRequest
    import com.example.levisappadmin.model.VentaDetalleRaw
    import com.example.levisappadmin.model.PerfilResponse
    import com.example.levisappadmin.model.ActualizarPerfilRequest
    import com.example.levisappadmin.model.RegisterRequest
    import com.example.levisappadmin.model.ItemVentaRequest
    import com.example.levisappadmin.model.VentaRequest
    import com.example.levisappadmin.model.VentaResponse
    
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
    
        @DELETE("api/usuarios/{id}")
        suspend fun eliminarUsuario(
            @Header("Authorization") token: String,
            @Path("id") id: Int
        ): Response<Unit>
    
        @GET("api/productos/ReporteVentas")
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

        @POST("api/productos/finalizar-compra")        suspend fun crearVenta(
            @Header("Authorization") token: String,
            @Body body: VentaRequest
        ): Response<VentaResponse>
    
        @POST("api/auth/recuperar")
        suspend fun recuperarPassword(
            @Body body: Map<String, String>
        ): Response<LoginResponse>
    }
