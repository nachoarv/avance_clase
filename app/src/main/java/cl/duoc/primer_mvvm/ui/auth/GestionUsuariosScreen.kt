// #gestion_usuarios #admin #cambio_clave #roles #desactivar_usuario #eliminar_usuario #kpi_metricas #animacion #kpi_contador #staggered_enter
package cl.duoc.primer_mvvm.ui.auth

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import cl.duoc.primer_mvvm.data.UsuarioRepository
import cl.duoc.primer_mvvm.model.Usuario
import java.util.UUID

/**
 * #gestion_usuarios #admin #roles #cambio_clave #desactivar_usuario #eliminar_usuario #animacion
 * Panel de administración completo para gestión de cuentas con contadores y entradas animadas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionUsuariosDialog(
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val usuariosList by UsuarioRepository.usuarios.collectAsState()

    var textoBusqueda by remember { mutableStateOf("") }
    var filtroRolSeleccionado by remember { mutableStateOf("Todos") } // Todos, Admin, Supervisor, Ejecutivo, Inactivos

    var usuarioEditarPassword by remember { mutableStateOf<Usuario?>(null) }
    var nuevaPasswordText by remember { mutableStateOf("") }
    var errorPasswordText by remember { mutableStateOf<String?>(null) }

    var usuarioEditarRol by remember { mutableStateOf<Usuario?>(null) }
    var nuevoRolSeleccionado by remember { mutableStateOf("Ejecutivo Comercial") }

    var usuarioEditarPerfil by remember { mutableStateOf<Usuario?>(null) }
    var editNombre by remember { mutableStateOf("") }
    var editApellido by remember { mutableStateOf("") }
    var editTelefono by remember { mutableStateOf("") }
    var editCargo by remember { mutableStateOf("") }
    var editDepartamento by remember { mutableStateOf("") }

    var usuarioEliminar by remember { mutableStateOf<Usuario?>(null) }

    var mostrarCrearUsuarioDialog by remember { mutableStateOf(false) }
    var nuevoNombre by remember { mutableStateOf("") }
    var nuevoApellido by remember { mutableStateOf("") }
    var nuevoCorreo by remember { mutableStateOf("") }
    var nuevaPassword by remember { mutableStateOf("") }
    var nuevoTelefono by remember { mutableStateOf("+56 9 ") }
    var nuevoCargo by remember { mutableStateOf("Ejecutivo Comercial") }
    var nuevoDepartamento by remember { mutableStateOf("Ventas & Convenios") }
    var nuevoRol by remember { mutableStateOf("Ejecutivo Comercial") }
    var errorCreacion by remember { mutableStateOf<String?>(null) }

    // Cálculo de Métricas KPI del Admin
    val totalCuentas = usuariosList.size
    val totalActivos = usuariosList.count { it.activo }
    val totalInactivos = usuariosList.count { !it.activo }

    // #animacion #kpi_contador - Conteo numérico animado para las métricas
    val animTotal by animateIntAsState(targetValue = totalCuentas, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "TotalKpiAnim")
    val animActivos by animateIntAsState(targetValue = totalActivos, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "ActivosKpiAnim")
    val animInactivos by animateIntAsState(targetValue = totalInactivos, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "InactivosKpiAnim")

    // Filtrado dinámico
    val usuariosFiltrados = usuariosList.filter { u ->
        val coincideTexto = u.nombre.contains(textoBusqueda, ignoreCase = true) ||
                u.apellido.contains(textoBusqueda, ignoreCase = true) ||
                u.correo.contains(textoBusqueda, ignoreCase = true) ||
                u.cargo.contains(textoBusqueda, ignoreCase = true) ||
                u.departamento.contains(textoBusqueda, ignoreCase = true) ||
                u.rol.contains(textoBusqueda, ignoreCase = true)

        val coincideFiltroRol = when (filtroRolSeleccionado) {
            "Admin" -> u.rol == "Admin"
            "Supervisor" -> u.rol.contains("Supervisor", ignoreCase = true)
            "Ejecutivo" -> u.rol.contains("Ejecutivo", ignoreCase = true)
            "Inactivos" -> !u.activo
            else -> true
        }

        coincideTexto && coincideFiltroRol
    }

    // #dialogo #editar_usuario - Diálogo para editar perfil completo del usuario
    if (usuarioEditarPerfil != null) {
        val target = usuarioEditarPerfil!!
        AlertDialog(
            onDismissRequest = { usuarioEditarPerfil = null },
            icon = { Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Editar Perfil de Usuario") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text("Correo: ${target.correo}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                    OutlinedTextField(
                        value = editNombre,
                        onValueChange = { editNombre = it },
                        label = { Text("Nombre") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editApellido,
                        onValueChange = { editApellido = it },
                        label = { Text("Apellido") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editTelefono,
                        onValueChange = { editTelefono = it },
                        label = { Text("Teléfono") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editCargo,
                        onValueChange = { editCargo = it },
                        label = { Text("Cargo / Título") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDepartamento,
                        onValueChange = { editDepartamento = it },
                        label = { Text("Área / Departamento") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val ok = UsuarioRepository.actualizarUsuarioCompleto(
                            correo = target.correo,
                            nuevoNombre = editNombre,
                            nuevoApellido = editApellido,
                            nuevoTelefono = editTelefono,
                            nuevoCargo = editCargo,
                            nuevoDepartamento = editDepartamento
                        )
                        if (ok) {
                            Toast.makeText(context, "Perfil de ${target.nombre} actualizado", Toast.LENGTH_SHORT).show()
                            usuarioEditarPerfil = null
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { usuarioEditarPerfil = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // #dialogo #eliminar_usuario - Diálogo de confirmación para eliminar usuario
    if (usuarioEliminar != null) {
        val target = usuarioEliminar!!
        AlertDialog(
            onDismissRequest = { usuarioEliminar = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Eliminar Cuenta de Usuario") },
            text = {
                Text("¿Está seguro de eliminar definitivamente la cuenta de ${target.nombreCompleto()} (${target.correo})? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = {
                        val ok = UsuarioRepository.eliminarUsuario(target.correo)
                        if (ok) {
                            Toast.makeText(context, "Usuario ${target.nombre} eliminado", Toast.LENGTH_SHORT).show()
                            usuarioEliminar = null
                        }
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { usuarioEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // #dialogo #cambiar_password - Diálogo modal para actualizar contraseña de usuario
    if (usuarioEditarPassword != null) {
        val targetUser = usuarioEditarPassword!!
        AlertDialog(
            onDismissRequest = { usuarioEditarPassword = null },
            icon = { Icon(Icons.Default.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Modificar Contraseña") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Usuario: ${targetUser.nombreCompleto()} (${targetUser.correo})")
                    OutlinedTextField(
                        value = nuevaPasswordText,
                        onValueChange = {
                            nuevaPasswordText = it
                            errorPasswordText = null
                        },
                        label = { Text("Nueva Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        isError = errorPasswordText != null,
                        supportingText = {
                            errorPasswordText?.let { err ->
                                Text(err, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nuevaPasswordText.length < 6) {
                            errorPasswordText = "Mínimo 6 caracteres"
                            return@Button
                        }
                        val exito = UsuarioRepository.cambiarPasswordUsuario(targetUser.correo, nuevaPasswordText)
                        if (exito) {
                            Toast.makeText(context, "Contraseña de ${targetUser.nombre} actualizada", Toast.LENGTH_SHORT).show()
                            usuarioEditarPassword = null
                            nuevaPasswordText = ""
                        } else {
                            errorPasswordText = "No se pudo actualizar"
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { usuarioEditarPassword = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // #dialogo #modificar_rol - Diálogo modal para cambiar el rol de usuario
    if (usuarioEditarRol != null) {
        val targetUser = usuarioEditarRol!!
        val rolesDisponibles = listOf("Ejecutivo Comercial", "Supervisor Comercial", "Admin")

        AlertDialog(
            onDismissRequest = { usuarioEditarRol = null },
            icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) },
            title = { Text("Modificar Rol de Usuario") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Usuario: ${targetUser.nombreCompleto()}")
                    Text("Rol actual: ${targetUser.rol}", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Seleccione nuevo rol:")
                    rolesDisponibles.forEach { rolOption ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        ) {
                            RadioButton(
                                selected = (nuevoRolSeleccionado == rolOption),
                                onClick = { nuevoRolSeleccionado = rolOption }
                            )
                            Text(text = rolOption, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        UsuarioRepository.cambiarRolUsuario(targetUser.correo, nuevoRolSeleccionado)
                        Toast.makeText(context, "Rol de ${targetUser.nombre} cambiado a $nuevoRolSeleccionado", Toast.LENGTH_SHORT).show()
                        usuarioEditarRol = null
                    }
                ) {
                    Text("Actualizar Rol")
                }
            },
            dismissButton = {
                TextButton(onClick = { usuarioEditarRol = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // #dialogo #crear_usuario - Diálogo modal para registrar una nueva cuenta con perfil completo
    if (mostrarCrearUsuarioDialog) {
        AlertDialog(
            onDismissRequest = { mostrarCrearUsuarioDialog = false },
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            title = { Text("Crear Nuevo Usuario") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = nuevoNombre,
                        onValueChange = { nuevoNombre = it },
                        label = { Text("Nombre") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nuevoApellido,
                        onValueChange = { nuevoApellido = it },
                        label = { Text("Apellido") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nuevoCorreo,
                        onValueChange = { nuevoCorreo = it },
                        label = { Text("Correo Electrónico") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nuevaPassword,
                        onValueChange = { nuevaPassword = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nuevoTelefono,
                        onValueChange = { nuevoTelefono = it },
                        label = { Text("Teléfono") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nuevoCargo,
                        onValueChange = { nuevoCargo = it },
                        label = { Text("Cargo") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nuevoDepartamento,
                        onValueChange = { nuevoDepartamento = it },
                        label = { Text("Departamento") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Rol asignado:")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = nuevoRol == "Ejecutivo Comercial",
                            onClick = { nuevoRol = "Ejecutivo Comercial" },
                            label = { Text("Ejecutivo") }
                        )
                        FilterChip(
                            selected = nuevoRol == "Supervisor Comercial",
                            onClick = { nuevoRol = "Supervisor Comercial" },
                            label = { Text("Supervisor") }
                        )
                        FilterChip(
                            selected = nuevoRol == "Admin",
                            onClick = { nuevoRol = "Admin" },
                            label = { Text("Admin") }
                        )
                    }
                    errorCreacion?.let { err ->
                        Text(err, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nuevoNombre.isBlank() || nuevoCorreo.isBlank() || nuevaPassword.length < 8) {
                            errorCreacion = "Complete los campos (mínimo 8 caracteres para contraseña)"
                            return@Button
                        }
                        val u = Usuario(
                            id = UUID.randomUUID().toString(),
                            correo = nuevoCorreo.trim(),
                            password = nuevaPassword,
                            nombre = nuevoNombre.trim(),
                            apellido = nuevoApellido.trim(),
                            rol = nuevoRol,
                            telefono = nuevoTelefono,
                            cargo = nuevoCargo,
                            departamento = nuevoDepartamento,
                            activo = true
                        )
                        val ok = UsuarioRepository.agregarUsuario(u)
                        if (ok) {
                            Toast.makeText(context, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show()
                            mostrarCrearUsuarioDialog = false
                            nuevoNombre = ""
                            nuevoApellido = ""
                            nuevoCorreo = ""
                            nuevaPassword = ""
                            errorCreacion = null
                        } else {
                            errorCreacion = "El correo ya está registrado"
                        }
                    }
                ) {
                    Text("Crear")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCrearUsuarioDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal Principal de Gestión de Usuarios
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.92f),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Panel de Administración",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // #kpi_metricas #animacion #kpi_contador - Bloque de tarjetas KPI con contadores animables
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier
                            .weight(1f)
                            .animateContentSize()
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("$animTotal", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("Total", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        modifier = Modifier
                            .weight(1f)
                            .animateContentSize()
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("$animActivos", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color(0xFF2E7D32))
                            Text("Activos", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .weight(1f)
                            .animateContentSize()
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("$animInactivos", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                            Text("Inactivos", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                // #campo_texto #buscador - Campo para filtrar la lista de usuarios por texto
                OutlinedTextField(
                    value = textoBusqueda,
                    onValueChange = { textoBusqueda = it },
                    placeholder = { Text("Buscar usuario, correo, cargo o área...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // #filtros #admin - Chips de filtro rápido por rol y estado
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val opciones = listOf("Todos", "Ejecutivo", "Supervisor", "Admin", "Inactivos")
                    items(opciones) { opt ->
                        FilterChip(
                            selected = filtroRolSeleccionado == opt,
                            onClick = { filtroRolSeleccionado = opt },
                            label = { Text(opt, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Resultados: ${usuariosFiltrados.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // #boton #nuevo_usuario - Botón para desplegar el modal de nuevo usuario
                    Button(
                        onClick = {
                            errorCreacion = null
                            mostrarCrearUsuarioDialog = true
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Nuevo Usuario", style = MaterialTheme.typography.bodySmall)
                    }
                }

                HorizontalDivider()

                // #lista #usuarios #staggered_enter - Renderizado con entrada animada de tarjetas
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    itemsIndexed(usuariosFiltrados, key = { _, u -> u.id }) { index, u ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(280, delayMillis = index * 40)) +
                                    slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(280, delayMillis = index * 40)),
                            label = "UserCardEnter"
                        ) {
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize(),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = if (!u.activo)
                                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                                    else if (UsuarioRepository.esAdminOSupervisor(u.rol, u.correo))
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                    else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = u.nombreCompleto(),
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                // Badge de Estado Activo / Inactivo
                                                Surface(
                                                    color = if (u.activo) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.errorContainer,
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text(
                                                        text = if (u.activo) "Activo" else "Inactivo",
                                                        color = if (u.activo) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = u.correo,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "${u.cargo} • ${u.departamento}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                            Text(
                                                text = "Teléfono: ${u.telefono}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }

                                        Surface(
                                            color = if (UsuarioRepository.esAdminOSupervisor(u.rol, u.correo))
                                                MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = if (UsuarioRepository.esAdminOSupervisor(u.rol, u.correo))
                                                MaterialTheme.colorScheme.onPrimary
                                            else MaterialTheme.colorScheme.onSecondaryContainer,
                                            shape = RoundedCornerShape(16.dp)
                                        ) {
                                            Text(
                                                text = u.rol,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider()
                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // #switch #desactivar_usuario - Interruptor rápido para activar / suspender cuenta
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (u.activo) "Cuenta activa" else "Desactivada",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (u.activo) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Switch(
                                                checked = u.activo,
                                                onCheckedChange = {
                                                    UsuarioRepository.toggleEstadoUsuario(u.correo)
                                                    val msg = if (!u.activo) "Cuenta de ${u.nombre} activada" else "Cuenta de ${u.nombre} desactivada"
                                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.scale(0.8f)
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            // #boton #editar_usuario - Botón para abrir modal de edición de perfil
                                            IconButton(
                                                onClick = {
                                                    editNombre = u.nombre
                                                    editApellido = u.apellido
                                                    editTelefono = u.telefono
                                                    editCargo = u.cargo
                                                    editDepartamento = u.departamento
                                                    usuarioEditarPerfil = u
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.Edit, contentDescription = "Editar datos", modifier = Modifier.size(18.dp))
                                            }

                                            // #boton #editar_rol - Botón para cambiar rol
                                            IconButton(
                                                onClick = {
                                                    nuevoRolSeleccionado = u.rol
                                                    usuarioEditarRol = u
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.AdminPanelSettings, contentDescription = "Cambiar Rol", modifier = Modifier.size(18.dp))
                                            }

                                            // #boton #cambiar_password - Botón para cambiar contraseña
                                            IconButton(
                                                onClick = {
                                                    nuevaPasswordText = ""
                                                    errorPasswordText = null
                                                    usuarioEditarPassword = u
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.Key, contentDescription = "Cambiar Contraseña", modifier = Modifier.size(18.dp))
                                            }

                                            // #boton #eliminar_usuario - Botón icono para eliminar usuario
                                            IconButton(
                                                onClick = { usuarioEliminar = u },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Eliminar Usuario", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        // #boton #cerrar_modal - Botón para cerrar el panel modal de administración
        confirmButton = {
            Button(onClick = onDismissRequest) {
                Text("Cerrar")
            }
        }
    )
}
