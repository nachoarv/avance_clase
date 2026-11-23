package cl.duoc.primer_mvvm.data.api

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Login: POST /api/login
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // Usuarios: GET, POST, PUT, DELETE
    @GET("usuarios")
    suspend fun getUsuarios(): Response<List<ApiUsuario>>

    @GET("usuarios/{id}")
    suspend fun getUsuarioById(@Path("id") id: Int): Response<ApiUsuario>

    @POST("usuarios")
    suspend fun crearUsuario(@Body usuario: ApiUsuario): Response<ApiUsuario>

    @PUT("usuarios/{id}")
    suspend fun actualizarUsuario(@Path("id") id: Int, @Body usuario: ApiUsuario): Response<ApiUsuario>

    @DELETE("usuarios/{id}")
    suspend fun eliminarUsuario(@Path("id") id: Int): Response<Unit>

    // Organizaciones: GET, POST
    @GET("organizaciones")
    suspend fun getOrganizaciones(): Response<List<ApiOrganizacion>>

    @POST("organizaciones")
    suspend fun crearOrganizacion(@Body org: ApiOrganizacion): Response<ApiOrganizacion>

    // Contactos: GET, POST
    @GET("contactos")
    suspend fun getContactos(): Response<List<ApiContacto>>

    @POST("contactos")
    suspend fun crearContacto(@Body contacto: ApiContacto): Response<ApiContacto>

    // Oportunidades: GET, POST, PUT, DELETE
    @GET("oportunidades")
    suspend fun getOportunidades(): Response<List<ApiOportunidad>>

    @GET("oportunidades/{id}")
    suspend fun getOportunidadById(@Path("id") id: Int): Response<ApiOportunidad>

    @POST("oportunidades")
    suspend fun crearOportunidad(@Body op: ApiOportunidad): Response<ApiOportunidad>

    @PUT("oportunidades/{id}")
    suspend fun actualizarOportunidad(@Path("id") id: Int, @Body op: ApiOportunidad): Response<ApiOportunidad>

    @DELETE("oportunidades/{id}")
    suspend fun eliminarOportunidad(@Path("id") id: Int): Response<Unit>

    // Seguimientos: GET, POST
    @GET("seguimientos")
    suspend fun getSeguimientos(): Response<List<ApiSeguimiento>>

    @POST("seguimientos")
    suspend fun crearSeguimiento(@Body seguimiento: ApiSeguimiento): Response<ApiSeguimiento>
}
