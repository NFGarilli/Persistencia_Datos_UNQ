package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.exceptions.ConsVacioException
import java.io.Serializable
import javax.persistence.*

@Entity(name = "Ubicacion")
class UbicacionJPA : Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(unique = true)
    var nombre: String

    constructor(nombre: String) {
        if (nombre.isNullOrBlank()) {
            throw ConsVacioException(javaClass.simpleName, nombre)
        }
        this.nombre = nombre
    }

    constructor(id: Long?, nombre: String) {
        if (nombre.isNullOrBlank()) {
            throw ConsVacioException(javaClass.simpleName, nombre)
        }
        this.nombre = nombre
        this.id = id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UbicacionNeo) return false
        return nombre == other.nombre
    }

    override fun hashCode(): Int {
        return nombre.hashCode()
    }

    @OneToMany(mappedBy = "ubicacionJPA", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var vectores: MutableSet<Vector> = HashSet()

}