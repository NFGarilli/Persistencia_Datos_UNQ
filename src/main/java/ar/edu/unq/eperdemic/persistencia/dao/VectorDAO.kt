package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.*
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface VectorDAO : CrudRepository<Vector, Long> {

    @Query("select v.enfermedades from Vector v where v.id = :vectorId")
    fun enfermedades(@Param("vectorId") vectorId: Long): List<Especie>
}