package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.exceptions.ConsVacioException
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import java.io.Serializable

@Node
class UbicacionNeo : Serializable {

    @Id
    @GeneratedValue
    var id: Long? = null

    var nombre: String

    constructor(nombre: String) {
        if (nombre.isNullOrBlank()) {
            throw ConsVacioException(javaClass.simpleName, nombre)
        }
        this.nombre = nombre
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UbicacionNeo) return false
        return nombre == other.nombre
    }

    override fun hashCode(): Int {
        return nombre.hashCode()
    }
}