package cl.duoc.primer_mvvm.`02_login`

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class CuentaPrueba(
    val correo: String,
    val password: String,
    val nombre: String,
    val rol: String
)

class LoginViewModel : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    private val cuentasPrueba = listOf(
        CuentaPrueba("supervisor@empresa.cl", "Password123", "Sofía Supervisor", "Supervisor Comercial"),
        CuentaPrueba("ejecutivo1@empresa.cl", "Password123", "Juan Pérez", "Ejecutivo Comercial"),
        CuentaPrueba("ejecutivo2@empresa.cl", "Password123", "María Soto", "Ejecutivo Comercial"),
        CuentaPrueba("ejecutivo3@empresa.cl", "Password123", "Carlos Gómez", "Ejecutivo Comercial")
    )

    fun onCorreoChange(correo: String) {
        uiState = uiState.copy(correo = correo, errorCorreo = false, mensajeError = null)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password, errorPassword = false, mensajeError = null)
    }

    fun iniciarSesion(onSuccess: (String, String) -> Unit) {
        val correoInput = uiState.correo.trim()
        val passwordInput = uiState.password.trim()

        val cuenta = cuentasPrueba.find { it.correo.equals(correoInput, ignoreCase = true) }

        if (cuenta != null) {
            if (cuenta.password == passwordInput) {
                uiState = uiState.copy(mensajeError = null, mostrarOpcionCambiarPassword = false)
                onSuccess(cuenta.nombre, cuenta.rol)
            } else {
                uiState = uiState.copy(
                    errorPassword = true,
                    mensajeError = "Contraseña incorrecta para ${cuenta.correo}",
                    mostrarOpcionCambiarPassword = true
                )
            }
            return
        }

        val errCorreo = correoInput.isBlank() || !correoInput.contains("@")
        val errPassword = passwordInput.length < 8

        if (errCorreo || errPassword) {
            uiState = uiState.copy(
                errorCorreo = errCorreo,
                errorPassword = errPassword,
                mensajeError = if (errPassword) "Contraseña incorrecta (mínimo 8 caracteres) o cuenta no registrada" else "Por favor, ingrese un correo válido",
                mostrarOpcionCambiarPassword = errPassword
            )
            return
        }

        uiState = uiState.copy(
            mensajeError = null,
            mostrarOpcionCambiarPassword = false
        )
        onSuccess(uiState.correo, "Ejecutivo Comercial")
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
