package cl.duoc.primer_mvvm.`03_oportunidades`

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.primer_mvvm.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadDetalleScreen(
    oportunidadId: String,
    uiState: OportunidadesUiState,
    onVolver: () -> Unit,
    onCambiarEstado: (EstadoOportunidad) -> Unit,
    onAgregarSeguimiento: (String, TipoAccionSeguimiento, String, String, String, String) -> Unit,
    onRegistrarResultado: (String, Int, Int, Int, Int, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val oportunidad = uiState.oportunidades.find { it.id == oportunidadId }
    val evento = uiState.eventos.find { it.oportunidadId == oportunidadId }
    val organizacion = uiState.organizaciones.find { it.id == evento?.organizacionId }
    val contactos = uiState.contactos.filter { it.organizacionId == organizacion?.id }
    val seguimientos = uiState.seguimientos.filter { it.oportunidadId == oportunidadId }
    val resultadoActivacion = uiState.resultados.find { it.oportunidadId == oportunidadId }

    var showDialogSeguimiento by remember { mutableStateOf(false) }
    var showDialogResultado by remember { mutableStateOf(false) }
    var showDialogCambiarEstado by remember { mutableStateOf(false) }

    if (oportunidad == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Oportunidad no encontrada.")
        }
        return
    }

    // Diálogo para Agregar Seguimiento
    if (showDialogSeguimiento) {
        DialogAgregarSeguimiento(
            onDismiss = { showDialogSeguimiento = false },
            onGuardar = { tipo, resultado, proxAccion, fechaProx, obs ->
                onAgregarSeguimiento("Usuario Actual", tipo, resultado, proxAccion, fechaProx, obs)
                showDialogSeguimiento = false
            }
        )
    }

    // Diálogo para Registrar Resultado de Activación
    if (showDialogResultado) {
        DialogRegistrarResultado(
            onDismiss = { showDialogResultado = false },
            onGuardar = { fecha, asistentes, contactosGen, personasInt, inscripciones, obs, eval ->
                onRegistrarResultado(fecha, asistentes, contactosGen, personasInt, inscripciones, obs, eval)
                showDialogResultado = false
            }
        )
    }

    // Diálogo para Cambiar Estado
    if (showDialogCambiarEstado) {
        AlertDialog(
            onDismissRequest = { showDialogCambiarEstado = false },
            title = { Text("Cambiar Estado de la Oportunidad") },
            text = {
                Column {
                    EstadoOportunidad.entries.forEach { estado ->
                        TextButton(
                            onClick = {
                                onCambiarEstado(estado)
                                showDialogCambiarEstado = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(estado.label)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDialogCambiarEstado = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(oportunidad.nombre) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showDialogCambiarEstado = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Cambiar estado")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta de Resumen y Estado
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Estado: ${oportunidad.estado.label}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Prioridad ${oportunidad.prioridad.name}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Tipo: ${oportunidad.tipo}")
                    Text("Responsable: ${oportunidad.responsableInterno}")
                    Text("Potencial: ${oportunidad.potencialComercial}")
                    Text("Fuente: ${oportunidad.fuenteInformacion}")
                    Text("Detección: ${oportunidad.fechaDeteccion}")
                    if (oportunidad.observaciones.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Observaciones: ${oportunidad.observaciones}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Información del Evento
            if (evento != null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Detalles del Evento",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Nombre del Evento: ${evento.nombre}")
                        Text("Fechas: ${evento.fechaInicio} al ${evento.fechaTermino} (${evento.horario})")
                        Text("Ubicación: ${evento.direccion}, ${evento.comuna}, ${evento.region}")
                        Text("Asistentes Estimados: ${evento.cantidadAsistentesEstimada} personas")
                        Text("Público Objetivo: ${evento.publicoObjetivo}")
                        Text("Condiciones: ${evento.condicionesParticipacion}")
                        Text("Requisitos: ${evento.requisitos}")
                        Text("Costos Referenciales: ${evento.costosReferenciales}")
                    }
                }
            }

            // Organización y Contactos
            if (organizacion != null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Organización Responsable",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Organización: ${organizacion.nombre}")
                        Text("Tipo: ${organizacion.tipo} | Rubro: ${organizacion.rubro}")
                        Text("Ubicación: ${organizacion.comuna}, ${organizacion.region}")
                        Text("Sitio Web: ${organizacion.sitioWeb}")

                        if (contactos.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Contactos Corporativos Ficticios",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            contactos.forEach { c ->
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text("• ${c.nombre} - ${c.cargo} (${c.area})", fontWeight = FontWeight.SemiBold)
                                    Text("  Correo: ${c.correo} | Tel: ${c.telefono}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }

            // Acciones de Seguimiento
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Historial de Seguimiento",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Button(onClick = { showDialogSeguimiento = true }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Agregar")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (seguimientos.isEmpty()) {
                        Text("No se han registrado acciones de seguimiento aún.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        seguimientos.forEach { seg ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = seg.tipoAccion.label,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = seg.fechaHora,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                    Text("Responsable: ${seg.usuarioResponsable}", style = MaterialTheme.typography.bodySmall)
                                    Text("Resultado: ${seg.resultado}", style = MaterialTheme.typography.bodySmall)
                                    if (seg.proximaAccion.isNotBlank()) {
                                        Text(
                                            text = "Próxima Acción: ${seg.proximaAccion} (${seg.fechaProximoSeguimiento})",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Resultado de Activación (si existe o botón para registrarlo)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Resultado de la Activación",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (resultadoActivacion != null) {
                        Text("Fecha Realización: ${resultadoActivacion.fechaRealizacion}")
                        Text("Asistentes Totales: ${resultadoActivacion.cantidadAsistentes}")
                        Text("Contactos Generados: ${resultadoActivacion.contactosGenerados}")
                        Text("Personas Interesadas: ${resultadoActivacion.personasInteresadas}")
                        Text("Inscripciones Concretadas: ${resultadoActivacion.inscripciones}")
                        Text("Evaluación General: ${resultadoActivacion.evaluacionGeneral}")
                        if (resultadoActivacion.observaciones.isNotBlank()) {
                            Text("Observaciones: ${resultadoActivacion.observaciones}")
                        }
                    } else {
                        Text("Aún no se ha registrado el resultado final de esta activación.", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { showDialogResultado = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Registrar Resultado de Activación")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DialogAgregarSeguimiento(
    onDismiss: () -> Unit,
    onGuardar: (TipoAccionSeguimiento, String, String, String, String) -> Unit
) {
    var tipoSeleccionado by remember { mutableStateOf(TipoAccionSeguimiento.LLAMADA) }
    var resultado by remember { mutableStateOf("") }
    var proximaAccion by remember { mutableStateOf("") }
    var fechaProximo by remember { mutableStateOf("DD/MM/AAAA") }
    var observaciones by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Acción de Seguimiento") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text("Tipo de Acción:", fontWeight = FontWeight.Bold)
                TipoAccionSeguimiento.entries.take(5).forEach { tipo ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = tipoSeleccionado == tipo,
                            onClick = { tipoSeleccionado = tipo }
                        )
                        Text(tipo.label)
                    }
                }

                OutlinedTextField(
                    value = resultado,
                    onValueChange = { resultado = it },
                    label = { Text("Resultado de la gestión") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = proximaAccion,
                    onValueChange = { proximaAccion = it },
                    label = { Text("Próxima Acción") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fechaProximo,
                    onValueChange = { fechaProximo = it },
                    label = { Text("Fecha Próximo Seguimiento") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = observaciones,
                    onValueChange = { observaciones = it },
                    label = { Text("Observaciones") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onGuardar(tipoSeleccionado, resultado, proximaAccion, fechaProximo, observaciones)
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun DialogRegistrarResultado(
    onDismiss: () -> Unit,
    onGuardar: (String, Int, Int, Int, Int, String, String) -> Unit
) {
    var fecha by remember { mutableStateOf("Hoy") }
    var asistentes by remember { mutableStateOf("250") }
    var contactos by remember { mutableStateOf("45") }
    var interesados by remember { mutableStateOf("30") }
    var inscripciones by remember { mutableStateOf("15") }
    var eval by remember { mutableStateOf("Excelente recepción de beneficios") }
    var obs by remember { mutableStateOf("Alta interacción con la mascota/stand") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Resultado de la Activación") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(value = fecha, onValueChange = { fecha = it }, label = { Text("Fecha Realización") })
                OutlinedTextField(value = asistentes, onValueChange = { asistentes = it }, label = { Text("Asistentes Estimados") })
                OutlinedTextField(value = contactos, onValueChange = { contactos = it }, label = { Text("Contactos Generados") })
                OutlinedTextField(value = interesados, onValueChange = { interesados = it }, label = { Text("Personas Interesadas") })
                OutlinedTextField(value = inscripciones, onValueChange = { inscripciones = it }, label = { Text("Inscripciones / Convenios") })
                OutlinedTextField(value = eval, onValueChange = { eval = it }, label = { Text("Evaluación General") })
                OutlinedTextField(value = obs, onValueChange = { obs = it }, label = { Text("Observaciones del Equipo") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onGuardar(
                    fecha,
                    asistentes.toIntOrNull() ?: 0,
                    contactos.toIntOrNull() ?: 0,
                    interesados.toIntOrNull() ?: 0,
                    inscripciones.toIntOrNull() ?: 0,
                    obs,
                    eval
                )
            }) {
                Text("Guardar Resultado")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
