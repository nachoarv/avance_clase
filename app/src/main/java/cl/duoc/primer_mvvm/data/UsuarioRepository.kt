package cl.duoc.primer_mvvm.data

import cl.duoc.primer_mvvm.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object UsuarioRepository {

    private val usuariosIniciales = listOf(
        Usuario(
            id = "1",
            correo = "supervisor@empresa.cl",
            password = "Password123",
            nombre = "Sofía",
            apellido = "Supervisor",
            rol = "Supervisor Comercial",
            activo = true
        ),
        Usuario(
            id = "2",
            correo = "admin@empresa.cl",
            password = "Admin123Password",
            nombre = "Administrador",
            apellido = "General",
            rol = "Admin",
            activo = true
        ),
        Usuario(
            id = "3",
            correo = "ejecutivo1@empresa.cl",
            password = "Password123",
            nombre = "Juan",
            apellido = "Pérez",
            rol = "Ejecutivo Comercial",
            activo = true
        ),
        Usuario(
            id = "4",
            correo = "ejecutivo2@empresa.cl",
            password = "Password123",
            nombre = "María",
            apellido = "Soto",
            rol = "Ejecutivo Comercial",
            activo = true
        ),
        Usuario(
            id = "5",
            correo = "ejecutivo3@empresa.cl",
            password = "Password123",
            nombre = "Carlos",
            apellido = "Gómez",
            rol = "Ejecutivo Comercial",
            activo = true
        )
    )

    private val _usuarios = MutableStateFlow<List<Usuario>>(usuariosIniciales)
    val usuarios: StateFlow<List<Usuario>> = _usuarios.asStateFlow()

    fun buscarPorCorreo(correo: String): Usuario? {
        return _usuarios.value.find { it.correo.equals(correo.trim(), ignoreCase = true) }
    }

    fun esAdminOSupervisor(rol: String, correo: String = ""): Boolean {
        val rolLower = rol.lowercase()
        val correoLower = correo.lowercase()
        return rolLower.contains("admin") ||
                rolLower.contains("supervisor") ||
                correoLower == "supervisor@empresa.cl" ||
                correoLower == "admin@empresa.cl"
    }

    fun cambiarPasswordUsuario(correo: String, nuevaPassword: String): Boolean {
        val index = _usuarios.value.indexOfFirst { it.correo.equals(correo.trim(), ignoreCase = true) }
        if (index != -1) {
            _usuarios.update { lista ->
                lista.toMutableList().apply {
                    this[index] = this[index].copy(password = nuevaPassword)
                }
            }
            return true
        }
        return false
    }

    fun cambiarRolUsuario(correo: String, nuevoRol: String): Boolean {
        val index = _usuarios.value.indexOfFirst { it.correo.equals(correo.trim(), ignoreCase = true) }
        if (index != -1) {
            _usuarios.update { lista ->
                lista.toMutableList().apply {
                    this[index] = this[index].copy(rol = nuevoRol)
                }
            }
            return true
        }
        return false
    }

    fun agregarUsuario(usuario: Usuario): Boolean {
        if (buscarPorCorreo(usuario.correo) != null) return false
        _usuarios.update { lista ->
            lista + usuario
        }
        return true
    }
}
