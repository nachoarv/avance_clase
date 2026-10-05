package cl.duoc.primer_mvvm.`01_formulario`

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegistroViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun cambiarNombre(nuevoNombre: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(nombre = nuevoNombre)
        }
    }

    fun cambiarApellido(nuevoApellido: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(apellido = nuevoApellido)
        }
    }

    fun cambiarFechaNacimiento(nuevaFecha: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(fechaNacimiento = nuevaFecha)
        }
    }

    fun cambiarCorreo(nuevoCorreo: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(correo = nuevoCorreo)
        }
    }

    fun cambiarPassword(nuevoPassword: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(password = nuevoPassword)
        }
    }

    fun cambiarRepetirPassword(nuevoRepetirPassword: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(repetirPassword = nuevoRepetirPassword)
        }
    }

    fun cambiarTerminos(nuevoTerminos: Boolean) {
        _uiState.update { estadoActual ->
            estadoActual.copy(aceptaTerminos = nuevoTerminos)
        }
    }

    // Funciones alias para mantener compatibilidad
    fun onNombreChange(nombre: String) = cambiarNombre(nombre)
    fun onApellidoChange(apellido: String) = cambiarApellido(apellido)
    fun onFechaNacimientoChange(fecha: String) = cambiarFechaNacimiento(fecha)
    fun onCorreoChange(correo: String) = cambiarCorreo(correo)
    fun onPasswordChange(password: String) = cambiarPassword(password)
    fun onRepetirPasswordChange(repetirPassword: String) = cambiarRepetirPassword(repetirPassword)
    fun cambiarAceptaTerminos(value: Boolean) = cambiarTerminos(value)

    fun registrar() {
        val estado = _uiState.value
        val errNombre = !validarNombre(estado.nombre)
        val errApellido = !validarApellido(estado.apellido)
        val errFecha = !validarFechaNacimiento(estado.fechaNacimiento)
        val errCorreo = !validarCorreo(estado.correo)
        val errPassword = !validarPassword(estado.password)
        val errRepetirPassword = !passwordsCoinciden(estado.password, estado.repetirPassword)
        val errTerminos = !estado.aceptaTerminos

        val formularioValido = !errNombre &&
                !errApellido &&
                !errFecha &&
                !errCorreo &&
                !errPassword &&
                !errRepetirPassword &&
                !errTerminos

        _uiState.update { estadoActual ->
            estadoActual.copy(
                errorNombre = errNombre,
                errorApellido = errApellido,
                errorFechaNacimiento = errFecha,
                errorCorreo = errCorreo,
                errorPassword = errPassword,
                errorRepetirPassword = errRepetirPassword,
                errorTerminos = errTerminos,
                mensajeExito = formularioValido
            )
        }

        if (formularioValido) {
            println("Usuario registrado correctamente")
        } else {
            println("Error en el formulario")
        }
    }
}
