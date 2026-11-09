// #login #uistate #palabra_clave
package cl.duoc.primer_mvvm.ui.auth

/**
 * #login #uistate #palabra_clave
 * Estado UI para la pantalla de inicio de sesión y recuperación de clave de seguridad.
 */
data class LoginUiState(
    val correo: String = "",
    val password: String = "",
    val errorCorreo: Boolean = false,
    val errorPassword: Boolean = false,
    val mensajeError: String? = null,
    val mostrarOpcionCambiarPassword: Boolean = false,
    val mostrarDialogoCambiarPassword: Boolean = false,
    val nuevaPassword: String = "",
    val repetirNuevaPassword: String = "",
    val palabraClaveInput: String = "",
    val errorNuevaPassword: String? = null,
    val mensajeExitoCambio: String? = null
)
