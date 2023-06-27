package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.ReporteDeContagios
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.repository.ReporteDeContagiosRepositoryImpl
import ar.edu.unq.eperdemic.services.EstadisticaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EstadisticaServiceImpl() : EstadisticaService {
    @Autowired
    private lateinit var reporteDeContagiosDAO: ReporteDeContagiosRepositoryImpl

    @Autowired
    private lateinit var especieDAO: EspecieDAO
    override fun especieLider(): Especie {
        return especieDAO.especieLider()
    }

    override fun lideres(): List<Especie> {
        val pageable: Pageable = PageRequest.of(0, 10)
        return especieDAO.lideresConlimite(pageable)
    }

    override fun reporteDeContagios(nombreDeLaUbicacion: String): ReporteDeContagios {
        val cantDeVectores = vectoresPresentesEn(nombreDeLaUbicacion).toInt()
        val cantDeVectoresInfectados = vectoresInfectadosEn(nombreDeLaUbicacion).toInt()
        val especieLiderNombre = especieLider().nombre
        return ReporteDeContagios(cantDeVectores, cantDeVectoresInfectados, especieLiderNombre!!)
    }

    fun vectoresPresentesEn(nombreUbicacion: String): Long {
        return reporteDeContagiosDAO.vectoresPresentesEn(nombreUbicacion)
    }

    fun vectoresInfectadosEn(nombreUbicacion: String): Long {
        return reporteDeContagiosDAO.vectoresInfectadosEn(nombreUbicacion)
    }
}