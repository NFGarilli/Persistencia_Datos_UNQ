package ar.edu.unq.eperdemic.service

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.*
import ar.edu.unq.eperdemic.utils.DataServiceImpl


import org.junit.Assert
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EspecieServiceTest : BaseTestServices() {

    @Test
    fun recuperarEspecieExistenteDevuelveEspecie() {

        val patogeno = Patogeno("Virus")
        val patogenoCreado = patogenoService.crearPatogeno(patogeno)
        val ubicacion1 = DataServiceImpl.ubicacionJPA
        val especieCreada = patogenoService.agregarEspecie(patogenoCreado.id!!, "Covid-19", ubicacion1.id!!, 0)
        val especieRecuperada = especieService.recuperarEspecie(especieCreada.id!!)

        Assert.assertEquals(especieCreada.id, especieRecuperada.id)
        assert(especieCreada !== especieRecuperada)
    }

    @Test
    fun recuperarTodasLasEspecies() {
        var especiesRecuperdas = especieService.recuperarTodos()
        val especieDelDataService = DataServiceImpl.especie
        var especie1 = especieService.recuperarEspecie(especieDelDataService.id!!)

        Assert.assertEquals(4, especiesRecuperdas.size)
        Assert.assertTrue(especiesRecuperdas.contains(especie1))
    }

    @Test
    fun laCantidadDeInfectadosDeUnaEspecieQueTodaviaNoInfectoANingunVectorEsCero() {
        val especieDelDataService = DataServiceImpl.especie
        val especie1 = especieService.recuperarEspecie(especieDelDataService.id!!)
        var cantidadInfectados = especieService.cantidadDeInfectados(especie1.id!!)

        Assert.assertEquals(0, cantidadInfectados)
    }

    @Test
    fun laCantidadDeInfectadosDeUnaEspecieQueYaInfectoAUnVectorEsUno() {
        val ubicacion2 = ubicacionService.crearUbicacion("Quilmes", 0.0, 0.0)
        val especieDelDataService = DataServiceImpl.especie
        val especie = especieService.recuperarEspecie(especieDelDataService.id!!)

        val vector = vectorService.crearVector(Vector.TipoDeVector.Humano, ubicacion2.id!!)
        vectorService.infectar(vector.id!!, especie.id!!)
        var especieInfectada = especieService.recuperarEspecie(especie.id!!)
        var cantidadInfectadosActualizada = especieService.cantidadDeInfectados(especieInfectada.id!!)

        Assert.assertEquals(1, cantidadInfectadosActualizada)
    }

    @Test
    fun agregarEspecieAUnPatogenoExistente() {
        val patogeno = Patogeno("Bacteria")
        val patogenoCreado = patogenoService.crearPatogeno(patogeno)
        val ubicacion1 = DataServiceImpl.ubicacionJPA

        val nuevaEspecie = patogenoService.agregarEspecie(patogenoCreado.id!!, "E. coli", ubicacion1.id!!, 0)
        val especieRecuperada = especieService.recuperarEspecie(nuevaEspecie.id!!)

        Assert.assertEquals(nuevaEspecie.id, especieRecuperada.id)
        Assert.assertEquals("E. coli", especieRecuperada.nombre)
        Assert.assertEquals(patogenoCreado.id, especieRecuperada.patogeno!!.id)
    }

    @Test
    fun incrementarCantidadDeInfectadosDeUnaEspecie() {
        val ubicacion2 = ubicacionService.crearUbicacion("Buenos Aires", 0.0, 0.0)
        val especieDelDataService = DataServiceImpl.especie
        val especie = especieService.recuperarEspecie(especieDelDataService.id!!)

        val vector1 = vectorService.crearVector(Vector.TipoDeVector.Humano, ubicacion2.id!!)
        val vector2 = vectorService.crearVector(Vector.TipoDeVector.Humano, ubicacion2.id!!)

        vectorService.infectar(vector1.id!!, especie.id!!)
        vectorService.infectar(vector2.id!!, especie.id!!)

        val especieInfectada = especieService.recuperarEspecie(especie.id!!)
        val cantidadInfectadosActualizada = especieService.cantidadDeInfectados(especieInfectada.id!!)

        Assert.assertEquals(2, cantidadInfectadosActualizada)
    }

}

