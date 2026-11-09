// #alertas #aviso_alertas #notificaciones #recordatorios #animacion #staggered_enter #press_scale
package cl.duoc.primer_mvvm.ui.oportunidades

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.primer_mvvm.model.Alerta
import cl.duoc.primer_mvvm.model.TipoAlerta

/**
 * #alertas #aviso_alertas #notificaciones #animacion #staggered_enter
 * Pantalla de lista de alertas y notificaciones con animaciones escalonadas de entrada.
 */
@Composable
fun AlertasScreen(
    uiState: OportunidadesUiState,
    onMarcarLeida: (String) -> Unit,
    onVerOportunidad: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // #aviso_alertas - Encabezado principal de alertas
        Text(
            text = "Alertas y Recordatorios Internos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Supervisión de eventos próximos, gestiones sin respuesta y seguimientos pendientes.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // #animacion #lista_alertas - Transición animada de la lista de alertas
        AnimatedContent(
            targetState = uiState.alertas.isEmpty(),
            label = "AlertsEmptyAnimation"
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
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color(0xFF388E3C)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "¡Al día! No tienes alertas ni seguimientos pendientes.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                // #lista #alertas #staggered_enter - Entrada escalonada de tarjetas de alerta
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(uiState.alertas, key = { _, alt -> alt.id }) { index, alerta ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(300, delayMillis = index * 60)) +
                                    slideInVertically(initialOffsetY = { 60 }, animationSpec = tween(300, delayMillis = index * 60)),
                            label = "AlertCardEnter"
                        ) {
                            AlertaCard(
                                alerta = alerta,
                                onMarcarLeida = { onMarcarLeida(alerta.id) },
                                onVerOportunidad = { onVerOportunidad(alerta.oportunidadId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * #tarjeta #alerta #notificacion #animacion #press_scale - Tarjeta visual con cambio de color y escala táctil animada
 */
@Composable
fun AlertaCard(
    alerta: Alerta,
    onMarcarLeida: () -> Unit,
    onVerOportunidad: () -> Unit
) {
    // #animacion #color_alerta - Animación de color de fondo según el estado de lectura
    val containerColor by animateColorAsState(
        targetValue = if (alerta.leida) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.45f)
        },
        animationSpec = tween(400),
        label = "AlertColorAnimation"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "AlertPressScale"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .animateContentSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = alerta.tipo.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Text(
                    text = alerta.fechaHora,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = alerta.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = alerta.descripcion,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (!alerta.leida) {
                    // #boton #marcar_leida - Botón para marcar la alerta como leída
                    TextButton(onClick = onMarcarLeida) {
                        Text("Marcar como leída")
                    }
                }
                // #boton #ver_oportunidad - Botón para navegar al detalle de la oportunidad
                Button(onClick = onVerOportunidad) {
                    Text("Ver Oportunidad")
                }
            }
        }
    }
}
