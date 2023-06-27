package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.mutaciones.Mutacion

interface MutacionService {
    fun agregarMutacion(especieId: Long, mutacion: Mutacion)
}