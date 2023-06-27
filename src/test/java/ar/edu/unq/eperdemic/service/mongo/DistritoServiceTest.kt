package ar.edu.unq.eperdemic.service.mongo

import ar.edu.unq.eperdemic.modelo.Distrito
import ar.edu.unq.eperdemic.modelo.UbicacionMongo
import ar.edu.unq.eperdemic.persistencia.dao.mongo.DistritoMongoDAO
import ar.edu.unq.eperdemic.persistencia.dao.mongo.UbicacionMongoDAO
import ar.edu.unq.eperdemic.service.BaseTestServices
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPoint
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DistritoServiceTest : BaseTestServices() {

    @Test
    fun crearDistrito() {
        var distrito = Distrito("Lanus")
        var distritoCreado = distritoService.crear(distrito)

        assertNotNull(distritoCreado.id)
        assertEquals(distrito.nombre, distritoCreado.nombre)
    }

    @Test
    fun crearDistritoConNombreRepetidoMongo() {
        var distrito = Distrito("Lanus")
        distritoService.crear(distrito)
        assertThrows<RuntimeException> {
            distritoService.crear(distrito)
        }
    }

    @Test
    fun masEnfermo() {

        var areaLanus = GeoJsonMultiPoint(listOf(Point(-34.61, -58.3)))

        var distritoLanus = Distrito("Lanus", areaLanus)
        distritoLanus.infectar()
        distritoService.crear(distritoLanus)

        var ubicacionMongo = UbicacionMongo("ub1", -34.61, -58.3, distritoLanus)
        ubicacionMongo.infectar()
        ubicacionMongoDAO.save(ubicacionMongo)

        var ubicacionMongo2 = UbicacionMongo("ub2", -34.61, -58.3, distritoLanus)
        ubicacionMongo2.infectar()
        ubicacionMongoDAO.save(ubicacionMongo2)

        var ubicacionMongo3 = UbicacionMongo("ub3", -34.61, -58.3, distritoLanus)
        ubicacionMongo3.infectar()
        ubicacionService.crearUbicacion(ubicacionMongo3.nombre,
            ubicacionMongo3.coordenada.x,ubicacionMongo3.coordenada.y)

        var areaBanfield = GeoJsonMultiPoint(listOf(Point(-1.1, -1.2)))
        var distritoBanfield = Distrito("Banfield", areaBanfield)
        distritoBanfield.infectar()
        distritoService.crear(distritoBanfield)


        var ubicacionM2 = UbicacionMongo("um2", -1.1, -1.2, distritoBanfield)
        ubicacionM2.infectar()
        ubicacionMongoDAO.save(ubicacionM2)
        ubicacionService.crearUbicacion(ubicacionM2.nombre,
            ubicacionM2.coordenada.x,ubicacionM2.coordenada.y)
        var dist = distritoService.distritoMasEnfermo()

        assertEquals(dist!!.nombre, distritoLanus.nombre)
    }


}