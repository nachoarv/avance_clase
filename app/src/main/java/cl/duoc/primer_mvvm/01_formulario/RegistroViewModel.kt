package cl.duoc.primer_mvvm.`01_formulario`

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RegistroViewModel : ViewModel() {

    var uiState by mutableStateOf(RegistroUiState())
        private set

    fun onNombreChange(nombre: String) {
        uiState = uiState.copy(nombre = nombre)
    }

    fun onApellidoChange(apellido: String) {
        uiState = uiState.copy(apellido = apellido)
    }

    fun onFechaNacimientoChange(fecha: String) {
        uiState = uiState.copy(fechaNacimiento = fecha)
    }

    fun onCorreoChange(correo: String) {
        uiState = uiState.copy(correo = correo)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password)
    }

    fun onRepetirPasswordChange(repetirPassword: String) {
        uiState = uiState.copy(repetirPassword = repetirPassword)
    }

    fun cambiarAceptaTerminos(value: Boolean) {
        uiState = uiState.copy(aceptaTerminos = value)
    }

    fun registrar() {
        val errNombre = !validarNombre(uiState.nombre)
        val errApellido = !validarApellido(uiState.apellido)
        val errFecha = !validarFechaNacimiento(uiState.fechaNacimiento)
        val errCorreo = !validarCorreo(uiState.correo)
        val errPassword = !validarPassword(uiState.password)
        val errRepetirPassword = !passwordsCoinciden(uiState.password, uiState.repetirPassword)
        val errTerminos = !uiState.aceptaTerminos

        val formularioValido = !errNombre &&
                !errApellido &&
                !errFecha &&
                !errCorreo &&
                !errPassword &&
                !errRepetirPassword &&
                !errTerminos

        uiState = uiState.copy(
            errorNombre = errNombre,
            errorApellido = errApellido,
            errorFechaNacimiento = errFecha,
            errorCorreo = errCorreo,
            errorPassword = errPassword,
            errorRepetirPassword = errRepetirPassword,
            errorTerminos = errTerminos,
            mensajeExito = formularioValido,
        )

        if (formularioValido) {
            println("Usuario registrado correctamente")
        } else {
            println("Error en el formulario")
        }
    }
}
