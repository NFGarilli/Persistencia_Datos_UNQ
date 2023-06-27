package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.exceptions.*
import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionJPADAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.UbicacionNeoDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.mongo.DistritoMongoDAO
import ar.edu.unq.eperdemic.persistencia.dao.mongo.UbicacionMongoDAO
import ar.edu.unq.eperdemic.services.UbicacionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional
import kotlin.random.Random

@Service
@Transactional
class UbicacionServiceImpl : UbicacionService {
    @Autowired
    private lateinit var ubicacionJPADAO: UbicacionJPADAO

    @Autowired
    private lateinit var vectorDAO: VectorDAO

    @Autowired
    lateinit var ubicacionNeoDAO: UbicacionNeoDAO

    @Autowired
    lateinit var ubicacionMongoDao: UbicacionMongoDAO
    @Autowired
    lateinit var distritoMongoDao: DistritoMongoDAO

    override fun mover(vectorId: Long, ubicacionid: Long) {
        val vector = vectorDAO.findById(vectorId).get()
        val ubicacionNueva = ubicacionJPADAO.findById(ubicacionid).get()

        val ubicActual = ubicacionMongoDao.findByNombre(vector.ubicacionJPA!!.nombre)
        val coordenadaUbicActual = ubicActual.coordenada

        if (ubicacionNueva.nombre == vector.ubicacionJPA!!.nombre) {
            throw ElVectorYaSeEncuentraEnLaUbicacion("el vector ya se encuentra en la ubicacion '$ubicacionNueva")
        } else {
            if (ubicacionNeoDAO.estanConectadas(vector.ubicacionJPA!!.nombre, ubicacionNueva.nombre)
                &&
                ubicacionMongoDao.estanAMenosDe100kmDeDistancia(coordenadaUbicActual.x, coordenadaUbicActual.y, ubicacionNueva.nombre)) {
                this.moverVectorSiHayCaminoParaEl(vector, ubicacionNueva, vector.ubicacionJPA!!)
            } else {
                throw UbicacionMuyLejanaException("No hay camino directo a '$ubicacionNueva' o '$ubicacionNueva' esta a mas de 100km")
            }
            vectorDAO.save(vector)
        }
    }


    private fun moverVectorSiHayCaminoParaEl(vector: Vector, ubicacionNueva: UbicacionJPA, ubicacionOriginal : UbicacionJPA) {
        if (ubicacionNeoDAO.vectorPuedeTransitarAlgunoDeLosCaminosA(vector.tiposDeCaminosPermitidos(), vector.ubicacionJPA!!.nombre, ubicacionNueva.nombre)) {
            moverDefinitivamenteAlVectorYContagiar(vector, ubicacionNueva)
            actualizarEstadoInfecciosoDeUbicacion(ubicacionOriginal)
            actualizarEstadoInfecciosoDeUbicacion(ubicacionNueva)
        } else {
            throw UbicacionNoAlcanzableException("No hay camino disponible para el vector '$vector")
        }
    }

    private fun actualizarEstadoInfecciosoDeUbicacion(ubicacion: UbicacionJPA) {
        if (ubicacionJPADAO.esUbicacionInfectada(ubicacion.nombre)){
            val ubicacionMongo = ubicacionMongoDao.findByNombre(ubicacion.nombre)
            ubicacionMongo.infectar()
            ubicacionMongoDao.save(ubicacionMongo)
        }
        else {
            val ubicacionMongo = ubicacionMongoDao.findByNombre(ubicacion.nombre)
            ubicacionMongo.desinfectarUbicacion()
            ubicacionMongoDao.save(ubicacionMongo)
        }
    }

    private fun moverDefinitivamenteAlVectorYContagiar(vector: Vector, ubicacionNueva: UbicacionJPA) {
        vector.ubicacionJPA = ubicacionNueva
        if (vector.estaInfectado()) {
            val vectoresEnNuevaUbicacion = ubicacionNueva.vectores.toList()
            vector.contagiar(vectoresEnNuevaUbicacion)
        }
    }

    override fun expandir(ubicacionId: Long) {
        val ubicacionNueva = ubicacionJPADAO.findById(ubicacionId).get()
        val vectoresContagiados = ubicacionNueva.vectores.filter { it.estaInfectado() }

        if (vectoresContagiados.isNotEmpty()) {

            val vectorElegido = vectoresContagiados[Random.nextInt(vectoresContagiados.size)]
            val todosLosVectoresDeLaUbicacion = ubicacionNueva.vectores
            todosLosVectoresDeLaUbicacion.remove(vectorElegido)
            val todosLosVectoresDeLaUbicacionSinElVectorElegido = todosLosVectoresDeLaUbicacion.toList()

            vectorElegido.contagiar(todosLosVectoresDeLaUbicacionSinElVectorElegido)
            vectorDAO.save(vectorElegido)
        }
    }

    override fun crearUbicacion(nombreUbicacion: String, longitud: Double, latitud: Double): UbicacionJPA {
        var distrito = distritoMongoDao.distritoEnUbicacion(latitud,longitud)
        if (distrito == null) {
            throw CoordenadasDeUbicacionNoEncontradaException()
        }
        else{
            ubicacionMongoDao.save(UbicacionMongo(nombreUbicacion, longitud, latitud, distrito))
            ubicacionNeoDAO.save(UbicacionNeo(nombreUbicacion))
            return ubicacionJPADAO.save(UbicacionJPA(nombreUbicacion))
        }
    }

    override fun recuperarUbicacion(ubicacionId: Long): UbicacionJPA {
        return ubicacionJPADAO.findByIdOrNull(ubicacionId)!!
    }

    override fun recuperarTodos(): List<UbicacionJPA> {
        return ubicacionJPADAO.findAll().toList()
    }

    override fun cantUbicacionesConEspecie(especieId: Long): Long {
        return ubicacionJPADAO.cantUbicacionesConEspecie(especieId)
    }

    override fun recuperarPorNombre(nombreUbicacion: String): UbicacionJPA {
        return ubicacionJPADAO.findByNombre(nombreUbicacion)
    }

    override fun conectar(nombreDeUbicacion1: String, nombreDeUbicacion2: String, tipoCamino: String) {
        val tipoDeCamino = normalizarTipoCamino(tipoCamino)

        // Verifica si el tipo de camino es válido
        if (!esTipoDeCaminoValido(tipoDeCamino)) {
            throw TipoCaminoInvalidoException(tipoDeCamino)
        }

        // Verifica que no se intente conectar a la ubicacion con si misma
        if (sonLaMismaUbicacion(nombreDeUbicacion1, nombreDeUbicacion2)) {
            throw NoSePuedeConectarLaUbicacionConSiMismaException(nombreDeUbicacion1)
        }

        // Verifica si la ubicación ya está conectada
        val ubicacionesConectadas = estanConectadasPorTipoDeCamino(nombreDeUbicacion1, nombreDeUbicacion2, tipoDeCamino)

        if (ubicacionesConectadas) {
            throw UbicacionesYaConectadasException(nombreDeUbicacion1, nombreDeUbicacion2, tipoDeCamino)
        }

        ubicacionNeoDAO.conectar(nombreDeUbicacion1, nombreDeUbicacion2, tipoDeCamino)
    }

    private fun sonLaMismaUbicacion(nombreUbicacionA: String, nombreUbicacionB: String): Boolean {
        return nombreUbicacionA == nombreUbicacionB
    }

    private fun normalizarTipoCamino(tipoCamino: String): String {
        return tipoCamino.lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    private fun esTipoDeCaminoValido(tipoDeCamino: String): Boolean {
        return tipoDeCamino == "Maritimo" || tipoDeCamino == "Aereo" || tipoDeCamino == "Terrestre"
    }

    override fun conectados(nombreDeUbicacion: String): List<UbicacionNeo> {
        return ubicacionNeoDAO.conectados(nombreDeUbicacion)
    }

    override fun moverMasCorto(vectorId: Long, nombreDeUbicacion: String) {
        val vector = vectorDAO.findById(vectorId).get()
        val tiposCaminosPermitidos = vector.tiposDeCaminosPermitidos()

        if (vector.ubicacionJPA!!.nombre == nombreDeUbicacion) {
            throw ElVectorYaSeEncuentraEnLaUbicacion(nombreDeUbicacion)
        }

        val caminoMasCorto = ubicacionNeoDAO.obtenerCaminoMasCortoTransitable(
            vector.ubicacionJPA!!.nombre,
            nombreDeUbicacion,
            tiposCaminosPermitidos
        )

        if (caminoMasCorto.isEmpty()) {
            throw UbicacionNoAlcanzableException(nombreDeUbicacion)
        }

        for (ubicacionNeo in caminoMasCorto) {
            val ubicacionJPA = recuperarPorNombre(ubicacionNeo.nombre)
            vector.historialMovimientos.add(ubicacionJPA.nombre)


            moverDefinitivamenteAlVectorYContagiar(vector, ubicacionJPA)

        }
    }

    override fun estanConectadasPorTipoDeCamino(
        nombreUbicacion1: String,
        nombreUbicacion2: String,
        tipoCamino: String
    ): Boolean {
        return ubicacionNeoDAO.verificarConexiones(nombreUbicacion1, nombreUbicacion2, tipoCamino)
    }
}

