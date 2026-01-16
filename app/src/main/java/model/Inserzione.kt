package model
import com.google.android.gms.maps.model.LatLng

class Inserzione(private val idInserzione: String, private val quadratura: Int, private val nBagni: Int, private val nLocali: Int, private val piano: Int,
                 private val categoria: D_catInser, private val arredamento: D_arred, private val balcone: Boolean, private val ascensore: Boolean,
                 private val terrazzo: Boolean, private val cantina: Boolean, private val piscina: Boolean, private val cucina: Boolean, private val prezzo: Float,
                 private val classeEnergetica: D_energ, private val citta: String, private val comune: String, private val indirizzo: String, private val agenteCreatore: String,
                 private val latitudine: Double, private val longitudine: Double
) {

    fun getIdInserzione(): String{
        return idInserzione
    }

    fun getQuadratura(): Int {
        return quadratura
    }

    fun getNBagni(): Int {
        return nBagni
    }

    fun getNLocali(): Int {
        return nLocali
    }

    fun getPiano(): Int {
        return piano
    }

    fun getCategoria(): D_catInser {
        return categoria
    }

    fun getArredamento(): D_arred {
        return arredamento
    }

    fun getBalcone(): Boolean {
        return balcone
    }

    fun getAscensore(): Boolean {
        return ascensore
    }

    fun getTerrazzo(): Boolean {
        return terrazzo
    }

    fun getCantina(): Boolean {
        return cantina
    }

    fun getPiscina(): Boolean {
        return piscina
    }

    fun getCucina(): Boolean {
        return cucina
    }

    fun getPrezzo(): Float {
        return prezzo
    }

    fun getClasseEnergetica(): D_energ {
        return classeEnergetica
    }

    fun getCitta(): String {
        return citta
    }

    fun getComune(): String {
        return comune
    }

    fun getIndirizzo(): String {
        return indirizzo
    }

    fun getAgenteCreatore(): String {
        return agenteCreatore
    }

    fun getLatitudine(): Double {
        return latitudine
    }

    fun getLongitudine(): Double {
        return longitudine
    }

}



data class InserzioneTemp(
    val nome: String,
    val coordinate: LatLng,
    val nomeImmagine:String ,//per inserzione temporanea è solo 1 immagine
    val idInserzione: String
)