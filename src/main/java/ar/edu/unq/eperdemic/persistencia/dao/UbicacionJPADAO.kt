package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.UbicacionJPA
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UbicacionJPADAO : JpaRepository<UbicacionJPA, Long> {

    @Query("SELECT COUNT(u) FROM Ubicacion u JOIN u.vectores v JOIN v.enfermedades e WHERE e.id = :especieId")
    fun cantUbicacionesConEspecie(@Param("especieId") especieId: Long): Long

    fun findByNombre(nombreUbicacion: String): UbicacionJPA

    @Query("SELECT COUNT(u) > 0 FROM Ubicacion u WHERE u.nombre = :nombre AND EXISTS (SELECT 1 FROM u.vectores v WHERE v.infectado = true)")
    fun esUbicacionInfectada(@Param("nombre") nombre: String): Boolean
}
