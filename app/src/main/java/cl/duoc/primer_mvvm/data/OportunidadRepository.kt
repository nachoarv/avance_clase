package cl.duoc.primer_mvvm.data

import cl.duoc.primer_mvvm.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

object OportunidadRepository {

    private val _organizaciones = MutableStateFlow<List<Organizacion>>(emptyList())
    val organizaciones: StateFlow<List<Organizacion>> = _organizaciones.asStateFlow()

    private val _contactos = MutableStateFlow<List<Contacto>>(emptyList())
    val contactos: StateFlow<List<Contacto>> = _contactos.asStateFlow()

    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> = _eventos.asStateFlow()

    private val _oportunidades = MutableStateFlow<List<Oportunidad>>(emptyList())
    val oportunidades: StateFlow<List<Oportunidad>> = _oportunidades.asStateFlow()

    private val _seguimientos = MutableStateFlow<List<Seguimiento>>(emptyList())
    val seguimientos: StateFlow<List<Seguimiento>> = _seguimientos.asStateFlow()

    private val _alertas = MutableStateFlow<List<Alerta>>(emptyList())
    val alertas: StateFlow<List<Alerta>> = _alertas.asStateFlow()

    private val _resultados = MutableStateFlow<List<ResultadoActivacion>>(emptyList())
    val resultados: StateFlow<List<ResultadoActivacion>> = _resultados.asStateFlow()

    init {
        cargarDatosFicticiosIniciales()
    }

    private fun cargarDatosFicticiosIniciales() {
        val org1 = Organizacion(
            id = "org_1",
            nombre = "Municipalidad de Puente Alto",
            tipo = "Municipalidad",
            rubro = "Sector Público / Gobierno Local",
            comuna = "Puente Alto",
            region = "Región Metropolitana",
            sitioWeb = "www.puentealto-ficticio.cl",
            observaciones = "Contacto recurrente para ferias comunitarias y de bienestar laboral."
        )

        val org2 = Organizacion(
            id = "org_2",
            nombre = "Clínicas Dentales Integral Chile",
            tipo = "Empresa Privada",
            rubro = "Salud Dental",
            comuna = "Providencia",
            region = "Región Metropolitana",
            sitioWeb = "www.dentalintegral-ficticio.cl",
            observaciones = "Aliado comercial para promociones corporativas."
        )

        val org3 = Organizacion(
            id = "org_3",
            nombre = "Banco Metropolitano S.A.",
            tipo = "Empresa Privada",
            rubro = "Financiero",
            comuna = "Santiago Centro",
            region = "Región Metropolitana",
            sitioWeb = "www.bancometro-ficticio.cl",
            observaciones = "Gran número de colaboradores en jornada de beneficios anual."
        )

        _organizaciones.value = listOf(org1, org2, org3)

        val c1 = Contacto(
            id = "cont_1",
            organizacionId = "org_1",
            nombre = "Valeria Morales Ficticia",
            cargo = "Coordinadora de Eventos y Bienestar",
            area = "Bienestar Social",
            correo = "v.morales@puentealto-ficticio.cl",
            telefono = "+56 9 8765 4321",
            observaciones = "Prefiere contacto por correo en mañanas."
        )

        val c2 = Contacto(
            id = "cont_2",
            organizacionId = "org_3",
            nombre = "Carlos Fuentes Ficticio",
            cargo = "Subgerente de Recursos Humanos",
            area = "Recursos Humanos",
            correo = "cfuentes@bancometro-ficticio.cl",
            telefono = "+56 9 1234 5678",
            observaciones = "Interesado en alianzas veterinarias y dentales para colaboradores."
        )

        _contactos.value = listOf(c1, c2)

        val op1 = Oportunidad(
            id = "op_1",
            nombre = "Feria de Beneficios Laborales Puente Alto 2026",
            tipo = "Jornada de beneficios",
            descripcion = "Espacio para stand de convenios dentales y atención clínica veterinaria para colaboradores.",
            fuenteInformacion = "Portal web municipal",
            fechaDeteccion = "10/02/2026",
            responsableInterno = "Ejecutivo Comercial - Juan Pérez",
            prioridad = Prioridad.ALTA,
            potencialComercial = "Alto (Estimado > 2.000 asistentes)",
            estado = EstadoOportunidad.PROPUESTA_ENVIADA,
            fechaUltimaActualizacion = "20/02/2026",
            observaciones = "Propuesta comercial enviada para instalar 2 modulos de atencion."
        )

        val ev1 = Evento(
            id = "ev_1",
            oportunidadId = "op_1",
            nombre = "Feria de Beneficios Laborales 2026",
            tipo = "Jornada de beneficios",
            fechaInicio = "15/03/2026",
            fechaTermino = "16/03/2026",
            horario = "09:00 - 18:00 hrs",
            direccion = "Av. Concha y Toro 1820",
            comuna = "Puente Alto",
            region = "Región Metropolitana",
            organizacionId = "org_1",
            publicoEsperado = "Trabajadores municipales y familias",
            publicoObjetivo = "Adultos y familias interesadas en salud dental y mascotas",
            cantidadAsistentesEstimada = 2500,
            condicionesParticipacion = "Modulo de 3x3m provisto por la municipalidad.",
            requisitos = "Llevar pendón e información impresa.",
            costosReferenciales = "$0 CLP (Convenio Gratuito)",
            observaciones = "Ubicación privilegiada cerca de la entrada principal."
        )

        val op2 = Oportunidad(
            id = "op_2",
            nombre = "Expo Salud y Bienestar Banco Metropolitano",
            tipo = "Feria empresarial",
            descripcion = "Jornada corporativa anual en casa matriz para 1.500 bancarios.",
            fuenteInformacion = "Redes Sociales / LinkedIn",
            fechaDeteccion = "15/02/2026",
            responsableInterno = "Ejecutiva Comercial - María González",
            prioridad = Prioridad.ALTA,
            potencialComercial = "Alto",
            estado = EstadoOportunidad.CONTACTO_REALIZADO,
            fechaUltimaActualizacion = "22/02/2026",
            observaciones = "Reunión virtual agendada con la gerencia de personas."
        )

        val ev2 = Evento(
            id = "ev_2",
            oportunidadId = "op_2",
            nombre = "Expo Salud Banco Metropolitano",
            tipo = "Feria empresarial",
            fechaInicio = "28/03/2026",
            fechaTermino = "28/03/2026",
            horario = "10:00 - 16:00 hrs",
            direccion = "Moneda 1120",
            comuna = "Santiago Centro",
            region = "Región Metropolitana",
            organizacionId = "org_3",
            publicoEsperado = "Colaboradores del banco",
            publicoObjetivo = "Familias con necesidad de servicios automotrices y dentales",
            cantidadAsistentesEstimada = 1500,
            condicionesParticipacion = "Requiere acreditación previa de personal.",
            requisitos = "Seguro contra accidentes para promotores.",
            costosReferenciales = "$150.000 CLP (Derecho de Stand)",
            observaciones = "Alta conversión esperada."
        )

        val op3 = Oportunidad(
            id = "op_3",
            nombre = "Activación Automotriz en Feria Empresarial Concepción",
            tipo = "Activación comercial",
            descripcion = "Muestra de servicios mecánicos y convenios automotrices corporativos.",
            fuenteInformacion = "Contacto directo",
            fechaDeteccion = "05/01/2026",
            responsableInterno = "Ejecutivo Comercial - Andrés Soto",
            prioridad = Prioridad.MEDIA,
            potencialComercial = "Medio",
            estado = EstadoOportunidad.EVENTO_IDENTIFICADO,
            fechaUltimaActualizacion = "10/01/2026",
            observaciones = "Pendiente identificar al encargado del evento."
        )

        val ev3 = Evento(
            id = "ev_3",
            oportunidadId = "op_3",
            nombre = "Feria Industrial Concepción 2026",
            tipo = "Feria empresarial",
            fechaInicio = "10/04/2026",
            fechaTermino = "12/04/2026",
            horario = "09:00 - 19:00 hrs",
            direccion = "Av. Paicaví 3000",
            comuna = "Concepción",
            region = "Región del Biobío",
            organizacionId = "org_2",
            publicoEsperado = "Ejecutivos y personal de transportes",
            publicoObjetivo = "Conductores y administradores de flota",
            cantidadAsistentesEstimada = 3000,
            condicionesParticipacion = "Pendiente por confirmar.",
            requisitos = "Formulario de postulación.",
            costosReferenciales = "Por definir",
            observaciones = "Requiere viajar a Concepción."
        )

        _oportunidades.value = listOf(op1, op2, op3)
        _eventos.value = listOf(ev1, ev2, ev3)

        val seg1 = Seguimiento(
            id = "seg_1",
            oportunidadId = "op_1",
            usuarioResponsable = "Juan Pérez",
            tipoAccion = TipoAccionSeguimiento.ENVIO_PROPUESTA,
            fechaHora = "20/02/2026 11:30 hrs",
            resultado = "Propuesta enviada por correo electrónico a Valeria Morales.",
            proximaAccion = "Llamada de seguimiento para confirmar recepción.",
            fechaProximoSeguimiento = "25/02/2026",
            observaciones = "Adjuntado dossier institucional y folletos digitales."
        )

        val seg2 = Seguimiento(
            id = "seg_2",
            oportunidadId = "op_2",
            usuarioResponsable = "María González",
            tipoAccion = TipoAccionSeguimiento.PRIMER_CONTACTO,
            fechaHora = "22/02/2026 15:00 hrs",
            resultado = "Contacto telefónico con Carlos Fuentes.",
            proximaAccion = "Enviar presentación corporativa.",
            fechaProximoSeguimiento = "26/02/2026",
            observaciones = "Se mostró muy entusiasmado con los convenios veterinarios."
        )

        _seguimientos.value = listOf(seg1, seg2)

        val alt1 = Alerta(
            id = "alt_1",
            oportunidadId = "op_1",
            titulo = "Seguimiento pendiente",
            descripcion = "Realizar llamada de seguimiento con la Municipalidad de Puente Alto por propuesta enviada.",
            tipo = TipoAlerta.PROPUESTA_SIN_RESPUESTA,
            fechaHora = "25/02/2026",
            leida = false
        )

        val alt2 = Alerta(
            id = "alt_2",
            oportunidadId = "op_2",
            titulo = "Evento próximo",
            descripcion = "Falta menos de 1 mes para Expo Salud Banco Metropolitano. Confirmar requerimientos de stand.",
            tipo = TipoAlerta.EVENTO_PROXIMO,
            fechaHora = "28/02/2026",
            leida = false
        )

        _alertas.value = listOf(alt1, alt2)
    }

    fun agregarOportunidadCompleta(
        oportunidad: Oportunidad,
        evento: Evento,
        organizacion: Organizacion,
        contacto: Contacto?
    ) {
        _organizaciones.update { list -> if (list.any { it.id == organizacion.id }) list else list + organizacion }
        contacto?.let { c ->
            _contactos.update { list -> if (list.any { it.id == c.id }) list else list + c }
        }
        _eventos.update { it + evento }
        _oportunidades.update { it + oportunidad }
    }

    fun actualizarEstadoOportunidad(oportunidadId: String, nuevoEstado: EstadoOportunidad) {
        _oportunidades.update { list ->
            list.map { op ->
                if (op.id == oportunidadId) {
                    op.copy(
                        estado = nuevoEstado,
                        fechaUltimaActualizacion = "Hoy"
                    )
                } else op
            }
        }
    }

    fun actualizarPrioridadOportunidad(oportunidadId: String, nuevaPrioridad: Prioridad) {
        _oportunidades.update { list ->
            list.map { op ->
                if (op.id == oportunidadId) op.copy(prioridad = nuevaPrioridad) else op
            }
        }
    }

    fun actualizarResponsableOportunidad(oportunidadId: String, nuevoResponsable: String) {
        _oportunidades.update { list ->
            list.map { op ->
                if (op.id == oportunidadId) op.copy(responsableInterno = nuevoResponsable) else op
            }
        }
    }

    fun agregarSeguimiento(seguimiento: Seguimiento) {
        _seguimientos.update { it + seguimiento }
        _oportunidades.update { list ->
            list.map { op ->
                if (op.id == seguimiento.oportunidadId) op.copy(fechaUltimaActualizacion = "Hoy") else op
            }
        }
    }

    fun marcarAlertaLeida(alertaId: String) {
        _alertas.update { list ->
            list.map { alt ->
                if (alt.id == alertaId) alt.copy(leida = true) else alt
            }
        }
    }

    fun registrarResultadoActivacion(resultado: ResultadoActivacion) {
        _resultados.update { it + resultado }
        actualizarEstadoOportunidad(resultado.oportunidadId, EstadoOportunidad.ACTIVACION_REALIZADA)
    }

    fun generarId(): String = UUID.randomUUID().toString().take(8)
}
