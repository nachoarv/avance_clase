package cl.duoc.primer_mvvm

import cl.duoc.primer_mvvm.data.UsuarioRepository
import cl.duoc.primer_mvvm.model.Usuario
import org.junit.Assert.*
import org.junit.Test

class UsuarioRepositoryTest {

    @Test
    fun testBuscarPorCorreo() {
        val admin = UsuarioRepository.buscarPorCorreo("admin@empresa.cl")
        assertNotNull(admin)
        assertEquals("Admin", admin?.rol)

        val noExiste = UsuarioRepository.buscarPorCorreo("noexiste@empresa.cl")
        assertNull(noExiste)
    }

    @Test
    fun testEsAdminOSupervisor() {
        assertTrue(UsuarioRepository.esAdminOSupervisor("Admin", "admin@empresa.cl"))
        assertTrue(UsuarioRepository.esAdminOSupervisor("Supervisor Comercial", "supervisor@empresa.cl"))
        assertFalse(UsuarioRepository.esAdminOSupervisor("Ejecutivo Comercial", "ejecutivo1@empresa.cl"))
    }

    @Test
    fun testValidarPalabraClave() {
        assertTrue(UsuarioRepository.validarPalabraClave("EMPRESA2026"))
        assertTrue(UsuarioRepository.validarPalabraClave("ADMIN2026"))
        assertFalse(UsuarioRepository.validarPalabraClave("CLAVE_EQUIVOCADA"))
    }

    @Test
    fun testAgregarYCambiarPassword() {
        val nuevoCorreo = "test_nuevo_${System.currentTimeMillis()}@empresa.cl"
        val nuevoUsuario = Usuario(
            id = "test_id",
            correo = nuevoCorreo,
            password = "Password123",
            nombre = "Test",
            apellido = "User",
            rol = "Ejecutivo Comercial"
        )
        val agregado = UsuarioRepository.agregarUsuario(nuevoUsuario)
        assertTrue(agregado)

        val encontrado = UsuarioRepository.buscarPorCorreo(nuevoCorreo)
        assertNotNull(encontrado)

        val cambio = UsuarioRepository.cambiarPasswordUsuario(nuevoCorreo, "NewPassword456")
        assertTrue(cambio)

        val actualizado = UsuarioRepository.buscarPorCorreo(nuevoCorreo)
        assertEquals("NewPassword456", actualizado?.password)

        // Limpieza
        UsuarioRepository.eliminarUsuario(nuevoCorreo)
    }
}
