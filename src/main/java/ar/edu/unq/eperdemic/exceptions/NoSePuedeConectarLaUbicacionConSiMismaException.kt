package ar.edu.unq.eperdemic.exceptions

class NoSePuedeConectarLaUbicacionConSiMismaException(nombreDeUbicacion1: String) :
    Exception("No se puede establecer una conexion entre '$nombreDeUbicacion1' y si misma")