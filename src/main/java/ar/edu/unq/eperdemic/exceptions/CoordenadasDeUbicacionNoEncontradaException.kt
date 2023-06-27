package ar.edu.unq.eperdemic.exceptions

class CoordenadasDeUbicacionNoEncontradaException(
    message: String = "Las coordenadas de la ubicacion no coinciden con ningun distrito"
) : Exception(message)