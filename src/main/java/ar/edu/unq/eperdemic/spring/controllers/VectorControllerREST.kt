package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.mongo.UbicacionMongoDAO
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.spring.controllers.dto.EspecieDTO
import ar.edu.unq.eperdemic.spring.controllers.dto.VectorDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/vector")
class VectorControllerREST(private val vectorService: VectorService) {
    @Autowired
    lateinit var ubicacionMongoDao: UbicacionMongoDAO
    @PostMapping("/vector")
    fun crearVector(@RequestBody vectorDTO: VectorDTO): VectorDTO {
        val response = vectorService.crearVector(vectorDTO.tipoDeVector!!, vectorDTO.ubicacionDTO!!.id!!)
        val ubi = vectorDTO.ubicacionDTO
        return VectorDTO.desdeModelo(response, ubi.latitud, ubi.longitud)
    }

    @GetMapping("/{vectorId}")
    fun recuperarVector(@PathVariable vectorId: Long) : VectorDTO{
        var vector = vectorService.recuperarVector(vectorId)!!
        var ubiVector = vector.ubicacionJPA!!.nombre
        var ubicacion = ubicacionMongoDao.findByNombre(ubiVector)
       return VectorDTO.desdeModelo(vector, ubicacion.coordenada.x, ubicacion.coordenada.y)
    }

    @GetMapping("/allVectores")
    fun recuperarTodos(): List<VectorDTO> {
        return vectorService.recuperarTodos().map { vector ->
            val ubicacionVector = vector.ubicacionJPA!!.nombre
            val ubicacion = ubicacionMongoDao.findByNombre(ubicacionVector)
            VectorDTO.desdeModelo(vector, ubicacion.coordenada.x, ubicacion.coordenada.y)
        }
    }
    @DeleteMapping("/{vectorId}")
    fun borrarVector(@PathVariable vectorId: Long) = vectorService.borrarVector(vectorId)

    @GetMapping("/enfermedades/{vectorId}")
    fun enfermedades(@PathVariable vectorId: Long) =
        vectorService.enfermedades(vectorId).map { especie -> EspecieDTO.desdeModelo(especie) }

    @PutMapping("/infectar/{vectorId}/{especieId}")
    fun infectar(@PathVariable vectorId: Long, @PathVariable especieId: Long): Long {
        vectorService.infectar(vectorId, especieId)
        //Tengo que retornar algo porque sino el postman se me rompia
        return vectorId
    }

    @PutMapping("/contagiar/{vectorId}")
    fun contagiar(@PathVariable vectorId: Long, @RequestBody vectores: List<Vector>): Long {
        vectorService.contagiar(vectorId, vectores)
        //Idem
        return vectorId
    }
}