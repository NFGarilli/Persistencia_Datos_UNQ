package ar.edu.unq.eperdemic.modelo.mutaciones

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class Mutacion(@ManyToOne var especie: Especie? = null) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    abstract fun resolverMutacion(vect: Vector)

    abstract fun esBioalteracion(): Boolean
    abstract fun tieneTipoDeVector(tipoDeVector: Vector.TipoDeVector?): Boolean
    abstract fun tieneDefensaPara(especie: Especie): Boolean
    abstract fun permiteContagioSinRestricciones(vector: Vector): Boolean
    abstract fun eliminaReestriccionesBasicasParaEnfermedadYVector(enfermedad: Especie, vector: Vector): Boolean
}