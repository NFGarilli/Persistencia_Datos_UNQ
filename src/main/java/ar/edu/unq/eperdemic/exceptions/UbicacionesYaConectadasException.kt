package ar.edu.unq.eperdemic.exceptions

class UbicacionesYaConectadasException(nombreDeUbicacion1: String, nombreDeUbicacion2: String, tipoCamino: String) :
    Exception("La ubicación '$nombreDeUbicacion1' ya está conectada con '$nombreDeUbicacion2' por un camino '$tipoCamino'")