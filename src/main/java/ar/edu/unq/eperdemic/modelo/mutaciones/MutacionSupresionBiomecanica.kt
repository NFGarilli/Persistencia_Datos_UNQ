package ar.edu.unq.eperdemic.modelo.mutaciones

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import javax.persistence.Entity

@Entity
class MutacionSupresionBiomecanica(val potencia: Int, especie: Especie?) : Mutacion(especie) {
    init {
        require(potencia in 1..100) { "La potencia debe estar entre 1 y 100" }
    }

    override fun resolverMutacion(vector: Vector) {
        var enfermedadesAEliminar = vector.enfermedades.filter { this.esEnfermedadAEliminar(it) }
        vector.eliminarEnfermedades(enfermedadesAEliminar)
    }

    override fun esBioalteracion(): Boolean {
        return false
    }

    private fun esEnfermedadAEliminar(enfermedad: Especie): Boolean {
        return enfermedad.nombre != this.especie?.nombre && potencia > enfermedad.defensa!!
    }

    override fun tieneTipoDeVector(tipoDeVector: Vector.TipoDeVector?): Boolean {
        return false
    }

    override fun tieneDefensaPara(especie: Especie): Boolean {
        return potencia > especie.defensa!!
    }

    override fun permiteContagioSinRestricciones(vector: Vector): Boolean {
        return false
    }

    override fun eliminaReestriccionesBasicasParaEnfermedadYVector(enfermedad: Especie, vector: Vector): Boolean {
        return false
    }
}
