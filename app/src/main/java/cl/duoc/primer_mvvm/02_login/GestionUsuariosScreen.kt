package cl.duoc.primer_mvvm.`02_login`

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import cl.duoc.primer_mvvm.data.UsuarioRepository
import cl.duoc.primer_mvvm.model.Usuario
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionUsuariosDialog(
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val usuariosList by UsuarioRepository.usuarios.collectAsState()

    var textoBusqueda by remember { mutableStateOf("") }
    var usuarioEditarPassword by remember { mutableStateOf<Usuario?>(null) }
    var nuevaPasswordText by remember { mutableStateOf("") }
    var errorPasswordText by remember { mutableStateOf<String?>(null) }

    var usuarioEditarRol by remember { mutableStateOf<Usuario?>(null) }
    var nuevoRolSeleccionado by remember { mutableStateOf("Ejecutivo Comercial") }

    var mostrarCrearUsuarioDialog by remember { mutableStateOf(false) }
    var nuevoNombre by remember { mutableStateOf("") }
    var nuevoCorreo by remember { mutableStateOf("") }
    var nuevaPassword by remember { mutableStateOf("") }
    var nuevoRol by remember { mutableStateOf("Ejecutivo Comercial") }
    var errorCreacion by remember { mutableStateOf<String?>(null) }

    val usuariosFiltrados = usuariosList.filter { u ->
        u.nombre.contains(textoBusqueda, ignoreCase = true) ||
                u.apellido.contains(textoBusqueda, ignoreCase = true) ||
                u.correo.contains(textoBusqueda, ignoreCase = true) ||
                u.rol.contains(textoBusqueda, ignoreCase = true)
    }

    // Diálogo para cambiar contraseña de un usuario
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

    // Diálogo para cambiar rol de un usuario
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

    // Diálogo para crear un usuario como Admin
    if (mostrarCrearUsuarioDialog) {
        AlertDialog(
            onDismissRequest = { mostrarCrearUsuarioDialog = false },
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            title = { Text("Crear Nuevo Usuario") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = nuevoNombre,
                        onValueChange = { nuevoNombre = it },
                        label = { Text("Nombre Completo") },
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
                            errorCreacion = "Complete todos los campos (mínimo 8 caracteres para contraseña)"
                            return@Button
                        }
                        val u = Usuario(
                            id = UUID.randomUUID().toString(),
                            correo = nuevoCorreo.trim(),
                            password = nuevaPassword,
                            nombre = nuevoNombre.trim(),
                            rol = nuevoRol
                        )
                        val ok = UsuarioRepository.agregarUsuario(u)
                        if (ok) {
                            Toast.makeText(context, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show()
                            mostrarCrearUsuarioDialog = false
                            nuevoNombre = ""
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
            .fillMaxHeight(0.88f),
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
                        text = "Gestión de Usuarios (Admin)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Buscador de usuarios
                OutlinedTextField(
                    value = textoBusqueda,
                    onValueChange = { textoBusqueda = it },
                    placeholder = { Text("Buscar usuario por nombre, correo o rol...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total cuentas: ${usuariosFiltrados.size}",
                        style = MaterialTheme.typography.bodyMedium,
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
                        Text("Nuevo Usuario")
                    }
                }

                HorizontalDivider()

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(usuariosFiltrados, key = { it.id }) { u ->
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = if (UsuarioRepository.esAdminOSupervisor(u.rol, u.correo))
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
                                        Text(
                                            text = u.nombreCompleto(),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = u.correo,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
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

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Contraseña: ${u.password}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            nuevoRolSeleccionado = u.rol
                                            usuarioEditarRol = u
                                        },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Rol", style = MaterialTheme.typography.bodySmall)
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        onClick = {
                                            nuevaPasswordText = ""
                                            errorPasswordText = null
                                            usuarioEditarPassword = u
                                        },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Cambiar Contraseña", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismissRequest) {
                Text("Cerrar")
            }
        }
    )
}
