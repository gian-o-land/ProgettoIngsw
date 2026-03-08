package DTO

import kotlinx.serialization.Serializable

@Serializable
data class FiltriRicercaDTO(
    val categoria: String = "Indifferente",
    val prezzoMin: String = "",
    val prezzoMax: String = "",
    val quadraturaMin: String = "",
    val quadraturaMax: String = "",
    val localiMin: String = "",
    val localiMax: String = "",
    val bagni: String = "",
    val piano: String = "Indifferente",
    val classeEnergetica: String = "",
    val arredamento: String = "Indifferente",
    val ascensore: Boolean = false,
    val cucina: Boolean = false,
    val piscina: Boolean = false,
    val cantina: Boolean = false,
    val balcone: Boolean = false,
    val terrazzo: Boolean = false
)