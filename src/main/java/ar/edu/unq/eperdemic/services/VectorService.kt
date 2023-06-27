package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector

interface VectorService {

    fun contagiar(vectorId: Long, vectores: List<Vector>)
    fun infectar(vectorId: Long, especieid: Long)
    fun enfermedades(vectorId: Long): List<Especie>
    fun crearVector(tipo: Vector.TipoDeVector, ubicacionId: Long?): Vector
    fun recuperarVector(vectorId: Long): Vector
    fun borrarVector(vectorId: Long)
    fun recuperarTodos(): List<Vector>

    fun actualizarEstadoInfectado(nombreUbicacion: String)

}