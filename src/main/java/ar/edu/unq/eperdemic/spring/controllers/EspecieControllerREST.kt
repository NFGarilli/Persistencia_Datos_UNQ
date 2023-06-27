package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.services.EspecieService
import ar.edu.unq.eperdemic.spring.controllers.dto.EspecieDTO
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/especie")
class EspecieControllerREST(private val especieService: EspecieService) {

    @GetMapping("/{especieId}")
    fun recuperarEspecie(@PathVariable especieId: Long) =
        EspecieDTO.desdeModelo(especieService.recuperarEspecie(especieId))

    @GetMapping("/allEspecies")
    fun recuperarTodos() = especieService.recuperarTodos().map { especie -> EspecieDTO.desdeModelo(especie) }

    @GetMapping("/{especieId}/cantidadDeInfectados")
    fun cantidadDeInfectados(@PathVariable especieId: Long) = especieService.cantidadDeInfectados(especieId)
}
