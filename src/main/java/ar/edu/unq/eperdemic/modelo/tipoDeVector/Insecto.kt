package ar.edu.unq.eperdemic.modelo.tipoDeVector

import ar.edu.unq.eperdemic.modelo.UbicacionJPA
import ar.edu.unq.eperdemic.modelo.Vector
import javax.persistence.Entity

@Entity
class Insecto(tipoDeVector: TipoDeVector = TipoDeVector.Insecto, ubicacionJPA: UbicacionJPA) :
    Vector(tipoDeVector, ubicacionJPA) {

    override fun puedeSerContagiadoPorTipoDeVector(vector: Vector): Boolean {
        return (vector.tipoDeVector != TipoDeVector.Insecto)
    }

    override fun tiposDeCaminosPermitidos(): List<String> {
        return listOf("Aereo", "Terrestre");
    }

}
