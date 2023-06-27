package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Especie
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param


interface EspecieDAO : JpaRepository<Especie, Long> {

    @Query("SELECT COUNT(v) FROM Vector v JOIN v.enfermedades e WHERE e.id = :especieId")
    fun cantidadDeInfectados(@Param("especieId") idDeEspecie: Long): Int

    @Query(
        "SELECT e FROM Especie e JOIN e.vectores v " +
                "WHERE v.tipoDeVector = 'Humano' GROUP BY e.id " +
                "ORDER BY COUNT(v) DESC"
    )
    fun especieLider(): Especie

    @Query(
        "SELECT e " +
                "FROM Especie e JOIN e.vectores v " +
                "WHERE v.tipoDeVector = 'Humano' OR v.tipoDeVector = 'Animal' " +
                "GROUP BY e " +
                "ORDER BY SUM(CASE WHEN v.tipoDeVector = 'Humano' THEN 1 ELSE 0 END) + " +
                "SUM(CASE WHEN v.tipoDeVector = 'Animal' THEN 1 ELSE 0 END) DESC"
    )
    fun lideresConlimite(pageable: Pageable): List<Especie>
}