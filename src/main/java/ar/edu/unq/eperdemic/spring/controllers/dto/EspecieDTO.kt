package ar.edu.unq.eperdemic.spring.controllers.dto

import ar.edu.unq.eperdemic.modelo.Especie

class EspecieDTO(
    val nombre: String,
    val paisDeOrigen: String,
    val patogenoId: Long?,
    val mutaciones: MutableList<MutacionDTO>?,
) {

    companion object {
        fun desdeModelo(especie: Especie) = EspecieDTO(
            nombre = especie.nombre!!,
            paisDeOrigen = especie.paisDeOrigen!!,
            patogenoId = especie.patogeno?.id,
            mutaciones = especie.mutaciones.map { m -> MutacionDTO.desdeModelo(m) }.toMutableList()
        )
    }
}
