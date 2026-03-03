package gui

import android.content.Context
import android.location.Geocoder
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.maps.android.SphericalUtil
import DTO.InserzioneTemp


val listaInserzioniFintoDB = listOf(
    // 1. Centro Storico
    InserzioneTemp("Pantheon", LatLng(41.8986, 12.4769),"PLACEHOLDER","1"),

    // 2. Snodo Ferroviario
    InserzioneTemp("Stazione Termini", LatLng(41.9009, 12.5020),"PLACEHOLDER","2"),

    // 3. Monumento Iconico
    InserzioneTemp("Colosseo", LatLng(41.8902, 12.4922),"PLACEHOLDER","3"),

    // 4. Area Vaticano
    InserzioneTemp("San Pietro / Vaticano", LatLng(41.9029, 12.4534),"PLACEHOLDER", "4" ),

    // 5. Movida / Centro Storico
    InserzioneTemp("Trastevere", LatLng(41.8883, 12.4701),"PLACEHOLDER","?"),

    // 6. Parco Principale
    InserzioneTemp("Villa Borghese", LatLng(41.9129, 12.4877),"PLACEHOLDER","?"),

    // 7. Centro Nord
    InserzioneTemp("Piazza del Popolo", LatLng(41.9107, 12.4763),"PLACEHOLDER","?"),

    // 8. Basilica Sud-Est
    InserzioneTemp("San Giovanni in Laterano", LatLng(41.8863, 12.5097),"PLACEHOLDER","?"),

    // 9. Quartiere Tipico
    InserzioneTemp("Testaccio", LatLng(41.8767, 12.4745),"PLACEHOLDER","?"),

    // 10. Quartiere Moderno/Industriale
    InserzioneTemp("Ostiense / Garbatella", LatLng(41.8710, 12.4820),"PLACEHOLDER","?"),

    // 11. Quartiere Residenziale/Uffici
    InserzioneTemp("Prati", LatLng(41.9100, 12.4630),"PLACEHOLDER","?"),

    // 12. Quartiere "Bene" Nord
    InserzioneTemp("Parioli", LatLng(41.9271, 12.4938),"PLACEHOLDER","?"),

    // 13. Punto Panoramico Nord-Ovest
    InserzioneTemp("Monte Mario", LatLng(41.9248, 12.4455),"PLACEHOLDER","?"),

    // 14. Quartiere Business Sud
    InserzioneTemp("Laghetto dell'EUR", LatLng(41.8286, 12.4646),"PLACEHOLDER","?"),

    // 15. Zona Universitaria / Residenziale
    InserzioneTemp("Piazza Bologna / Nomentano", LatLng(41.9180, 12.5180),"PLACEHOLDER","?"),

    // 16. Quartiere Alternativo Est
    InserzioneTemp("Pigneto", LatLng(41.8890, 12.5280),"PLACEHOLDER","?"),

    // 17. Residenziale Ovest
    InserzioneTemp("Monteverde", LatLng(41.8760, 12.4550),"PLACEHOLDER","?"),

    // 18. Sport / Nord
    InserzioneTemp("Stadio Olimpico", LatLng(41.9340, 12.4547),"PLACEHOLDER","?"),

    // 19. Zona Universitaria Sapienza
    InserzioneTemp("San Lorenzo", LatLng(41.8990, 12.5150),"PLACEHOLDER","?"),

    // 20. Parco Archeologico Sud
    InserzioneTemp("Parco dell'Appia Antica", LatLng(41.8550, 12.5300),"PLACEHOLDER","?")
)
// Funzione sospesa per non bloccare la UI mentre cerca
suspend fun cercaCoordinate(context: Context, locationName: String): LatLng? {
    return withContext(Dispatchers.IO) { // Esegue in background
        try {
            val geocoder = Geocoder(context)
            // Chiede a Google max 1 risultato per quel nome
            // Nota: getFromLocationName è lo standard per fare questo
            val addresses = geocoder.getFromLocationName(locationName, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                LatLng(address.latitude, address.longitude)
            } else {
                null // Nessun risultato trovato
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null // Errore di rete o altro
        }
    }
}


suspend fun cercaCoordinateConVerifica(
    context: Context,
    indirizzoInserito: String,
    cittaInserita: String,
    comuneInserito: String
): LatLng? {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context)
            // Creiamo la stringa di ricerca completa per Google
            val stringaRicerca = "$indirizzoInserito, $comuneInserito, $cittaInserita"
            val addresses = geocoder.getFromLocationName(stringaRicerca, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]

                // 1. Raccogliamo TUTTI i dati geografici che Google ha per questo punto esatto
                // (città, quartiere, provincia, regione) e li mettiamo in un'unica stringa minuscola
                val datiUfficialiGoogle = listOfNotNull(
                    address.locality,
                    address.subLocality,
                    address.subAdminArea,
                    address.adminArea
                ).joinToString(" ").lowercase()

                // 2. Prepariamo le parole dell'utente (minuscole e senza spazi extra)
                val cittaUtente = cittaInserita.lowercase().trim()
                val comuneUtente = comuneInserito.lowercase().trim()

                // 3. IL CONTROLLO SPIETATO:
                // I dati ufficiali di Google contengono la parola inserita in 'città'?
                val cittaOk = datiUfficialiGoogle.contains(cittaUtente)
                // I dati ufficiali di Google contengono la parola inserita in 'comune'?
                val comuneOk = datiUfficialiGoogle.contains(comuneUtente)

                // Usiamo && (AND): DEVONO ESSERE VERI ENTRAMBI!
                if (cittaOk && comuneOk) {
                    LatLng(address.latitude, address.longitude)
                } else {
                    android.util.Log.e("GEOCODER", "Incongruenza! Dati trovati: $datiUfficialiGoogle. Utente ha cercato: $cittaUtente, $comuneUtente")
                    null // Se l'utente ha mischiato Roma e Napoli, uno dei due 'contains' fallirà!
                }

            } else {
                null // Nessun risultato
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun eseguiRicerca(luogo:String, scope: CoroutineScope, context:Context, cameraPositionState: CameraPositionState) {
   // active = false // Chiude la tendina
    scope.launch {
        // 1. Cerca le coordinate (in background)
        val coordinateTrovate = cercaCoordinate(context, luogo)

        if (coordinateTrovate != null) {
            // 2. Se trovate, muovi la camera con un'animazione fluida
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(coordinateTrovate, 12f), // 12f è un buon zoom cittadino
                durationMs = 1500 // Ci mette 1.5 secondi ad arrivare (effetto volo)
            )
        } else {
            // Qui potresti mostrare un Toast "Città non trovata"
            Toast.makeText(context, "Luogo non trovato", Toast.LENGTH_SHORT).show()
        }
    }
}


// Restituisce la distanza in Metri (o Km se dividi)
fun calcolaDistanza(puntoA: LatLng, puntoB: LatLng): String {
    // Questa funzione calcola la distanza sulla curvatura della terra
    val distanzaMetri = SphericalUtil.computeDistanceBetween(puntoA, puntoB)

    return if (distanzaMetri > 1000) {
        String.format("%.2f km", distanzaMetri / 1000)
    } else {
        "${distanzaMetri.toInt()} metri"
    }
}

data class SuggerimentoPlace(
    val id: String,
    val testoPrincipale: String, // Es: "Colosseo"
    val testoSecondario: String  // Es: "Piazza del Colosseo, 1, Roma"
)

