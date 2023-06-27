package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.mutaciones.DadoMutaciones
import ar.edu.unq.eperdemic.modelo.mutaciones.Mutacion
import ar.edu.unq.eperdemic.modelo.mutaciones.MutacionSupresionBiomecanica
import org.junit.Assert
import org.junit.jupiter.api.Test

class DadoMutacionesTest {

    @Test
    fun dadoGeneraMutacionAleatoriaCorrectaEnModoProd() {
        // Configuramos la estrategia en modo producción
        DadoMutaciones.setEstrategia(DadoMutaciones.EstrategiaProduccion)

        val patogeno = Patogeno("Virus")
        val mecaViruela = Especie(patogeno, "meca", "Ur", 25, 60, 30)
        mecaViruela.id = 1
        val mutacion = MutacionSupresionBiomecanica(35, mecaViruela)
        var mutacion2 = MutacionSupresionBiomecanica(10, mecaViruela)
        var mutacion3 = MutacionSupresionBiomecanica(15, mecaViruela)

        val mutaciones = mutableListOf<Mutacion>(mutacion, mutacion2, mutacion3)

        // Realizamos una serie de tiradas y comprobamos que las mutaciones generadas están en la lista
        repeat(5) {
            val mutacionGenerada = DadoMutaciones.generarMutacionRandom(mutaciones = mutaciones)
            Assert.assertTrue(
                "La mutacion generada ($mutacionGenerada) está fuera de la lista",
                mutacionGenerada in mutaciones
            )
        }
    }

    @Test
    fun DadoGeneraMutacionFijaEnModoDepuracion() {

        val patogeno = Patogeno("Virus")
        val mecaViruela = Especie(patogeno, "meca", "Ur", 25, 60, 30)
        mecaViruela.id = 1
        val mutacion = MutacionSupresionBiomecanica(35, mecaViruela)
        var mutacion2 = MutacionSupresionBiomecanica(10, mecaViruela)
        var mutacion3 = MutacionSupresionBiomecanica(15, mecaViruela)

        val mutaciones = mutableListOf<Mutacion>(mutacion, mutacion2, mutacion3)

        // Configuramos la estrategia en modo depuración y establecemos la mutacion fija
        DadoMutaciones.setEstrategia(DadoMutaciones.EstrategiaDepuracion)
        DadoMutaciones.EstrategiaDepuracion.setMutacionFija(mutacion = mutacion)

        // Realizamos una serie de tiradas y comprobamos que siempre se genera el número fijo
        repeat(5) {
            val mutacionGenerada = DadoMutaciones.generarMutacionRandom(mutaciones = mutaciones)
            Assert.assertEquals(
                "La mutacion generada ($mutacionGenerada) no coincide con la mutacion fija",
                mutacion,
                mutacionGenerada
            )
        }
    }
}