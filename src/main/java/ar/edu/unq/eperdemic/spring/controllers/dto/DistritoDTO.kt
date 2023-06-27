package ar.edu.unq.eperdemic.spring.controllers.dto

import ar.edu.unq.eperdemic.modelo.Distrito
import com.mongodb.client.model.geojson.Point
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPoint
import org.springframework.data.mongodb.core.geo.GeoJsonPoint

class DistritoDTO(
    var id: String? = null,
    var nombre: String,
    var area: GeoJsonMultiPoint
){
    fun aModelo(): Distrito {
        return Distrito(nombre, area)
    }
}
