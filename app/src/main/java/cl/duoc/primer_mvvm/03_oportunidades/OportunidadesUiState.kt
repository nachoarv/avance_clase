package cl.duoc.primer_mvvm.`03_oportunidades`

import cl.duoc.primer_mvvm.model.*

data class OportunidadesUiState(
    val oportunidades: List<Oportunidad> = emptyList(),
    val eventos: List<Evento> = emptyList(),
    val organizaciones: List<Organizacion> = emptyList(),
    val contactos: List<Contacto> = emptyList(),
    val seguimientos: List<Seguimiento> = emptyList(),
    val alertas: List<Alerta> = emptyList(),
    val resultados: List<ResultadoActivacion> = emptyList(),
    
    // Filtros de búsqueda
    val busquedaTexto: String = "",
    val filtroEstado: EstadoOportunidad? = null,
    val filtroPrioridad: Prioridad? = null,
    val filtroTipo: String? = null,
    val filtroComuna: String? = null,

    // Oportunidad Seleccionada para Detalle
    val oportunidadSeleccionadaId: String? = null,

    // Métricas rápidas
    val totalOportunidades: Int = 0,
    val totalPendientes: Int = 0,
    val totalConfirmadas: Int = 0,
    val totalAlertasSinLeer: Int = 0
)
