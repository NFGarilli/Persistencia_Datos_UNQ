package ar.edu.unq.eperdemic.spring.controllers.dto

import ar.edu.unq.eperdemic.modelo.ReporteDeContagios

class ReporteDeContagiosDTO(
    val vectoresPresentes: Int,
    val vectoresInfecatods: Int,
    val nombreDeEspecieMasInfecciosa: String
) {

    companion object {
        fun desdeModelo(reporte: ReporteDeContagios) = ReporteDeContagiosDTO(
            vectoresPresentes = reporte.vectoresPresentes,
            vectoresInfecatods = reporte.vectoresInfecatods,
            nombreDeEspecieMasInfecciosa = reporte.nombreDeEspecieMasInfecciosa
        )
    }
}
