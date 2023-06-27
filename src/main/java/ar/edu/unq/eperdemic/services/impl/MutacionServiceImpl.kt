package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.mutaciones.Mutacion
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.MutacionDAO
import ar.edu.unq.eperdemic.services.MutacionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MutacionServiceImpl : MutacionService {
    @Autowired
    private lateinit var especieDAO: EspecieDAO

    @Autowired
    private lateinit var mutacionDAO: MutacionDAO

    override fun agregarMutacion(especieId: Long, mutacion: Mutacion) {
        var especie = especieDAO.findById(especieId).get()
        mutacion.especie = especie
        mutacionDAO.save(mutacion)
    }
}