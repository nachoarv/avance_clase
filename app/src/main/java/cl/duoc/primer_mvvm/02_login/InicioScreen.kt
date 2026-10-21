package cl.duoc.primer_mvvm.`02_login`

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
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import cl.duoc.primer_mvvm.`03_oportunidades`.OportunidadesUiState
import cl.duoc.primer_mvvm.model.EstadoOportunidad

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

    // Launcher para la Cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            onPerfilBitmapChange(bitmap)
        }
    }

    // Launcher para solicitar Permiso de Cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(
                context,
                "Se requiere permiso de cámara para tomar la foto",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Función auxiliar para verificar y solicitar permiso
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

    // Launcher para la Galería (Photo Picker)
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

    // Diálogo para elegir origen de la imagen
    if (showOpcionesFotoDialog) {
        AlertDialog(
            onDismissRequest = { showOpcionesFotoDialog = false },
            title = { Text("Foto de Perfil") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar con icono de edición
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.clickable { showOpcionesFotoDialog = true }
        ) {
            val currentBitmap = perfilBitmap
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

            // Indicador / Botón flotante para cambiar foto
            SmallFloatingActionButton(
                onClick = { showOpcionesFotoDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = CircleShape,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Cambiar foto",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = { showOpcionesFotoDialog = true }) {
            Text("Cambiar foto de perfil")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¡Sesión Iniciada!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Bienvenido/a: $usuario",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(6.dp))

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

        Spacer(modifier = Modifier.height(24.dp))

        var showDialogConfirmadas by remember { mutableStateOf(false) }
        var showDialogHistorial by remember { mutableStateOf(false) }

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

        OutlinedButton(
            onClick = { showDialogConfirmadas = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ver Confirmaciones")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { showDialogHistorial = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.History, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Mi Historial de Creaciones y Gestiones")
        }

        Spacer(modifier = Modifier.height(16.dp))

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
