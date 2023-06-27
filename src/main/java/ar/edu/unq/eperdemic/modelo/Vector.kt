package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.dado.Dado
import ar.edu.unq.eperdemic.modelo.mutaciones.Mutacion
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class Vector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var tipoDeVector: TipoDeVector? = null
    var infectado : Boolean = false

    //Este esta bueno para ver que funcionen correctamente los test :)
    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    var historialMovimientos: MutableList<String> = mutableListOf()

    @ManyToOne
    var ubicacionJPA: UbicacionJPA? = null

    @ManyToMany(mappedBy = "vectores")
    @LazyCollection(LazyCollectionOption.FALSE)
    var enfermedades: MutableList<Especie> = mutableListOf()

    @ManyToMany(fetch = FetchType.LAZY)
    @LazyCollection(LazyCollectionOption.FALSE)
    var mutaciones: MutableList<Mutacion> = mutableListOf()

    constructor(tipoDeVector: TipoDeVector, ubicacionJPA: UbicacionJPA) {
        this.tipoDeVector = tipoDeVector
        this.ubicacionJPA = ubicacionJPA
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val vector = o as Vector?
        return id == vector!!.id
    }

    fun contagiar(vectores: List<Vector>) {
        for (vector in vectores) {
            this.intentoDeContagio(vector, enfermedades)
        }
    }

    fun intentoDeContagio(vector: Vector, enfermedades: List<Especie>) {
        var enfermedadesAContagiar = enfermedades.filter { e -> this.puedeContagiarEnfermedadA(e, vector) }
        enfermedadesAContagiar.forEach { e -> vector.infectar(e) }
        enfermedadesAContagiar.forEach { e -> e.generarMutacionEnVector(this) }
        this.resolverMutaciones()
    }

    private fun puedeContagiarEnfermedadA(enfermedad: Especie, vector: Vector): Boolean {
        return this.tieneMutacionQueEliminaReestriccionesBasicas(enfermedad, vector)
                || puedeContagiarAVectorPorReglasBasicas(vector, enfermedad)
                && vector.noTieneDefensaPorMutacion(enfermedad)
    }

    fun puedeContagiarAVectorPorReglasBasicas(vector: Vector, enfermedad: Especie): Boolean {
        val indiceDeContagio = 20
        return vector.porcentajeDeContagio(enfermedad) > indiceDeContagio && vector.puedeSerContagiadoPorTipoDeVector(
            this
        )
    }

    abstract fun puedeSerContagiadoPorTipoDeVector(vector: Vector): Boolean


    private fun tieneMutacionQueEliminaReestriccionesBasicas(enfermedad: Especie, vector: Vector): Boolean {
        return mutaciones.any { mutacion ->
            mutacion.eliminaReestriccionesBasicasParaEnfermedadYVector(
                enfermedad,
                vector
            )
        }
    }

    private fun resolverMutaciones() {
        for (mutacion in mutaciones) {
            mutacion.resolverMutacion(this)
        }
    }

    fun porcentajeDeContagio(especie: Especie): Int {
        return Dado.probabilidadDeContagioRandom() + especie.capacidadDeContagio!!
    }

    fun eliminarEnfermedades(enfermedadesAEliminar: List<Especie>) {
        enfermedadesAEliminar.forEach { enfermedad -> this.curarseDe(enfermedad) }
    }

    fun curarseDe(enfermedad: Especie) {
        enfermedades.remove(enfermedad)
    }

    fun infectar(especie: Especie) {
        this.enfermedades.add(especie)
        especie.vectores.add(this)
        infectado = true
    }

    fun agregarMutacion(mutacion: Mutacion) {
        if (!mutaciones.contains(mutacion)) {
            mutaciones.add(mutacion)
        }
    }

    fun noTieneDefensaPorMutacion(especie: Especie): Boolean {
        return mutaciones.none { mutacion -> mutacion.tieneDefensaPara(especie) }
    }

    fun tieneMutacion(mutacion: Mutacion): Boolean {
        return mutaciones.contains(mutacion)
    }

    fun estaInfectado(): Boolean {
        return !(this.enfermedades.isEmpty())
    }

    fun estaInfectadoDe(especie: Especie): Boolean {
        return (this.enfermedades.contains(especie))
    }

    abstract fun tiposDeCaminosPermitidos(): List<String>

    enum class TipoDeVector {
        Humano, Insecto, Animal;

    }
}

