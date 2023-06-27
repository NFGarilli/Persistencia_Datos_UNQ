package ar.edu.unq.eperdemic.service

import ar.edu.unq.eperdemic.modelo.mutaciones.DadoMutaciones
import ar.edu.unq.eperdemic.utils.DataServiceImpl.Companion.animal
import ar.edu.unq.eperdemic.utils.DataServiceImpl.Companion.bioalteracionGenetica
import ar.edu.unq.eperdemic.utils.DataServiceImpl.Companion.especie
import ar.edu.unq.eperdemic.utils.DataServiceImpl.Companion.especie4
import ar.edu.unq.eperdemic.utils.DataServiceImpl.Companion.persona
import ar.edu.unq.eperdemic.utils.DataServiceImpl.Companion.persona2
import ar.edu.unq.eperdemic.utils.DataServiceImpl.Companion.supresionBiomecanica
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MutacionServiceTest : BaseTestServices() {

    @Test
    fun agregarMutacion() {
        val especieRecuperada = especieService.recuperarEspecie(especie.id!!)
        assertFalse(especieRecuperada.hayMutacionesDisponibles())
        mutacionService.agregarMutacion(especieRecuperada.id!!, supresionBiomecanica)

        val especieActualizada = especieService.recuperarEspecie(especie.id!!)
        val mutacionesEspecie = especieActualizada.mutaciones

        assertEquals(1, mutacionesEspecie.size)

        mutacionService.agregarMutacion(especieActualizada.id!!, bioalteracionGenetica)

        val especieConMutacionesActualizadas = especieService.recuperarEspecie(especie.id!!)
        val mutacionesEspecieRecuperada = especieConMutacionesActualizadas.mutaciones
        assertEquals(2, mutacionesEspecieRecuperada.size)
    }


    @Test
    fun unHumanoContagiarAOtroHumanoMutaLaEspecieYContagiaAUnAnimal() {

        val especie = especieService.recuperarEspecie(especie4.id!!)
        val vectorInfectado = vectorService.recuperarVector(persona.id!!)
        val vectorAInfectar = vectorService.recuperarVector(persona2.id!!)
        mutacionService.agregarMutacion(especie.id!!, bioalteracionGenetica)
        val especieActualizada = especieService.recuperarEspecie(especie.id!!)
        vectorService.infectar(vectorInfectado.id!!, especieActualizada.id!!)
        DadoMutaciones.setEstrategia(DadoMutaciones.EstrategiaDepuracion)
        DadoMutaciones.EstrategiaDepuracion.setMutacionFija(bioalteracionGenetica)
        val listaDeVectores = listOf(vectorAInfectar)

        vectorService.contagiar(vectorInfectado.id!!, listaDeVectores)

        var enfermedades = vectorService.enfermedades(vectorAInfectar.id!!)

        assertTrue(enfermedades.isNotEmpty())
        assertTrue(enfermedades.contains(especieActualizada))

        val animalAInfectar = vectorService.recuperarVector(animal.id!!)
        val animalDeVectores = listOf(animalAInfectar)

        vectorService.contagiar(vectorInfectado.id!!, animalDeVectores)

        var enfermedadesAnimal = vectorService.enfermedades(animalAInfectar.id!!)
        assertTrue(enfermedadesAnimal.isNotEmpty())
        assertTrue(enfermedadesAnimal.contains(especieActualizada))
    }
}