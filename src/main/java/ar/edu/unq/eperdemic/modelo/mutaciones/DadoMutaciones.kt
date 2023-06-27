package ar.edu.unq.eperdemic.modelo.mutaciones

object DadoMutaciones {

    interface EstrategiaMutacion {
        fun generarMutacionRandom(mutaciones: MutableList<Mutacion>): Mutacion
    }

    object EstrategiaProduccion : DadoMutaciones.EstrategiaMutacion {
        override fun generarMutacionRandom(mutaciones: MutableList<Mutacion>): Mutacion {
            return mutaciones.random()
        }
    }

    object EstrategiaDepuracion : DadoMutaciones.EstrategiaMutacion {
        private lateinit var mutacionFija: Mutacion

        fun setMutacionFija(mutacion: Mutacion) {
            mutacionFija = mutacion
        }

        override fun generarMutacionRandom(mutaciones: MutableList<Mutacion>): Mutacion {
            return mutacionFija
        }
    }

    private var estrategia: DadoMutaciones.EstrategiaMutacion = DadoMutaciones.EstrategiaProduccion

    fun setEstrategia(estrategia: DadoMutaciones.EstrategiaMutacion) {
        this.estrategia = estrategia
    }

    fun generarMutacionRandom(mutaciones: MutableList<Mutacion>): Mutacion {
        return estrategia.generarMutacionRandom(mutaciones)
    }
}