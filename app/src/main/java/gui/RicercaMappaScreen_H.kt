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

suspend fun cercaCoordinate(context: Context, locationName: String): LatLng? {
    return withContext(Dispatchers.IO) { // Esegue in background
        try {
            val geocoder = Geocoder(context)
            val addresses = geocoder.getFromLocationName(locationName, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                LatLng(address.latitude, address.longitude)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
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
            val stringaRicerca = "$indirizzoInserito, $comuneInserito, $cittaInserita"
            val addresses = geocoder.getFromLocationName(stringaRicerca, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]

                val datiUfficialiGoogle = listOfNotNull(
                    address.locality,
                    address.subLocality,
                    address.subAdminArea,
                    address.adminArea
                ).joinToString(" ").lowercase()

                val cittaUtente = cittaInserita.lowercase().trim()
                val comuneUtente = comuneInserito.lowercase().trim()

                val cittaOk = datiUfficialiGoogle.contains(cittaUtente)
                val comuneOk = datiUfficialiGoogle.contains(comuneUtente)

                if (cittaOk && comuneOk) {
                    LatLng(address.latitude, address.longitude)
                } else {
                    android.util.Log.e("GEOCODER", "Incongruenza! Dati trovati: $datiUfficialiGoogle. Utente ha cercato: $cittaUtente, $comuneUtente")
                    null
                }

            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun eseguiRicerca(luogo:String, scope: CoroutineScope, context:Context, cameraPositionState: CameraPositionState) {
    scope.launch {
        val coordinateTrovate = cercaCoordinate(context, luogo)

        if (coordinateTrovate != null) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(coordinateTrovate, 12f),
                durationMs = 1500
            )
        } else {
            Toast.makeText(context, "Luogo non trovato", Toast.LENGTH_SHORT).show()
        }
    }
}

data class SuggerimentoPlace(
    val id: String,
    val testoPrincipale: String,
    val testoSecondario: String
)

