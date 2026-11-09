// #modelos #entidades #data_classes #enums
package cl.duoc.primer_mvvm.model

/**
 * #modelos - Enum de Prioridades para oportunidades comerciales.
 */
enum class Prioridad {
    ALTA,
    MEDIA,
    BAJA
}

/**
 * #modelos - Estados del flujo de vida de una oportunidad comercial.
 */
enum class EstadoOportunidad(val label: String) {
    EVENTO_IDENTIFICADO("Evento identificado"),
    CONTACTO_PENDIENTE("Contacto pendiente"),
    CONTACTO_REALIZADO("Contacto realizado"),
    PROPUESTA_ENVIADA("Propuesta enviada"),
    EN_ESPERA_RESPUESTA("En espera de respuesta"),
    PARTICIPACION_CONFIRMADA("Participación confirmada"),
    ACTIVACION_REALIZADA("Activación realizada"),
    OPORTUNIDAD_DESCARTADA("Oportunidad descartada")
}

/**
 * #modelos - Tipos de acciones de seguimiento comercial.
 */
enum class TipoAccionSeguimiento(val label: String) {
    INVESTIGACION_INICIAL("Investigación inicial"),
    PRIMER_CONTACTO("Primer contacto"),
    ENVIO_PRESENTACION("Envío de presentación"),
    ENVIO_PROPUESTA("Envío de propuesta"),
    LLAMADA("Llamada telefónica"),
    REUNION("Reunión comercial"),
    SEGUIMIENTO("Seguimiento"),
    CONFIRMACION("Confirmación"),
    CIERRE("Cierre de gestión")
}

/**
 * #modelos #alertas #aviso_alertas - Tipos de alertas y notificaciones del sistema.
 */
enum class TipoAlerta(val label: String) {
    EVENTO_PROXIMO("Evento próximo"),
    SEGUIMIENTO_PENDIENTE("Seguimiento pendiente"),
    SEGUIMIENTO_ATRASADO("Seguimiento atrasado"),
    PROPUESTA_SIN_RESPUESTA("Propuesta sin respuesta")
}

/**
 * #modelos #usuario - Entidad completa de usuario de la aplicación.
 */
data class Usuario(
    val id: String,
    val correo: String,
    val password: String,
    val nombre: String,
    val apellido: String = "",
    val rol: String = "Ejecutivo Comercial",
    val fechaNacimiento: String = "",
    val telefono: String = "+56 9 1234 5678",
    val cargo: String = "Ejecutivo Comercial",
    val departamento: String = "Ventas & Convenios",
    val fechaRegistro: String = "01/01/2026",
    val activo: Boolean = true
) {
    fun nombreCompleto(): String {
        return if (apellido.isNotBlank()) "$nombre $apellido".trim() else nombre
    }
}

/**
 * #modelos #oportunidad - Oportunidad comercial detectada.
 */
data class Oportunidad(
    val id: String,
    val nombre: String,
    val tipo: String, // Ej: Evento corporativo, Feria empresarial, Actividad municipal, Jornada de beneficios
    val descripcion: String,
    val fuenteInformacion: String,
    val fechaDeteccion: String,
    val responsableInterno: String,
    val prioridad: Prioridad,
    val potencialComercial: String, // Ej: Alto, Medio, Bajo
    val estado: EstadoOportunidad,
    val fechaUltimaActualizacion: String,
    val observaciones: String
)

/**
 * #modelos #evento - Información del evento corporativo o feria.
 */
data class Evento(
    val id: String,
    val oportunidadId: String,
    val nombre: String,
    val tipo: String,
    val fechaInicio: String,
    val fechaTermino: String,
    val horario: String,
    val direccion: String,
    val comuna: String,
    val region: String,
    val organizacionId: String,
    val publicoEsperado: String,
    val publicoObjetivo: String,
    val cantidadAsistentesEstimada: Int,
    val condicionesParticipacion: String,
    val requisitos: String,
    val costosReferenciales: String,
    val observaciones: String
)

/**
 * #modelos #organizacion - Organización responsable del evento.
 */
data class Organizacion(
    val id: String,
    val nombre: String,
    val tipo: String, // Ej: Empresa, Municipalidad, Institución pública, etc.
    val rubro: String,
    val comuna: String,
    val region: String,
    val sitioWeb: String,
    val observaciones: String
)

/**
 * #modelos #contacto - Contacto corporativo de la organización.
 */
data class Contacto(
    val id: String,
    val organizacionId: String,
    val nombre: String,
    val cargo: String,
    val area: String, // Ej: Recursos Humanos, Bienestar, Marketing, Eventos
    val correo: String,
    val telefono: String,
    val observaciones: String
)

/**
 * #modelos #seguimiento - Gestión de seguimiento ejecutada.
 */
data class Seguimiento(
    val id: String,
    val oportunidadId: String,
    val usuarioResponsable: String,
    val tipoAccion: TipoAccionSeguimiento,
    val fechaHora: String,
    val resultado: String,
    val proximaAccion: String,
    val fechaProximoSeguimiento: String,
    val observaciones: String
)

/**
 * #modelos #alertas #aviso_alertas - Objeto de alerta/notificación interna.
 */
data class Alerta(
    val id: String,
    val oportunidadId: String,
    val titulo: String,
    val descripcion: String,
    val tipo: TipoAlerta,
    val fechaHora: String,
    val leida: Boolean = false
)

/**
 * #modelos #resultado_activacion - Resultado final posterior a la realización del evento.
 */
data class ResultadoActivacion(
    val id: String,
    val oportunidadId: String,
    val fechaRealizacion: String,
    val estadoFinal: String,
    val cantidadAsistentes: Int,
    val contactosGenerados: Int,
    val personasInteresadas: Int,
    val inscripciones: Int,
    val observaciones: String,
    val evaluacionGeneral: String
)
