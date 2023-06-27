package ar.edu.unq.eperdemic.service

import ar.edu.unq.eperdemic.exceptions.VectorNoDisponibleException
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.*
import ar.edu.unq.eperdemic.utils.DataServiceImpl.Companion.animal
import ar.edu.unq.eperdemic.utils.DataServiceImpl.Companion.especie
import ar.edu.unq.eperdemic.utils.DataServiceImpl.Companion.patogenoBacteria
import ar.edu.unq.eperdemic.utils.DataServiceImpl.Companion.patogenoVirus
import ar.edu.unq.eperdemic.utils.DataServiceImpl.Companion.persona
import ar.edu.unq.eperdemic.utils.DataServiceImpl.Companion.ubicacionJPA
import ar.edu.unq.eperdemic.utils.DataServiceImpl.Companion.ubicacionJPA3
import org.junit.Assert.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(PER_CLASS)
class PatogenoServiceTest : BaseTestServices() {
    @Test
    fun crearPatogeno() {
        var patogenoVirus = Patogeno("Virus2")
        var patogenoCreado = patogenoService.crearPatogeno(patogenoVirus)

        assertNotNull(patogenoCreado.id)
        assertEquals(patogenoVirus.tipo, patogenoCreado.tipo)
        assertEquals(patogenoVirus.cantidadDeEspecies, patogenoCreado.cantidadDeEspecies)
    }

    @Test
    fun testAgregarEspecie() {
        var patogenoVirus = Patogeno("Virus2")
        val patogenoCreado = patogenoService.crearPatogeno(patogenoVirus)
        val ubicacion = ubicacionService.recuperarUbicacion(ubicacionJPA.id!!)
        patogenoService.agregarEspecie(patogenoCreado.id!!, "Gripe2", ubicacion.id!!, 0)

        val patogenoRecuperado = patogenoService.recuperarPatogeno(patogenoCreado.id!!)
        val ubicacionRecuperada = ubicacionService.recuperarUbicacion(ubicacion.id!!)
        val vector = ubicacionRecuperada.vectores.toList()[0]

        assertNotNull(patogenoRecuperado.id)
        assertTrue(patogenoRecuperado.especies.size == 1)
        assertEquals("Gripe2", patogenoRecuperado.especies[0].nombre)
        assertEquals(patogenoRecuperado.cantidadDeEspecies, 1)
        assertEquals("Gripe2", vector.enfermedades[0].nombre)
    }

    @Test
    fun testAgregarEspecieEspecieSinVectoresEnUbicacion() {
        val patogenoRecuperado = patogenoService.recuperarPatogeno(patogenoBacteria.id!!)
        val ubicacion = ubicacionService.recuperarUbicacion(ubicacionJPA3.id!!)

        assertThrows<VectorNoDisponibleException> {
            patogenoService.agregarEspecie(patogenoRecuperado.id!!, "Gripe2", ubicacion.id!!, 0)
        }
    }

    @Test
    fun alCrearYLuegoRecuperarSeObtieneObjetosSimilares() {
        var patogenoVirus = Patogeno("Virus2")
        var patogenoCreado = patogenoService.crearPatogeno(patogenoVirus)
        val patogenoRecuperado = patogenoService.recuperarPatogeno(patogenoVirus.id!!)

        assertEquals(patogenoCreado.tipo, patogenoRecuperado.tipo)
        assertEquals(patogenoCreado.cantidadDeEspecies, patogenoRecuperado.cantidadDeEspecies)
        assertEquals(patogenoCreado.id!!, patogenoRecuperado.id)

        assertTrue(patogenoCreado !== patogenoRecuperado)
    }

    @Test
    fun recuperarPatogenoInexistente() {
        val patogenoInexistenteId = 9999L
        assertThrows<NullPointerException> {
            patogenoService.recuperarPatogeno(patogenoInexistenteId)
        }
    }

    @Test
    fun recuperarATodosLosPatogenos() {
        val patogenosRecuperados = patogenoService.recuperarATodosLosPatogenos()
        assertEquals(2, patogenosRecuperados.size)
        assertEquals("Bacteria", patogenosRecuperados[1].tipo)
    }

    @Test
    fun recuperarATodos_Vacio() {
        dataService.eliminarTodo()
        val patogenosRecuperados = patogenoService.recuperarATodosLosPatogenos()
        assertTrue(patogenosRecuperados.isEmpty())
    }

    @Test
    fun especiesDePatogenoSinPatogeno() {
        val patogenoGripeA = patogenoService.recuperarPatogeno(patogenoBacteria.id!!)
        val especiesDePatogeno = patogenoService.especiesDePatogeno(patogenoGripeA.id!!)
        assertEquals(0, especiesDePatogeno.size)
    }

    @Test
    fun especiesDePatogeno() {

        val patogenoGripeA = patogenoService.recuperarPatogeno(patogenoVirus.id!!)
        val patogenoBacteria = patogenoService.recuperarPatogeno(patogenoBacteria.id!!)
        val ubicacion = ubicacionService.recuperarUbicacion(ubicacionJPA.id!!)

        patogenoService.agregarEspecie(patogenoGripeA.id!!, "Gripe2", ubicacion.id!!, 0)
        patogenoService.agregarEspecie(patogenoBacteria.id!!, "Bacteria", ubicacion.id!!, 0)
        val especiesDePatogeno = patogenoService.especiesDePatogeno(patogenoGripeA.id!!)
        val patogenoRecuperado = patogenoService.recuperarPatogeno(patogenoGripeA.id!!)

        assertEquals(6, patogenoRecuperado.cantidadDeEspecies)
        assertEquals(patogenoGripeA.especies[0].patogeno?.tipo, especiesDePatogeno[0].patogeno?.tipo)
    }

    @Test
    fun esPandemia() {
        val persona = vectorService.recuperarVector(persona.id!!)
        val animal = vectorService.recuperarVector(animal.id!!)
        val especie = especieService.recuperarEspecie(especie.id!!)
        vectorService.infectar(persona.id!!, especie.id!!)
        vectorService.infectar(animal.id!!, especie.id!!)
        val especieRecuperada = especieService.recuperarEspecie(especie.id!!)
        assertTrue(patogenoService.esPandemia(especieRecuperada.id!!))
    }

    @Test
    fun intentoDeAgregarEspecieAPatogenoInexistente() {
        val patogenoInexistenteId = 9999L
        val ubicacion = ubicacionService.recuperarUbicacion(ubicacionJPA.id!!)

        assertThrows<Exception> {
            patogenoService.agregarEspecie(patogenoInexistenteId, "Gripe2", ubicacion.id!!, 0)
        }
    }

    @Test
    fun obtenerEspeciesDePatogenoSinEspecies() {
        val patogenoSinEspecies = patogenoService.recuperarPatogeno(patogenoBacteria.id!!)
        val especiesDePatogeno = patogenoService.especiesDePatogeno(patogenoSinEspecies.id!!)
        assertTrue(especiesDePatogeno.isEmpty())
    }


}