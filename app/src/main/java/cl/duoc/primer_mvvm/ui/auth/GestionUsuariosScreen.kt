// #gestion_usuarios #admin #cambio_clave #roles #desactivar_usuario #eliminar_usuario #kpi_metricas #animacion #editar_oportunidad_admin
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
import cl.duoc.primer_mvvm.data.OportunidadRepository
import cl.duoc.primer_mvvm.data.UsuarioRepository
import cl.duoc.primer_mvvm.model.Oportunidad
import cl.duoc.primer_mvvm.model.Prioridad
import cl.duoc.primer_mvvm.model.Usuario
import java.util.UUID

/**
 * #gestion_usuarios #admin #roles #editar_oportunidad_admin #animacion
 * Panel de administración para gestión de usuarios y modificación de oportunidades comerciales para el Supervisor/Admin.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionUsuariosDialog(
    onDismissRequest: () -> Unit,
    onVerOportunidad: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val usuariosList by UsuarioRepository.usuarios.collectAsState()
    val oportunidadesList by OportunidadRepository.oportunidades.collectAsState()
    val eventosList by OportunidadRepository.eventos.collectAsState()

    var selectedAdminTab by remember { mutableIntStateOf(0) } // 0: Usuarios, 1: Oportunidades

    // Variables para la pestaña de usuarios
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

    // Variables para la pestaña de modificación de oportunidades desde Admin
    var busquedaOportunidadAdmin by remember { mutableStateOf("") }
    var oportunidadEditarAdmin by remember { mutableStateOf<Oportunidad?>(null) }
    var editOpNombre by remember { mutableStateOf("") }
    var editOpTipo by remember { mutableStateOf("") }
    var editOpDesc by remember { mutableStateOf("") }
    var editOpFuente by remember { mutableStateOf("") }
    var editOpPrioridad by remember { mutableStateOf(Prioridad.ALTA) }
    var editOpPotencial by remember { mutableStateOf("Alto") }
    var editOpEventoNombre by remember { mutableStateOf("") }
    var editOpFechaInicio by remember { mutableStateOf("") }
    var editOpFechaTermino by remember { mutableStateOf("") }
    var editOpHorario by remember { mutableStateOf("") }
    var editOpDireccion by remember { mutableStateOf("") }
    var editOpComuna by remember { mutableStateOf("") }
    var editOpRegion by remember { mutableStateOf("") }
    var editOpAsistentes by remember { mutableStateOf("500") }
    var editOpObs by remember { mutableStateOf("") }

    // Métricas KPI de usuarios
    val totalCuentas = usuariosList.size
    val totalActivos = usuariosList.count { it.activo }
    val totalInactivos = usuariosList.count { !it.activo }

    val animTotal by animateIntAsState(targetValue = totalCuentas, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "TotalKpiAnim")
    val animActivos by animateIntAsState(targetValue = totalActivos, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "ActivosKpiAnim")
    val animInactivos by animateIntAsState(targetValue = totalInactivos, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "InactivosKpiAnim")

    // Filtrado de usuarios
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

    // Filtrado de oportunidades en el panel Admin
    val oportunidadesFiltradasAdmin = oportunidadesList.filter { op ->
        val ev = eventosList.find { it.oportunidadId == op.id }
        busquedaOportunidadAdmin.isBlank() ||
                op.nombre.contains(busquedaOportunidadAdmin, ignoreCase = true) ||
                op.responsableInterno.contains(busquedaOportunidadAdmin, ignoreCase = true) ||
                op.tipo.contains(busquedaOportunidadAdmin, ignoreCase = true) ||
                (ev?.nombre?.contains(busquedaOportunidadAdmin, ignoreCase = true) == true)
    }

    // #dialogo #editar_oportunidad_admin - Modal de modificación de oportunidad desde el panel Admin/Supervisor
    if (oportunidadEditarAdmin != null) {
        val targetOp = oportunidadEditarAdmin!!
        AlertDialog(
            onDismissRequest = { oportunidadEditarAdmin = null },
            icon = { Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Modificar Oportunidad (Supervisor)") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text("Responsable: ${targetOp.responsableInterno}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)

                    OutlinedTextField(
                        value = editOpNombre,
                        onValueChange = { editOpNombre = it },
                        label = { Text("Nombre Oportunidad") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editOpTipo,
                        onValueChange = { editOpTipo = it },
                        label = { Text("Tipo Oportunidad") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editOpDesc,
                        onValueChange = { editOpDesc = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editOpFuente,
                        onValueChange = { editOpFuente = it },
                        label = { Text("Fuente Información") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editOpPotencial,
                        onValueChange = { editOpPotencial = it },
                        label = { Text("Potencial Comercial") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Prioridad:", fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Prioridad.entries.forEach { p ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = editOpPrioridad == p, onClick = { editOpPrioridad = p })
                                Text(p.name, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    HorizontalDivider()
                    Text("Datos del Evento", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    OutlinedTextField(
                        value = editOpEventoNombre,
                        onValueChange = { editOpEventoNombre = it },
                        label = { Text("Nombre del Evento") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editOpFechaInicio,
                            onValueChange = { editOpFechaInicio = it },
                            label = { Text("Fecha Inicio") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = editOpFechaTermino,
                            onValueChange = { editOpFechaTermino = it },
                            label = { Text("Fecha Término") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = editOpHorario,
                        onValueChange = { editOpHorario = it },
                        label = { Text("Horario") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editOpDireccion,
                        onValueChange = { editOpDireccion = it },
                        label = { Text("Dirección") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editOpComuna,
                            onValueChange = { editOpComuna = it },
                            label = { Text("Comuna") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = editOpRegion,
                            onValueChange = { editOpRegion = it },
                            label = { Text("Región") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = editOpAsistentes,
                        onValueChange = { editOpAsistentes = it },
                        label = { Text("Asistentes Estimados") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editOpObs,
                        onValueChange = { editOpObs = it },
                        label = { Text("Observaciones") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            // #boton #guardar_edicion_admin - Guarda las modificaciones realizadas por Supervisor/Admin
            confirmButton = {
                Button(
                    onClick = {
                        if (editOpNombre.isBlank()) return@Button
                        OportunidadRepository.actualizarOportunidadCompleta(
                            targetOp.id, editOpNombre, editOpTipo, editOpDesc, editOpFuente,
                            editOpPrioridad, editOpPotencial, editOpEventoNombre, editOpFechaInicio, editOpFechaTermino,
                            editOpHorario, editOpDireccion, editOpComuna, editOpRegion, editOpAsistentes.toIntOrNull() ?: 500, editOpObs
                        )
                        Toast.makeText(context, "Oportunidad modificada por el Supervisor", Toast.LENGTH_SHORT).show()
                        oportunidadEditarAdmin = null
                    }
                ) {
                    Text("Guardar Cambios")
                }
            },
            dismissButton = {
                TextButton(onClick = { oportunidadEditarAdmin = null }) {
                    Text("Cancelar")
                }
            }
        )
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

    // Modal Principal de Gestión Admin/Supervisor
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.92f),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        title = {
            Column {
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

                Spacer(modifier = Modifier.height(8.dp))

                // #tab #panel_admin - Pestañas para cambiar entre Gestión de Cuentas y Modificación de Oportunidades
                TabRow(selectedTabIndex = selectedAdminTab) {
                    Tab(
                        selected = selectedAdminTab == 0,
                        onClick = { selectedAdminTab = 0 },
                        text = { Text("Cuentas ($totalCuentas)") }
                    )
                    Tab(
                        selected = selectedAdminTab == 1,
                        onClick = { selectedAdminTab = 1 },
                        text = { Text("Oportunidades (${oportunidadesList.size})") }
                    )
                }
            }
        },
        text = {
            // #animacion #tabs_admin - Transición animada entre gestión de usuarios y modificación de oportunidades
            AnimatedContent(
                targetState = selectedAdminTab,
                label = "AdminTabsAnimation"
            ) { tab ->
                if (tab == 0) {
                    // PESTAÑA 0: GESTIÓN DE USUARIOS
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
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

                        OutlinedTextField(
                            value = textoBusqueda,
                            onValueChange = { textoBusqueda = it },
                            placeholder = { Text("Buscar usuario, correo, cargo...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

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

                                                    IconButton(
                                                        onClick = {
                                                            nuevoRolSeleccionado = u.rol
                                                            usuarioEditarRol = u
                                                        },
                                                        modifier = Modifier.size(32.dp)
                                                    ) {
                                                        Icon(Icons.Default.AdminPanelSettings, contentDescription = "Cambiar Rol", modifier = Modifier.size(18.dp))
                                                    }

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
                } else {
                    // PESTAÑA 1: GESTIÓN Y MODIFICACIÓN DE OPORTUNIDADES POR SUPERVISOR / ADMIN
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = busquedaOportunidadAdmin,
                            onValueChange = { busquedaOportunidadAdmin = it },
                            placeholder = { Text("Buscar oportunidad, evento o responsable...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "Supervisión Comercial (${oportunidadesFiltradasAdmin.size} oportunidades)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        HorizontalDivider()

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            itemsIndexed(oportunidadesFiltradasAdmin, key = { _, op -> op.id }) { index, op ->
                                val ev = eventosList.find { it.oportunidadId == op.id }

                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(animationSpec = tween(280, delayMillis = index * 40)) +
                                            slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(280, delayMillis = index * 40)),
                                    label = "OpAdminCardEnter"
                                ) {
                                    ElevatedCard(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .animateContentSize()
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
                                                    Text(
                                                        text = op.nombre,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = "Evento: ${ev?.nombre ?: op.nombre} (${ev?.comuna ?: "Santiago"})",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    Text(
                                                        text = "Responsable: ${op.responsableInterno}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }

                                                Surface(
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = MaterialTheme.colorScheme.secondaryContainer
                                                ) {
                                                    Text(
                                                        text = op.estado.label,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // #boton #ver_detalle_admin - Abre la vista completa de detalle
                                                if (onVerOportunidad != null) {
                                                    OutlinedButton(
                                                        onClick = {
                                                            onVerOportunidad(op.id)
                                                            onDismissRequest()
                                                        },
                                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                        modifier = Modifier.height(36.dp)
                                                    ) {
                                                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("Ver Detalle", style = MaterialTheme.typography.bodySmall)
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                }

                                                // #boton #editar_oportunidad_admin - Permite al Supervisor/Admin modificar la oportunidad
                                                Button(
                                                    onClick = {
                                                        editOpNombre = op.nombre
                                                        editOpTipo = op.tipo
                                                        editOpDesc = op.descripcion
                                                        editOpFuente = op.fuenteInformacion
                                                        editOpPrioridad = op.prioridad
                                                        editOpPotencial = op.potencialComercial
                                                        editOpEventoNombre = ev?.nombre ?: op.nombre
                                                        editOpFechaInicio = ev?.fechaInicio ?: "15/04/2026"
                                                        editOpFechaTermino = ev?.fechaTermino ?: "16/04/2026"
                                                        editOpHorario = ev?.horario ?: "09:00 - 18:00 hrs"
                                                        editOpDireccion = ev?.direccion ?: "Dirección corporativa"
                                                        editOpComuna = ev?.comuna ?: "Santiago"
                                                        editOpRegion = ev?.region ?: "Región Metropolitana"
                                                        editOpAsistentes = ev?.cantidadAsistentesEstimada?.toString() ?: "500"
                                                        editOpObs = op.observaciones
                                                        oportunidadEditarAdmin = op
                                                    },
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                    modifier = Modifier.height(36.dp)
                                                ) {
                                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Modificar Oportunidad", style = MaterialTheme.typography.bodySmall)
                                                }
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
