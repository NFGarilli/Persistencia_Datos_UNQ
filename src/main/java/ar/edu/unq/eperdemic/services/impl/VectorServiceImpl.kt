package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.tipoDeVector.Animal
import ar.edu.unq.eperdemic.modelo.tipoDeVector.Humano
import ar.edu.unq.eperdemic.modelo.tipoDeVector.Insecto
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionJPADAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.mongo.UbicacionMongoDAO
import ar.edu.unq.eperdemic.services.VectorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class VectorServiceImpl() : VectorService {
    @Autowired
    private lateinit var vectorDAO: VectorDAO

    @Autowired
    private lateinit var ubicacionJPADAO: UbicacionJPADAO

    @Autowired
    private lateinit var especieDAO: EspecieDAO

    @Autowired
    private lateinit var ubicacionMongoDAO: UbicacionMongoDAO
    override fun contagiar(vectorId: Long, vectores: List<Vector>) {
        val vector = vectorDAO.findById(vectorId).get()
        vector.contagiar(vectores)
        vectorDAO.save(vector)
    }

    override fun infectar(vectorId: Long, especieid: Long) {
        val vector = vectorDAO.findById(vectorId).get()
        val especie = especieDAO.findById(especieid).get()
        val nombreDeLaUbicacionDelVector = vector.ubicacionJPA!!.nombre
        if (!vector.estaInfectadoDe(especie)) {
            vector.infectar(especie)
            vectorDAO.save(vector)
            especieDAO.save(especie)
            actualizarEstadoInfectado(nombreDeLaUbicacionDelVector)
        } else {
            throw Exception("El vector ya está infectado con la especie")
        }
    }

    override fun actualizarEstadoInfectado(nombreUbicacion: String){
        val ubicacionMongo = ubicacionMongoDAO.findByNombre(nombreUbicacion)
        ubicacionMongo.hayinfectado = true
        ubicacionMongoDAO.save(ubicacionMongo)
    }

    override fun enfermedades(vectorId: Long): List<Especie> {
        return vectorDAO.enfermedades(vectorId)
    }

    override fun crearVector(tipo: Vector.TipoDeVector, ubicacionId: Long?): Vector {
        val ubicacion = ubicacionJPADAO.findById(ubicacionId!!).get()
        val vector: Vector = when (tipo) {
            Vector.TipoDeVector.Humano -> Humano(tipo, ubicacion)
            Vector.TipoDeVector.Animal -> Animal(tipo, ubicacion)
            Vector.TipoDeVector.Insecto -> Insecto(tipo, ubicacion)
        }
        return vectorDAO.save(vector)
    }

    override fun recuperarVector(vectorId: Long): Vector {
        return vectorDAO.findById(vectorId).get()
    }

    override fun borrarVector(vectorId: Long) {
        vectorDAO.deleteById(vectorId)
    }

    override fun recuperarTodos(): List<Vector> {
        return vectorDAO.findAll().toList()
    }
}