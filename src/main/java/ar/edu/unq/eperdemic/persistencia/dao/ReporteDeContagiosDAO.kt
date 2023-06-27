package ar.edu.unq.eperdemic.persistencia.dao

interface ReporteDeContagiosDAO {
    fun vectoresPresentesEn(nombreUbicacion: String): Long

    fun vectoresInfectadosEn(nombreUbicacion: String): Long
}