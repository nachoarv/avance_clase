package cl.duoc.primer_mvvm.`01_formulario`

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    modifier: Modifier = Modifier,
    viewModel: RegistroViewModel = viewModel(),
    onIrALogin: (() -> Unit)? = null,
    onRegistroExitoso: ((String) -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                                timeZone = TimeZone.getTimeZone("UTC")
                            }
                            val fechaFormateada = formatter.format(Date(millis))
                            viewModel.onFechaNacimientoChange(fechaFormateada)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = uiState.nombre,
            onValueChange = { viewModel.onNombreChange(it) },
            label = { Text("Nombre") },
            isError = uiState.errorNombre,
            supportingText = {
                if (uiState.errorNombre) {
                    Text("El nombre es obligatorio")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.apellido,
            onValueChange = { viewModel.onApellidoChange(it) },
            label = { Text("Apellido") },
            isError = uiState.errorApellido,
            supportingText = {
                if (uiState.errorApellido) {
                    Text("El apellido es obligatorio")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = uiState.fechaNacimiento,
                onValueChange = { viewModel.onFechaNacimientoChange(it) },
                readOnly = true,
                label = { Text("Fecha de nacimiento") },
                placeholder = { Text("DD/MM/AAAA") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Seleccionar fecha"
                        )
                    }
                },
                isError = uiState.errorFechaNacimiento,
                supportingText = {
                    if (uiState.errorFechaNacimiento) {
                        Text("Debe ingresar fecha DD/MM/AAAA y ser mayor de 18 años")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDatePicker = true }
            )
        }

        OutlinedTextField(
            value = uiState.correo,
            onValueChange = { viewModel.onCorreoChange(it) },
            label = { Text("Correo") },
            isError = uiState.errorCorreo,
            supportingText = {
                if (uiState.errorCorreo) {
                    Text("El correo debe contener '@' y finalizar con '.cl'")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            isError = uiState.errorPassword,
            supportingText = {
                if (uiState.errorPassword) {
                    Text("La contraseña debe tener al menos 8 caracteres")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.repetirPassword,
            onValueChange = { viewModel.onRepetirPasswordChange(it) },
            label = { Text("Repetir contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            isError = uiState.errorRepetirPassword,
            supportingText = {
                if (uiState.errorRepetirPassword) {
                    Text("Las contraseñas no coinciden")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = uiState.aceptaTerminos,
                onCheckedChange = {
                    viewModel.cambiarAceptaTerminos(it)
                }
            )
            Text("Acepto los términos y condiciones")
        }

        if (uiState.errorTerminos) {
            Text(
                text = "Debe aceptar los términos y condiciones",
                color = MaterialTheme.colorScheme.error
            )
        }

        if (uiState.mensajeExito) {
            Text(
                text = "¡Usuario registrado correctamente!",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge
            )
            onRegistroExitoso?.let { onExito ->
                Button(
                    onClick = {
                        val usuario = uiState.nombre.ifBlank { uiState.correo }
                        onExito(usuario)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ir a mi cuenta")
                }
            }
        }

        Button(
            onClick = {
                viewModel.registrar()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }

        onIrALogin?.let { onLogin ->
            TextButton(
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("¿Ya tienes una cuenta? Inicia sesión aquí")
            }
        }
    }
}
