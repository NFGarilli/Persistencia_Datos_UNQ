package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface PatogenoDAO : CrudRepository<Patogeno, Long> {

    @Query(
        "SELECT e FROM Especie e WHERE e.patogeno.id = :patogenoId " +
                "ORDER BY e.id ASC"
    )
    fun especiesDePatogeno(patogenoId: Long): List<Especie>
}