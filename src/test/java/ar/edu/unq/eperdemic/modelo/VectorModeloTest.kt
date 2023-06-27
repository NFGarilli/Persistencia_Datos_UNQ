package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.dado.Dado
import ar.edu.unq.eperdemic.modelo.mutaciones.DadoMutaciones
import ar.edu.unq.eperdemic.modelo.mutaciones.MutacionBioalteracionGenetica
import ar.edu.unq.eperdemic.modelo.mutaciones.MutacionSupresionBiomecanica
import ar.edu.unq.eperdemic.modelo.tipoDeVector.Animal
import ar.edu.unq.eperdemic.modelo.tipoDeVector.Humano
import ar.edu.unq.eperdemic.modelo.tipoDeVector.Insecto
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VectorModeloTest {
    private lateinit var patogeno: Patogeno
    private lateinit var especie: Especie
    private lateinit var especiePolenta: Especie
    private lateinit var especieConCapacidadMuyBaja: Especie
    private lateinit var ubicacionJPA: UbicacionJPA
    private lateinit var humano: Humano
    private lateinit var animal: Animal
    private lateinit var insecto: Insecto
    private lateinit var humanoAInfectar: Humano
    private lateinit var mecaViruela: Especie

    @BeforeEach
    fun setup() {
        patogeno = Patogeno("Virus")
        especie = Especie(patogeno, "VirusPolenta", "Argentum", 20)
        especiePolenta = Especie(patogeno, "VirusPolenta", "Argentum", 50)
        especieConCapacidadMuyBaja = Especie(patogeno, "VirusBajo", "Argentum", 1)
        ubicacionJPA = UbicacionJPA("Ranelagh")
        humano = Humano(Vector.TipoDeVector.Humano, ubicacionJPA)
        animal = Animal(Vector.TipoDeVector.Animal, ubicacionJPA)
        insecto = Insecto(Vector.TipoDeVector.Insecto, ubicacionJPA)
        patogeno.agregarEspecie(especie)
        humanoAInfectar = Humano(Vector.TipoDeVector.Humano, ubicacionJPA)
        mecaViruela = Especie(patogeno, "meca", "Ur", 25, 60, 30)

    }

    private fun vectorInfectaYContagiaA(vectorEmisor: Vector, listaDeVectores: List<Vector>, especie: Especie) {
        vectorEmisor.infectar(especie)
        vectorEmisor.contagiar(listaDeVectores)
    }

    @Test
    fun unVectorHumanoIntentaContagiarAUnAnimalYNoPuedo() {
        val listaDeVectores = listOf(animal)

        humano.infectar(especiePolenta)
        humano.contagiar(listaDeVectores)

        Assertions.assertFalse(animal.estaInfectado())
        Assertions.assertFalse(animal.estaInfectadoDe(especie))
    }

    @Test
    fun unInsectoNoPuedeContagiarAOtroInsecto() {
        val otroInsecto = Insecto(Vector.TipoDeVector.Insecto, ubicacionJPA)
        val listaDeVectores = listOf(otroInsecto)

        vectorInfectaYContagiaA(insecto, listaDeVectores, especie)

        Assertions.assertFalse(otroInsecto.estaInfectado())
        Assertions.assertFalse(otroInsecto.estaInfectadoDe(especie))
    }

    @Test
    fun unInsectoPuedeContagiarAUnHumanoYAUnAnimal() {
        val listaDeVectores = listOf(animal, humano)

        vectorInfectaYContagiaA(insecto, listaDeVectores, especie)

        Assertions.assertTrue(animal.estaInfectado())
        Assertions.assertTrue(animal.estaInfectadoDe(especie))
        Assertions.assertTrue(humano.estaInfectado())
        Assertions.assertTrue(humano.estaInfectadoDe(especie))
    }


    @Test
    fun unVectorHumanoIntentaContagiarAOtroHumanoConUnaEspecieQueInfectaSiOSiYPuede() {
        val listaDeVectores = listOf(humanoAInfectar)
        patogeno.agregarEspecie(especiePolenta)

        vectorInfectaYContagiaA(humano, listaDeVectores, especiePolenta)

        Assertions.assertTrue(humanoAInfectar.estaInfectado())
        Assertions.assertTrue(humanoAInfectar.estaInfectadoDe(especiePolenta))
    }

    @Test
    fun unVectorHumanoIntentaContagiarAOtroHumanoConUnaEspecieConUnaCapacidadDeContagioTanBajaQueNuncaPuedeInfectarAOtro() {
        val listaDeVectores = listOf(humanoAInfectar)
        patogeno.agregarEspecie(especieConCapacidadMuyBaja)

        vectorInfectaYContagiaA(humano, listaDeVectores, especieConCapacidadMuyBaja)

        Assertions.assertFalse(humanoAInfectar.estaInfectado())
        Assertions.assertFalse(humanoAInfectar.estaInfectadoDe(especieConCapacidadMuyBaja))
    }

    @Test
    fun unVectorHumanoIntentaContagiarAOtroHumanoConUnaEspecieConUnaCapacidadDeContagioDe15YSiElDadoTiraAltoLoContagia() {

        val listaDeVectores = listOf(humanoAInfectar)

        humano.infectar(especie)
        Dado.setEstrategia(Dado.EstrategiaDepuracion)
        Dado.EstrategiaDepuracion.setNumeroFijo(10)
        humano.contagiar(listaDeVectores)

        Assertions.assertTrue(humanoAInfectar.estaInfectado())
        Assertions.assertTrue(humanoAInfectar.estaInfectadoDe(especie))
        //Vuelvo a setear el dado en modo produccion.
        Dado.setEstrategia(Dado.EstrategiaProduccion)
    }

    @Test
    fun unVectorHumanoIntentaContagiarAOtroHumanoConUnaEspecieConUnaCapacidadDeContagioDe15YSiElDadoTiraBajoNoContagia() {
        val listaDeVectores = listOf(humanoAInfectar)
        humano.infectar(especie)
        Dado.setEstrategia(Dado.EstrategiaDepuracion)
        Dado.EstrategiaDepuracion.setNumeroFijo(2)

        humano.contagiar(listaDeVectores)
    }

    @Test
    fun vectorHumanoContagiaAOtroHumanoYMutaLaEspecie() {
        val mutacion = MutacionSupresionBiomecanica(100, mecaViruela)

        DadoMutaciones.setEstrategia(DadoMutaciones.EstrategiaDepuracion)
        DadoMutaciones.EstrategiaDepuracion.setMutacionFija(mutacion)

        mecaViruela.agregarMutacion(mutacion)

        val listaDeVectores = listOf(humanoAInfectar)

        humano.infectar(mecaViruela)
        humano.contagiar(listaDeVectores)

        val mutaciones = humano.mutaciones

        Assertions.assertTrue(humanoAInfectar.estaInfectadoDe(mecaViruela))
        Assertions.assertTrue(mutaciones.contains(mutacion))

    }

    @Test
    fun vectorHumanoContagiaAOtroHumanoYMutaConSupresionBiomecanicaEliminandoLasOtrasEspeciesDelVectorConDefensaMenorALaPotenciaDeLaMutacion() {

        mecaViruela.id = 1
        val roboRabia = Especie(patogeno, "robo", "Ur", 10, 60, 80)
        roboRabia.id = 3
        val cromaGripe = Especie(patogeno, "croma", "Ur", 10, 60, 30)
        cromaGripe.id = 2

        val mutacion = MutacionSupresionBiomecanica(35, mecaViruela)

        DadoMutaciones.setEstrategia(DadoMutaciones.EstrategiaDepuracion)
        DadoMutaciones.EstrategiaDepuracion.setMutacionFija(mutacion)

        mecaViruela.agregarMutacion(mutacion)

        val listaDeVectores = listOf(humanoAInfectar)

        humano.infectar(mecaViruela)
        humano.infectar(cromaGripe)
        humano.infectar(roboRabia)
        humano.contagiar(listaDeVectores)

        Assertions.assertTrue(humanoAInfectar.estaInfectadoDe(mecaViruela))
        Assertions.assertTrue(humano.tieneMutacion(mutacion))
        Assertions.assertTrue(humano.estaInfectadoDe(mecaViruela))
        Assertions.assertTrue(humano.estaInfectadoDe(roboRabia))
        Assertions.assertFalse(humano.estaInfectadoDe(cromaGripe))
    }

    @Test
    fun vectorHumanoContagiaAOtroHumanoMutaConSupresionBiomecanicaYCuandoUnAnimalIntentanContagiarloUnaEnfermedadConDefensaMenorALaPotenciaDeLaMutacionNoPuedeContagiarlo() {
        mecaViruela.id = 1
        val roboRabia = Especie(patogeno, "robo", "Ur", 30, 70, 10)
        roboRabia.id = 2
        val mutacion = MutacionSupresionBiomecanica(35, mecaViruela)

        DadoMutaciones.setEstrategia(DadoMutaciones.EstrategiaDepuracion)
        DadoMutaciones.EstrategiaDepuracion.setMutacionFija(mutacion)

        mecaViruela.agregarMutacion(mutacion)

        val listaDeVectores = listOf(humanoAInfectar)
        val listaDeVectores2 = listOf(humano)

        humano.infectar(mecaViruela)
        humano.contagiar(listaDeVectores)

        animal.infectar(roboRabia)
        animal.contagiar(listaDeVectores2)

        Assertions.assertFalse(humano.estaInfectadoDe(roboRabia))
    }

    @Test
    fun animalPuedeSerContagiadoPorHumanoBioalteracionGenetica() {
        val roboRabia = Especie(
            patogeno, "RoboRabia", "Ur",
            1000, 130
        )
        roboRabia.id = 100
        val mutacion = MutacionBioalteracionGenetica(Vector.TipoDeVector.Animal, roboRabia)

        DadoMutaciones.setEstrategia(DadoMutaciones.EstrategiaDepuracion)
        DadoMutaciones.EstrategiaDepuracion.setMutacionFija(mutacion)

        //La mutacion se deberia generar al contagiar otro vector?
        roboRabia.agregarMutacion(mutacion)

        //John es un vector de tipo humano, no puede contagiar animales
        Assertions.assertFalse(animal.puedeSerContagiadoPorTipoDeVector(humano))
        // pero contrae RoboRabia,
        humano.infectar(roboRabia)

        // la contagia a otro humano
        val listaDeVectores = listOf(humanoAInfectar)
        humano.contagiar(listaDeVectores)

        // y muta contrayendo la mutacion de Bioalteracion genetica de tipo animal.
        Assertions.assertTrue(humano.mutaciones.contains(mutacion))

        // A partir de ese momento, John podra contagiar animales con RoboRabia.
        val animalAInfectar = Animal(Vector.TipoDeVector.Animal, ubicacionJPA)
        val listaAnimal = listOf(animalAInfectar)
        humano.contagiar(listaAnimal)

        Assertions.assertTrue(humano.mutaciones.contains(mutacion))

        //animal con RoboRabia.
        Assertions.assertTrue(animalAInfectar.estaInfectadoDe(roboRabia))
    }

    @Test
    fun noPuedeContagiarUnAnimalConOtraEspecieQueNoSeaRoboRabia() {
        animalPuedeSerContagiadoPorHumanoBioalteracionGenetica()

        mecaViruela.id = 200

        val vaca = Animal(Vector.TipoDeVector.Animal, ubicacionJPA)
        val listaVaca = listOf(vaca)
        Assertions.assertFalse(vaca.puedeSerContagiadoPorTipoDeVector(humano))

        humano.infectar(mecaViruela)
        humano.contagiar(listaVaca)
        Assertions.assertFalse(vaca.puedeSerContagiadoPorTipoDeVector(humano))
        Assertions.assertFalse(vaca.estaInfectadoDe(mecaViruela))

    }

}