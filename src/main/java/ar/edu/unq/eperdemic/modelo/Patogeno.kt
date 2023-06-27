package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.exceptions.ConsVacioException
import java.io.Serializable
import javax.persistence.*

@Entity
class Patogeno : Serializable {

    var tipo: String

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var cantidadDeEspecies: Int = 0

    @OneToMany(mappedBy = "patogeno", cascade = [CascadeType.PERSIST], fetch = FetchType.EAGER, orphanRemoval = true)
    var especies: MutableList<Especie> = mutableListOf()

    constructor(tipo: String) {
        if (tipo.isNullOrBlank()) {
            throw ConsVacioException(javaClass.simpleName, tipo)
        }
        this.tipo = tipo
    }

    override fun toString(): String {
        return tipo
    }

    fun agregarEspecie(especie: Especie) {
        val especiesAnteriores = this.especies.size
        this.especies.add(especie)
        this.cantidadDeEspecies = especiesAnteriores + 1
    }


    fun crearEspecie(nombreEspecie: String, paisDeOrigen: String, capacidadDeContagio: Int): Especie {
        val especie = Especie(this, nombreEspecie, paisDeOrigen, capacidadDeContagio)
        agregarEspecie(especie)
        return especie
    }
}