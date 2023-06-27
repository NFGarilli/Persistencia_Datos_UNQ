package ar.edu.unq.eperdemic.spring.controllers.dto

import ar.edu.unq.eperdemic.modelo.UbicacionJPA

class UbicacionDTO(
    val id: Long? = null,
    val nombre: String,
    var latitud: Double,
    var longitud: Double
) {

    fun aModelo(): UbicacionJPA {
        val ubicacionJPA = UbicacionJPA(this.id, this.nombre)
        return ubicacionJPA
    }

    companion object {
        fun desdeModelo(ubicacionJPA: UbicacionJPA, latitud: Double,longitud: Double) =
            UbicacionDTO(
                id = ubicacionJPA.id,
                nombre = ubicacionJPA.nombre,
                latitud,
                longitud
            )
    }
}
