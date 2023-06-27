package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.exceptions.ConsVacioException
import ar.edu.unq.eperdemic.modelo.mutaciones.DadoMutaciones
import ar.edu.unq.eperdemic.modelo.mutaciones.Mutacion
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import javax.persistence.*

@Entity
class Especie() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var nombre: String? = null
    var paisDeOrigen: String? = null
    var capacidadDeContagio: Int = 0
    var capacidadDeBiomecanizacion: Int = 0
    var defensa: Int = 0

    @ManyToOne
    var patogeno: Patogeno? = null

    @ManyToMany
    @JoinTable(
        name = "enfermedades",
        joinColumns = arrayOf(JoinColumn(name = "especie_id")),
        inverseJoinColumns = arrayOf(JoinColumn(name = "vector_id"))
    )

    @LazyCollection(LazyCollectionOption.FALSE)
    var vectores: MutableList<Vector> = mutableListOf()

    @OneToMany(mappedBy = "especie", cascade = [CascadeType.PERSIST], fetch = FetchType.EAGER)
    var mutaciones: MutableList<Mutacion> = mutableListOf()


    constructor(patogeno: Patogeno, nombre: String, paisDeOrigen: String, capacidadDeContagio: Int) : this() {
        if (nombre.isNullOrBlank()) {
            throw ConsVacioException(javaClass.simpleName, "nombre")
        }
        if (paisDeOrigen.isNullOrBlank()) {
            throw ConsVacioException(javaClass.simpleName, "paisDeOrigen")
        }
        this.patogeno = patogeno
        this.nombre = nombre
        this.paisDeOrigen = paisDeOrigen
        this.capacidadDeContagio = capacidadDeContagio
    }

    constructor(
        patogeno: Patogeno,
        nombre: String,
        paisDeOrigen: String,
        capacidadDeContagio: Int,
        capacidadDeBiomecanizacion: Int
    )
            : this(patogeno, nombre, paisDeOrigen, capacidadDeContagio) {
        this.capacidadDeBiomecanizacion = capacidadDeBiomecanizacion
    }

    constructor(
        patogeno: Patogeno,
        nombre: String,
        paisDeOrigen: String,
        capacidadDeContagio: Int,
        capacidadDeBiomecanizacion: Int,
        defensa: Int
    )
            : this(patogeno, nombre, paisDeOrigen, capacidadDeContagio, capacidadDeBiomecanizacion) {
        this.defensa = defensa
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val especie = o as Especie?
        return id == especie!!.id
    }

    fun agregarMutacion(mutacion: Mutacion) {
        mutaciones.add(mutacion)
    }

    fun generarMutacionEnVector(vector: Vector) {
        if (hayProbabilidadDeMutacion() && hayMutacionesDisponibles()) {
            val mutacionAleatoria = DadoMutaciones.generarMutacionRandom(mutaciones)
            vector.agregarMutacion(mutacionAleatoria)
        }
    }

    fun hayMutacionesDisponibles(): Boolean {
        return mutaciones.isNotEmpty()
    }

    fun hayProbabilidadDeMutacion(): Boolean {
        val porcentajeBiomecanizacion = capacidadDeBiomecanizacion ?: 0
        return porcentajeBiomecanizacion > 50
    }

}