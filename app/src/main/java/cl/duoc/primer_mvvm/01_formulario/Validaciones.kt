package cl.duoc.primer_mvvm.`01_formulario`

import java.util.Calendar

fun validarNombre(nombre: String): Boolean {
    return nombre.isNotBlank()
}

fun validarApellido(apellido: String): Boolean {
    return apellido.isNotBlank()
}

fun validarCorreo(correo: String): Boolean {
    return correo.isNotBlank() &&
            correo.contains("@") &&
            correo.endsWith(".cl")
}

fun validarPassword(password: String): Boolean {
    return password.length >= 8
}

fun passwordsCoinciden(password: String, repetirPassword: String): Boolean {
    return password == repetirPassword
}

fun validarFechaNacimiento(fecha: String): Boolean {
    if (fecha.isBlank()) return false
    val partes = fecha.split("/")
    if (partes.size != 3) return false
    val dia = partes[0].toIntOrNull() ?: return false
    val mes = partes[1].toIntOrNull() ?: return false
    val anio = partes[2].toIntOrNull() ?: return false
    if ((dia !in 1..31) || (mes !in 1..12) || (anio < 1900)) return false

    val hoy = Calendar.getInstance()
    val hoyAnio = hoy[Calendar.YEAR]
    val hoyMes = hoy[Calendar.MONTH] + 1
    val hoyDia = hoy[Calendar.DAY_OF_MONTH]

    var edad = hoyAnio - anio
    if (hoyMes < mes || (hoyMes == mes && hoyDia < dia)) {
        edad--
    }
    return edad >= 18
}
