package ar.edu.unq.eperdemic.service


import ar.edu.unq.eperdemic.persistencia.dao.mongo.UbicacionMongoDAO
import ar.edu.unq.eperdemic.services.*
import ar.edu.unq.eperdemic.utils.DataService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired

open class BaseTestServices {

    @Autowired
    protected lateinit var dataService: DataService

    @Autowired
    protected lateinit var patogenoService: PatogenoService

    @Autowired
    protected lateinit var especieService: EspecieService

    @Autowired
    protected lateinit var estadisticaService: EstadisticaService

    @Autowired
    protected lateinit var ubicacionService: UbicacionService

    @Autowired
    protected lateinit var vectorService: VectorService

    @Autowired
    protected lateinit var mutacionService: MutacionService

    @Autowired
    protected lateinit var distritoService: DistritoService
    @Autowired
    lateinit var ubicacionMongoDAO : UbicacionMongoDAO
    @BeforeEach
    fun crearModelo() {
        dataService.crearSetDeDatosIniciales()
    }

    @AfterEach
    fun eliminarModelo() {
        dataService.eliminarTodo()
    }
}
