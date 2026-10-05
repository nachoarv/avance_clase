package cl.duoc.primer_mvvm.`02_login`

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(),
    onLoginExitoso: (String) -> Unit,
    onIrARegistro: () -> Unit
) {
    val uiState = viewModel.uiState

    if (uiState.mostrarDialogoCambiarPassword) {
        AlertDialog(
            onDismissRequest = { viewModel.cerrarDialogoCambiarPassword() },
            title = { Text("Cambiar contraseña") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Ingrese la nueva contraseña para su cuenta:")
                    OutlinedTextField(
                        value = uiState.nuevaPassword,
                        onValueChange = { viewModel.onNuevaPasswordChange(it) },
                        label = { Text("Nueva contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = uiState.repetirNuevaPassword,
                        onValueChange = { viewModel.onRepetirNuevaPasswordChange(it) },
                        label = { Text("Repetir nueva contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    uiState.errorNuevaPassword?.let { err ->
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.cambiarPassword() }) {
                    Text("Guardar")
                }
            },
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

        uiState.mensajeError?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        uiState.mensajeExitoCambio?.let { mensajeExito ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mensajeExito,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (uiState.mostrarOpcionCambiarPassword) {
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

        Button(
            onClick = {
                viewModel.iniciarSesion(onSuccess = onLoginExitoso)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onIrARegistro) {
            Text("¿No tienes una cuenta? Regístrate aquí")
        }
    }
}
