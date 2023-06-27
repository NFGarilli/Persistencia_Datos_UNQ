package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.spring.controllers.dto.EspecieDTO
import ar.edu.unq.eperdemic.spring.controllers.dto.PatogenoDTO
import org.springframework.web.bind.annotation.*

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@CrossOrigin
@ServiceREST
@RequestMapping("/patogeno")
class PatogenoControllerREST(
    private val patogenoService: PatogenoService,
    private val ubicacionService: UbicacionService
) {

    @PostMapping
    fun create(@RequestBody patogenoDTO: PatogenoDTO): PatogenoDTO {
        val patogeno = patogenoDTO.aModelo()
        return PatogenoDTO.desdeModelo(patogenoService.crearPatogeno(patogeno))
    }

    @PostMapping("/{id}")
    fun agregarEspecie(
        @PathVariable id: Long,
        @RequestBody especieDTO: EspecieDTO,
        @RequestParam capacidadDeContagio: Int
    ): EspecieDTO {
        val ubicacion = ubicacionService.recuperarPorNombre(especieDTO.paisDeOrigen)
        val especie = patogenoService.agregarEspecie(id, especieDTO.nombre, ubicacion.id!!, capacidadDeContagio)
        return EspecieDTO.desdeModelo(especie)
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long) = PatogenoDTO.desdeModelo(patogenoService.recuperarPatogeno(id))

    @GetMapping
    fun getAll() = patogenoService.recuperarATodosLosPatogenos().map { PatogenoDTO.desdeModelo(it) }


}