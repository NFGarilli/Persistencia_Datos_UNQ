package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.services.EstadisticaService
import ar.edu.unq.eperdemic.spring.controllers.dto.EspecieDTO
import ar.edu.unq.eperdemic.spring.controllers.dto.ReporteDeContagiosDTO
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/estadistica")
class EstadisticaControllerREST(private val estadisticaService: EstadisticaService) {

    @GetMapping("/especieLider")
    fun especieLider() = EspecieDTO.desdeModelo(estadisticaService.especieLider())

    @GetMapping("/lideres")
    fun lideres() = estadisticaService.lideres().map { especie -> EspecieDTO.desdeModelo(especie) }

    @GetMapping("/{nombreDeLaUbicacion}")
    fun reporteDeContagios(@PathVariable nombreDeLaUbicacion: String) =
        ReporteDeContagiosDTO.desdeModelo(estadisticaService.reporteDeContagios(nombreDeLaUbicacion))
}