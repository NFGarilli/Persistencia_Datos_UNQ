package ar.edu.unq.eperdemic.persistencia.dao.mongo

import ar.edu.unq.eperdemic.modelo.Distrito
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPoint
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query


interface DistritoMongoDAO : MongoRepository<Distrito, String> {
    fun existsByNombre(nombre: String): Boolean
    fun findByNombre(nombre: String): Distrito
    @Query(value = "{ 'area' : { \$geoIntersects: { \$geometry: { type: 'Point', coordinates: [ ?0, ?1 ] } } } }")
    fun distritoEnUbicacion(latitude1: Double, longitude1: Double): Distrito?
}
