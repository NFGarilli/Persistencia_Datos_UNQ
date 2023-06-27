package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.exceptions.*
import ar.edu.unq.eperdemic.modelo.UbicacionJPA
import ar.edu.unq.eperdemic.modelo.UbicacionNeo
import ar.edu.unq.eperdemic.persistencia.dao.mongo.UbicacionMongoDAO
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.spring.controllers.dto.UbicacionDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/ubicacion")
class UbicacionControllerREST(private val ubicacionService: UbicacionService) {
    @Autowired
    lateinit var ubicacionMongoDao: UbicacionMongoDAO

    @PostMapping("/ubicacion")
    fun crearUbicacion(
        @RequestBody ubicacion: UbicacionDTO,
        @RequestParam latitud: Double,
        @RequestParam longitud: Double
    ): ResponseEntity<UbicacionJPA> {
        try {
            val ubicacionJPA = ubicacionService.crearUbicacion(ubicacion.aModelo().nombre, latitud, longitud)
            return ResponseEntity.ok(ubicacionJPA)
        } catch (e: CoordenadasDeUbicacionNoEncontradaException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/nombre/{nombreUbicacion}")
    fun recuperarPorNombre(@PathVariable nombreUbicacion: String) {
        var ubicacionMongo = ubicacionMongoDao.findByNombre(nombreUbicacion)
        UbicacionDTO.desdeModelo(ubicacionService.recuperarPorNombre(nombreUbicacion),
            ubicacionMongo.coordenada.x, ubicacionMongo.coordenada.y)
    }
    @GetMapping("/{ubicacionId}")
    fun recuperUbicacion(@PathVariable ubicacionId: Long){
        var ubicacion = ubicacionService.recuperarUbicacion(ubicacionId)
        var ubicacionMongo = ubicacionMongoDao.findByNombre(ubicacion.nombre)
        UbicacionDTO.desdeModelo(ubicacion,ubicacionMongo.coordenada.x, ubicacionMongo.coordenada.y)
    }

    @GetMapping("/allUbicaciones")
    fun recuperarTodos(): List<UbicacionDTO> {
        val ubicaciones = ubicacionService.recuperarTodos()
        return ubicaciones.map { ubicacion ->
            val ubicacionMongo = ubicacionMongoDao.findByNombre(ubicacion.nombre)
            UbicacionDTO.desdeModelo(ubicacion, ubicacionMongo.coordenada.x, ubicacionMongo.coordenada.y)
        }
    }
    @PutMapping("/mover/{vectorId}/{ubicacionId}")
    fun mover(@PathVariable vectorId: Long, @PathVariable ubicacionId: Long): ResponseEntity<String> {
        try {
            ubicacionService.mover(vectorId, ubicacionId)
            return ResponseEntity.ok("El vector de id: '$vectorId' se movio a la ubicacion de id: '$ubicacionId'")
        } catch (ex: ElVectorYaSeEncuentraEnLaUbicacion) {
            val errorMessage = "El vector de id: '$vectorId' ya se encuentra en la ubicacion de id: '$ubicacionId'"
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage)
        } catch (ex: UbicacionMuyLejanaException) {
            val errorMessage = "La ubicacion de id: '$ubicacionId' es muy lejana"
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage)
        }
    }

    @PutMapping("/expandir/{ubicacionId}")
    fun expandir(@PathVariable ubicacionId: Long): ResponseEntity<String> {
        try {
            ubicacionService.expandir(ubicacionId)
            return ResponseEntity.ok("La ubicacion de id: '$ubicacionId' se expandio")
        } catch (ex:Exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ocurrio un error al expandir la ubicacion")
        }
    }


    @GetMapping("/ubicacionesConEspecie/{especieId}")
    fun cantUbicacionesConEspecie(@PathVariable especieId: Long) = ubicacionService.cantUbicacionesConEspecie(especieId)

    @PostMapping("/conectar")
    fun conectarUbicaciones(
        @RequestParam("ubicacion1") nombreDeUbicacion1: String,
        @RequestParam("ubicacion2") nombreDeUbicacion2: String,
        @RequestParam("tipoCamino") tipoCamino: String
    ): ResponseEntity<String> {
        try {
            ubicacionService.conectar(nombreDeUbicacion1, nombreDeUbicacion2, tipoCamino)
            return ResponseEntity.ok("Conexión exitosa")
        } catch (ex: TipoCaminoInvalidoException) {
            val errorMessage = "El tipo de camino '$tipoCamino' es inválido."
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage)
        } catch (ex: NoSePuedeConectarLaUbicacionConSiMismaException) {
            val errorMessage = "No se puede conectar la ubicación '$nombreDeUbicacion1' consigo misma."
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage)
        } catch (ex: UbicacionesYaConectadasException) {
            val errorMessage =
                "Las ubicaciones '$nombreDeUbicacion1' y '$nombreDeUbicacion2' ya están conectadas por el tipo de camino '$tipoCamino'."
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage)
        }
    }

    @GetMapping("/conectados/{nombreUbicacion}")
    fun conectados(@PathVariable nombreUbicacion: String): List<UbicacionDTO> {
        val ubicacionesConectadas = ubicacionService.conectados(nombreUbicacion)
        val ubicacionesDTO = ubicacionesConectadas.map { ubicacion ->
            val ubicacionMongo = ubicacionMongoDao.findByNombre(ubicacion.nombre)
            UbicacionDTO.desdeModelo(convertirAUbicacionJPA(ubicacion), ubicacionMongo.coordenada.x, ubicacionMongo.coordenada.y)
        }
        return ubicacionesDTO
    }

    private fun convertirAUbicacionJPA(ubicacion: UbicacionNeo): UbicacionJPA {
        val ubicacionJPA = UbicacionJPA(ubicacion.nombre)
        ubicacionJPA.id = ubicacion.id
        return ubicacionJPA
    }


    @PutMapping("/moverMasCorto/{vectorId}/{nombreUbicacion}")
    fun moverMasCorto(@PathVariable vectorId: Long, @PathVariable nombreUbicacion: String): ResponseEntity<String> {
        try {
            ubicacionService.moverMasCorto(vectorId, nombreUbicacion)
            return ResponseEntity.ok("El vector se movio con exito a '$nombreUbicacion'")
        } catch (ex: UbicacionNoAlcanzableException) {
            val errorMessage = "El vector no pudo alcanzar la ubicacion '$nombreUbicacion'"
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage)
        } catch (ex: ElVectorYaSeEncuentraEnLaUbicacion) {
            val errorMessage = "El vector ya se encuentra en '$nombreUbicacion'"
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage)
        }
    }
}