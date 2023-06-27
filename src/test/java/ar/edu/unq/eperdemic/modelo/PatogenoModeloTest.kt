package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.exceptions.ConsVacioException
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PatogenoModeloTest {

    @Test
    fun crearPatogenoConTipoNulo() {
        assertThrows<ConsVacioException> {
            val patogeno = Patogeno("")
        }
    }

    @Test
    fun agregarEspecie() {
        var patogenoVirus = Patogeno("Virus")
        var especie = Especie(patogenoVirus, "Gripe", "Argentina", 50)
        patogenoVirus.agregarEspecie(especie);
        assertEquals(1, patogenoVirus.especies.size)
    }

    @Test
    fun crearEspecie() {
        var patogenoVirus = Patogeno("Virus")
        var especie = patogenoVirus.crearEspecie("Gripe", "Argentina", 50)
        var especie2 = patogenoVirus.crearEspecie("Gripe2", "Argentina", 60)
        assertEquals(2, patogenoVirus.especies.size)
        assertEquals(2, patogenoVirus.cantidadDeEspecies)
        assertEquals("Gripe", especie.nombre)
    }

    @Test
    fun testAgregarEspecie_EspecieAgregadaCorrectamente() {
        val patogeno = Patogeno("Virus")
        val especie = Especie()

        patogeno.agregarEspecie(especie)

        assertTrue(patogeno.especies.contains(especie))
        assertEquals(1, patogeno.cantidadDeEspecies)
    }

    @Test
    fun testCrearEspecie_EspecieCreadaYAgregadaCorrectamente() {
        val patogeno = Patogeno("Bacteria")
        val nombreEspecie = "E. coli"
        val paisDeOrigen = "Estados Unidos"
        val capacidadDeContagio = 75

        val especie = patogeno.crearEspecie(nombreEspecie, paisDeOrigen, capacidadDeContagio)

        assertTrue(patogeno.especies.contains(especie))
        assertEquals(1, patogeno.cantidadDeEspecies)
        assertEquals(nombreEspecie, especie.nombre)
        assertEquals(paisDeOrigen, especie.paisDeOrigen)
        assertEquals(capacidadDeContagio, especie.capacidadDeContagio)
        assertEquals(patogeno, especie.patogeno)
    }


}
