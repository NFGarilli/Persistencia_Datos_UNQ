package ar.edu.unq.eperdemic.modelo.mutaciones

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import javax.persistence.Entity

@Entity
class MutacionBioalteracionGenetica(
    var tipoDeVector: Vector.TipoDeVector,
    especie: Especie?
) : Mutacion(especie) {

    override fun resolverMutacion(vector: Vector) {}

    override fun esBioalteracion(): Boolean {
        return true
    }

    override fun tieneTipoDeVector(tipoDeVectorCorrespondiente: Vector.TipoDeVector?): Boolean {
        return tipoDeVector == tipoDeVectorCorrespondiente
    }

    override fun tieneDefensaPara(especie: Especie): Boolean {
        return false
    }

    override fun permiteContagioSinRestricciones(vector: Vector): Boolean {
        return tipoDeVector == vector.tipoDeVector
    }

    override fun eliminaReestriccionesBasicasParaEnfermedadYVector(enfermedad: Especie, vector: Vector): Boolean {
        return especie == enfermedad && tipoDeVector == vector.tipoDeVector
    }
}