package ar.edu.unq.eperdemic.service

import ar.edu.unq.eperdemic.exceptions.ElVectorYaSeEncuentraEnLaUbicacion
import ar.edu.unq.eperdemic.exceptions.UbicacionMuyLejanaException
import ar.edu.unq.eperdemic.exceptions.UbicacionNoAlcanzableException
import ar.edu.unq.eperdemic.modelo.Distrito
import ar.edu.unq.eperdemic.modelo.UbicacionJPA
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.utils.DataServiceImpl
import org.junit.Assert
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPoint
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UbicacionJPAServiceTest : BaseTestServices() {

    @Test
    fun crearUbicacion() {
        var ubicacionJPA = UbicacionJPA("Argentica")
        var ubicacionCreada = ubicacionService.crearUbicacion("Argentica", 0.0, 0.0)

        Assert.assertNotNull(ubicacionCreada.id)
        Assert.assertEquals(ubicacionJPA.nombre, ubicacionCreada.nombre)
        Assert.assertEquals(ubicacionJPA.vectores, ubicacionCreada.vectores)
    }

    @Test
    fun crearUbicacionConNombreRepetido() {
        assertThrows<Exception> {
            ubicacionService.crearUbicacion("Argentina", 0.0, 0.0)
        }
    }

    @Test
    fun alCrearYLuegoRecuperarSeObtieneObjetosSimilares() {

        var ubicacionCreada = ubicacionService.crearUbicacion("ubicacion", 0.0, 0.0)
        val ubicacionRecuperada = ubicacionService.recuperarUbicacion(ubicacionCreada.id!!)

        Assert.assertEquals(ubicacionCreada.nombre, ubicacionRecuperada.nombre)
        Assert.assertEquals(ubicacionCreada.vectores, ubicacionRecuperada.vectores)
        Assert.assertEquals(ubicacionCreada.id!!, ubicacionRecuperada.id)

        Assert.assertTrue(ubicacionCreada !== ubicacionRecuperada)
    }

    @Test
    fun recuperarUbicacionExistenteDevuelveUbicacion() {

        var ubicacionCreada = ubicacionService.crearUbicacion("Ranelagh", 0.0, 0.0)
        var idUbicacion = ubicacionCreada.id
        var ubicacionRecuperada = ubicacionService.recuperarUbicacion(idUbicacion!!)

        Assert.assertEquals(ubicacionCreada.id, ubicacionRecuperada.id)
    }

    @Test
    fun recuperarATodasLasUbicaciones() {
        val ubicacionesRecuperadas = ubicacionService.recuperarTodos()

        Assert.assertEquals(3, ubicacionesRecuperadas.size)
        Assert.assertEquals("Argentina", ubicacionesRecuperadas[0].nombre)
    }

    @Test
    fun cantUbicacionesConEspecie() {
        val especie1DelDataService = DataServiceImpl.especie
        val vectorPersonaDelDataService = DataServiceImpl.persona
        val vectorAnimalDelDataService = DataServiceImpl.animal

        vectorService.infectar(vectorPersonaDelDataService.id!!, especie1DelDataService.id!!)
        vectorService.infectar(vectorAnimalDelDataService.id!!, especie1DelDataService.id!!)
        var cantUbicacionesConEspecie = ubicacionService.cantUbicacionesConEspecie(especie1DelDataService.id!!)
        Assert.assertEquals(2, cantUbicacionesConEspecie)
    }

    @Test
    fun seIntentaMoverUnVectorHumanoPorUnCaminoTerrestreYEsMovidoSatisfactoriamente() {
        val kingsLanding = ubicacionService.crearUbicacion("Kings Landing", 0.0, 0.0)
        val casterlyRock = ubicacionService.crearUbicacion("Casterly Rock", 0.0, 0.0)

        ubicacionService.conectar(kingsLanding.nombre, casterlyRock.nombre, "Terrestre")

        val lannister = vectorService.crearVector(Vector.TipoDeVector.Humano, kingsLanding.id)

        ubicacionService.mover(lannister.id!!, casterlyRock.id!!)

        val lannister2 = vectorService.recuperarVector(lannister.id!!)

        Assertions.assertEquals(lannister2.ubicacionJPA!!.nombre, casterlyRock.nombre)
    }

    @Test
    fun mover_cambiaDeUbicacionAlVectorCorrectamenteYAlMoverloContagiaCorrectamenteConUnaEspecieQueInfectaSiOSiALosVectoresDeEsaUbicacion() {
        // Given
        var winterfell = ubicacionService.crearUbicacion("winterfell", 0.0, 0.0)
        var theWall = ubicacionService.crearUbicacion("The Wall", 0.0, 0.0)

        ubicacionService.conectar(winterfell.nombre, theWall.nombre, "Terrestre")

        var jonSnow = vectorService.crearVector(Vector.TipoDeVector.Humano, winterfell.id!!)
        var samTarlly = vectorService.crearVector(Vector.TipoDeVector.Humano, theWall.id!!)

        var especieZarpada = DataServiceImpl.especie
        vectorService.infectar(jonSnow.id!!, especieZarpada.id!!)

        //todavia Sam no esta infectado
        Assert.assertFalse(samTarlly.estaInfectadoDe(especieZarpada))

        //Jon se mueve a la ubicacion de Sam
        ubicacionService.mover(jonSnow.id!!, theWall.id!!)

        //recupero ambos vectores y chequeo que Jon haya contagiado a Sam
        val jonActualizado = vectorService.recuperarVector(jonSnow.id!!)
        val samActualizado = vectorService.recuperarVector(samTarlly.id!!)

        Assert.assertEquals(theWall.nombre, jonActualizado.ubicacionJPA!!.nombre)
        Assert.assertTrue(samActualizado.estaInfectado())
        Assert.assertTrue(samActualizado.estaInfectadoDe(especieZarpada))
    }

    @Test
    fun moverUnVectorAUnaUbicacionDestinoQueEsIgualASuUbicacionOriginalLevantaUnError() {
        val winterfell = ubicacionService.crearUbicacion("Winterfell", 0.0, 0.0)

        val lannister = vectorService.crearVector(Vector.TipoDeVector.Humano, winterfell.id)

        assertThrows<ElVectorYaSeEncuentraEnLaUbicacion> {
            ubicacionService.mover(lannister.id!!, winterfell.id!!)
        }
    }

    @Test
    fun moverUnVectorHumanoPorCaminoAereoLanzaExcepcion() {
        val valeOfAryn = ubicacionService.crearUbicacion("Vale Of Aryn", 0.0, 0.0)
        val riverlands = ubicacionService.crearUbicacion("Riverlands", 0.0, 0.0)

        ubicacionService.conectar(valeOfAryn.nombre, riverlands.nombre, "Aereo")

        val stark = vectorService.crearVector(Vector.TipoDeVector.Humano, valeOfAryn.id)

        assertThrows<UbicacionNoAlcanzableException> {
            ubicacionService.mover(stark.id!!, riverlands.id!!)
        }
    }

    @Test
    fun moverElVectorAUnaUbicacionAMasDeUnMovimientoDeDistanciaLanzaExcepcionUbicacionMuyLejana() {
        val valeOfAryn = ubicacionService.crearUbicacion("Vale Of Aryn", 0.0, 0.0)
        val riverlands = ubicacionService.crearUbicacion("Riverlands", 0.0, 0.0)
        val westerlands = ubicacionService.crearUbicacion("Westerlands", 0.0, 0.0)

        ubicacionService.conectar(valeOfAryn.nombre, riverlands.nombre, "Terrestre")
        ubicacionService.conectar(riverlands.nombre, westerlands.nombre, "Terrestre")

        val stark = vectorService.crearVector(Vector.TipoDeVector.Humano, valeOfAryn.id)

        assertThrows<UbicacionMuyLejanaException> {
            ubicacionService.mover(stark.id!!, westerlands.id!!)
        }
    }

    @Test
    fun expandirEnUnaUbicacionCon1vectorInfectadoExpandeSuInfectaccionATodosLosVectoresDeSuMismaUbicacionSiPuede() {
        // Given
        var ubicacionCreada1 = ubicacionService.crearUbicacion("Ranelagh", 0.0, 0.0)
        var idUbicacion1 = ubicacionCreada1.id
        var ubicacionRecuperada1 = ubicacionService.recuperarUbicacion(idUbicacion1!!)

        //Creo todos vectores Humanos asi se cumple que pueda contagiar ya que 1 humano puede contagiar a otros humanos.
        var vectorA = vectorService.crearVector(Vector.TipoDeVector.Humano, ubicacionRecuperada1.id!!)
        var vectorB = vectorService.crearVector(Vector.TipoDeVector.Humano, ubicacionRecuperada1.id!!)
        var vectorC = vectorService.crearVector(Vector.TipoDeVector.Humano, ubicacionRecuperada1.id!!)
        var vectorD = vectorService.crearVector(Vector.TipoDeVector.Humano, ubicacionRecuperada1.id!!)
        var vectorE = vectorService.crearVector(Vector.TipoDeVector.Humano, ubicacionRecuperada1.id!!)

        //Infecto a el vectorA con el virus con ID 1, el cual es un virus que tiene una capacidadDeContagio muy alta entonces siempre es efectiva el infectar
        val especie1DelDataService = DataServiceImpl.especie
        vectorService.infectar(vectorA.id!!, especie1DelDataService.id!!)


        var ubicacionRecuperadaOtraVez = ubicacionService.recuperarUbicacion(idUbicacion1)

        //Expando la infeccion
        ubicacionService.expandir(ubicacionRecuperadaOtraVez.id!!)

        var vectorBRecuperado = vectorService.recuperarVector(vectorB.id!!)
        var vectorCRecuperado = vectorService.recuperarVector(vectorC.id!!)
        var vectorERecuperado = vectorService.recuperarVector(vectorD.id!!)
        var vectorDRecuperado = vectorService.recuperarVector(vectorE.id!!)

        //Al expandir, todos estos vectores, si pueden ser contagiados por la infeccion que se expande, se infectan.
        Assert.assertTrue(vectorBRecuperado.estaInfectado())
        Assert.assertTrue(vectorCRecuperado.estaInfectado())
        Assert.assertTrue(vectorDRecuperado.estaInfectado())
        Assert.assertTrue(vectorERecuperado.estaInfectado())
    }

    @Test
    fun recuperarPorNombre() {
        var ubicacionRecuperada = ubicacionService.recuperarPorNombre("Argentina")

        val ubicacionesRecuperadas = ubicacionService.recuperarTodos()
        val primerUbicacion = ubicacionesRecuperadas[0]
        Assert.assertEquals("Argentina", primerUbicacion.nombre)
        Assert.assertEquals(ubicacionRecuperada.id, primerUbicacion.id)

    }

    @Test
    fun moverVectorAUnaUbicacionInexistenteLanzaExcepcion() {
        val winterfell = ubicacionService.crearUbicacion("Winterfell", 0.0, 0.0)
        val idUbicacion = winterfell.id
        val lannister = vectorService.crearVector(Vector.TipoDeVector.Humano, idUbicacion)

        assertThrows<Exception> {
            ubicacionService.mover(lannister.id!!, 99999L)
        }
    }

    @Test
    fun moverUnVectorPorUnCaminoNoExistenteLanzaExcepcion() {
        val winterfell = ubicacionService.crearUbicacion("Winterfell", 0.0, 0.0)
        val theWall = ubicacionService.crearUbicacion("The Wall", 0.0, 0.0)

        val lannister = vectorService.crearVector(Vector.TipoDeVector.Humano, winterfell.id)

        assertThrows<Exception> {
            ubicacionService.mover(lannister.id!!, theWall.id!!)
        }
    }

    @Test
    fun moverVectorInexistenteLanzaExcepcion() {
        val winterfell = ubicacionService.crearUbicacion("Winterfell", 0.0, 0.0)
        val lannisterId = 9999L

        assertThrows<Exception> {
            ubicacionService.mover(lannisterId, winterfell.id!!)
        }
    }

    @Test
    fun moverVectorAUnaUbicacionConectadaPeroAMasDe100kmDeDistanciaLanzaExcepcionUbicacionMuyLejana() {
        var areaVOA = GeoJsonMultiPoint(listOf(Point(10.0,10.0)))
        var distritoValeOfAryn = Distrito("distritoValeOfAryn", areaVOA)

        var areaR = GeoJsonMultiPoint(listOf(Point(90.0,90.0)))
        var distritoRiverlands = Distrito("distritoRiverlands", areaR)

        distritoService.crear(distritoRiverlands)
        distritoService.crear(distritoValeOfAryn)

        val valeOfAryn = ubicacionService.crearUbicacion("Vale Of Aryn", 10.0, 10.0)
        val riverlands = ubicacionService.crearUbicacion("Riverlands", 90.0, 90.0)

        ubicacionService.conectar(valeOfAryn.nombre, riverlands.nombre, "Terrestre")

        val stark = vectorService.crearVector(Vector.TipoDeVector.Humano, valeOfAryn.id)

        assertThrows<UbicacionMuyLejanaException> {
            ubicacionService.mover(stark.id!!, riverlands.id!!)
        }

    }

    @Test
    fun moverVectorAUnaUbicacionConectadaAMenosDe100kmDeDistancia() {

        var areaR = GeoJsonMultiPoint(listOf(Point(0.1,0.1)))
        var distritoRiverlands = Distrito("distritoRiverlands", areaR)

        distritoService.crear(distritoRiverlands)

        val valeOfAryn = ubicacionService.crearUbicacion("Vale Of Aryn", 0.0, 0.0)
        val riverlands = ubicacionService.crearUbicacion("Riverlands", 0.1, 0.1)

        ubicacionService.conectar(valeOfAryn.nombre, riverlands.nombre, "Terrestre")

        val stark = vectorService.crearVector(Vector.TipoDeVector.Humano, valeOfAryn.id)

        ubicacionService.mover(stark.id!!, riverlands.id!!)

        val starkRecuperado = vectorService.recuperarVector(stark.id!!)

        Assertions.assertEquals(starkRecuperado.ubicacionJPA!!.nombre, riverlands.nombre)

    }

    @Test
    fun seMueveElUltimoVectorInfectadoYSeActualizarEstadoInfecciosoDeLasUbicaciones(){
        /*val valeOfAryn = ubicacionService.crearUbicacion("Vale Of Aryn", 10.0, 10.0)
        val riverlands = ubicacionService.crearUbicacion("Riverlands", 10.1, 10.1)*/

        var areaR = GeoJsonMultiPoint(listOf(Point(0.1,0.1)))
        var distritoRiverlands = Distrito("distritoRiverlands", areaR)

        distritoService.crear(distritoRiverlands)

        val valeOfAryn = ubicacionService.crearUbicacion("Vale Of Aryn", 0.0, 0.0)
        val riverlands = ubicacionService.crearUbicacion("Riverlands", 0.1, 0.1)

        ubicacionService.conectar(valeOfAryn.nombre, riverlands.nombre, "Terrestre")

        val stark = vectorService.crearVector(Vector.TipoDeVector.Humano, valeOfAryn.id)
        var especieZarpada = DataServiceImpl.especie
        vectorService.infectar(stark.id!!, especieZarpada.id!!)

        ubicacionService.mover(stark.id!!, riverlands.id!!)

        val valeOfArynRec = ubicacionMongoDAO.findByNombre(valeOfAryn.nombre)
        val riverlandsRec = ubicacionMongoDAO.findByNombre(riverlands.nombre)

        Assertions.assertFalse(valeOfArynRec.hayinfectado)
        Assertions.assertTrue(riverlandsRec.hayinfectado)

    }

    @Test
    fun seMueveUnVectorPeroQuedanVectoresInfectadosLaUbicOriginalSigueInfectado(){
       /* val valeOfAryn = ubicacionService.crearUbicacion("Vale Of Aryn", 10.0, 10.0)
        val riverlands = ubicacionService.crearUbicacion("Riverlands", 10.1, 10.1)*/

        var areaR = GeoJsonMultiPoint(listOf(Point(0.1,0.1)))
        var distritoRiverlands = Distrito("distritoRiverlands", areaR)

        distritoService.crear(distritoRiverlands)

        val valeOfAryn = ubicacionService.crearUbicacion("Vale Of Aryn", 0.0, 0.0)
        val riverlands = ubicacionService.crearUbicacion("Riverlands", 0.1, 0.1)

        ubicacionService.conectar(valeOfAryn.nombre, riverlands.nombre, "Terrestre")

        var especieZarpada = DataServiceImpl.especie

        val stark = vectorService.crearVector(Vector.TipoDeVector.Humano, valeOfAryn.id)
        vectorService.infectar(stark.id!!, especieZarpada.id!!)

        val snow = vectorService.crearVector(Vector.TipoDeVector.Humano, valeOfAryn.id)
        vectorService.infectar(snow.id!!, especieZarpada.id!!)

        ubicacionService.mover(stark.id!!, riverlands.id!!)

        val valeOfArynRec = ubicacionMongoDAO.findByNombre(valeOfAryn.nombre)

        Assertions.assertTrue(valeOfArynRec.hayinfectado)

    }



}


