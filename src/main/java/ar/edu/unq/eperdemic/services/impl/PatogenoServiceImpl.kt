package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.exceptions.VectorNoDisponibleException
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionJPADAO
import ar.edu.unq.eperdemic.services.PatogenoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PatogenoServiceImpl() : PatogenoService {
    @Autowired
    private lateinit var patogenoDAO: PatogenoDAO

    @Autowired
    private lateinit var ubicacionJPADAO: UbicacionJPADAO

    @Autowired
    private lateinit var especieDAO: EspecieDAO

    override fun crearPatogeno(patogeno: Patogeno): Patogeno {
        return patogenoDAO.save(patogeno)
    }

    override fun recuperarPatogeno(id: Long): Patogeno {
        return patogenoDAO.findByIdOrNull(id)!!
    }

    override fun recuperarATodosLosPatogenos(): List<Patogeno> {
        return patogenoDAO.findAll().toList()
    }

    override fun agregarEspecie(id: Long, nombre: String, ubicacionId: Long, capacidadDeContagio: Int): Especie {
        val patogeno = patogenoDAO.findById(id).get()
        val ubicacion = ubicacionJPADAO.findById(ubicacionId).get()
        val vectoresEnUbicacion = ubicacion.vectores

        if (vectoresEnUbicacion.isEmpty()) {
            throw VectorNoDisponibleException()
        }

        val especie = patogeno.crearEspecie(nombre, ubicacion.nombre, capacidadDeContagio)
        vectoresEnUbicacion.random().infectar(especie)
        especieDAO.save(especie)


        return especie
    }

    override fun esPandemia(especieId: Long): Boolean {
        val ubicaciones = ubicacionJPADAO.findAll().toMutableList()
        val cantUbicacionesConEspecie = ubicacionJPADAO.cantUbicacionesConEspecie(especieId)

        return cantUbicacionesConEspecie > ubicaciones.size / 2
    }

    override fun especiesDePatogeno(patogenoId: Long): List<Especie> {
        return patogenoDAO.especiesDePatogeno(patogenoId)
    }
}

