package ar.edu.unq.eperdemic.exceptions

class ConsVacioException(clase: String, parametro: String) : Exception("No se puede crear una $clase sin $parametro")
