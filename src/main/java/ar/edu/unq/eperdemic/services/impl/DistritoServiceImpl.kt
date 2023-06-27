package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Distrito
import ar.edu.unq.eperdemic.persistencia.dao.mongo.DistritoMongoDAO
import ar.edu.unq.eperdemic.persistencia.dao.mongo.UbicacionMongoDAO
import ar.edu.unq.eperdemic.services.DistritoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DistritoServiceImpl : DistritoService {
    @Autowired
    lateinit var ubicacionMongoDAO : UbicacionMongoDAO
    @Autowired
    lateinit var distritoDAO: DistritoMongoDAO
    override fun crear(distrito: Distrito): Distrito {
        if (distritoDAO.existsByNombre(distrito.nombre)) {
            throw RuntimeException("Ya existe un distrito con el nombre ${distrito.nombre}")
        }
        return distritoDAO.save(distrito)
    }

    override fun distritoMasEnfermo(): Distrito {
        val nombre = ubicacionMongoDAO.obtenerDistritoMasInfectado()!!.nombreInfectado
        return distritoDAO.findByNombre(nombre)

    }

    override fun deleteAll() {
        distritoDAO.deleteAll()
    }
}