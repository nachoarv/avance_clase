// #oportunidades #lista_oportunidades #kpi_metricas #filtros #busqueda #animacion #kpi_contador #staggered_enter #press_scale
package cl.duoc.primer_mvvm.ui.oportunidades

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.primer_mvvm.model.EstadoOportunidad
import cl.duoc.primer_mvvm.model.Oportunidad
import cl.duoc.primer_mvvm.model.Prioridad

/**
 * #oportunidades #lista_oportunidades #kpi_metricas #filtros #animacion
 * Pantalla principal con animación de conteo en KPIs, entradas escalonadas y efectos táctiles.
 */
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
        // #boton #nueva_oportunidad - Botón flotante animado para crear una nueva oportunidad comercial
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
            // #campo_texto #buscador - Campo para filtrar la lista por nombre de evento, organización u oportunidad
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

            // #tarjeta #kpi #animacion #kpi_contador - Bloque de tarjetas KPI con contador numérico animado
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

            // #filtros - Fila de chips interactivos para filtrar el listado
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

            // #animacion #lista_oportunidades - Transición animada de la lista
            AnimatedContent(
                targetState = uiState.oportunidades.isEmpty(),
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(200))
                },
                label = "ListEmptyStateAnimation"
            ) { isEmpty ->
                if (isEmpty) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
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
                    // #lista #oportunidades #staggered_enter - Entradas de tarjetas con rebote suave
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(uiState.oportunidades, key = { _, op -> op.id }) { index, oportunidad ->
                            val evento = uiState.eventos.find { it.oportunidadId == oportunidad.id }
                            val organizacion = uiState.organizaciones.find { it.id == evento?.organizacionId }

                            // #animacion #staggered_enter - Entrada individual animada para cada tarjeta de la lista
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = index * 50)) +
                                        slideInVertically(initialOffsetY = { 80 }, animationSpec = tween(300, delayMillis = index * 50)),
                                label = "CardStaggeredEnter"
                            ) {
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
    }
}

/**
 * #tarjeta #kpi #animacion #kpi_contador - Tarjeta KPI con contador de números animado
 */
@Composable
fun MetricCard(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    // #animacion #kpi_contador - Transición animada del valor numérico con rebote
    val animatedCount by animateIntAsState(
        targetValue = count,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "KpiCountAnimation"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = modifier.animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = animatedCount.toString(),
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

/**
 * #tarjeta #oportunidad #animacion #press_scale - Tarjeta interactiva con efecto de escala al ser presionada
 */
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

    // #animacion #press_scale - Escala táctil al hacer clic/presionar sobre la tarjeta
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "CardPressScale"
    )

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .animateContentSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
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

            HorizontalDivider()

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
