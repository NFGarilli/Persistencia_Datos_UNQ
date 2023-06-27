package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.mutaciones.Mutacion
import org.springframework.data.jpa.repository.JpaRepository

interface MutacionDAO : JpaRepository<Mutacion, Long> {
}