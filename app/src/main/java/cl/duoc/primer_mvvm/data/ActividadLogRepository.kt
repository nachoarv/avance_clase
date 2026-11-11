// #repositorio #auditoria #log_actividad #hu49
package cl.duoc.primer_mvvm.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ActividadLog(
    val id: String,
    val usuarioResponsable: String,
    val accion: String,
    val detalle: String,
    val fechaHora: String
)

object ActividadLogRepository {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    private val _logs = MutableStateFlow<List<ActividadLog>>(
        listOf(
            ActividadLog(
                id = "log_1",
                usuarioResponsable = "admin@empresa.cl",
                accion = "INICIALIZACION",
                detalle = "Sistema inicializado con datos ficticios",
                fechaHora = "01/01/2026 09:00"
            )
        )
    )
    val logs: StateFlow<List<ActividadLog>> = _logs.asStateFlow()

    fun registrarActividad(usuario: String, accion: String, detalle: String) {
        val nuevoLog = ActividadLog(
            id = "log_${System.currentTimeMillis()}",
            usuarioResponsable = usuario,
            accion = accion,
            detalle = detalle,
            fechaHora = dateFormat.format(Date())
        )
        _logs.update { lista -> listOf(nuevoLog) + lista }
    }
}
