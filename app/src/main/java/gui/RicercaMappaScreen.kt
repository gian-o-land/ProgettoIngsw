package gui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch
import model.InserzioneTemp
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RicercaMappaScreen(location: LatLng, navController : NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 10f)
    }

    var puntoSelezionato by remember { mutableStateOf<LatLng?>(null) }
    var raggioKm by remember { mutableFloatStateOf(10f) }
    var listaInserzioniNelCerchio by remember { mutableStateOf<List<InserzioneTemp>>(emptyList()) }
    var isPannelloSliderVisibile by remember { mutableStateOf(false) }
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden, // Parte nascosto
            skipHiddenState = false // <--- FONDAMENTALE: DEVE ESSERE FALSE!
        )
    )
    var isBottomSheetEnabled by remember { mutableStateOf(false) }

    if (!Places.isInitialized()) {
        Places.initialize(context, "AIzaSyCe6be-GA-ccdqwDwzHzJ5l1prpQWxvEIk")
    }
    val pagerState = rememberPagerState(pageCount = { listaInserzioniNelCerchio.size })

    val placesClient = remember { Places.createClient(context) }
    val sessionToken = remember { AutocompleteSessionToken.newInstance() }

    var listaSuggerimenti by remember { mutableStateOf<List<SuggerimentoPlace>>(emptyList()) }

    var currentPeekHeight by remember { mutableStateOf(0.dp) }

    LaunchedEffect(query) {

        if (query.length > 1) { //scrivi almeno 2 lettere

            delay(300)

            val request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(sessionToken)
                .setQuery(query)
                .setCountries("IT")
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->

                    listaSuggerimenti = response.autocompletePredictions.map {
                        SuggerimentoPlace(
                            it.placeId,
                            it.getPrimaryText(null).toString(),
                            it.getSecondaryText(null).toString()
                        )
                    }
                }
                .addOnFailureListener { exception ->

                    listaSuggerimenti = emptyList()
                }
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        if (listaInserzioniNelCerchio.isNotEmpty()) {

            val inserzioneSelezionata = listaInserzioniNelCerchio[pagerState.currentPage]//prendo l'inserzione corrispondente alla pagina corrente


            cameraPositionState.animate( // sposto la telecamera su quel punto (Animazione fluida)

                update = CameraUpdateFactory.newLatLng(inserzioneSelezionata.coordinate),
                durationMs = 800
            )
        }
    }

    fun vaiAlLuogo(placeId: String, descrizione: String) {
        val fields = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.builder(placeId, fields).setSessionToken(sessionToken).build()

        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            val coordinate = response.place.latLng
            if (coordinate != null) {
                scope.launch { cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(coordinate, 12f)) }

                puntoSelezionato = coordinate
                query = descrizione
                active = false

                // Reset interfaccia per nuova selezione
                listaInserzioniNelCerchio = emptyList()
                isPannelloSliderVisibile = true
                isBottomSheetEnabled = false
                currentPeekHeight = 0.dp // Nascondo la sheet
                scope.launch { scaffoldState.bottomSheetState.partialExpand() }
            }
        }
    }


    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContainerColor = Color.White,
        sheetContentColor = Color.White,


        sheetPeekHeight = currentPeekHeight,
        sheetDragHandle = null,
        sheetSwipeEnabled = isBottomSheetEnabled,

        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // MANIGLIA PERSONALIZZATA
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50)) // Arrotonda gli angoli (forma a pillola)
                        .background(Color.Black)
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Inserzioni Trovate",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Left
                )
            }

            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 30.dp), // Lascia intravedere le card laterali
                pageSpacing = 10.dp, // Spazio tra le card
                modifier = Modifier.fillMaxWidth()  ) {
                    pageIndex ->
                val inserzione = listaInserzioniNelCerchio[pageIndex]
                Card(
                    modifier = Modifier
                        .fillMaxWidth() // La larghezza è gestita dal Pager
                        .height(165.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Red), // Usa i tuoi colori
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {Column {
                    // Immagine (se ce l'hai nell'oggetto InserzioneTemp)
                    // Image(...)

                    Text(
                        text = inserzione.nome, // Titolo dinamico
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp)
                    )
                    Text(
                        text = "Lat: ${inserzione.coordinate.latitude}",
                        color = Color.LightGray,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }}


        }
            Spacer(modifier = Modifier.height(20.dp))
        }) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = remember { MapProperties(mapStyleOptions = MapStyleOptions(jsonStyleChiaro)) },
            onMapClick = { coordinataCliccata ->
                puntoSelezionato = coordinataCliccata
                isPannelloSliderVisibile = true   // Mostro lo slider
                listaInserzioniNelCerchio = emptyList() // Pulisco i vecchi risultati per pulizia
                isBottomSheetEnabled = false // Disabilito l'interazione/swipe
                scope.launch {
                    scaffoldState.bottomSheetState.hide() // La nascondo fisicamente
                }
            }
        ) {
            listaInserzioniNelCerchio.forEachIndexed { index, inserzione ->
                val isSelezionato = (pagerState.currentPage == index)
                val markerState = rememberMarkerState(position = inserzione.coordinate)
                LaunchedEffect(isSelezionato) {
                    if (isSelezionato) {
                        markerState.showInfoWindow()
                    } else {
                        markerState.hideInfoWindow()
                    }
                }
                Marker(
                    state = markerState,
                    title = inserzione.nome,

                    // A. Z-INDEX: Se selezionato, stà sopra a tutti gli altri (1f)
                    zIndex = if (isSelezionato) 1.0f else 0.0f,

                    // B. COLORE: Azzurro se selezionato, Giallo Ocra (50f) se normale
                    icon = BitmapDescriptorFactory.defaultMarker(
                        if (isSelezionato) BitmapDescriptorFactory.HUE_AZURE else 50f
                    ),
                    onClick = {
                        scope.launch {
                            isBottomSheetEnabled = true
                            //currentPeekHeight = 160.dp // Alza la bottom sheet se era giù
                            scaffoldState.bottomSheetState.expand() // O partialExpand

                            // IMPORTANTE: Dico alla lista di andare a questo indice
                            pagerState.animateScrollToPage(index)
                        }
                        false
                    },

                )
            }

            puntoSelezionato?.let { punto ->
                Marker(
                    state = MarkerState(position = punto),
                    title = "Posizione Scelta",
                    snippet = "Lat: ${punto.latitude}, Lng: ${punto.longitude}",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)

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
            query = query,
            onQueryChange = { query = it },
            onSearch = {
                active = false

                isBottomSheetEnabled = false
                currentPeekHeight = 0.dp
                scope.launch {
                    scaffoldState.bottomSheetState.partialExpand() // La chiudo visivamente
                }

                listaInserzioniNelCerchio = emptyList()

                eseguiRicerca(query, scope, context, cameraPositionState)
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
        ) {

            LazyColumn {
                items(listaSuggerimenti) { luogo ->
                    ListItem(
                        headlineContent = {
                            Text(
                                luogo.testoPrincipale,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        supportingContent = { Text(luogo.testoSecondario) },
                        leadingContent = { Icon(Icons.Default.LocationOn, null) },
                        modifier = Modifier.clickable {
                            vaiAlLuogo(luogo.id, luogo.testoPrincipale)
                        }
                    )
                }
            }
        }

        if (puntoSelezionato != null) {
            if (isPannelloSliderVisibile) {
                Box(modifier=Modifier.fillMaxSize()){
                Surface(
                    border = BorderStroke(width = 1.dp, color = Color.Black),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(180.dp),
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
                            color = mieiTitoli
                        )

                        Slider(
                            colors = SliderDefaults.colors(
                                thumbColor = mioGialloOcra,
                                inactiveTrackColor = mioGialloOcra,
                                activeTrackColor = mioGialloOcra,
                                activeTickColor = mioBluScuro,
                                inactiveTickColor = Color.White
                            ),
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

                            onClick = {
                                val centro = puntoSelezionato!!
                                val raggioMetri = raggioKm * 1000

                                // 1. FILTRO: Uso SphericalUtil per confrontare i numeri
                                val risultati = listaInserzioniFintoDB.filter { casa ->
                                    val distanza = SphericalUtil.computeDistanceBetween(
                                        centro,
                                        casa.coordinate
                                    )
                                    distanza <= raggioMetri
                                }

                                // 2. AGGIORNO GLI STATI
                                listaInserzioniNelCerchio = risultati // La mappa si aggiorna e mostra i marker
                                isPannelloSliderVisibile = false

                                if (risultati.isEmpty()) {
                                    Toast.makeText(
                                        context,
                                        "Nessuna casa trovata in questo raggio",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Trovate ${risultati.size} case!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = mioGialloOcra,
                                contentColor = mieScritte
                            ),
                            modifier = Modifier.height(50.dp)
                                .fillMaxWidth()
                                .padding(vertical = 5.dp, horizontal = 80.dp)
                        ) {
                            Text("Conferma", fontSize = 17.sp)
                        }
                    }
                }
                }
            }
        }
    }
}