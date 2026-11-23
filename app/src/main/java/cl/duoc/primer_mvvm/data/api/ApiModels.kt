package cl.duoc.primer_mvvm.data.api

import com.google.gson.annotations.SerializedName

// DTO para la petición de Login
data class LoginRequest(
    @SerializedName("correo") val correo: String,
    @SerializedName("password") val password: String
)

// DTO para la respuesta de Login
data class LoginResponse(
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("usuario") val usuario: ApiUsuario
)

// DTO para Usuario desde/hacia la API Node.js
data class ApiUsuario(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("correo") val correo: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("rol") val rol: String,
    @SerializedName("password") val password: String? = null
)

// DTO para Organización
data class ApiOrganizacion(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("rubro") val rubro: String,
    @SerializedName("comuna") val comuna: String,
    @SerializedName("region") val region: String,
    @SerializedName("sitioWeb") val sitioWeb: String? = null,
    @SerializedName("observaciones") val observaciones: String? = null
)

// DTO para Contacto
data class ApiContacto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("organizacionId") val organizacionId: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("cargo") val cargo: String,
    @SerializedName("area") val area: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("observaciones") val observaciones: String? = null
)

// DTO para Oportunidad
data class ApiOportunidad(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("fuenteInformacion") val fuenteInformacion: String,
    @SerializedName("fechaDeteccion") val fechaDeteccion: String,
    @SerializedName("responsableInterno") val responsableInterno: String,
    @SerializedName("prioridad") val prioridad: String,
    @SerializedName("potencialComercial") val potencialComercial: String,
    @SerializedName("estado") val estado: String,
    @SerializedName("fechaUltimaActualizacion") val fechaUltimaActualizacion: String,
    @SerializedName("observaciones") val observaciones: String? = null
)

// DTO para Seguimiento
data class ApiSeguimiento(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("oportunidadId") val oportunidadId: Int,
    @SerializedName("usuarioResponsable") val usuarioResponsable: String,
    @SerializedName("tipoAccion") val tipoAccion: String,
    @SerializedName("fechaHora") val fechaHora: String,
    @SerializedName("resultado") val resultado: String,
    @SerializedName("proximaAccion") val proximaAccion: String? = null,
    @SerializedName("fechaProximoSeguimiento") val fechaProximoSeguimiento: String? = null,
    @SerializedName("observaciones") val observaciones: String? = null
)
