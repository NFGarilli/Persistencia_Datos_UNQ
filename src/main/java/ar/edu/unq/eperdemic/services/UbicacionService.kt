package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.UbicacionJPA
import ar.edu.unq.eperdemic.modelo.UbicacionNeo

interface UbicacionService {
    fun mover(vectorId: Long, ubicacionid: Long)
    fun expandir(ubicacionId: Long)
    fun crearUbicacion(nombreUbicacion: String, longitud: Double, latitud: Double): UbicacionJPA
    fun recuperarUbicacion(ubicacionId: Long): UbicacionJPA
    fun recuperarTodos(): List<UbicacionJPA>
    fun cantUbicacionesConEspecie(especieId: Long): Long
    fun recuperarPorNombre(nombreUbicacion: String): UbicacionJPA
    fun conectar(nombreDeUbicacion1: String, nombreDeUbicacion2: String, tipoCamino: String)
    fun conectados(nombreDeUbicacion: String): List<UbicacionNeo>
    fun moverMasCorto(vectorId: Long, nombreDeUbicacion: String)
    fun estanConectadasPorTipoDeCamino(nombreUbicacion1: String, nombreUbicacion2: String, tipoCamino: String): Boolean

}