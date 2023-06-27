package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.exceptions.ConsVacioException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class EspecieModeloTest {
    @Test
    fun testConstructor() {
        val patogeno = Patogeno("Test")
        val nombre = "COVID-19"
        val paisDeOrigen = "China"
        val capacidadDeContagio = 4
        val virus = Especie(patogeno, nombre, paisDeOrigen, capacidadDeContagio)
        assertEquals(patogeno, virus.patogeno)
        assertEquals(nombre, virus.nombre)
        assertEquals(paisDeOrigen, virus.paisDeOrigen)
        assertEquals(capacidadDeContagio, virus.capacidadDeContagio)

        assertThrows<ConsVacioException> {
            Especie(patogeno, "", paisDeOrigen, capacidadDeContagio)
        }
        assertThrows<ConsVacioException> {
            Especie(patogeno, nombre, "", capacidadDeContagio)
        }

    }
}