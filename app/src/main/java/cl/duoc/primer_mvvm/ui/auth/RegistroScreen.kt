// #formulario #registro #datepicker #terminos #validaciones #animacion
package cl.duoc.primer_mvvm.ui.auth

import androidx.compose.animation.*
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

/**
 * #formulario #registro #datepicker #animacion
 * Pantalla de registro de nuevos usuarios con formulario animado y validaciones.
 */
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

    // #redireccion_automatica #registro - Inicia sesión de inmediato y redirige al Dashboard sin clics adicionales
    LaunchedEffect(uiState.mensajeExito) {
        if (uiState.mensajeExito) {
            val usuario = uiState.nombre.ifBlank { uiState.correo }
            onRegistroExitoso?.invoke(usuario)
        }
    }

    // #dialogo #datepicker - Selector modal de fecha de nacimiento
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            // #boton #aceptar_fecha - Botón para confirmar fecha seleccionada
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
            // #boton #cancelar_fecha - Botón para cerrar selector
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

        // #campo_texto #nombre - Campo para escribir el nombre
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

        // #campo_texto #apellido - Campo para escribir el apellido
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

        // #campo_texto #fecha_nacimiento - Campo con DatePicker para fecha de nacimiento
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = uiState.fechaNacimiento,
                onValueChange = { viewModel.onFechaNacimientoChange(it) },
                readOnly = true,
                label = { Text("Fecha de nacimiento") },
                placeholder = { Text("DD/MM/AAAA") },
                // #boton #datepicker - Botón icono para desplegar el selector de fecha
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

        // #campo_texto #correo - Campo para ingresar el correo
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

        // #campo_texto #password - Campo para ingresar la contraseña
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

        // #campo_texto #repetir_password - Campo para repetir la contraseña
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

        // #checkbox #terminos - Casilla para aceptar los términos y condiciones
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

        // #animacion #error_terminos - Aparición animada del error de términos
        AnimatedVisibility(
            visible = uiState.errorTerminos,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = "Debe aceptar los términos y condiciones",
                color = MaterialTheme.colorScheme.error
            )
        }

        // #animacion #mensaje_exito - Despliegue animado del aviso de registro exitoso
        AnimatedVisibility(
            visible = uiState.mensajeExito,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "¡Usuario registrado correctamente!",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )
                onRegistroExitoso?.let { onExito ->
                    // #boton #ir_a_mi_cuenta - Botón desplegado tras registro exitoso
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
        }

        // #boton #registrarse - Botón principal para enviar el formulario de registro
        Button(
            onClick = {
                viewModel.registrar()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }

        onIrALogin?.let { onLogin ->
            // #boton #ir_a_login - Botón para navegar de vuelta al login
            TextButton(
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("¿Ya tienes una cuenta? Inicia sesión aquí")
            }
        }
    }
}
