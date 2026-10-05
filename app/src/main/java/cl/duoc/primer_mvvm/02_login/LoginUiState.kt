package cl.duoc.primer_mvvm.`02_login`

data class LoginUiState(
    val correo: String = "",
    val password: String = "",
    val errorCorreo: Boolean = false,
    val errorPassword: Boolean = false,
    val mensajeError: String? = null,

    // Opciones para cambiar contraseña cuando se falla el ingreso
    val mostrarOpcionCambiarPassword: Boolean = false,
    val mostrarDialogoCambiarPassword: Boolean = false,
    val nuevaPassword: String = "",
    val repetirNuevaPassword: String = "",
    val errorNuevaPassword: String? = null,
    val mensajeExitoCambio: String? = null
)
