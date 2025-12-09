package gui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.material3.SearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ListItem
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RicercaMappaScreen(navController : NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // STATI
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    // STATO DELLA CAMERA
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(41.9028, 12.4964), 10f)
    }

    var puntoSelezionato by remember { mutableStateOf<LatLng?>(null) }
    var raggioKm by remember { mutableFloatStateOf(10f) }

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. LA MAPPA (Deve chiudersi PRIMA della SearchBar)
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            // Assicurati che jsonStyleChiaro sia definito o rimuovi questa riga se non ce l'hai
            properties = remember { MapProperties(mapStyleOptions = MapStyleOptions(jsonStyleChiaro)) },
            onMapClick = { coordinataCliccata ->
                puntoSelezionato = coordinataCliccata
            }
        ) {
            // DENTRO la mappa ci vanno solo i Marker
            puntoSelezionato?.let { punto ->
                Marker(
                    state = MarkerState(position = punto),
                    title = "Posizione Scelta",
                    snippet = "Lat: ${punto.latitude}, Lng: ${punto.longitude}",
                )

                Circle(
                    center = punto,
                    // Convertiamo km in metri perché Maps usa i metri
                    radius = (raggioKm * 1000).toDouble(),
                    strokeColor = androidx.compose.ui.graphics.Color.Blue, // Bordo Blu
                    strokeWidth = 3f, // Spessore bordo
                    fillColor = androidx.compose.ui.graphics.Color.Blue.copy(alpha = 0.2f) // Interno azzurro trasparente
                )

            }
        } // <--- QUESTA PARENTESI CHIUSA QUI È FONDAMENTALE!

        // 2. LA BARRA DI RICERCA (Fuori dalla mappa, ma dentro il Box)
        SearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = {
                active = false
                // Assicurati di avere la funzione eseguiRicerca definita
                eseguiRicerca(it, scope, context, cameraPositionState)
            },
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text("Cerca città (es. Milano)...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (active) {
                    IconButton(onClick = {
                        if (query.isNotEmpty()) query = "" else active = false
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Chiudi")
                    }
                }
            },
            modifier = Modifier.align(Alignment.TopCenter) // Ora funziona perché è figlio diretto di Box
        ) {
            val suggerimenti = listOf("Roma", "Milano", "Napoli")
            val suggerimentiFiltrati = suggerimenti.filter { it.contains(query, ignoreCase = true) }

            suggerimentiFiltrati.forEach { item ->
                ListItem(
                    headlineContent = { Text(item) },
                    leadingContent = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    modifier = Modifier.clickable {
                        query = item
                        active = false
                        eseguiRicerca(item, scope, context, cameraPositionState)
                    }
                )
            }
        }
        if (puntoSelezionato != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter) // Incolla in basso
                    .fillMaxWidth()
                    .padding(16.dp), // Un po' di margine dai bordi
                shape = MaterialTheme.shapes.medium, // Angoli arrotondati
                shadowElevation = 8.dp, // Ombretta per farla "galleggiare"
                color = mioBluScuro
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Testo che cambia mentre muovi lo slider
                    Text(
                        text = "Raggio di ricerca: ${raggioKm.toInt()} km",
                        style = MaterialTheme.typography.titleMedium,
                        color= mieiTitoli
                    )

                    // Lo Slider
                    Slider(
                        colors= SliderDefaults.colors(thumbColor = mioGialloOcra,
                            inactiveTrackColor = mioGialloOcra,
                            activeTrackColor = mioGialloOcra,
                            activeTickColor = mioBluScuro,
                            inactiveTickColor = Color.White),
                        value = raggioKm,
                        onValueChange = { nuovoValore ->
                            raggioKm = nuovoValore
                        },
                        valueRange = 1f..20f, // Da 1km a 20km
                        steps = 99, // (Opzionale) Fa scattare lo slider di 1 in 1
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
    }
}
}