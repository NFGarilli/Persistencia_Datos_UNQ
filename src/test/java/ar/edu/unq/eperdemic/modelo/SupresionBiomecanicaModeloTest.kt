package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.mutaciones.Mutacion
import ar.edu.unq.eperdemic.modelo.mutaciones.MutacionSupresionBiomecanica
import ar.edu.unq.eperdemic.modelo.tipoDeVector.Animal
import ar.edu.unq.eperdemic.modelo.tipoDeVector.Humano
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SupresionBiomecanicaModeloTest {

    private lateinit var patogeno: Patogeno
    private lateinit var virus: Especie
    private lateinit var virusDebil: Especie
    private lateinit var virusFuerte: Especie
    private lateinit var ubicacionJPA: UbicacionJPA
    private lateinit var humano: Humano
    private lateinit var animal: Animal
    private lateinit var supresionBiomecanica: Mutacion

    @BeforeEach
    fun setup() {
        patogeno = Patogeno("Virus")
        virus = Especie(patogeno, "COVID-19", "China", 100)
        virusDebil = Especie(patogeno, "Debil", "Chile", 100)
        virusDebil.defensa = 1
        virusFuerte = Especie(patogeno, "Fuerte", "Chile", 100)
        virusFuerte.defensa = 100
        ubicacionJPA = UbicacionJPA("Bera")
        humano = Humano(Vector.TipoDeVector.Humano, ubicacionJPA)
        animal = Animal(Vector.TipoDeVector.Animal, ubicacionJPA)
        supresionBiomecanica = MutacionSupresionBiomecanica(50, virus)
        animal.infectar(virusDebil)
    }

    @Test
    fun resolverMutacion() {
        supresionBiomecanica.resolverMutacion(animal)
        assertFalse(animal.estaInfectado())
    }

    @Test
    fun noEsBioalteracion() {
        assertFalse(supresionBiomecanica.esBioalteracion())
    }

    @Test
    fun noTieneTipoDeVector() {
        assertFalse(supresionBiomecanica.tieneTipoDeVector(Vector.TipoDeVector.Animal))
    }

    @Test
    fun tieneDefensaPara() {
        assertTrue(supresionBiomecanica.tieneDefensaPara(virusDebil))
    }

    @Test
    fun noTieneDefensaParaVirusFuerte() {
        assertFalse(supresionBiomecanica.tieneDefensaPara(virusFuerte))
    }

    @Test
    fun permiteContagioSinRestricciones() {
        assertFalse(supresionBiomecanica.permiteContagioSinRestricciones(animal))
    }

    @Test
    fun eliminaReestriccionesBasicasParaEnfermedadYVector() {
        assertFalse(supresionBiomecanica.eliminaReestriccionesBasicasParaEnfermedadYVector(virus, animal))
    }
}