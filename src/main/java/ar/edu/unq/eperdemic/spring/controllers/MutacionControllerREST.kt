package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.services.EspecieService
import ar.edu.unq.eperdemic.services.MutacionService
import ar.edu.unq.eperdemic.spring.controllers.dto.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/mutacion")
class MutacionControllerREST(private val mutacionService: MutacionService, private val especieService: EspecieService) {

    @PostMapping("/{especieId}")
    fun agregarMutacion(@PathVariable especieId: Long, @RequestBody mutacionDTO: MutacionDTO): ResponseEntity<Void> {
        mutacionService.agregarMutacion(especieId, mutacionDTO.aModelo())
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

}


