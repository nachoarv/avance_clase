// #hu02 #hu03 #hu04 #login #viewmodel #autenticacion #desactivar_usuario #palabra_clave
package cl.duoc.primer_mvvm.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cl.duoc.primer_mvvm.data.UsuarioRepository

/**
 * #hu02 #hu03 #hu04 #login #viewmodel #autenticacion #palabra_clave
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

    // [HU-02] Inicio de Sesión Exitoso & [HU-03] Validación de Cuenta Desactivada & [HU-27] Bloqueo Temporal por Intentos Fallidos
    fun iniciarSesion(onSuccess: (String, String) -> Unit) {
        val ahora = System.currentTimeMillis()
        if (uiState.bloqueadoHasta > ahora) { // [HU-27]
            val segundosRestantes = (uiState.bloqueadoHasta - ahora) / 1000
            uiState = uiState.copy(
                mensajeError = "Acceso bloqueado temporalmente por intentos fallidos. Intente en ${segundosRestantes.coerceAtLeast(1)} segundos."
            )
            return
        }

        val correoInput = uiState.correo.trim()
        val passwordInput = uiState.password.trim()

        val usuario = UsuarioRepository.buscarPorCorreo(correoInput)

        if (usuario != null) {
            // [HU-03] Validación de Cuenta Desactivada - Bloquea el inicio de sesión si activo = false
            if (!usuario.activo) {
                uiState = uiState.copy(
                    mensajeError = "Tu cuenta se encuentra desactivada. Contacta al administrador",
                    errorCorreo = true
                )
                return
            }

            // [HU-02] Inicio de Sesión Exitoso
            if (usuario.password == passwordInput) {
                uiState = uiState.copy(mensajeError = null, mostrarOpcionCambiarPassword = false, intentosFallidos = 0, bloqueadoHasta = 0L)
                onSuccess(usuario.nombreCompleto(), usuario.rol)
            } else {
                // [HU-27] Conteo de intentos fallidos y bloqueo por 30s
                val nuevosIntentos = uiState.intentosFallidos + 1
                val bloqueo = if (nuevosIntentos >= 3) ahora + 30000 else 0L
                uiState = uiState.copy(
                    errorPassword = true,
                    mensajeError = if (bloqueo > 0) "Demasiados intentos fallidos. Acceso bloqueado por 30 segundos." else "Contraseña incorrecta para ${usuario.correo} (Intento $nuevosIntentos/3)",
                    mostrarOpcionCambiarPassword = true,
                    intentosFallidos = nuevosIntentos,
                    bloqueadoHasta = bloqueo
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

    // [HU-04] Solicitud de Cambio de Contraseña - Verifica longitud, coincidencia y clave maestra para cuentas privilegiadas
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
