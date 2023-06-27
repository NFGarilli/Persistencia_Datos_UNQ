package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.exceptions.ConsVacioException
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPoint
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.geo.GeoJsonPoint

@Document("Distrito")
class Distrito {
    @Id
    var id: String? = null
    lateinit var nombre: String
    lateinit var area: GeoJsonMultiPoint
    var hayinfectado: Boolean = false

    // Se necesita un constructor vacío para que Jackson pueda
    // convertir de JSON a este objeto.
    protected constructor()

    constructor(nombre: String, area: GeoJsonMultiPoint) {
        if (nombre.isNullOrBlank()) {
            throw ConsVacioException(javaClass.simpleName, nombre)
        }
        this.nombre = nombre
        this.area = area
    }


    constructor(nombre: String) {
        if (nombre.isNullOrBlank()) {
            throw ConsVacioException(javaClass.simpleName, nombre)
        }
        this.nombre = nombre
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Distrito) return false

        return nombre == other.nombre
    }

    override fun hashCode(): Int {
        return nombre.hashCode()
    }

    fun infectar(){
        this.hayinfectado = true
    }

}
