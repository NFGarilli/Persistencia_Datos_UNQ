package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.dado.Dado
import org.junit.Assert
import org.junit.jupiter.api.Test

class DadoTest {

    @Test
    fun dado_genera_numero_aleatorio_correcto_en_modo_produccion() {
        // Configuramos la estrategia en modo producción
        Dado.setEstrategia(Dado.EstrategiaProduccion)

        // Realizamos una serie de tiradas y comprobamos que los números generados están en el rango esperado
        repeat(100) {
            val numeroGenerado = Dado.probabilidadDeContagioRandom()
            Assert.assertTrue(
                "El número generado ($numeroGenerado) está fuera del rango esperado",
                numeroGenerado in 1..10
            )
        }
    }

    @Test
    fun dado_genera_numero_fijo_en_modo_depuracion() {
        // Configuramos la estrategia en modo depuración y establecemos el número fijo a 7
        Dado.setEstrategia(Dado.EstrategiaDepuracion)
        Dado.EstrategiaDepuracion.setNumeroFijo(7)

        // Realizamos una serie de tiradas y comprobamos que siempre se genera el número fijo
        repeat(100) {
            val numeroGenerado = Dado.probabilidadDeContagioRandom()
            Assert.assertEquals(
                "El número generado ($numeroGenerado) no coincide con el número fijo (7)",
                7,
                numeroGenerado
            )
        }

    }
}