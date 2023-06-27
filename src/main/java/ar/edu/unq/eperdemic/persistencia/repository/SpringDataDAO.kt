package ar.edu.unq.eperdemic.persistencia.repository

import ar.edu.unq.eperdemic.persistencia.dao.DataDAO
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Repository
@Transactional
class SpringDataDAO(val entityManager: EntityManager) : DataDAO {
    override fun clearAll() {
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate()
        entityManager.createNativeQuery("SHOW TABLES").resultList.forEach {
            entityManager.createNativeQuery("TRUNCATE TABLE $it").executeUpdate()
        }
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate()
    }
}
