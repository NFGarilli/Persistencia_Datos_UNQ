package ar.edu.unq.eperdemic.exceptions

class TipoCaminoInvalidoException(tipoCamino: String) : Exception("Tipo de camino incorrecto: $tipoCamino")
