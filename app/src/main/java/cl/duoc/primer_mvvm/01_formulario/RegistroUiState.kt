package cl.duoc.primer_mvvm.`01_formulario`

data class RegistroUiState(
    // Campos del formulario
    val nombre: String = "",
    val apellido: String = "",
    val fechaNacimiento: String = "",
    val correo: String = "",
    val password: String = "",
    val repetirPassword: String = "",
    val aceptaTerminos: Boolean = false,

    // Estados de error
    val errorNombre: Boolean = false,
    val errorApellido: Boolean = false,
    val errorFechaNacimiento: Boolean = false,
    val errorCorreo: Boolean = false,
    val errorPassword: Boolean = false,
    val errorRepetirPassword: Boolean = false,
    val errorTerminos: Boolean = false,

    // Estado de resultado
    val mensajeExito: Boolean = false
)
