package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.mutaciones.Mutacion
import ar.edu.unq.eperdemic.modelo.mutaciones.MutacionBioalteracionGenetica
import ar.edu.unq.eperdemic.modelo.tipoDeVector.Animal
import ar.edu.unq.eperdemic.modelo.tipoDeVector.Humano
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BioalteracionGeneticaModeloTest {

    private lateinit var patogeno: Patogeno
    private lateinit var virus: Especie
    private lateinit var ubicacionJPA: UbicacionJPA
    private lateinit var humano: Humano
    private lateinit var animal: Animal
    private lateinit var bioalteracionGenetica: Mutacion

    @BeforeEach
    fun setup() {
        patogeno = Patogeno("Virus")
        virus = Especie(patogeno, "COVID-19", "China", 100)
        ubicacionJPA = UbicacionJPA("Bera")
        humano = Humano(Vector.TipoDeVector.Humano, ubicacionJPA)
        animal = Animal(Vector.TipoDeVector.Animal, ubicacionJPA)
        bioalteracionGenetica = MutacionBioalteracionGenetica(Vector.TipoDeVector.Animal, virus)
    }

    @Test
    fun esBioalteracion() {
        assertTrue(bioalteracionGenetica.esBioalteracion())
    }

    @Test
    fun tieneTipoDeVectorAnimal() {
        assertTrue(bioalteracionGenetica.tieneTipoDeVector(Vector.TipoDeVector.Animal))
    }

    @Test
    fun noTieneTipoDeVectorInsectoNiHumano() {
        assertFalse(bioalteracionGenetica.tieneTipoDeVector(Vector.TipoDeVector.Insecto))
        assertFalse(bioalteracionGenetica.tieneTipoDeVector(Vector.TipoDeVector.Humano))
    }

    @Test
    fun noTieneDefensaPara() {
        assertFalse(bioalteracionGenetica.tieneDefensaPara(virus))
    }

    @Test
    fun permiteContagioSinRestricciones() {
        assertTrue(bioalteracionGenetica.permiteContagioSinRestricciones(animal))
    }

    @Test
    fun eliminaReestriccionesBasicasParaEnfermedadYVector() {
        assertTrue(bioalteracionGenetica.eliminaReestriccionesBasicasParaEnfermedadYVector(virus, animal))
    }
}