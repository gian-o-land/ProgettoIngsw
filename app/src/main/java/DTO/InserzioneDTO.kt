package DTO
import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable

@Serializable
data class Inserzione(
    val idInserzione: String, val quadratura: Int, val nBagni: Int, val nLocali: Int, val piano: Int,
    val categoria: D_catInser, val arredamento: D_arred, val balcone: Boolean, val ascensore: Boolean,
    val terrazzo: Boolean, val cantina: Boolean, val piscina: Boolean, val cucina: Boolean, val prezzo: Float,
    val classeEnergetica: D_energ, val citta: String, val comune: String, val indirizzo: String, val agenteCreatore: String,
    val latitudine: Double, val longitudine: Double,
    val immagini: MutableList<ImmagineDTO>
)



data class InserzioneTemp(
    val nome: String,
    val coordinate: LatLng,
    val nomeImmagine:String ,//per inserzione temporanea è solo 1 immagine
    val idInserzione: String
)