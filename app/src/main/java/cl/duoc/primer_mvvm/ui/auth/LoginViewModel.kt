// #login #viewmodel #autenticacion #desactivar_usuario #palabra_clave
package cl.duoc.primer_mvvm.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cl.duoc.primer_mvvm.data.UsuarioRepository

/**
 * #login #viewmodel #autenticacion #palabra_clave
 * ViewModel encargado del estado, validación de estado activo, autenticación y palabra clave de seguridad.
 */
class LoginViewModel : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onCorreoChange(correo: String) {
        uiState = uiState.copy(correo = correo, errorCorreo = false, mensajeError = null)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password, errorPassword = false, mensajeError = null)
    }

    // #autenticacion #desactivar_usuario - Validación de credenciales y estado activo de la cuenta
    fun iniciarSesion(onSuccess: (String, String) -> Unit) {
        val correoInput = uiState.correo.trim()
        val passwordInput = uiState.password.trim()

        val usuario = UsuarioRepository.buscarPorCorreo(correoInput)

        if (usuario != null) {
            // #desactivar_usuario - Bloquea el inicio de sesión si la cuenta está desactivada por el Admin
            if (!usuario.activo) {
                uiState = uiState.copy(
                    mensajeError = "Su cuenta se encuentra desactivada por el Administrador. Contacte a soporte.",
                    errorCorreo = true
                )
                return
            }

            if (usuario.password == passwordInput) {
                uiState = uiState.copy(mensajeError = null, mostrarOpcionCambiarPassword = false)
                onSuccess(usuario.nombreCompleto(), usuario.rol)
            } else {
                uiState = uiState.copy(
                    errorPassword = true,
                    mensajeError = "Contraseña incorrecta para ${usuario.correo}",
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
                mensajeError = if (errPassword) "Contraseña incorrecta o cuenta no registrada" else "Por favor, ingrese un correo válido",
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
            palabraClaveInput = "",
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

    // #palabra_clave - Actualiza el valor de la palabra clave de seguridad ingresada
    fun onPalabraClaveChange(palabra: String) {
        uiState = uiState.copy(palabraClaveInput = palabra, errorNuevaPassword = null)
    }

    // #cambio_clave #palabra_clave - Verifica palabra clave de seguridad para cuentas privilegiadas
    fun cambiarPassword() {
        if (uiState.nuevaPassword.length < 8) {
            uiState = uiState.copy(errorNuevaPassword = "La nueva contraseña debe tener al menos 8 caracteres")
            return
        }
        if (uiState.nuevaPassword != uiState.repetirNuevaPassword) {
            uiState = uiState.copy(errorNuevaPassword = "Las contraseñas no coinciden")
            return
        }

        val usuarioTarget = UsuarioRepository.buscarPorCorreo(uiState.correo)
        val esCuentaPrivilegiada = usuarioTarget != null && UsuarioRepository.esAdminOSupervisor(usuarioTarget.rol, usuarioTarget.correo)

        // #palabra_clave #seguridad - Si es una cuenta Admin/Supervisor, requiere validar la Palabra Clave de Seguridad
        if (esCuentaPrivilegiada) {
            val esPalabraCorrecta = UsuarioRepository.validarPalabraClave(uiState.palabraClaveInput)
            if (!esPalabraCorrecta) {
                uiState = uiState.copy(
                    errorNuevaPassword = "Palabra clave de seguridad incorrecta. No está autorizado para cambiar la clave de esta cuenta privilegiada."
                )
                return
            }
        }

        val exito = UsuarioRepository.cambiarPasswordUsuario(uiState.correo, uiState.nuevaPassword)

        if (exito) {
            uiState = uiState.copy(
                password = uiState.nuevaPassword,
                errorPassword = false,
                mensajeError = null,
                mostrarOpcionCambiarPassword = false,
                mostrarDialogoCambiarPassword = false,
                palabraClaveInput = "",
                mensajeExitoCambio = "¡Contraseña actualizada correctamente para ${uiState.correo}!"
            )
        } else {
            uiState = uiState.copy(
                errorNuevaPassword = "No se pudo actualizar la contraseña. Verifique el correo ingresado."
            )
        }
    }
}
