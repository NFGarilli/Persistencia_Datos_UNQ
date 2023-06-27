package ar.edu.unq.eperdemic.exceptions

class UbicacionNoAlcanzableException(nombreDeUbicacion: String) :
    Exception("No se puede alcanzar la ubicacion '$nombreDeUbicacion'") {
}