package ar.edu.unq.eperdemic.spring.controllers.dto

import ar.edu.unq.eperdemic.modelo.Patogeno

class PatogenoDTO(val id: Long?, val tipo: String, val cantidadDeEspecies: Int?) {

    fun aModelo(): Patogeno {
        val patogeno = Patogeno(this.tipo)
        patogeno.cantidadDeEspecies = this.cantidadDeEspecies!!

        return patogeno
    }

    companion object {
        fun desdeModelo(patogeno: Patogeno): PatogenoDTO {
            return PatogenoDTO(
                patogeno.id,
                patogeno.tipo,
                patogeno.cantidadDeEspecies,
            )
        }
    }
}