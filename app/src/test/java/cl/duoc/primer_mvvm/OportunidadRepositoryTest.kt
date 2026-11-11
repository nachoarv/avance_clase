package cl.duoc.primer_mvvm

import cl.duoc.primer_mvvm.data.OportunidadRepository
import org.junit.Assert.*
import org.junit.Test

class OportunidadRepositoryTest {

    @Test
    fun testCargaDatosIniciales() {
        val organizaciones = OportunidadRepository.organizaciones.value
        assertFalse(organizaciones.isEmpty())

        val contactos = OportunidadRepository.contactos.value
        assertFalse(contactos.isEmpty())

        val oportunidades = OportunidadRepository.oportunidades.value
        assertFalse(organizaciones.isEmpty())
    }
}
