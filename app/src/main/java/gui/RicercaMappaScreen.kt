package gui

import DTO.FiltriRicercaDTO
import DTO.InserzioneDTO
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
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlinx.coroutines.delay
import navigation.Screen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.runtime.saveable.rememberSaveable
import connection.scaricaInserzioniFiltrate
import connection.scaricaTutteLeInserzioni
import gui.template.myCardInserzione
import kotlinx.serialization.encodeToString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RicercaMappaScreen(
    location: LatLng,
    navController: NavController,
    email: String,
    filtri: FiltriRicercaDTO
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 10f)
    }

    var puntoSelezionato by remember { mutableStateOf<LatLng?>(null) }
    var raggioKm by remember { mutableFloatStateOf(10f) }

    // --- STATE PER I DATI DEL DATABASE ---
    var listaTutteLeInserzioni by remember { mutableStateOf<List<InserzioneDTO>>(emptyList()) }
    var listaInserzioniNelCerchio by remember { mutableStateOf<List<InserzioneDTO>>(emptyList()) }
    var isLoadingData by remember { mutableStateOf(true) } // Per mostrare un caricamento iniziale se vuoi

    var isPannelloSliderVisibile by remember { mutableStateOf(false) }
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
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
    var pulisciAlRitorno by rememberSaveable { mutableStateOf(false) }

    // --- SCARICHIAMO I DATI AL CARICAMENTO DELLA PAGINA CON I FILTRI ---
    LaunchedEffect(location, filtri) {
        isLoadingData = true
        // 1. Chiamata API POST con i filtri
        listaTutteLeInserzioni = scaricaInserzioniFiltrate(
            lat = location.latitude,
            lng = location.longitude,
            filtri = filtri
        )
        isLoadingData = false

        // 2. Simuliamo subito un "cerchio" attorno alla località cercata per mostrare i risultati subito
        puntoSelezionato = location
        isPannelloSliderVisibile = true // Mostriamo lo slider per permettere all'utente di aggiustare il raggio
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && pulisciAlRitorno) {
                isBottomSheetEnabled = false
                currentPeekHeight = 0.dp
                listaInserzioniNelCerchio = emptyList()
                puntoSelezionato = null

                scope.launch {
                    try { scaffoldState.bottomSheetState.hide() } catch (e: Exception) {}
                }
                pulisciAlRitorno = false
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(query) {
        if (query.isNotEmpty()) {
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
                .addOnFailureListener { listaSuggerimenti = emptyList() }
        }
    }

    // --- SPOSTAMENTO CAMERA TRAMITE SWIPE SULLE CARD ---
    LaunchedEffect(pagerState.currentPage) {
        if (listaInserzioniNelCerchio.isNotEmpty()) {
            val inserzioneSelezionata = listaInserzioniNelCerchio[pagerState.currentPage]
            val coordinateVeroDB = LatLng(inserzioneSelezionata.latitudine, inserzioneSelezionata.longitudine)

            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLng(coordinateVeroDB),
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

                listaInserzioniNelCerchio = emptyList()
                isPannelloSliderVisibile = true
                isBottomSheetEnabled = false
                currentPeekHeight = 0.dp
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
                Box(modifier = Modifier.padding(top = 10.dp).width(40.dp).height(4.dp).clip(RoundedCornerShape(50)).background(Color.Black))
                Spacer(modifier = Modifier.height(10.dp))
                Text("Inserzioni Trovate", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black)

                Spacer(modifier = Modifier.height(10.dp))

                // --- CAROSELLO DELLE CARD (HorizontalPager) ---
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 30.dp),
                    pageSpacing = 10.dp,
                    modifier = Modifier.fillMaxWidth()
                ) { pageIndex ->

                    val inserzione = listaInserzioniNelCerchio[pageIndex]

                    myCardInserzione(
                        inserzione = inserzione,
                        myModifier = Modifier
                            .fillMaxWidth()
                            .height(165.dp),
                        onClick = {
                            pulisciAlRitorno = true
                            val json = kotlinx.serialization.json.Json.encodeToString(inserzione)
                            navController.currentBackStackEntry?.savedStateHandle?.set("inserzione_json", json)
                            navController.currentBackStackEntry?.savedStateHandle?.set("email_utente", email)
                            navController.navigate(Screen.InserzioneScreen.route)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            // --- LA MAPPA DI GOOGLE ---
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                // properties = remember { MapProperties(mapStyleOptions = MapStyleOptions(jsonStyleChiaro)) },
                onMapClick = { coordinataCliccata ->
                    puntoSelezionato = coordinataCliccata
                    isPannelloSliderVisibile = true
                    isBottomSheetEnabled = false
                    scope.launch { scaffoldState.bottomSheetState.hide() }
                    listaInserzioniNelCerchio = emptyList()
                }
            ) {
                // DISEGNO DEI MARKER DELLE CASE
                listaInserzioniNelCerchio.forEachIndexed { index, inserzione ->
                    val isSelezionato = (pagerState.currentPage == index)
                    val coordinateInserzione = LatLng(inserzione.latitudine, inserzione.longitudine)
                    val markerState = rememberMarkerState(position = coordinateInserzione)

                    LaunchedEffect(isSelezionato) {
                        if (isSelezionato) markerState.showInfoWindow() else markerState.hideInfoWindow()
                    }

                    Marker(
                        state = markerState,
                        title = "${inserzione.prezzo} €",
                        snippet = inserzione.citta,
                        zIndex = if (isSelezionato) 1.0f else 0.0f,
                        icon = BitmapDescriptorFactory.defaultMarker(
                            if (isSelezionato) BitmapDescriptorFactory.HUE_AZURE else 50f
                        ),
                        onClick = {
                            scope.launch {
                                isBottomSheetEnabled = true
                                scaffoldState.bottomSheetState.expand()
                                pagerState.animateScrollToPage(index)
                            }
                            false
                        }
                    )
                }

                // DISEGNO DEL CERCHIO BLU
                puntoSelezionato?.let { punto ->
                    Marker(
                        state = MarkerState(position = punto),
                        title = "Posizione Scelta",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    )

                    Circle(
                        center = punto,
                        radius = (raggioKm * 1000).toDouble(),
                        strokeColor = Color.Blue,
                        strokeWidth = 3f,
                        fillColor = Color.Blue.copy(alpha = 0.2f)
                    )
                }
            }

            // --- BARRA DI RICERCA IN ALTO ---
            Box(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)) {
                SearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = {
                        active = false
                        isBottomSheetEnabled = false
                        currentPeekHeight = 0.dp
                        scope.launch { scaffoldState.bottomSheetState.partialExpand() }
                        listaInserzioniNelCerchio = emptyList()
                        // eseguiRicerca(query, scope, context, cameraPositionState)
                    },
                    active = active,
                    onActiveChange = { active = it },
                    placeholder = { Text("Cerca città") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (active) {
                            IconButton(onClick = { if (query.isNotEmpty()) query = "" else active = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Chiudi")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    LazyColumn {
                        items(listaSuggerimenti) { luogo ->
                            ListItem(
                                headlineContent = { Text(luogo.testoPrincipale, fontWeight = FontWeight.Bold) },
                                supportingContent = { Text(luogo.testoSecondario) },
                                leadingContent = { Icon(Icons.Default.LocationOn, null) },
                                modifier = Modifier.clickable { vaiAlLuogo(luogo.id, luogo.testoPrincipale) }
                            )
                        }
                    }
                }
            }

            // --- PANNELLO DELLO SLIDER IN BASSO ---
            if (puntoSelezionato != null && isPannelloSliderVisibile) {
                Surface(
                    border = BorderStroke(width = 1.dp, color = Color.Black),
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(180.dp),
                    shape = MaterialTheme.shapes.medium,
                    shadowElevation = 8.dp,
                    color = Color(0xFF000080) // mioBluScuro
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Raggio di ricerca: ${raggioKm.toInt()} km",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )

                        Slider(
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFDAA520), // mioGialloOcra
                                activeTrackColor = Color(0xFFDAA520),
                                inactiveTrackColor = Color.Gray
                            ),
                            value = raggioKm,
                            onValueChange = { nuovoValore -> raggioKm = nuovoValore },
                            valueRange = 1f..20f,
                            steps = 18,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Button(
                            onClick = {
                                val centro = puntoSelezionato!!
                                val raggioMetri = raggioKm * 1000

                                // IL FILTRO DEL RAGGIO AVVIENE SULLE INSERZIONI GIA' FILTRATE DAL SERVER
                                val risultati = listaTutteLeInserzioni.filter { casa ->
                                    val coordinateCasa = LatLng(casa.latitudine, casa.longitudine)
                                    val distanza = SphericalUtil.computeDistanceBetween(centro, coordinateCasa)
                                    distanza <= raggioMetri
                                }

                                listaInserzioniNelCerchio = risultati
                                isPannelloSliderVisibile = false
                                isBottomSheetEnabled = true // Permettiamo lo swipe del menu inferiore
                                currentPeekHeight = 50.dp   // Mostriamo un po' del menu in basso

                                if (risultati.isEmpty()) {
                                    Toast.makeText(context, "Nessuna casa trovata in questo raggio", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Trovate ${risultati.size} case!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDAA520), contentColor = Color(0xFF000080)),
                            modifier = Modifier.height(50.dp).fillMaxWidth().padding(vertical = 5.dp, horizontal = 80.dp)
                        ) {
                            Text("Conferma", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // --- OVERLAY DI CARICAMENTO ---
            if (isLoadingData) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF000080))
                }
            }
        }
    }
}