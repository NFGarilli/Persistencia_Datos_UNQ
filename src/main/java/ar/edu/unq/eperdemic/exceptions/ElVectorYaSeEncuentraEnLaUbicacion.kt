package ar.edu.unq.eperdemic.exceptions

class ElVectorYaSeEncuentraEnLaUbicacion(nombreUbicacion: String) :
    Exception("El vector ya se encuentra en la ubicacion: '$nombreUbicacion'") {
}