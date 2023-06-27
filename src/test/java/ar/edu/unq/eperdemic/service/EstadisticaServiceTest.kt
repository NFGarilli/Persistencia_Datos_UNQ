package ar.edu.unq.eperdemic.service

import ar.edu.unq.eperdemic.utils.DataServiceImpl
import org.junit.Assert.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(PER_CLASS)
class EstadisticaServiceTest : BaseTestServices() {
    @Test
    fun especieLider() {
        var vector1 = DataServiceImpl.persona
        var vector2 = DataServiceImpl.animal
        val persona = vectorService.recuperarVector(vector1.id!!)
        val animal = vectorService.recuperarVector(vector2.id!!)

        var especie1 = DataServiceImpl.especie
        val especie = especieService.recuperarEspecie(especie1.id!!)

        vectorService.infectar(persona.id!!, especie.id!!)
        vectorService.infectar(animal.id!!, especie.id!!)

        var especieLider = estadisticaService.especieLider()

        assertEquals(especie.id!!, especieLider.id!!)

    }

    @Test
    fun reporteDeContagios() {
        var vector1 = DataServiceImpl.persona
        var vector2 = DataServiceImpl.animal
        var vector3 = DataServiceImpl.persona2
        val persona = vectorService.recuperarVector(vector1.id!!)
        val animal = vectorService.recuperarVector(vector2.id!!)

        val persona2 = vectorService.recuperarVector(vector3.id!!)

        var especie1 = DataServiceImpl.especie
        val especie = especieService.recuperarEspecie(especie1.id!!)

        vectorService.infectar(persona2.id!!, especie.id!!)
        vectorService.infectar(animal.id!!, especie.id!!)
        vectorService.infectar(persona.id!!, especie.id!!)

        var reporte = estadisticaService.reporteDeContagios("Uruguay")
        assertEquals(2, reporte.vectoresInfecatods)
        assertEquals(2, reporte.vectoresPresentes)
        assertEquals("Gripe", reporte.nombreDeEspecieMasInfecciosa)
    }

    @Test
    fun lideres() {
        dataService.ambienteLideres()
        var vector1 = DataServiceImpl.persona.id!!
        var vector2 = DataServiceImpl.animal.id!!
        var especies = especieService.recuperarTodos()

        for (especie in especies) {
            vectorService.infectar(vector1, especie.id!!)
            vectorService.infectar(vector2, especie.id!!)
        }

        val especiesLider = estadisticaService.lideres()

        val listaEsperada = especies.take(10)

        assertEquals(10, especiesLider.size)
        assertTrue(especiesLider.containsAll(listaEsperada))
    }


    @Test
    fun lideresSinEspeciesLideres() {
        dataService.ambienteLideres() // Limpiar el ambiente de líderes

        val especiesLider = estadisticaService.lideres()

        assertTrue(especiesLider.isEmpty())
    }


}


