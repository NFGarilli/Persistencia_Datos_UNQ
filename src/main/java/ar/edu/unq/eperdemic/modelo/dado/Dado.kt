package ar.edu.unq.eperdemic.modelo.dado

import kotlin.random.Random

object Dado {
    interface EstrategiaNumerica {
        fun probabilidadDeContagioRandom(): Int
    }

    object EstrategiaProduccion : EstrategiaNumerica {
        override fun probabilidadDeContagioRandom(): Int = Random.nextInt(1, 11)
    }

    object EstrategiaDepuracion : EstrategiaNumerica {
        private var numeroFijo: Int = 1

        fun setNumeroFijo(numero: Int) {
            numeroFijo = numero
        }

        override fun probabilidadDeContagioRandom(): Int = numeroFijo
    }

    private var estrategia: EstrategiaNumerica = EstrategiaProduccion

    fun setEstrategia(estrategia: EstrategiaNumerica) {
        this.estrategia = estrategia
    }

    fun probabilidadDeContagioRandom(): Int {
        return estrategia.probabilidadDeContagioRandom()
    }
}
