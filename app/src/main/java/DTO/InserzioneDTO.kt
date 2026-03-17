package DTO
import kotlinx.serialization.Serializable

@Serializable
data class InserzioneDTO(
    val idInserzione: String, val quadratura: Int, val nBagni: Int, val nLocali: Int, val piano: Int,
    val categoria: D_catInser, val arredamento: D_arred, val balcone: Boolean, val ascensore: Boolean,
    val terrazzo: Boolean, val cantina: Boolean, val piscina: Boolean, val cucina: Boolean, val prezzo: Float,
    val classeEnergetica: D_energ, val citta: String, val comune: String, val indirizzo: String, val agenteCreatore: String,
    val latitudine: Double, val longitudine: Double,
    val immagini: MutableList<ImmagineDTO>,
    val descrizione : String
)