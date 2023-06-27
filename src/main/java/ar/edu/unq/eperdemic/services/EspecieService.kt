package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Especie

interface EspecieService {
    fun recuperarEspecie(id: Long): Especie
    fun recuperarTodos(): List<Especie>
    fun cantidadDeInfectados(especieId: Long): Int
}