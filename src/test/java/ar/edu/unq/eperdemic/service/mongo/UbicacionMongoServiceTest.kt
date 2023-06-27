package ar.edu.unq.eperdemic.service.mongo

import ar.edu.unq.eperdemic.exceptions.CoordenadasDeUbicacionNoEncontradaException
import ar.edu.unq.eperdemic.modelo.Distrito
import ar.edu.unq.eperdemic.modelo.UbicacionMongo
import ar.edu.unq.eperdemic.persistencia.dao.mongo.DistritoMongoDAO
import ar.edu.unq.eperdemic.persistencia.dao.mongo.UbicacionMongoDAO
import ar.edu.unq.eperdemic.service.BaseTestServices
import com.mongodb.client.MongoDatabase
import junit.framework.TestCase.*
import org.bson.Document
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPoint
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UbicacionMongoServiceTest : BaseTestServices() {
    @Autowired
    lateinit var mongoTemplate: MongoTemplate
    @Autowired
    lateinit var ubicacionMongoDao: UbicacionMongoDAO

    @Autowired
    lateinit var distritoMongoDao: DistritoMongoDAO

   //para geojson
        @BeforeAll
    fun setup() {
        val db: MongoDatabase = mongoTemplate.db
        val ubicacionCollection = db.getCollection("Ubicacion")
        ubicacionCollection.createIndex(Document("coordenada", "2dsphere"))
    }
    @Test
    fun crearUbicacionMongo() {
        var area = GeoJsonMultiPoint(listOf(Point(-34.6037, -58.3816)))
        var distrito1 = Distrito("distrito1", area)

        distritoService.crear(distrito1)

        var ubicacionMongo = UbicacionMongo("Lanus", -34.6037, -58.3816)
        var ubicacionCreada = ubicacionService.crearUbicacion(ubicacionMongo.nombre,
            ubicacionMongo.coordenada.x,ubicacionMongo.coordenada.y)

        assertNotNull(ubicacionCreada.id)
        assertEquals(ubicacionMongo.nombre, ubicacionCreada.nombre)
    }

    @Test
    fun crearUbicacionConNombreRepetidoMongo() {
        ubicacionService.crearUbicacion("Canada", 0.0, 0.0)
        assertThrows<Exception> {
            ubicacionService.crearUbicacion("Canada", 0.0, 0.0)
        }
    }
    @Test
    fun estanAMenosDe100kmDeDistancia() {
        var area = GeoJsonMultiPoint(listOf(Point(0.0,0.1)))
        var distrito1 = Distrito("distrito1", area)

        distritoService.crear(distrito1)

        val ubicacion1 = UbicacionMongo("Ubicacion 1", 0.0, 0.0)
        val ubicacion2 = UbicacionMongo("Ubicacion 2", 0.0, 0.1)
        ubicacionService.crearUbicacion(ubicacion1.nombre, ubicacion1.coordenada.x,ubicacion1.coordenada.y)
        ubicacionService.crearUbicacion(ubicacion2.nombre, ubicacion2.coordenada.x,ubicacion2.coordenada.y)

        Assertions.assertTrue(ubicacionMongoDao.estanAMenosDe100kmDeDistancia(ubicacion1.coordenada.x,
            ubicacion1.coordenada.y, ubicacion2.nombre))
    }

    @Test
    fun distritoEnUbicacion(){
        var coord1 = Point(1.0,1.1)
        var coord2 = Point(1.2,1.1)
        var coord3 = Point(1.3,1.1)

        var coordenadasDistrito : List<Point> = listOf(coord1,coord2,coord3)
        var area = GeoJsonMultiPoint(coordenadasDistrito)
        var distritoLanus = Distrito("Lanus", area)
        var lanus = distritoService.crear(distritoLanus)

        var ubic = ubicacionService.crearUbicacion("ubicacion", 1.1, 1.2)
        var ubicacionMongo = ubicacionMongoDao.findByNombre(ubic.nombre)

        var distrito = distritoMongoDao.distritoEnUbicacion(ubicacionMongo.coordenada.x,ubicacionMongo.coordenada.y)

        Assertions.assertEquals(distrito!!.nombre, lanus.nombre)
    }

    @Test
    fun seCreaUbicacionCuyasCoordenadasNoCoincidenConNingunDistritoYLanzaExcepcion(){

        var area = GeoJsonMultiPoint(listOf(Point(1.0,1.1)))
        var distritoLanus = Distrito("Lanus", area)
        var lanus = distritoService.crear(distritoLanus)

        assertThrows<CoordenadasDeUbicacionNoEncontradaException> {
            ubicacionService.crearUbicacion("ubicacion", 0.0, 8.0)
        }

    }
}