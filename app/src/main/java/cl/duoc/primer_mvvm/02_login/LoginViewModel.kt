package cl.duoc.primer_mvvm.`02_login`

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onCorreoChange(correo: String) {
        uiState = uiState.copy(correo = correo, errorCorreo = false, mensajeError = null)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password, errorPassword = false, mensajeError = null)
    }

    fun iniciarSesion(onSuccess: (String) -> Unit) {
        val errCorreo = uiState.correo.isBlank() || !uiState.correo.contains("@")
        val errPassword = uiState.password.length < 8

        if (errCorreo || errPassword) {
            uiState = uiState.copy(
                errorCorreo = errCorreo,
                errorPassword = errPassword,
                mensajeError = if (errPassword) "Contraseña incorrecta (debe tener al menos 8 caracteres)" else "Por favor, ingrese un correo válido",
                mostrarOpcionCambiarPassword = errPassword
            )
            return
        }

        uiState = uiState.copy(
            mensajeError = null,
            mostrarOpcionCambiarPassword = false
        )
        onSuccess(uiState.correo)
    }

    fun abrirDialogoCambiarPassword() {
        uiState = uiState.copy(
            mostrarDialogoCambiarPassword = true,
            nuevaPassword = "",
            repetirNuevaPassword = "",
            errorNuevaPassword = null
        )
    }

    fun cerrarDialogoCambiarPassword() {
        uiState = uiState.copy(mostrarDialogoCambiarPassword = false)
    }

    fun onNuevaPasswordChange(password: String) {
        uiState = uiState.copy(nuevaPassword = password, errorNuevaPassword = null)
    }

    fun onRepetirNuevaPasswordChange(password: String) {
        uiState = uiState.copy(repetirNuevaPassword = password, errorNuevaPassword = null)
    }

    fun cambiarPassword() {
        if (uiState.nuevaPassword.length < 8) {
            uiState = uiState.copy(errorNuevaPassword = "La nueva contraseña debe tener al menos 8 caracteres")
            return
        }
        if (uiState.nuevaPassword != uiState.repetirNuevaPassword) {
            uiState = uiState.copy(errorNuevaPassword = "Las contraseñas no coinciden")
            return
        }

        uiState = uiState.copy(
            password = uiState.nuevaPassword,
            errorPassword = false,
            mensajeError = null,
            mostrarOpcionCambiarPassword = false,
            mostrarDialogoCambiarPassword = false,
            mensajeExitoCambio = "¡Contraseña actualizada correctamente!"
        )
    }
}
