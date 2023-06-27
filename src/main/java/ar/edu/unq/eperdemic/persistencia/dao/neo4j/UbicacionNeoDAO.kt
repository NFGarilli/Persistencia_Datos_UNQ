package ar.edu.unq.eperdemic.persistencia.dao.neo4j

import ar.edu.unq.eperdemic.modelo.UbicacionNeo
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UbicacionNeoDAO : Neo4jRepository<UbicacionNeo, Long> {

    fun findByNombre(nombreUbicacion: String): UbicacionNeo

    @Query(
        "MATCH (o:UbicacionNeo {nombre: ${'$'}nombreDeUbicacion1}), " +
                "(d:UbicacionNeo {nombre: ${'$'}nombreDeUbicacion2}) " +
                "CREATE (o)-[rel:CAMINO {tipoCamino: ${'$'}tipoCamino}]->(d) " +
                "SET rel.nombre = ${'$'}tipoCamino"
    )
    fun conectar(
        @Param("nombreDeUbicacion1") nombreDeUbicacion1: String,
        @Param("nombreDeUbicacion2") nombreDeUbicacion2: String,
        @Param("tipoCamino") tipoCamino: String
    )

    @Query("MATCH (o:UbicacionNeo {nombre: ${'$'}nombreDeUbicacion1})-[rel:CAMINO]->(d:UbicacionNeo {nombre: ${'$'}nombreDeUbicacion2}) WHERE rel.tipoCamino = ${'$'}tipoCamino RETURN COUNT(rel) > 0")
    fun verificarConexiones(
        @Param("nombreDeUbicacion1") nombreDeUbicacion1: String,
        @Param("nombreDeUbicacion2") nombreDeUbicacion2: String,
        @Param("tipoCamino") tipoCamino: String
    ): Boolean

    @Query("MATCH (o:UbicacionNeo {nombre: \$nombreDeUbicacion})-[:CAMINO*]->(d:UbicacionNeo) WHERE EXISTS((o)-[:CAMINO]->(d)) RETURN d")
    fun conectados(@Param("nombreDeUbicacion") nombreDeUbicacion: String?): List<UbicacionNeo>

    @Query("MATCH (u1:UbicacionNeo {nombre: \$nombreUbicacion})-[rel:CAMINO]-(u2:UbicacionNeo {nombre: \$nombreUbicNueva}) RETURN COUNT(rel) > 0")
    fun estanConectadas(
        @Param("nombreUbicacion") nombreUbicacion: String,
        @Param("nombreUbicNueva") nombreUbicNueva: String
    ): Boolean

    @Query("MATCH (u1:UbicacionNeo {nombre: \$nombreUbicacion})-[rel:CAMINO]->(u2:UbicacionNeo {nombre: \$nombreUbicNueva}) RETURN rel.tipoCamino")
    fun caminos(
        @Param("nombreUbicacion") nombreUbicacion: String,
        @Param("nombreUbicNueva") nombreUbicNueva: String
    ): List<String>

    @Query(
        "MATCH path = (o:UbicacionNeo {nombre: \$nombreUbicacionOrigen})-[*]->(d:UbicacionNeo {nombre: \$nombreUbicacionDestino}) " +
                "WHERE ALL(rel IN relationships(path) WHERE rel.tipoCamino IN \$tiposCaminosPermitidos) " +
                "AND NOT (o = d AND o.nombre = \$nombreUbicacionOrigen) " +
                "WITH path, relationships(path) AS rels, length(path) AS length " +
                "ORDER BY length ASC " +
                "LIMIT 1 " +
                "RETURN nodes(path)[1..]"
    )
    fun obtenerCaminoMasCortoTransitable(
        @Param("nombreUbicacionOrigen") nombreUbicacionOrigen: String,
        @Param("nombreUbicacionDestino") nombreUbicacionDestino: String,
        @Param("tiposCaminosPermitidos") tiposCaminosPermitidos: List<String>
    ): List<UbicacionNeo>

    @Query("MATCH path = (o:UbicacionNeo {nombre: \$nombreUbicacion})-[rel:CAMINO]->(d:UbicacionNeo {nombre: \$nombreUbicNueva}) " +
            "WHERE ANY(rel IN relationships(path) WHERE rel.tipoCamino IN \$caminosPermitidos) RETURN COUNT(rel) > 0 ")
    fun vectorPuedeTransitarAlgunoDeLosCaminosA(
        @Param("caminosPermitidos") caminosPermitidos: List<String>,
        @Param("nombreUbicacion") nombreUbicacion: String,
        @Param("nombreUbicNueva") nombreUbicNueva: String): Boolean

    @Query("MATCH(n) DETACH DELETE n")
    fun detachDelete()
}


