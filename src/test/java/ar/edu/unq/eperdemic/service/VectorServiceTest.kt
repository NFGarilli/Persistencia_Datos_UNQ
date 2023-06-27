package ar.edu.unq.eperdemic.service

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.persistencia.dao.*
import ar.edu.unq.eperdemic.persistencia.dao.mongo.UbicacionMongoDAO
import ar.edu.unq.eperdemic.utils.DataServiceImpl.Companion.animal
import ar.edu.unq.eperdemic.utils.DataServiceImpl.Companion.especie
import ar.edu.unq.eperdemic.utils.DataServiceImpl.Companion.especie2
import ar.edu.unq.eperdemic.utils.DataServiceImpl.Companion.persona
import ar.edu.unq.eperdemic.utils.DataServiceImpl.Companion.persona2
import ar.edu.unq.eperdemic.utils.DataServiceImpl.Companion.ubicacionJPA
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPoint
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(PER_CLASS)
class VectorServiceTest : BaseTestServices() {
    @Test
    fun crearVector() {

        var vectorHumano = vectorService.crearVector(Vector.TipoDeVector.Humano, ubicacionJPA.id)
        var vectorAnimal = vectorService.crearVector(Vector.TipoDeVector.Animal, ubicacionJPA.id)
        var vectorInsecto = vectorService.crearVector(Vector.TipoDeVector.Insecto, ubicacionJPA.id)

        Assertions.assertNotNull(vectorHumano.id)
        Assertions.assertNotNull(vectorAnimal.id)
        Assertions.assertNotNull(vectorInsecto.id)

        Assertions.assertEquals(vectorHumano.tipoDeVector, Vector.TipoDeVector.Humano)
        Assertions.assertEquals(vectorAnimal.tipoDeVector, Vector.TipoDeVector.Animal)
        Assertions.assertEquals(vectorInsecto.tipoDeVector, Vector.TipoDeVector.Insecto)

        Assertions.assertEquals(vectorHumano.ubicacionJPA!!.id, ubicacionJPA.id)
        Assertions.assertEquals(vectorAnimal.ubicacionJPA!!.id, ubicacionJPA.id)
        Assertions.assertEquals(vectorInsecto.ubicacionJPA!!.id, ubicacionJPA.id)

    }

    @Test
    fun crearVectorConUbicacionInexistente() {

        Assertions.assertThrows(NoSuchElementException::class.java) {
            vectorService.crearVector(
                Vector.TipoDeVector.Humano,
                123L
            )
        }
    }


    @Test
    fun recuperarVector() {
        val vectorRecuperado = vectorService.recuperarVector(persona.id!!)
        Assertions.assertEquals(vectorRecuperado.id, persona.id)
    }

    @Test
    fun recuperarVectorInexistente() {
        Assertions.assertThrows(NoSuchElementException::class.java) { vectorService.recuperarVector(2000) }
    }

    @Test
    fun recuperarTodos() {

        val persona = vectorService.recuperarVector(persona.id!!)
        val vectores = vectorService.recuperarTodos()

        Assertions.assertEquals(3, vectores.size.toLong())
        Assertions.assertTrue(vectores.contains(persona))
    }

    @Test
    fun infectarVector() {

        val especie = especieService.recuperarEspecie(especie.id!!)
        val persona = vectorService.recuperarVector(persona.id!!)

        vectorService.infectar(persona.id!!, especie.id!!)

        val vectorInfectado = vectorService.recuperarVector(persona.id!!)

        Assertions.assertEquals(1, vectorInfectado.enfermedades.size.toLong())
        Assertions.assertTrue(vectorInfectado.enfermedades.contains(especie))
    }

    @Test
    fun enfermedades() {

        val especie = especieService.recuperarEspecie(especie.id!!)
        val especie2 = especieService.recuperarEspecie(especie2.id!!)

        val persona = vectorService.recuperarVector(persona.id!!)

        vectorService.infectar(persona.id!!, especie.id!!)
        vectorService.infectar(persona.id!!, especie2.id!!)

        val vectorInfectado = vectorService.recuperarVector(persona.id!!)

        val enfermedades = vectorService.enfermedades(persona.id!!)

        Assertions.assertEquals(2, vectorInfectado.enfermedades.size.toLong())
        Assertions.assertTrue(enfermedades.contains(especie))
        Assertions.assertTrue(enfermedades.contains(especie2))
    }

    @Test
    fun unVectorHumanoIntentaContagiarAOtroHumanoConUnaEspecieQueInfectaSiOSiYPuede() {

        val especie = especieService.recuperarEspecie(especie.id!!)

        val vectorInfectado = vectorService.recuperarVector(persona.id!!)
        val vectorAInfectar = vectorService.recuperarVector(persona2.id!!)

        vectorService.infectar(vectorInfectado.id!!, especie.id!!)

        val listaDeVectores = listOf(vectorAInfectar)

        vectorService.contagiar(vectorInfectado.id!!, listaDeVectores)

        var enfermedades = vectorService.enfermedades(vectorAInfectar.id!!)

        Assertions.assertTrue(enfermedades.isNotEmpty())
        Assertions.assertTrue(enfermedades.contains(especie))
    }

    @Test
    fun unVectorHumanoIntentaContagiarAUnVectorAnimalYNoPuede() {
        val especie = especieService.recuperarEspecie(especie.id!!)
        val vectorInfectado = vectorService.recuperarVector(persona.id!!)
        val vectorAnimal = vectorService.recuperarVector(animal.id!!)

        vectorService.infectar(vectorInfectado.id!!, especie.id!!)

        val listaDeVectores = listOf(vectorAnimal)

        vectorService.contagiar(vectorInfectado.id!!, listaDeVectores)

        var enfermedades = vectorService.enfermedades(vectorAnimal.id!!)

        Assertions.assertFalse(enfermedades.contains(especie))
    }

    @Test
    fun noSePuedeInfectarVectorYaInfectadoConLaMismaEspecie() {
        val especie = especieService.recuperarEspecie(especie.id!!)
        val vectorInfectado = vectorService.recuperarVector(persona.id!!)

        vectorService.infectar(vectorInfectado.id!!, especie.id!!)

        assertThrows<Exception> { vectorService.infectar(vectorInfectado.id!!, especie.id!!) }
    }

    @Test
    fun contagioMasivo() {
        val especie = especieService.recuperarEspecie(especie.id!!)
        val vectorInfectado = vectorService.crearVector(Vector.TipoDeVector.Humano, ubicacionJPA.id)
        val vectorNoInfectado1 = vectorService.crearVector(Vector.TipoDeVector.Humano, ubicacionJPA.id)
        val vectorNoInfectado2 = vectorService.crearVector(Vector.TipoDeVector.Humano, ubicacionJPA.id)

        vectorService.infectar(vectorInfectado.id!!, especie.id!!)

        val vectoresAContagiar = listOf(vectorNoInfectado1, vectorNoInfectado2)

        vectorService.contagiar(vectorInfectado.id!!, vectoresAContagiar)

        val enfermedadesVector1 = vectorService.enfermedades(vectorNoInfectado1.id!!)
        val enfermedadesVector2 = vectorService.enfermedades(vectorNoInfectado2.id!!)

        Assertions.assertTrue(enfermedadesVector1.contains(especie))
        Assertions.assertTrue(enfermedadesVector2.contains(especie))
    }

    @Test
    fun intentoInfeccionDuplicada() {
        val especie = especieService.recuperarEspecie(especie.id!!)
        val vectorInfectado = vectorService.crearVector(Vector.TipoDeVector.Humano, ubicacionJPA.id)

        vectorService.infectar(vectorInfectado.id!!, especie.id!!)

        assertThrows<Exception> { vectorService.infectar(vectorInfectado.id!!, especie.id!!) }
    }

    @Test
    fun actualizarEstadoInfectado() {
        var area = GeoJsonMultiPoint(listOf(Point(0.0,2.0)))
        var distrito1 = Distrito("distrito1", area)

        distritoService.crear(distrito1)

        val ubicacion1 = UbicacionMongo("Ubicacion 1", 0.0, 2.0)
        ubicacionService.crearUbicacion(ubicacion1.nombre, ubicacion1.coordenada.x,ubicacion1.coordenada.y)

        vectorService.actualizarEstadoInfectado(ubicacion1.nombre)
        val ubicRec = ubicacionMongoDAO.findByNombre(ubicacion1.nombre)

        Assertions.assertTrue(ubicRec.hayinfectado)
    }

}