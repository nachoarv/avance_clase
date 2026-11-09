// #perfil #camara #galeria #aviso_camara #permiso_camara #gestion_admin #animacion #palabra_clave
package cl.duoc.primer_mvvm.ui.profile

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import cl.duoc.primer_mvvm.data.UsuarioRepository
import cl.duoc.primer_mvvm.model.EstadoOportunidad
import cl.duoc.primer_mvvm.ui.auth.GestionUsuariosDialog
import cl.duoc.primer_mvvm.ui.oportunidades.OportunidadesUiState

/**
 * #perfil #camara #galeria #aviso_camara #permiso_camara #animacion #palabra_clave
 * Pantalla de perfil de usuario completa con modificación de contraseña segura mediante Palabra Clave.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    usuario: String,
    rol: String,
    uiState: OportunidadesUiState,
    onCerrarSesion: () -> Unit,
    perfilBitmap: Bitmap?,
    onPerfilBitmapChange: (Bitmap?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showOpcionesFotoDialog by remember { mutableStateOf(false) }
    var showDialogGestionUsuarios by remember { mutableStateOf(false) }
    var showEditarPerfilDialog by remember { mutableStateOf(false) }

    var showCambiarMiPasswordDialog by remember { mutableStateOf(false) }
    var miNuevaPassword by remember { mutableStateOf("") }
    var miRepetirPassword by remember { mutableStateOf("") }
    var miPalabraClave by remember { mutableStateOf("") }
    var errorMiPassword by remember { mutableStateOf<String?>(null) }

    // Obtener datos extendidos del usuario en sesión
    val datosUsuario = UsuarioRepository.usuarios.collectAsState().value.find {
        it.nombreCompleto().equals(usuario, ignoreCase = true) || it.correo.equals(usuario, ignoreCase = true)
    }

    var editNombre by remember { mutableStateOf(datosUsuario?.nombre ?: "") }
    var editApellido by remember { mutableStateOf(datosUsuario?.apellido ?: "") }
    var editTelefono by remember { mutableStateOf(datosUsuario?.telefono ?: "+56 9 1234 5678") }
    var editCargo by remember { mutableStateOf(datosUsuario?.cargo ?: "Ejecutivo Comercial") }
    var editDepartamento by remember { mutableStateOf(datosUsuario?.departamento ?: "Ventas & Convenios") }

    val esAdminOSupervisor = UsuarioRepository.esAdminOSupervisor(rol, usuario)

    // #animacion #camera_glow - Transición infinita para brillo animado sobre el botón de cámara
    val infiniteTransition = rememberInfiniteTransition(label = "CameraGlowTransition")
    val fabScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FabCameraPulse"
    )

    // #dialogo #gestion_usuarios - Diálogo de gestión para administradores
    if (showDialogGestionUsuarios) {
        GestionUsuariosDialog(
            onDismissRequest = { showDialogGestionUsuarios = false }
        )
    }

    // #dialogo #cambiar_mi_password #palabra_clave - Modal para cambiar clave desde la pantalla de perfil
    if (showCambiarMiPasswordDialog && datosUsuario != null) {
        val esPrivilegiado = UsuarioRepository.esAdminOSupervisor(datosUsuario.rol, datosUsuario.correo)

        AlertDialog(
            onDismissRequest = { showCambiarMiPasswordDialog = false },
            icon = { Icon(Icons.Default.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Cambiar Mi Contraseña") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text("Usuario: ${datosUsuario.nombreCompleto()} (${datosUsuario.correo})")

                    OutlinedTextField(
                        value = miNuevaPassword,
                        onValueChange = {
                            miNuevaPassword = it
                            errorMiPassword = null
                        },
                        label = { Text("Nueva Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = miRepetirPassword,
                        onValueChange = {
                            miRepetirPassword = it
                            errorMiPassword = null
                        },
                        label = { Text("Repetir Nueva Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // #palabra_clave #seguridad - Solicita Palabra Clave para cuentas Admin / Supervisor
                    if (esPrivilegiado) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "🔒 Cuenta Privilegiada (${datosUsuario.rol})",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Ingrese la Palabra Clave de Seguridad para autorizar este cambio.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        OutlinedTextField(
                            value = miPalabraClave,
                            onValueChange = {
                                miPalabraClave = it
                                errorMiPassword = null
                            },
                            label = { Text("Palabra Clave de Seguridad *") },
                            placeholder = { Text("Ej: EMPRESA2026") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    errorMiPassword?.let { err ->
                        Text(err, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (miNuevaPassword.length < 8) {
                            errorMiPassword = "La contraseña debe tener al menos 8 caracteres"
                            return@Button
                        }
                        if (miNuevaPassword != miRepetirPassword) {
                            errorMiPassword = "Las contraseñas no coinciden"
                            return@Button
                        }

                        if (esPrivilegiado) {
                            val esClaveValida = UsuarioRepository.validarPalabraClave(miPalabraClave)
                            if (!esClaveValida) {
                                errorMiPassword = "Palabra clave de seguridad incorrecta. No está autorizado para modificar la clave de esta cuenta privilegiada."
                                return@Button
                            }
                        }

                        val ok = UsuarioRepository.cambiarPasswordUsuario(datosUsuario.correo, miNuevaPassword)
                        if (ok) {
                            Toast.makeText(context, "Contraseña actualizada exitosamente", Toast.LENGTH_SHORT).show()
                            showCambiarMiPasswordDialog = false
                            miNuevaPassword = ""
                            miRepetirPassword = ""
                            miPalabraClave = ""
                        }
                    }
                ) {
                    Text("Guardar Contraseña")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCambiarMiPasswordDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // #dialogo #editar_mi_perfil - Diálogo para que el propio usuario edite sus datos de contacto
    if (showEditarPerfilDialog && datosUsuario != null) {
        AlertDialog(
            onDismissRequest = { showEditarPerfilDialog = false },
            icon = { Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Editar Mis Datos de Perfil") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
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
                        label = { Text("Teléfono de Contacto") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editCargo,
                        onValueChange = { editCargo = it },
                        label = { Text("Cargo") },
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
                        UsuarioRepository.actualizarUsuarioCompleto(
                            correo = datosUsuario.correo,
                            nuevoNombre = editNombre,
                            nuevoApellido = editApellido,
                            nuevoTelefono = editTelefono,
                            nuevoCargo = editCargo,
                            nuevoDepartamento = editDepartamento
                        )
                        Toast.makeText(context, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                        showEditarPerfilDialog = false
                    }
                ) {
                    Text("Guardar Cambios")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditarPerfilDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // #camara - Launcher para tomar foto utilizando la cámara del dispositivo
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            onPerfilBitmapChange(bitmap)
        }
    }

    // #permiso_camara #aviso_camara - Solicitud de permiso de cámara con aviso (Toast) si es denegado
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            // #aviso_camara - Aviso en pantalla sobre el permiso denegado
            Toast.makeText(
                context,
                "Se requiere permiso de cámara para tomar la foto de perfil",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // #camara #permiso_camara #aviso_camara - Función para verificar y solicitar permiso de cámara
    val solicitarPermisoAbrirCamara = {
        val tienePermiso = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (tienePermiso) {
            cameraLauncher.launch(null)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // #galeria - Launcher para seleccionar una foto de la galería usando Photo Picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    BitmapFactory.decodeStream(inputStream)
                }
                onPerfilBitmapChange(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // #dialogo #camara #galeria - Diálogo modal para elegir el origen de la foto
    if (showOpcionesFotoDialog) {
        AlertDialog(
            onDismissRequest = { showOpcionesFotoDialog = false },
            title = { Text("Foto de Perfil") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // #boton #camara - Botón para activar la cámara del dispositivo
                    TextButton(
                        onClick = {
                            showOpcionesFotoDialog = false
                            solicitarPermisoAbrirCamara()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tomar foto con la cámara")
                    }

                    // #boton #galeria - Botón para abrir la galería de imágenes
                    TextButton(
                        onClick = {
                            showOpcionesFotoDialog = false
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Seleccionar de la galería")
                    }

                    if (perfilBitmap != null) {
                        // #boton #eliminar_foto - Botón para remover la foto actual
                        TextButton(
                            onClick = {
                                onPerfilBitmapChange(null)
                                showOpcionesFotoDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Eliminar foto actual", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showOpcionesFotoDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // #avatar #perfil #animacion - Foto o icono principal de perfil con animación al actualizar foto
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.clickable { showOpcionesFotoDialog = true }
        ) {
            // #animacion #foto_perfil - Transición animada al cambiar la foto tomada con cámara o galería
            AnimatedContent(
                targetState = perfilBitmap,
                transitionSpec = {
                    fadeIn(animationSpec = tween(350)) + scaleIn(initialScale = 0.85f) togetherWith
                            fadeOut(animationSpec = tween(250))
                },
                label = "PerfilPhotoAnimation"
            ) { currentBitmap ->
                if (currentBitmap != null) {
                    Image(
                        bitmap = currentBitmap.asImageBitmap(),
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Usuario sin foto",
                        modifier = Modifier.size(120.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // #boton #camara #animacion - Botón flotante directo con escala pulsante animada
            SmallFloatingActionButton(
                onClick = { showOpcionesFotoDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = CircleShape,
                modifier = Modifier
                    .size(36.dp)
                    .scale(fabScale)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Cambiar foto",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // #boton #cambiar_foto - Texto clickeable para abrir opciones de foto
        TextButton(onClick = { showOpcionesFotoDialog = true }) {
            Text("Cambiar foto de perfil")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = datosUsuario?.nombreCompleto() ?: usuario,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = CircleShape
        ) {
            Text(
                text = "Rol: $rol",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // #tarjeta #datos_perfil #animacion - Tarjeta informativa con datos extendidos del perfil
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Información de la Cuenta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    
                    Row {
                        // #boton #cambiar_mi_password - Botón para cambiar clave propia con seguridad de palabra clave
                        IconButton(
                            onClick = {
                                miNuevaPassword = ""
                                miRepetirPassword = ""
                                miPalabraClave = ""
                                errorMiPassword = null
                                showCambiarMiPasswordDialog = true
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Key, contentDescription = "Cambiar mi contraseña", modifier = Modifier.size(18.dp))
                        }

                        // #boton #editar_mi_perfil - Botón icono para editar perfil propio
                        IconButton(
                            onClick = {
                                if (datosUsuario != null) {
                                    editNombre = datosUsuario.nombre
                                    editApellido = datosUsuario.apellido
                                    editTelefono = datosUsuario.telefono
                                    editCargo = datosUsuario.cargo
                                    editDepartamento = datosUsuario.departamento
                                }
                                showEditarPerfilDialog = true
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar mi perfil", modifier = Modifier.size(18.dp))
                        }
                    }
                }

                HorizontalDivider()

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Correo: ${datosUsuario?.correo ?: usuario}", style = MaterialTheme.typography.bodyMedium)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Teléfono: ${datosUsuario?.telefono ?: "+56 9 1234 5678"}", style = MaterialTheme.typography.bodyMedium)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Work, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cargo: ${datosUsuario?.cargo ?: "Ejecutivo Comercial"}", style = MaterialTheme.typography.bodyMedium)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.BusinessCenter, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Área: ${datosUsuario?.departamento ?: "Ventas & Convenios"}", style = MaterialTheme.typography.bodyMedium)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Estado Cuenta: Activa", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // #gestion_admin - Panel exclusivo para administradores y supervisores
        if (esAdminOSupervisor) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Panel de Administración",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Gestione cuentas de usuario, modifique contraseñas, active/desactive e inspeccione métricas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    // #boton #gestion_usuarios - Botón para abrir el diálogo de administración de cuentas
                    Button(
                        onClick = { showDialogGestionUsuarios = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ManageAccounts, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gestión de Usuarios (Admin)")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        var showDialogConfirmadas by remember { mutableStateOf(false) }
        var showDialogHistorial by remember { mutableStateOf(false) }

        // #dialogo #confirmaciones - Diálogo para revisar oportunidades confirmadas
        if (showDialogConfirmadas) {
            val confirmadas = uiState.oportunidades.filter {
                it.estado == EstadoOportunidad.PARTICIPACION_CONFIRMADA || it.estado == EstadoOportunidad.ACTIVACION_REALIZADA
            }
            AlertDialog(
                onDismissRequest = { showDialogConfirmadas = false },
                title = { Text("Oportunidades Confirmadas") },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (confirmadas.isEmpty()) {
                            Text("No hay oportunidades confirmadas actualmente.")
                        } else {
                            confirmadas.forEach { op ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(op.nombre, fontWeight = FontWeight.Bold)
                                        Text("Estado: ${op.estado.label}", style = MaterialTheme.typography.bodySmall)
                                        Text("Responsable: ${op.responsableInterno}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDialogConfirmadas = false }) {
                        Text("Cerrar")
                    }
                }
            )
        }

        // #dialogo #historial - Diálogo para revisar el historial de actividades
        if (showDialogHistorial) {
            val misSeguimientos = uiState.seguimientos.filter {
                it.usuarioResponsable.contains(usuario, ignoreCase = true) || usuario.isBlank()
            }
            val misOportunidades = uiState.oportunidades.filter {
                it.responsableInterno.contains(usuario, ignoreCase = true)
            }

            AlertDialog(
                onDismissRequest = { showDialogHistorial = false },
                title = { Text("Historial de Actividad") },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Oportunidades a cargo (${misOportunidades.size}):", fontWeight = FontWeight.Bold)
                        if (misOportunidades.isEmpty()) {
                            Text("Ninguna registrada.", style = MaterialTheme.typography.bodySmall)
                        } else {
                            misOportunidades.forEach { op ->
                                Text("• ${op.nombre} (${op.estado.label})", style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Gestiones y Seguimientos (${misSeguimientos.size}):", fontWeight = FontWeight.Bold)
                        if (misSeguimientos.isEmpty()) {
                            Text("Ningún seguimiento registrado.", style = MaterialTheme.typography.bodySmall)
                        } else {
                            misSeguimientos.forEach { seg ->
                                Text("• [${seg.fechaHora}] ${seg.tipoAccion.label}: ${seg.resultado}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDialogHistorial = false }) {
                        Text("Cerrar")
                    }
                }
            )
        }

        // #boton #ver_confirmaciones - Botón para desplegar lista de confirmadas
        OutlinedButton(
            onClick = { showDialogConfirmadas = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ver Confirmaciones")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // #boton #ver_historial - Botón para desplegar historial de creaciones y gestiones
        OutlinedButton(
            onClick = { showDialogHistorial = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.History, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Mi Historial de Creaciones y Gestiones")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // #boton #cerrar_sesion - Botón para salir y cerrar la sesión activa
        Button(
            onClick = onCerrarSesion,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Cerrar sesión"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar sesión")
        }
    }
}
