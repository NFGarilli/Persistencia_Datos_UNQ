package ar.edu.unq.eperdemic.persistencia.dao.mongo

import ar.edu.unq.eperdemic.modelo.DistritoInfectado
import ar.edu.unq.eperdemic.modelo.UbicacionMongo
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query


interface UbicacionMongoDAO : MongoRepository<UbicacionMongo, String> {

  @Query(value = "{ 'coordenada' : { \$nearSphere: { \$geometry: { type: 'Point', coordinates: [ ?1, ?0 ] }, \$maxDistance: 100000 } }, 'nombre': { \$eq: ?2 } }",exists = true)
  fun estanAMenosDe100kmDeDistancia(latitude1: Double, longitude1: Double, nombre: String): Boolean
  fun findByNombre(nombre: String): UbicacionMongo

  @Aggregation(pipeline = [
    "{\$match: { hayinfectado: true }}",
    "{\$group: { _id: '\$distrito.nombre'," +
            " nombreInfectado: { \$first: '\$distrito.nombre' }," +
            " cantidadInfectados: { \$sum: 1 } }}",
    "{\$sort: { cantidadInfectados: -1 }}",
    "{\$limit: 1 }"
  ])
  fun obtenerDistritoMasInfectado(): DistritoInfectado?



}


