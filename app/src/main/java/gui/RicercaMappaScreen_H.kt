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