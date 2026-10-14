package cl.duoc.primer_mvvm.model

enum class Prioridad {
    ALTA,
    MEDIA,
    BAJA
}

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

enum class TipoAlerta(val label: String) {
    EVENTO_PROXIMO("Evento próximo"),
    SEGUIMIENTO_PENDIENTE("Seguimiento pendiente"),
    SEGUIMIENTO_ATRASADO("Seguimiento atrasado"),
    PROPUESTA_SIN_RESPUESTA("Propuesta sin respuesta")
}

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

data class Alerta(
    val id: String,
    val oportunidadId: String,
    val titulo: String,
    val descripcion: String,
    val tipo: TipoAlerta,
    val fechaHora: String,
    val leida: Boolean = false
)

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
