package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.exceptions.ConsVacioException
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.mapping.Document

@Document("Ubicacion")
class UbicacionMongo {
    @Id
    var id: String? = null
    lateinit var nombre: String
    @GeoSpatialIndexed
    lateinit var coordenada : GeoJsonPoint
    lateinit var distrito: Distrito
    var hayinfectado: Boolean = false

    fun infectar(){
        this.hayinfectado = true
    }

    fun desinfectarUbicacion() {
        this.hayinfectado = false
    }

    // Cons vacio para convertir de JSON a este objeto.
    protected constructor()

    constructor(nombre: String, latitud: Double, longitud: Double, distrito: Distrito) : this(nombre, latitud,longitud) {
        if (nombre.isNullOrBlank()) {
            throw ConsVacioException(javaClass.simpleName, nombre)
        }
        this.nombre = nombre
        this.coordenada = GeoJsonPoint(longitud, latitud)
        this.distrito = distrito
    }
    constructor(nombre: String, latitud: Double, longitud: Double) : this(nombre) {
        if (nombre.isNullOrBlank()) {
            throw ConsVacioException(javaClass.simpleName, nombre)
        }
        this.nombre = nombre
        this.coordenada = GeoJsonPoint(longitud, latitud)
    }

    constructor(nombre: String) {
        if (nombre.isNullOrBlank()) {
            throw ConsVacioException(javaClass.simpleName, nombre)
        }
        this.nombre = nombre
        this.coordenada = GeoJsonPoint(0.0, 0.0)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UbicacionMongo) return false
        return nombre == other.nombre
    }

    override fun hashCode(): Int {
        return nombre.hashCode()
    }
}