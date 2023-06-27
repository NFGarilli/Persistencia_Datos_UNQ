package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.exceptions.ConsVacioException
import ar.edu.unq.eperdemic.modelo.tipoDeVector.Humano
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UbicacionJPAModeloTest {
    @Test
    fun crearPUbicacionSinNombre() {
        assertThrows<ConsVacioException> {
            val ubicacionJPA = UbicacionJPA("")
        }
    }

    @Test
    fun ubicacionesSonIgualesSiTienenElMismoNombre() {
        val ubicacion1 = UbicacionJPA("Ubicacion1")
        val ubicacion2 = UbicacionJPA("Ubicacion1")

        assertEquals(ubicacion1.toString(), ubicacion2.toString())
    }

    @Test
    fun ubicacionesSonDiferentesSiTienenNombresDiferentes() {
        val ubicacion1 = UbicacionJPA("Ubicacion1")
        val ubicacion2 = UbicacionJPA("Ubicacion2")

        assertNotEquals(ubicacion1, ubicacion2)
    }

    @Test
    fun testCreacionVectores_CrearYAsignarVectores_ListaVectoresContieneVectoresAgregados() {
        val ubicacion = UbicacionJPA("Ubicacion1")
        val vector1 = Humano(Vector.TipoDeVector.Humano, ubicacion)
        val vector2 = Humano(Vector.TipoDeVector.Humano, ubicacion)

        ubicacion.vectores.add(vector1)
        ubicacion.vectores.add(vector2)

        assertTrue(ubicacion.vectores.contains(vector1))
        assertTrue(ubicacion.vectores.contains(vector2))
    }
}