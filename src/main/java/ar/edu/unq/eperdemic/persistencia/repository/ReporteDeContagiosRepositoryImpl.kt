package ar.edu.unq.eperdemic.persistencia.repository

import ar.edu.unq.eperdemic.persistencia.dao.ReporteDeContagiosDAO
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager

@Repository
class ReporteDeContagiosRepositoryImpl(private val entityManager: EntityManager) : ReporteDeContagiosDAO {

    override fun vectoresPresentesEn(nombreUbicacion: String): Long {
        val query = entityManager.createQuery(
            "select count(v) " +
                    "from Vector v join v.ubicacionJPA as vu " +
                    "where vu.nombre = :nombreUbicacion"
        )
        query.setParameter("nombreUbicacion", nombreUbicacion)
        return query.singleResult as Long
    }

    override fun vectoresInfectadosEn(nombreUbicacion: String): Long {
        val query = entityManager.createQuery(
            "select count(distinct v) " +
                    "from Vector v join v.ubicacionJPA vu join v.enfermedades ve " +
                    "where vu.nombre = :nombreUbicacion"
        )
        query.setParameter("nombreUbicacion", nombreUbicacion)
        return query.singleResult as Long
    }
}
