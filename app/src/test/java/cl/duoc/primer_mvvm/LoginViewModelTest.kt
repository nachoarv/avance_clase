package cl.duoc.primer_mvvm

import cl.duoc.primer_mvvm.ui.auth.LoginViewModel
import org.junit.Assert.*
import org.junit.Test

class LoginViewModelTest {

    @Test
    fun testLoginSuccess() {
        val viewModel = LoginViewModel()
        viewModel.onCorreoChange("admin@empresa.cl")
        viewModel.onPasswordChange("Admin123Password")

        var successCalled = false
        var loggedUser = ""

        viewModel.iniciarSesion { nombre, rol ->
            successCalled = true
            loggedUser = rol
        }

        assertTrue(successCalled)
        assertEquals("Admin", loggedUser)
        assertNull(viewModel.uiState.mensajeError)
    }

    @Test
    fun testLoginIncorrectPassword() {
        val viewModel = LoginViewModel()
        viewModel.onCorreoChange("admin@empresa.cl")
        viewModel.onPasswordChange("WrongPassword")

        var successCalled = false
        viewModel.iniciarSesion { _, _ ->
            successCalled = true
        }

        assertFalse(successCalled)
        assertTrue(viewModel.uiState.errorPassword)
        assertNotNull(viewModel.uiState.mensajeError)
        assertTrue(viewModel.uiState.mostrarOpcionCambiarPassword)
    }

    @Test
    fun testLoginCuentaInactiva() {
        val viewModel = LoginViewModel()
        // ejecutivo3@empresa.cl is inactive in initial data
        viewModel.onCorreoChange("ejecutivo3@empresa.cl")
        viewModel.onPasswordChange("Password123")

        var successCalled = false
        viewModel.iniciarSesion { _, _ ->
            successCalled = true
        }

        assertFalse(successCalled)
        assertTrue(viewModel.uiState.errorCorreo)
        assertNotNull(viewModel.uiState.mensajeError)
        assertTrue(viewModel.uiState.mensajeError!!.contains("desactivada"))
    }
}
