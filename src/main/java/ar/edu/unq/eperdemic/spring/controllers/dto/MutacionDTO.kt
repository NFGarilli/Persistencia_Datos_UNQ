package ar.edu.unq.eperdemic.spring.controllers.dto

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.mutaciones.Mutacion
import ar.edu.unq.eperdemic.modelo.mutaciones.MutacionBioalteracionGenetica
import ar.edu.unq.eperdemic.modelo.mutaciones.MutacionSupresionBiomecanica

class MutacionDTO(
    val tipoDeMutacion: TipoDeMutacion,
    val especieId: Long,
    val tipoDeVector: Vector.TipoDeVector?,
    val poderDeMutacion: Int?
) {

    enum class TipoDeVector {
        Persona, Insecto, Animal
    }

    enum class TipoDeMutacion {
        Supresion_Biomecanica, Bioalteracion_Genetica,
    }

    fun aModelo(especie: Especie): Mutacion {
        return when (tipoDeMutacion) {
            TipoDeMutacion.Supresion_Biomecanica -> MutacionSupresionBiomecanica(
                poderDeMutacion!!,
                especie
            )

            TipoDeMutacion.Bioalteracion_Genetica -> MutacionBioalteracionGenetica(
                tipoDeVector!!, especie
            )
        }
    }

    fun aModelo(): Mutacion {
        return when (tipoDeMutacion) {
            TipoDeMutacion.Supresion_Biomecanica -> MutacionSupresionBiomecanica(
                poderDeMutacion!!,
                null
            )

            TipoDeMutacion.Bioalteracion_Genetica -> MutacionBioalteracionGenetica(
                tipoDeVector!!,
                null
            )
        }
    }


    companion object {
        fun desdeModelo(m: Mutacion): MutacionDTO {
            return when (m) {
                is MutacionSupresionBiomecanica -> desdeSupresionBiomecanica(m)
                is MutacionBioalteracionGenetica -> desdeBioalteracionGenetica(m)
                else -> throw IllegalArgumentException("Tipo de mutación desconocido: ${m.javaClass.simpleName}")
            }
        }

        private fun desdeSupresionBiomecanica(m: MutacionSupresionBiomecanica): MutacionDTO {
            return MutacionDTO(
                tipoDeMutacion = TipoDeMutacion.Supresion_Biomecanica,
                especieId = m.especie?.id!!,
                poderDeMutacion = m.potencia,
                tipoDeVector = null
            )
        }

        private fun desdeBioalteracionGenetica(m: MutacionBioalteracionGenetica): MutacionDTO {
            return MutacionDTO(
                tipoDeMutacion = TipoDeMutacion.Bioalteracion_Genetica,
                especieId = m.especie?.id!!,
                poderDeMutacion = null,
                tipoDeVector = m.tipoDeVector
            )
        }
    }
}
