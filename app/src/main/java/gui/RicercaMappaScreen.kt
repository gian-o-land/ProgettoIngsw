package gui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.SearchBarDefaults.inputFieldColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import navigation.Screen

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

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = remember { MapProperties(mapStyleOptions = MapStyleOptions(jsonStyleChiaro)) },
            onMapClick = { coordinataCliccata ->
                puntoSelezionato = coordinataCliccata
            }
        ) {
            puntoSelezionato?.let { punto ->
                Marker(
                    state = MarkerState(position = punto),
                    title = "Posizione Scelta",
                    snippet = "Lat: ${punto.latitude}, Lng: ${punto.longitude}",
                )

                Circle(
                    center = punto,
                    radius = (raggioKm * 1000).toDouble(), // converto km in metri perché Maps usa i metri
                    strokeColor = Color.Blue,
                    strokeWidth = 3f,
                    fillColor = Color.Blue.copy(alpha = 0.2f)
                )

            }
        }

        SearchBar(
            //colors = SearchBarDefaults.colors(containerColor = Color.Transparent),// inputField= inputFieldColors () ),
            query = query,
            onQueryChange = { query = it },
            onSearch = {
                active = false
                eseguiRicerca(it, scope, context, cameraPositionState)
            },
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text("Cerca città") },
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
            modifier = Modifier.align(Alignment.TopCenter)
            //.padding(horizontal = 12.dp)
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
                border = BorderStroke(width = 1.dp, color = Color.Black),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium, // angoli arrotondati
                shadowElevation = 8.dp,
                color = mioBluScuro
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                        valueRange = 1f..20f, // da 1km a 20km
                        steps = 99, //fa scattare lo slider di 1 in 1
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Button(
                        /*
                        deve trovare tutte le case a distanza di massimo "raggioKM" km dal "puntoCliccato"
                        le case sono locate a "Inserzione.indirizzo", bisogna calcolare la distanza tra tutte le case
                        e paragonare quella distanza agli "raggioKM" km
                        SERVE FUNZIONE calcolaDistanza(context, location1, location2)
                            con location1=puntoCliccato e location2=cercaCoordinate(context, Inserzione.indirizzo)
                         SOLUZIONE PRE-DATABASE: lista di indirizzi come sostituto a location2, cosi da poter testare
                        */
                        onClick={
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = mioGialloOcra,
                            contentColor = mieScritte
                        ),
                        modifier = Modifier.height(50.dp)
                            .fillMaxWidth()
                            .padding(vertical=5.dp,horizontal = 80.dp)
                    ) {
                        Text("Conferma", fontSize = 17.sp)
                    }
                }
            }
    }
}
}