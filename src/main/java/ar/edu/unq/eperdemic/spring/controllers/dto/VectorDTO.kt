package ar.edu.unq.eperdemic.spring.controllers.dto

import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.tipoDeVector.Animal
import ar.edu.unq.eperdemic.modelo.tipoDeVector.Humano
import ar.edu.unq.eperdemic.modelo.tipoDeVector.Insecto

class VectorDTO(
    val tipoDeVector: Vector.TipoDeVector,
    val ubicacionDTO: UbicacionDTO,
    val vectorId: Long? = null
) {

    enum class TipoDeVector {
        Humano, Insecto, Animal
    }

    fun aModelo(): Vector {
        val vector: Vector = when (tipoDeVector) {
            Vector.TipoDeVector.Humano -> Humano(tipoDeVector, ubicacionDTO.aModelo())
            Vector.TipoDeVector.Animal -> Animal(tipoDeVector, ubicacionDTO.aModelo())
            else -> {
                Insecto(tipoDeVector!!, ubicacionDTO.aModelo())
            }
        }
        return vector
    }

    companion object {
        fun desdeModelo(vector: Vector, latitud: Double, longitud: Double) =
            VectorDTO(
                vectorId = vector.id,
                tipoDeVector = vector.tipoDeVector!!,
                ubicacionDTO = UbicacionDTO.desdeModelo(vector.ubicacionJPA!!, latitud, longitud)
            )
    }
}