package ar.edu.unq.eperdemic.service.neo4j

import ar.edu.unq.eperdemic.exceptions.TipoCaminoInvalidoException
import ar.edu.unq.eperdemic.exceptions.UbicacionesYaConectadasException
import ar.edu.unq.eperdemic.modelo.UbicacionNeo
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.service.BaseTestServices
import ar.edu.unq.eperdemic.utils.DataServiceImpl
import junit.framework.TestCase.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UbicacionNeoServiceTest : BaseTestServices() {

    fun crearUbicacionesParaTestDeConectados() {
        ubicacionService.crearUbicacion("A", 0.0, 0.0)
        ubicacionService.crearUbicacion("B", 0.0, 0.0)
        ubicacionService.crearUbicacion("C", 0.0, 0.0)
        ubicacionService.crearUbicacion("D", 0.0, 0.0)
        ubicacionService.crearUbicacion("E", 0.0, 0.0)
    }

    fun setUpConectadosConCamino(tipoDeCamino: String) {
        crearUbicacionesParaTestDeConectados()
        ubicacionService.conectar("A", "B", tipoDeCamino)
        ubicacionService.conectar("B", "C", tipoDeCamino)
        ubicacionService.conectar("C", "D", tipoDeCamino)
        ubicacionService.conectar("A", "E", tipoDeCamino)
        ubicacionService.conectar("E", "D", tipoDeCamino)
    }

    @Test
    fun crearUbicacionNeo() {
        var ubicacionNeo = UbicacionNeo("Canada")
        var ubicacionCreada = ubicacionService.crearUbicacion("Canada", 0.0, 0.0)

        assertNotNull(ubicacionCreada.id)
        assertEquals(ubicacionNeo.nombre, ubicacionCreada.nombre)
    }

    @Test
    fun crearUbicacionConNombreRepetidoNeo() {
        ubicacionService.crearUbicacion("Canada", 0.0, 0.0)
        assertThrows<Exception> {
            ubicacionService.crearUbicacion("Canada", 0.0, 0.0)
        }
    }
    
    fun conectar() {
        ubicacionService.crearUbicacion("Ranelagh", 0.0, 0.0)
        ubicacionService.crearUbicacion("Bera", 0.0, 0.0)
        ubicacionService.conectar("Ranelagh", "Bera", "AEREO")
        ubicacionService.conectar("Ranelagh", "Bera", "Maritimo")
        ubicacionService.conectar("Ranelagh", "Bera", "Terrestre")
    }

    @Test
    fun conectarUnaUbicacionConSiMismaTiraExcepcion() {
        //Set up
        ubicacionService.crearUbicacion("Ranelagh", 0.0, 0.0)
        assertThrows<Exception> {
            ubicacionService.conectar("Ranelagh", "Ranelagh", "AEREO")
        }
    }

    @Test
    fun conectados() {
        //Setup
        ubicacionService.crearUbicacion("Ranelagh", 0.0, 0.0)
        ubicacionService.crearUbicacion("Berazachusets", 0.0, 0.0)
        ubicacionService.crearUbicacion("Lagartolandia", 0.0, 0.0)
        ubicacionService.crearUbicacion("EstoEsBoka", 0.0, 0.0)

        ubicacionService.conectar("Ranelagh", "Berazachusets", "AEREO")
        ubicacionService.conectar("Ranelagh", "Lagartolandia", "Maritimo")
        ubicacionService.conectar("Ranelagh", "EstoEsBoka", "Terrestre")

        var resultado = ubicacionService.conectados("Ranelagh")

        var ubicacionesConectadas: List<UbicacionNeo> = listOf(
            UbicacionNeo("EstoEsBoka"),
            UbicacionNeo("Lagartolandia"),
            UbicacionNeo("Berazachusets")
        )

        assertEquals(resultado, ubicacionesConectadas)
    }

    @Test
    fun moverMasCortoAlExistirDosCaminosParaLlegarAUnaUbicacionDestinoUtilizaEfectivamenteElMasCorto() {
        //Setup
        setUpConectadosConCamino("Terrestre")


        //Se crea un vector con la ubicacion inicial en ubicacion "A"
        var a = ubicacionService.recuperarPorNombre("A")
        var personita = vectorService.crearVector(Vector.TipoDeVector.Humano, a.id!!)


        var d = ubicacionService.recuperarPorNombre("D")

        //Se lo mueve de la manera mas corta hasta D.
        ubicacionService.moverMasCorto(personita.id!!, d.nombre)

        var dd = ubicacionService.recuperarPorNombre("D")
        val vectorPersona = vectorService.recuperarVector(personita.id!!)

        // Verificar que el vector se haya movido a la ubicación de destino
        assertEquals(dd.toString(), vectorPersona.ubicacionJPA.toString())

        //Verificar que el vector haya recorrido el camino que debia de recorrer.
        //EL VECTOR NO SE MUEVE A LA UBICACION A PORQUE INICIALMENTE YA SE ENCONTRABA AHI!
        val historialMovimientos = vectorPersona.historialMovimientos
        val esperado = listOf("E", "D")
        assertEquals(esperado, historialMovimientos)
    }

    @Test
    fun moverMasCortoElVectorNoPuedeUtilizarElCaminoMasCortoPorLoCualUtilizaElCaminomasLargo() {
        //Setup
        //Creo Ubicaciones
        crearUbicacionesParaTestDeConectados()

        //Conecto las ubicaciones
        ubicacionService.conectar("A", "B", "Terrestre")
        ubicacionService.conectar("B", "C", "Terrestre")
        ubicacionService.conectar("C", "D", "Terrestre")
        ubicacionService.conectar("A", "E", "Aereo")
        ubicacionService.conectar("E", "D", "Terrestre")
        // Se crea un camino asi: A --> B --> C --> D
        //                         \-----> E ------/
        //Por lo cual, el camino mas corto es ir de A a E y de ahí a D, pero como E es de tipo AEREO, Utiliza la ruta A --> B --> C --> D

        //Se crea un vector con la ubicacion inicial en ubicacion "A"
        var a = ubicacionService.recuperarPorNombre("A")
        var personita = vectorService.crearVector(Vector.TipoDeVector.Humano, a.id!!)

        var d = ubicacionService.recuperarPorNombre("D")

        //Se lo mueve de la manera mas corta hasta D.
        ubicacionService.moverMasCorto(personita.id!!, d.nombre)

        var dd = ubicacionService.recuperarPorNombre("D")
        val vectorPersona = vectorService.recuperarVector(personita.id!!)

        // Verificar que el vector se haya movido a la ubicación de destino
        assertEquals(dd.toString(), vectorPersona.ubicacionJPA.toString())

        //Verificar que el vector haya recorrido el camino que debia de recorrer.
        val historialMovimientos = vectorPersona.historialMovimientos
        //EL VECTOR NO SE MUEVE A LA UBICACION A PORQUE INICIALMENTE YA SE ENCONTRABA AHI!
        val esperado = listOf("B", "C", "D")
        assertEquals(esperado, historialMovimientos)
    }

    @Test
    fun moverMasCortoElVectorNoPuedeUtilizarNingunCaminoPorLoCualTiraUnaExcepcion() {

        //Setup
        setUpConectadosConCamino("Aereo")


        //Se crea un vector con la ubicacion inicial en ubicacion "A"
        var a = ubicacionService.recuperarPorNombre("A")
        var personita = vectorService.crearVector(Vector.TipoDeVector.Humano, a.id!!)

        var d = ubicacionService.recuperarPorNombre("D")

        assertThrows<Exception> {
            ubicacionService.moverMasCorto(personita.id!!, d.nombre)
        }

    }

    @Test
    fun moverMasCortoElVectorYaSeEncuentraEnLaUbicacionDestinoPorLoCualNoSeMueve() {

        //Setup
        setUpConectadosConCamino("Terrestre")
        var persona = DataServiceImpl.persona

        //Se crea un vector con la ubicacion inicial en ubicacion "D"
        var d = ubicacionService.recuperarPorNombre("D")
        var personita = vectorService.crearVector(Vector.TipoDeVector.Humano, d.id!!)

        //Se mueve mas corto a la ubicacion destino "D" que es igual a la inicial del vector tira excepcion.
        assertThrows<Exception> {
            ubicacionService.moverMasCorto(personita.id!!, d.nombre)
        }

        // Verificación
        assertEquals(d.nombre, personita.ubicacionJPA?.nombre)
        assertFalse(personita.historialMovimientos.contains(d.nombre))
    }

    @Test
    fun ubicacionesYaConectadasException() {
        conectar()
        assertThrows<UbicacionesYaConectadasException> {
            ubicacionService.conectar("Ranelagh", "Bera", "AEREO")
        }
    }

    @Test
    fun tipoCaminoInvalidoException() {
        assertThrows<TipoCaminoInvalidoException> {
            ubicacionService.conectar("Ranelagh", "Bera", "Subterraneo")
        }
    }

    @Test
    fun verificarConexiones() {
        conectar()
        val ubicacionesConectadas =
            ubicacionService.estanConectadasPorTipoDeCamino("Ranelagh", "Bera", "Aereo")
        assertTrue(ubicacionesConectadas)
    }

    @Test
    fun conectarUbicacionesConTipoCaminoInvalidoLanzaExcepcion() {
        ubicacionService.crearUbicacion("Ranelagh", 0.0, 0.0)
        ubicacionService.crearUbicacion("Bera", 0.0, 0.0)
        assertThrows<TipoCaminoInvalidoException> {
            ubicacionService.conectar("Ranelagh", "Bera", "Subterraneo")
        }
    }

    @Test
    fun buscarConexionesPorTipoCaminoInexistenteDevuelveListaVacia() {
        conectar()
        val ubicacionesConectadas = ubicacionService.estanConectadasPorTipoDeCamino("Ranelagh", "Bera", "Subterraneo")
        assertFalse(ubicacionesConectadas)
    }


}