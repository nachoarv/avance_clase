package cl.duoc.primer_mvvm.`03_oportunidades`

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.primer_mvvm.data.OportunidadRepository
import cl.duoc.primer_mvvm.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class OportunidadesViewModel : ViewModel() {

    private val repository = OportunidadRepository

    private val _busquedaTexto = MutableStateFlow("")
    private val _filtroEstado = MutableStateFlow<EstadoOportunidad?>(null)
    private val _filtroPrioridad = MutableStateFlow<Prioridad?>(null)
    private val _filtroTipo = MutableStateFlow<String?>(null)
    private val _filtroComuna = MutableStateFlow<String?>(null)
    private val _oportunidadSeleccionadaId = MutableStateFlow<String?>(null)

    private val filtersFlow = combine(
        _busquedaTexto,
        _filtroEstado,
        _filtroPrioridad,
        _filtroTipo,
        _filtroComuna
    ) { text, estado, prioridad, tipo, comuna ->
        FilterState(text, estado, prioridad, tipo, comuna)
    }

    private val dataFlow = combine(
        repository.oportunidades,
        repository.eventos,
        repository.organizaciones,
        repository.contactos,
        repository.seguimientos
    ) { ops, evs, orgs, conts, segs ->
        DataSnapshot(ops, evs, orgs, conts, segs)
    }

    val uiState: StateFlow<OportunidadesUiState> = combine(
        dataFlow,
        repository.alertas,
        repository.resultados,
        filtersFlow,
        _oportunidadSeleccionadaId
    ) { snapshot, alts, res, filters, selId ->

        val opsFiltradas = snapshot.ops.filter { op ->
            val ev = snapshot.evs.find { it.oportunidadId == op.id }
            val org = snapshot.orgs.find { it.id == ev?.organizacionId }

            val coincideTexto = filters.text.isBlank() ||
                    op.nombre.contains(filters.text, ignoreCase = true) ||
                    op.descripcion.contains(filters.text, ignoreCase = true) ||
                    (ev?.nombre?.contains(filters.text, ignoreCase = true) == true) ||
                    (org?.nombre?.contains(filters.text, ignoreCase = true) == true)

            val coincideEstado = filters.estado == null || op.estado == filters.estado
            val coincidePrioridad = filters.prioridad == null || op.prioridad == filters.prioridad
            val coincideTipo = filters.tipo == null || op.tipo == filters.tipo
            val coincideComuna = filters.comuna == null || ev?.comuna.equals(filters.comuna, ignoreCase = true)

            coincideTexto && coincideEstado && coincidePrioridad && coincideTipo && coincideComuna
        }

        val totalOps = snapshot.ops.size
        val totalPend = snapshot.ops.count { it.estado != EstadoOportunidad.ACTIVACION_REALIZADA && it.estado != EstadoOportunidad.OPORTUNIDAD_DESCARTADA }
        val totalConf = snapshot.ops.count { it.estado == EstadoOportunidad.PARTICIPACION_CONFIRMADA || it.estado == EstadoOportunidad.ACTIVACION_REALIZADA }
        val totalAltNoLeidas = alts.count { !it.leida }

        OportunidadesUiState(
            oportunidades = opsFiltradas,
            eventos = snapshot.evs,
            organizaciones = snapshot.orgs,
            contactos = snapshot.conts,
            seguimientos = snapshot.segs,
            alertas = alts,
            resultados = res,
            busquedaTexto = filters.text,
            filtroEstado = filters.estado,
            filtroPrioridad = filters.prioridad,
            filtroTipo = filters.tipo,
            filtroComuna = filters.comuna,
            oportunidadSeleccionadaId = selId,
            totalOportunidades = totalOps,
            totalPendientes = totalPend,
            totalConfirmadas = totalConf,
            totalAlertasSinLeer = totalAltNoLeidas
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OportunidadesUiState()
    )

    fun onBusquedaTextoChange(texto: String) {
        _busquedaTexto.value = texto
    }

    fun onFiltroEstadoChange(estado: EstadoOportunidad?) {
        _filtroEstado.value = if (_filtroEstado.value == estado) null else estado
    }

    fun onFiltroPrioridadChange(prioridad: Prioridad?) {
        _filtroPrioridad.value = if (_filtroPrioridad.value == prioridad) null else prioridad
    }

    fun onFiltroTipoChange(tipo: String?) {
        _filtroTipo.value = if (_filtroTipo.value == tipo) null else tipo
    }

    fun onFiltroComunaChange(comuna: String?) {
        _filtroComuna.value = if (_filtroComuna.value == comuna) null else comuna
    }

    fun limpiarFiltros() {
        _busquedaTexto.value = ""
        _filtroEstado.value = null
        _filtroPrioridad.value = null
        _filtroTipo.value = null
        _filtroComuna.value = null
    }

    fun seleccionarOportunidad(id: String?) {
        _oportunidadSeleccionadaId.value = id
    }

    fun actualizarEstadoOportunidad(oportunidadId: String, nuevoEstado: EstadoOportunidad) {
        repository.actualizarEstadoOportunidad(oportunidadId, nuevoEstado)
    }

    fun actualizarPrioridadOportunidad(oportunidadId: String, nuevaPrioridad: Prioridad) {
        repository.actualizarPrioridadOportunidad(oportunidadId, nuevaPrioridad)
    }

    fun actualizarResponsable(oportunidadId: String, nuevoResponsable: String) {
        repository.actualizarResponsableOportunidad(oportunidadId, nuevoResponsable)
    }

    fun agregarSeguimiento(
        oportunidadId: String,
        usuario: String,
        tipo: TipoAccionSeguimiento,
        resultado: String,
        proximaAccion: String,
        fechaProximoSeguimiento: String,
        observaciones: String
    ) {
        val seg = Seguimiento(
            id = repository.generarId(),
            oportunidadId = oportunidadId,
            usuarioResponsable = usuario,
            tipoAccion = tipo,
            fechaHora = "Hoy",
            resultado = resultado,
            proximaAccion = proximaAccion,
            fechaProximoSeguimiento = fechaProximoSeguimiento,
            observaciones = observaciones
        )
        repository.agregarSeguimiento(seg)
    }

    fun marcarAlertaLeida(alertaId: String) {
        repository.marcarAlertaLeida(alertaId)
    }

    fun registrarResultadoActivacion(
        oportunidadId: String,
        fecha: String,
        asistentes: Int,
        contactos: Int,
        interesados: Int,
        inscripciones: Int,
        observaciones: String,
        evaluacion: String
    ) {
        val res = ResultadoActivacion(
            id = repository.generarId(),
            oportunidadId = oportunidadId,
            fechaRealizacion = fecha,
            estadoFinal = "Realizada exitosamente",
            cantidadAsistentes = asistentes,
            contactosGenerados = contactos,
            personasInteresadas = interesados,
            inscripciones = inscripciones,
            observaciones = observaciones,
            evaluacionGeneral = evaluacion
        )
        repository.registrarResultadoActivacion(res)
    }

    fun crearOportunidadCompleta(
        nombreOportunidad: String,
        tipoOportunidad: String,
        descripcion: String,
        fuenteInformacion: String,
        responsableInterno: String,
        prioridad: Prioridad,
        potencialComercial: String,
        nombreEvento: String,
        fechaInicio: String,
        fechaTermino: String,
        horario: String,
        direccion: String,
        comuna: String,
        region: String,
        publicoEsperado: String,
        publicoObjetivo: String,
        asistentesEstimados: Int,
        condicionesParticipacion: String,
        requisitos: String,
        costosReferenciales: String,
        nombreOrganizacion: String,
        tipoOrganizacion: String,
        rubroOrganizacion: String,
        sitioWebOrganizacion: String,
        nombreContacto: String,
        cargoContacto: String,
        areaContacto: String,
        correoContacto: String,
        telefonoContacto: String,
        observaciones: String
    ) {
        val opId = "op_" + repository.generarId()
        val orgId = "org_" + repository.generarId()
        val evId = "ev_" + repository.generarId()
        val contId = "cont_" + repository.generarId()

        val org = Organizacion(
            id = orgId,
            nombre = nombreOrganizacion,
            tipo = tipoOrganizacion,
            rubro = rubroOrganizacion,
            comuna = comuna,
            region = region,
            sitioWeb = sitioWebOrganizacion,
            observaciones = "Organización creada desde formulario."
        )

        val cont = if (nombreContacto.isNotBlank()) {
            Contacto(
                id = contId,
                organizacionId = orgId,
                nombre = nombreContacto,
                cargo = cargoContacto,
                area = areaContacto,
                correo = correoContacto,
                telefono = telefonoContacto,
                observaciones = "Contacto de prueba ficticio."
            )
        } else null

        val ev = Evento(
            id = evId,
            oportunidadId = opId,
            nombre = nombreEvento,
            tipo = tipoOportunidad,
            fechaInicio = fechaInicio,
            fechaTermino = fechaTermino,
            horario = horario,
            direccion = direccion,
            comuna = comuna,
            region = region,
            organizacionId = orgId,
            publicoEsperado = publicoEsperado,
            publicoObjetivo = publicoObjetivo,
            cantidadAsistentesEstimada = asistentesEstimados,
            condicionesParticipacion = condicionesParticipacion,
            requisitos = requisitos,
            costosReferenciales = costosReferenciales,
            observaciones = observaciones
        )

        val op = Oportunidad(
            id = opId,
            nombre = nombreOportunidad,
            tipo = tipoOportunidad,
            descripcion = descripcion,
            fuenteInformacion = fuenteInformacion,
            fechaDeteccion = "Hoy",
            responsableInterno = responsableInterno.ifBlank { "Ejecutivo Comercial" },
            prioridad = prioridad,
            potencialComercial = potencialComercial,
            estado = EstadoOportunidad.EVENTO_IDENTIFICADO,
            fechaUltimaActualizacion = "Hoy",
            observaciones = observaciones
        )

        repository.agregarOportunidadCompleta(op, ev, org, cont)
    }

    private data class FilterState(
        val text: String,
        val estado: EstadoOportunidad?,
        val prioridad: Prioridad?,
        val tipo: String?,
        val comuna: String?
    )

    private data class DataSnapshot(
        val ops: List<Oportunidad>,
        val evs: List<Evento>,
        val orgs: List<Organizacion>,
        val conts: List<Contacto>,
        val segs: List<Seguimiento>
    )
}
