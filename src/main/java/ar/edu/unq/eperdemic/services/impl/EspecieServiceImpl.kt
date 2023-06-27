package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.services.EspecieService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EspecieServiceImpl() : EspecieService {
    @Autowired
    private lateinit var especieDAO: EspecieDAO
    override fun recuperarEspecie(id: Long): Especie {
        val especie = especieDAO.findByIdOrNull(id)!!
        return especie
    }

    override fun recuperarTodos(): List<Especie> {
        return especieDAO.findAll().toList()
    }

    override fun cantidadDeInfectados(especieId: Long): Int {
        return especieDAO.cantidadDeInfectados(especieId)
    }
}
