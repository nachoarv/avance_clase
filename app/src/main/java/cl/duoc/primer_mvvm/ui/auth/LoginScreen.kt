// #login #autenticacion #cambio_clave #sesion #animacion #palabra_clave
package cl.duoc.primer_mvvm.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.primer_mvvm.data.UsuarioRepository

/**
 * #login #autenticacion #animacion #palabra_clave
 * Pantalla de inicio de sesión con validación de palabra clave de seguridad para cuentas Admin/Supervisor.
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(),
    onLoginExitoso: (String, String) -> Unit,
    onIrARegistro: () -> Unit
) {
    val uiState = viewModel.uiState

    // #dialogo #cambiar_password #palabra_clave - Diálogo modal para ingresar la nueva contraseña y palabra clave
    if (uiState.mostrarDialogoCambiarPassword) {
        val usuarioTarget = UsuarioRepository.buscarPorCorreo(uiState.correo)
        val esCuentaPrivilegiada = usuarioTarget != null && UsuarioRepository.esAdminOSupervisor(usuarioTarget.rol, usuarioTarget.correo)

        AlertDialog(
            onDismissRequest = { viewModel.cerrarDialogoCambiarPassword() },
            title = { Text("Cambiar contraseña") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Ingrese la nueva contraseña para su cuenta (${uiState.correo}):")

                    // #campo_texto #nueva_password - Campo para ingresar la nueva contraseña
                    OutlinedTextField(
                        value = uiState.nuevaPassword,
                        onValueChange = { viewModel.onNuevaPasswordChange(it) },
                        label = { Text("Nueva contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    // #campo_texto #repetir_nueva_password - Campo para confirmar la nueva contraseña
                    OutlinedTextField(
                        value = uiState.repetirNuevaPassword,
                        onValueChange = { viewModel.onRepetirNuevaPasswordChange(it) },
                        label = { Text("Repetir nueva contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // #palabra_clave #seguridad - Solicita Palabra Clave de Seguridad obligatoria para cuentas Admin o Supervisor
                    if (esCuentaPrivilegiada) {
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
                                        text = "🔒 Cuenta Privilegiada (${usuarioTarget?.rol})",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Se requiere la Palabra Clave de Seguridad para autorizar este cambio.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        // #campo_texto #palabra_clave - Campo para ingresar la palabra clave de seguridad
                        OutlinedTextField(
                            value = uiState.palabraClaveInput,
                            onValueChange = { viewModel.onPalabraClaveChange(it) },
                            label = { Text("Palabra Clave de Seguridad *") },
                            placeholder = { Text("Ej: EMPRESA2026") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // #animacion #mensaje_error - Visibilidad animada para mensajes de error de contraseña o palabra clave
                    AnimatedVisibility(
                        visible = uiState.errorNuevaPassword != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        uiState.errorNuevaPassword?.let { err ->
                            Text(
                                text = err,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            },
            // #boton #guardar_password - Botón para confirmar el cambio de contraseña
            confirmButton = {
                Button(onClick = { viewModel.cambiarPassword() }) {
                    Text("Guardar")
                }
            },
            // #boton #cancelar - Botón para cancelar el diálogo
            dismissButton = {
                TextButton(onClick = { viewModel.cerrarDialogoCambiarPassword() }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // #campo_texto #correo - Campo de texto para ingresar el correo electrónico del usuario
        OutlinedTextField(
            value = uiState.correo,
            onValueChange = { viewModel.onCorreoChange(it) },
            label = { Text("Correo electrónico") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            isError = uiState.errorCorreo,
            supportingText = {
                if (uiState.errorCorreo) {
                    Text("Ingrese un correo válido")
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // #campo_texto #password - Campo de texto para la contraseña
        OutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            isError = uiState.errorPassword,
            supportingText = {
                if (uiState.errorPassword) {
                    Text("La contraseña no puede estar vacía")
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // #animacion #mensaje_error - Visibilidad animada para el mensaje de error de login
        AnimatedVisibility(
            visible = uiState.mensajeError != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            uiState.mensajeError?.let { error ->
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // #animacion #mensaje_exito - Visibilidad animada para el mensaje de cambio de contraseña exitoso
        AnimatedVisibility(
            visible = uiState.mensajeExitoCambio != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            uiState.mensajeExitoCambio?.let { mensajeExito ->
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = mensajeExito,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (uiState.mostrarOpcionCambiarPassword) {
            // #boton #recuperar_password - Botón de texto para solicitar cambio o recuperación de contraseña
            TextButton(
                onClick = { viewModel.abrirDialogoCambiarPassword() }
            ) {
                Text(
                    text = "¿Olvidaste o deseas cambiar tu contraseña?",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // #boton #ingresar - Botón principal para validar datos e iniciar sesión
        Button(
            onClick = {
                viewModel.iniciarSesion(onSuccess = onLoginExitoso)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // #boton #ir_a_registro - Botón de texto para cambiar a la pestaña de registro
        TextButton(onClick = onIrARegistro) {
            Text("¿No tienes una cuenta? Regístrate aquí")
        }
    }
}
