package cl.duoc.primer_mvvm

import cl.duoc.primer_mvvm.ui.auth.RegistroViewModel
import org.junit.Assert.*
import org.junit.Test

class RegistroViewModelTest {

    @Test
    fun testRegistroValidacionInvalida() {
        val viewModel = RegistroViewModel()
        viewModel.cambiarNombre("")
        viewModel.cambiarApellido("")
        viewModel.cambiarCorreo("correo-invalido")
        viewModel.cambiarPassword("123")
        viewModel.cambiarRepetirPassword("456")
        viewModel.cambiarTerminos(false)

        viewModel.registrar()

        val state = viewModel.uiState.value
        assertTrue(state.errorNombre)
        assertTrue(state.errorApellido)
        assertTrue(state.errorCorreo)
        assertTrue(state.errorPassword)
        assertTrue(state.errorRepetirPassword)
        assertTrue(state.errorTerminos)
        assertFalse(state.mensajeExito)
    }

    @Test
    fun testRegistroValidacionValida() {
        val viewModel = RegistroViewModel()
        val correoUnico = "test_reg_${System.currentTimeMillis()}@empresa.cl"
        viewModel.cambiarNombre("Roberto")
        viewModel.cambiarApellido("Gonzalez")
        viewModel.cambiarFechaNacimiento("15/05/1995")
        viewModel.cambiarCorreo(correoUnico)
        viewModel.cambiarPassword("Password123")
        viewModel.cambiarRepetirPassword("Password123")
        viewModel.cambiarTerminos(true)

        viewModel.registrar()

        val state = viewModel.uiState.value
        assertFalse(state.errorNombre)
        assertFalse(state.errorApellido)
        assertFalse(state.errorCorreo)
        assertFalse(state.errorPassword)
        assertFalse(state.errorRepetirPassword)
        assertFalse(state.errorTerminos)
        assertTrue(state.mensajeExito)
    }
}
