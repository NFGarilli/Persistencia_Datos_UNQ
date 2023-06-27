package ar.edu.unq.eperdemic.utils

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.mutaciones.MutacionBioalteracionGenetica
import ar.edu.unq.eperdemic.modelo.mutaciones.MutacionSupresionBiomecanica
import ar.edu.unq.eperdemic.modelo.tipoDeVector.Animal
import ar.edu.unq.eperdemic.modelo.tipoDeVector.Humano
import ar.edu.unq.eperdemic.persistencia.dao.*
import ar.edu.unq.eperdemic.persistencia.dao.mongo.DistritoMongoDAO
import ar.edu.unq.eperdemic.persistencia.dao.mongo.UbicacionMongoDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.UbicacionNeoDAO
import ar.edu.unq.eperdemic.services.UbicacionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPoint
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DataServiceImpl(
    @Autowired private var patogenoDAO: PatogenoDAO,
    @Autowired private var ubicacionService: UbicacionService,
    @Autowired private var ubicacionJPADAO: UbicacionJPADAO,
    @Autowired private var ubicacionNeo: UbicacionNeoDAO,
    @Autowired private var vectorDAO: VectorDAO,
    @Autowired private var especieDAO: EspecieDAO,
    @Autowired private var dataDAO: DataDAO,
    @Autowired private var mutacionDAO: MutacionDAO,
    @Autowired private var ubicacionMongoDAO: UbicacionMongoDAO,
    @Autowired private var distritoMongoDAO: DistritoMongoDAO
) : DataService {

    companion object {

        var area = GeoJsonMultiPoint(listOf(Point(0.0,0.0)))
        var distrito : Distrito = Distrito("pangea", area)

        var ubicacionJPA: UbicacionJPA = UbicacionJPA("Argentina")
        var ubicacionJPA2: UbicacionJPA = UbicacionJPA("Uruguay")
        var ubicacionJPA3: UbicacionJPA = UbicacionJPA("Chile")

        var patogenoVirus: Patogeno = Patogeno("Virus")
        var patogenoBacteria: Patogeno = Patogeno("Bacteria")

        var persona: Vector = Humano(Vector.TipoDeVector.Humano, ubicacionJPA)
        var animal: Animal = Animal(Vector.TipoDeVector.Animal, ubicacionJPA2)
        var persona2: Vector = Humano(Vector.TipoDeVector.Humano, ubicacionJPA2)

        var especie = Especie(patogenoVirus, "Gripe", "Argentina", 50)
        var especie2 = Especie(patogenoVirus, "Roblox", "Japon", 25)
        var especie3 = Especie(patogenoVirus, "Gripe", "Uruguay", 70)
        var especie4 = Especie(patogenoVirus, "Gripe", "Chile", 60, 90)

        var bioalteracionGenetica = MutacionBioalteracionGenetica(Vector.TipoDeVector.Animal, especie4)
        var supresionBiomecanica = MutacionSupresionBiomecanica(30, especie4)

    }

    override fun crearSetDeDatosIniciales() {
        //Distrito
        distritoMongoDAO.save(distrito)

        //Ubicacion
        ubicacionJPADAO.save(ubicacionJPA)
        ubicacionJPADAO.save(ubicacionJPA2)
        ubicacionJPADAO.save(ubicacionJPA3)
        var ubic = ubicacionService.recuperarPorNombre(ubicacionJPA.nombre)
        var ubic2 = ubicacionService.recuperarPorNombre(ubicacionJPA2.nombre)
        var ubic3 = ubicacionService.recuperarPorNombre(ubicacionJPA3.nombre)
        ubicacionMongoDAO.save(UbicacionMongo(ubic.nombre, 0.0, 0.0))
        ubicacionMongoDAO.save(UbicacionMongo(ubic2.nombre, 0.0, 0.0))
        ubicacionMongoDAO.save(UbicacionMongo(ubic3.nombre, 0.0, 0.0))



        /*ubicacionService.crearUbicacion("Argentina", 0.0,0.0)
        ubicacionService.crearUbicacion("Uruguay", 0.0,0.0)
        ubicacionService.crearUbicacion("Chile", 0.0,0.0)*/

        //Patogeno
        patogenoDAO.save(patogenoVirus)
        patogenoDAO.save(patogenoBacteria)

        //Vector
        vectorDAO.save(persona)
        vectorDAO.save(animal)
        vectorDAO.save(persona2)


        //Especie
        especieDAO.save(especie)
        especieDAO.save(especie2)
        especieDAO.save(especie3)
        especieDAO.save(especie4)

        //Mutacion
        mutacionDAO.save(bioalteracionGenetica)
        mutacionDAO.save(supresionBiomecanica)

    }

    override fun eliminarTodo() {
        dataDAO.clearAll()
        ubicacionNeo.detachDelete()
        ubicacionMongoDAO.deleteAll()
        distritoMongoDAO.deleteAll()
    }

    override fun ambienteLideres() {
        var especie5 = Especie(patogenoVirus, "Gripe", "P", 50)
        var especie6 = Especie(patogenoVirus, "Roblox", "S", 25)
        var especie7 = Especie(patogenoVirus, "Gripe", "A", 70)
        var especie8 = Especie(patogenoVirus, "Gripe", "M", 60)
        var especie9 = Especie(patogenoVirus, "Roblox", "L", 25)
        var especie10 = Especie(patogenoVirus, "Gripe", "N", 70)
        var especie11 = Especie(patogenoVirus, "Gripe", "R", 60)

        especieDAO.save(especie5)
        especieDAO.save(especie6)
        especieDAO.save(especie7)
        especieDAO.save(especie8)
        especieDAO.save(especie9)
        especieDAO.save(especie10)
        especieDAO.save(especie11)
    }
}
