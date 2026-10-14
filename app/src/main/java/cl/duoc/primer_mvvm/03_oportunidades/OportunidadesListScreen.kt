package cl.duoc.primer_mvvm.`03_oportunidades`

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.primer_mvvm.model.EstadoOportunidad
import cl.duoc.primer_mvvm.model.Oportunidad
import cl.duoc.primer_mvvm.model.Prioridad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadesListScreen(
    uiState: OportunidadesUiState,
    onSearchTextChange: (String) -> Unit,
    onFilterEstadoChange: (EstadoOportunidad?) -> Unit,
    onFilterPrioridadChange: (Prioridad?) -> Unit,
    onLimpiarFiltros: () -> Unit,
    onOportunidadClick: (String) -> Unit,
    onNuevaOportunidadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevaOportunidadClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nueva Oportunidad") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Buscador
            OutlinedTextField(
                value = uiState.busquedaTexto,
                onValueChange = onSearchTextChange,
                label = { Text("Buscar evento, organización u oportunidad") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (uiState.busquedaTexto.isNotEmpty()) {
                        IconButton(onClick = { onSearchTextChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tarjetas de Métricas KPI
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    title = "Total",
                    count = uiState.totalOportunidades,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Pendientes",
                    count = uiState.totalPendientes,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Confirmadas",
                    count = uiState.totalConfirmadas,
                    color = Color(0xFFE8F5E9),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chips de Filtro
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    Text(
                        text = "Filtros:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Chips Prioridad
                item {
                    FilterChip(
                        selected = uiState.filtroPrioridad == Prioridad.ALTA,
                        onClick = { onFilterPrioridadChange(Prioridad.ALTA) },
                        label = { Text("Prioridad Alta") }
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.filtroPrioridad == Prioridad.MEDIA,
                        onClick = { onFilterPrioridadChange(Prioridad.MEDIA) },
                        label = { Text("Prioridad Media") }
                    )
                }

                // Chips Estado
                item {
                    FilterChip(
                        selected = uiState.filtroEstado == EstadoOportunidad.PROPUESTA_ENVIADA,
                        onClick = { onFilterEstadoChange(EstadoOportunidad.PROPUESTA_ENVIADA) },
                        label = { Text("Propuesta Enviada") }
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.filtroEstado == EstadoOportunidad.PARTICIPACION_CONFIRMADA,
                        onClick = { onFilterEstadoChange(EstadoOportunidad.PARTICIPACION_CONFIRMADA) },
                        label = { Text("Confirmadas") }
                    )
                }

                if (uiState.filtroPrioridad != null || uiState.filtroEstado != null || uiState.busquedaTexto.isNotEmpty()) {
                    item {
                        TextButton(onClick = onLimpiarFiltros) {
                            Text("Limpiar", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Lista de Oportunidades
            if (uiState.oportunidades.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.EventBusy,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No se encontraron oportunidades con los filtros seleccionados.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.oportunidades, key = { it.id }) { oportunidad ->
                        val evento = uiState.eventos.find { it.oportunidadId == oportunidad.id }
                        val organizacion = uiState.organizaciones.find { it.id == evento?.organizacionId }

                        OportunidadCard(
                            oportunidad = oportunidad,
                            nombreEvento = evento?.nombre ?: "Sin evento",
                            nombreOrganizacion = organizacion?.nombre ?: "Organización no especificada",
                            ubicacion = if (evento != null) "${evento.comuna}, ${evento.region}" else "Ubicación pendiente",
                            fechaEvento = evento?.fechaInicio ?: "Fecha por definir",
                            onClick = { onOportunidadClick(oportunidad.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun OportunidadCard(
    oportunidad: Oportunidad,
    nombreEvento: String,
    nombreOrganizacion: String,
    ubicacion: String,
    fechaEvento: String,
    onClick: () -> Unit
) {
    val colorPrioridad = when (oportunidad.prioridad) {
        Prioridad.ALTA -> Color(0xFFD32F2F)
        Prioridad.MEDIA -> Color(0xFFF57C00)
        Prioridad.BAJA -> Color(0xFF388E3C)
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badge Tipo Evento / Oportunidad
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = oportunidad.tipo,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                // Badge Prioridad
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colorPrioridad.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Prioridad ${oportunidad.prioridad.name}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colorPrioridad,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = oportunidad.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Evento: $nombreEvento",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = nombreOrganizacion,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = ubicacion,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = fechaEvento,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider()

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = oportunidad.responsableInterno,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = oportunidad.estado.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
