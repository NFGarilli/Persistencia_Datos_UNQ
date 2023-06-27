package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.modelo.Distrito
import ar.edu.unq.eperdemic.services.DistritoService
import ar.edu.unq.eperdemic.spring.controllers.dto.DistritoDTO
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/distrito")
class DistritoControllerREST(private val distritoService: DistritoService) {

    @PostMapping("/distrito")
    fun crear(@RequestBody distrito: DistritoDTO) = distritoService.crear(distrito.aModelo())

    @GetMapping("/distrito/masEnfermo")
    fun distritoMasEnfermo(): Distrito = distritoService.distritoMasEnfermo()!!
}