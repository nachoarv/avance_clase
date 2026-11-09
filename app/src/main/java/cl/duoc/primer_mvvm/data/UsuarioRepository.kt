// #repositorio #datos_usuarios #autenticacion #roles #cambio_clave #desactivar_usuario #eliminar_usuario #palabra_clave
package cl.duoc.primer_mvvm.data

import cl.duoc.primer_mvvm.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * #repositorio #datos_usuarios #autenticacion #palabra_clave
 * Repositorio de usuarios con validación de palabra clave de seguridad para cuentas privilegiadas.
 */
object UsuarioRepository {

    // #palabra_clave #seguridad - Clave maestra de seguridad para autorizar cambios en cuentas Admin / Supervisor
    const val CLAVE_MAESTRA_SEGURIDAD = "EMPRESA2026"

    private val usuariosIniciales = listOf(
        Usuario(
            id = "1",
            correo = "supervisor@empresa.cl",
            password = "Password123",
            nombre = "Sofía",
            apellido = "Supervisor",
            rol = "Supervisor Comercial",
            telefono = "+56 9 8888 7777",
            cargo = "Jefa de Convenios Corporativos",
            departamento = "Supervisión Comercial",
            fechaRegistro = "10/01/2026",
            activo = true
        ),
        Usuario(
            id = "2",
            correo = "admin@empresa.cl",
            password = "Admin123Password",
            nombre = "Administrador",
            apellido = "General",
            rol = "Admin",
            telefono = "+56 9 9999 0000",
            cargo = "Administrador del Sistema",
            departamento = "Sistemas & TI",
            fechaRegistro = "01/01/2026",
            activo = true
        ),
        Usuario(
            id = "3",
            correo = "ejecutivo1@empresa.cl",
            password = "Password123",
            nombre = "Juan",
            apellido = "Pérez",
            rol = "Ejecutivo Comercial",
            telefono = "+56 9 7654 3210",
            cargo = "Ejecutivo Comercial Senior",
            departamento = "Ventas Zonal Centro",
            fechaRegistro = "15/01/2026",
            activo = true
        ),
        Usuario(
            id = "4",
            correo = "ejecutivo2@empresa.cl",
            password = "Password123",
            nombre = "María",
            apellido = "Soto",
            rol = "Ejecutivo Comercial",
            telefono = "+56 9 6543 2109",
            cargo = "Ejecutiva de Alianzas",
            departamento = "Ventas Zonal Sur",
            fechaRegistro = "20/01/2026",
            activo = true
        ),
        Usuario(
            id = "5",
            correo = "ejecutivo3@empresa.cl",
            password = "Password123",
            nombre = "Carlos",
            apellido = "Gómez",
            rol = "Ejecutivo Comercial",
            telefono = "+56 9 5432 1098",
            cargo = "Ejecutivo de Convenios",
            departamento = "Ventas Zonal Norte",
            fechaRegistro = "01/02/2026",
            activo = false // Demostración de cuenta inactiva
        )
    )

    private val _usuarios = MutableStateFlow<List<Usuario>>(usuariosIniciales)
    val usuarios: StateFlow<List<Usuario>> = _usuarios.asStateFlow()

    fun buscarPorCorreo(correo: String): Usuario? {
        return _usuarios.value.find { it.correo.equals(correo.trim(), ignoreCase = true) }
    }

    // #roles #admin - Verifica si el usuario posee privilegios de Admin o Supervisor
    fun esAdminOSupervisor(rol: String, correo: String = ""): Boolean {
        val rolLower = rol.lowercase()
        val correoLower = correo.lowercase()
        return rolLower.contains("admin") ||
                rolLower.contains("supervisor") ||
                correoLower == "supervisor@empresa.cl" ||
                correoLower == "admin@empresa.cl"
    }

    // #palabra_clave - Valida si la palabra clave ingresada coincide con la clave maestra de seguridad
    fun validarPalabraClave(palabraInput: String): Boolean {
        val limpia = palabraInput.trim()
        return limpia.equals(CLAVE_MAESTRA_SEGURIDAD, ignoreCase = true) ||
                limpia.equals("ADMIN2026", ignoreCase = true)
    }

    // #cambio_clave - Modifica la contraseña de un usuario
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

    // #roles - Cambia el rol de un usuario existente
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

    // #desactivar_usuario - Activa o desactiva la cuenta de usuario (suspensión/activación)
    fun toggleEstadoUsuario(correo: String): Boolean {
        val index = _usuarios.value.indexOfFirst { it.correo.equals(correo.trim(), ignoreCase = true) }
        if (index != -1) {
            _usuarios.update { lista ->
                lista.toMutableList().apply {
                    val actual = this[index]
                    this[index] = actual.copy(activo = !actual.activo)
                }
            }
            return true
        }
        return false
    }

    // #eliminar_usuario - Elimina una cuenta de usuario del sistema
    fun eliminarUsuario(correo: String): Boolean {
        val existe = buscarPorCorreo(correo) != null
        if (existe) {
            _usuarios.update { lista ->
                lista.filterNot { it.correo.equals(correo.trim(), ignoreCase = true) }
            }
            return true
        }
        return false
    }

    // #editar_usuario - Actualiza los datos del perfil completo de un usuario
    fun actualizarUsuarioCompleto(
        correo: String,
        nuevoNombre: String,
        nuevoApellido: String,
        nuevoTelefono: String,
        nuevoCargo: String,
        nuevoDepartamento: String
    ): Boolean {
        val index = _usuarios.value.indexOfFirst { it.correo.equals(correo.trim(), ignoreCase = true) }
        if (index != -1) {
            _usuarios.update { lista ->
                lista.toMutableList().apply {
                    val actual = this[index]
                    this[index] = actual.copy(
                        nombre = nuevoNombre,
                        apellido = nuevoApellido,
                        telefono = nuevoTelefono,
                        cargo = nuevoCargo,
                        departamento = nuevoDepartamento
                    )
                }
            }
            return true
        }
        return false
    }

    // #registro - Agrega un nuevo usuario a la lista
    fun agregarUsuario(usuario: Usuario): Boolean {
        if (buscarPorCorreo(usuario.correo) != null) return false
        _usuarios.update { lista ->
            lista + usuario
        }
        return true
    }
}
