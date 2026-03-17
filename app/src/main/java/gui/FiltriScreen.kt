package gui

import DTO.FiltriRicercaDTO
import android.location.Geocoder
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.progettoingsw.R

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Icon
import androidx.compose.material3.Text

import androidx.compose.ui.text.TextStyle

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import com.google.android.gms.maps.model.LatLng
import gui.template.MyButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import navigation.Screen
import java.util.Locale
import kotlin.math.roundToInt
import kotlinx.serialization.encodeToString



@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun FiltriScreen(navController: NavController, email: String) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    var textLocalita by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var showErrors by remember { mutableStateOf(false) }

    // --- VARIABILI DI STATO PER I FILTRI ---
    var statoCategoria by remember { mutableStateOf("Indifferente") }

    var statoPrezzoMin by remember { mutableStateOf("") }
    var statoPrezzoMax by remember { mutableStateOf("") }

    var statoQuadraturaMin by remember { mutableStateOf("") }
    var statoQuadraturaMax by remember { mutableStateOf("") }

    var statoLocaliMin by remember { mutableStateOf("") }
    var statoLocaliMax by remember { mutableStateOf("") }

    var statoBagni by remember { mutableStateOf("") }
    var statoPiano by remember { mutableStateOf("Indifferente") }
    var statoClasseEnergetica by remember { mutableStateOf("Indifferente") }
    var statoArredamento by remember { mutableStateOf("Indifferente") }

    var statoAscensore by remember { mutableStateOf(false) }
    var statoCucina by remember { mutableStateOf(false) }
    var statoPiscina by remember { mutableStateOf(false) }
    var statoCantina by remember { mutableStateOf(false) }
    var statoBalcone by remember { mutableStateOf(false) }
    var statoTerrazzo by remember { mutableStateOf(false) }

    fun isMinMaggioreDiMax(min: String, max: String): Boolean {
        if (min.isBlank() || max.isBlank()) return false
        val minVal = min.toIntOrNull() ?: 0
        val maxVal = max.toIntOrNull() ?: Int.MAX_VALUE
        return minVal > maxVal
    }

    // --- CALCOLO DEGLI ERRORI PER LE RIGHE ---
    val errorePrezzo = isMinMaggioreDiMax(statoPrezzoMin, statoPrezzoMax)
    val erroreQuadratura = isMinMaggioreDiMax(statoQuadraturaMin, statoQuadraturaMax)
    val erroreLocali = isMinMaggioreDiMax(statoLocaliMin, statoLocaliMax)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        containerColor = Color.White,

        // --- SNACKBAR PER GLI ERRORI (Stile CaricaImmobili) ---
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(horizontal = 35.dp, vertical = 5.dp)
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFFB00020),
                    contentColor = Color.White
                )
            }
        },

        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor =mioBluScuro
                ),
                title = {
                    Text(
                        text = "Filtri",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.height(50.dp).width(50.dp)
                    )
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Imposta i filtri\nper la tua ricerca",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // --- LISTA FILTRI ---
            RigaCliccabileConErrore(
                icona = Icons.Default.LocationCity,
                testo = "Categoria",
                valoreAttuale = statoCategoria,
                opzioni = listOf("Indifferente","In vendita","In affitto"),
                isError = false,
                onValoreCambiato = { statoCategoria = it }
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            val labelPrezzo = if (statoCategoria == "In affitto") "Prezzo / mese" else "Prezzo"

            RigaMinMax(
                icona = Icons.Default.AttachMoney,
                testo = labelPrezzo,
                valoreMin = statoPrezzoMin,
                onValueMinChange = { statoPrezzoMin = it },
                valoreMax = statoPrezzoMax,
                onValueMaxChange = { statoPrezzoMax = it },
                isError = errorePrezzo
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            RigaMinMax(
                icona = Icons.Default.Home,
                testo = "Quadratura",
                valoreMin = statoQuadraturaMin,
                onValueMinChange = { statoQuadraturaMin = it },
                valoreMax = statoQuadraturaMax,
                onValueMaxChange = { statoQuadraturaMax = it },
                isError = erroreQuadratura
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            RigaMinMax(
                icona = Icons.Default.Dashboard,
                testo = "Locali",
                valoreMin = statoLocaliMin,
                onValueMinChange = { statoLocaliMin = it },
                valoreMax = statoLocaliMax,
                onValueMaxChange = { statoLocaliMax = it },
                isError = erroreLocali
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            RigaBottoni(
                icona = Icons.Default.Bathtub,
                testo = "Bagni",
                valoreAttuale = statoBagni,
                opzioni = listOf("1", "2", "3", ">3"),
                onValueChange = { statoBagni = it }
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            RigaCliccabileConErrore(
                icona = Icons.Default.Menu,
                testo = "Piano",
                valoreAttuale = statoPiano,
                opzioni = listOf("Indifferente", "1", "2", "3", "4", "5", "6", "7", "8"),
                isError = false,
                onValoreCambiato = { statoPiano = it }
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            RigaSlider(
                icona = Icons.Default.FlashOn,
                testo = "Classe\nenergetica",
                valoreAttuale = statoClasseEnergetica,
                onValueChange = { statoClasseEnergetica = it }
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            RigaCliccabileConErrore(
                icona = Icons.Default.Chair,
                testo = "Arredamento",
                valoreAttuale = statoArredamento,
                opzioni = listOf("Indifferente","Completo", "Parziale", "No"),
                isError = false,
                onValoreCambiato = { statoArredamento = it }
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            // --- ALTRE CARATTERISTICHE ---
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(
                    text = "Altre caratteristiche",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BottoneCaratteristica(testo = "Ascensore", selezionato = statoAscensore, onClick = { statoAscensore = !statoAscensore }, modifier = Modifier.weight(1f))
                    BottoneCaratteristica(testo = "Cucina", selezionato = statoCucina, onClick = { statoCucina = !statoCucina }, modifier = Modifier.weight(1f))
                    BottoneCaratteristica(testo = "Piscina", selezionato = statoPiscina, onClick = { statoPiscina = !statoPiscina }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BottoneCaratteristica(testo = "Cantina", selezionato = statoCantina, onClick = { statoCantina = !statoCantina }, modifier = Modifier.weight(1f))
                    BottoneCaratteristica(testo = "Balcone", selezionato = statoBalcone, onClick = { statoBalcone = !statoBalcone }, modifier = Modifier.weight(1f))
                    BottoneCaratteristica(testo = "Terrazzo", selezionato = statoTerrazzo, onClick = { statoTerrazzo = !statoTerrazzo }, modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- CAMPO OBBLIGATORIO: LOCALITÀ (Stile CaricaImmobili) ---
            OutlinedTextField(
                value = textLocalita,
                onValueChange = { textLocalita = it },
                label = { Text("Località (obbligatorio)") },
                isError = showErrors && textLocalita.isBlank(),
                singleLine = true,
                modifier = Modifier.padding(horizontal = 35.dp).fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- BOTTONE DI RICERCA CON PASSAGGIO FILTRI E LOGICA SNACKBAR ---
            MyButton(
                myClick = {
                    val ciSonoErroriMinMax = errorePrezzo || erroreQuadratura || erroreLocali

                    if (textLocalita.isBlank() || ciSonoErroriMinMax) {
                        showErrors = true
                        coroutineScope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            if (textLocalita.isBlank()) {
                                snackbarHostState.showSnackbar("Inserisci una località per poter cercare!")
                            } else {
                                snackbarHostState.showSnackbar("Attenzione: i valori Min non possono essere maggiori dei Max.")
                            }
                        }
                    } else {
                        showErrors = false
                        isSearching = true

                        coroutineScope.launch(Dispatchers.IO) {
                            try {
                                val geocoder = Geocoder(context, Locale.getDefault())

                                @Suppress("DEPRECATION")
                                val addresses = geocoder.getFromLocationName(textLocalita, 1)

                                if (!addresses.isNullOrEmpty()) {
                                    val location = addresses[0]
                                    val latLng = LatLng(location.latitude, location.longitude)

                                    withContext(Dispatchers.Main) {
                                        isSearching = false

                                        val filtriSelezionati = FiltriRicercaDTO(
                                            categoria = statoCategoria,
                                            prezzoMin = statoPrezzoMin,
                                            prezzoMax = statoPrezzoMax,
                                            quadraturaMin = statoQuadraturaMin,
                                            quadraturaMax = statoQuadraturaMax,
                                            localiMin = statoLocaliMin,
                                            localiMax = statoLocaliMax,
                                            bagni = statoBagni,
                                            piano = statoPiano,
                                            classeEnergetica = statoClasseEnergetica,
                                            arredamento = statoArredamento,
                                            ascensore = statoAscensore,
                                            cucina = statoCucina,
                                            piscina = statoPiscina,
                                            cantina = statoCantina,
                                            balcone = statoBalcone,
                                            terrazzo = statoTerrazzo
                                        )

                                        val filtriJson = kotlinx.serialization.json.Json.encodeToString(filtriSelezionati)
                                        navController.currentBackStackEntry?.savedStateHandle?.set("filtri_json", filtriJson)

                                        navController.navigate(
                                            Screen.RicercaMappaScreen.withLocation(
                                                location = latLng,
                                                email = email
                                            )
                                        )
                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        isSearching = false
                                        snackbarHostState.currentSnackbarData?.dismiss()
                                        snackbarHostState.showSnackbar("Località non trovata, prova a essere più preciso.")
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    isSearching = false
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar("Errore di connessione. Riprova.")
                                }
                            }
                        }
                    }
                },
                testo = if (isSearching) "Ricerca in corso..." else "Cerca"
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun RigaMinMax(
    icona: ImageVector,
    testo: String,
    valoreMin: String,
    onValueMinChange: (String) -> Unit,
    valoreMax: String,
    onValueMaxChange: (String) -> Unit,
    isError: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icona, contentDescription = testo, tint = mioBluScuro)
        Spacer(modifier = Modifier.width(16.dp))

        Text(text = testo, fontSize = 16.sp, modifier = Modifier.width(90.dp))

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = valoreMin,
                onValueChange = { nuovoValore ->
                    // Filtra l'input: accetta solo cifre (niente negativi o lettere)
                    onValueMinChange(nuovoValore.filter { it.isDigit() })
                },
                isError = isError,
                label = { Text("Min") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
            )
            OutlinedTextField(
                value = valoreMax,
                onValueChange = { nuovoValore ->
                    onValueMaxChange(nuovoValore.filter { it.isDigit() })
                },
                isError = isError,
                label = { Text("Max") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
            )
        }
    }
}

@Composable
fun RigaBottoni(
    icona: ImageVector,
    testo: String,
    valoreAttuale: String,
    opzioni: List<String>,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icona,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = mioBluScuro
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = testo, fontSize = 16.sp, modifier = Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            opzioni.forEach { opzione ->
                val isSelected = valoreAttuale == opzione
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) mioGialloOcra else mioBluScuro)
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                        .clickable { onValueChange(opzione) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = opzione,
                        fontSize = 14.sp,
                        color = if (isSelected) mioBluScuro else Color.White
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RigaSlider(
    icona: ImageVector,
    testo: String,
    valoreAttuale: String,
    onValueChange: (String) -> Unit
) {
    val opzioni = listOf("Indifferente", "Bassa", "Media", "Alta")

    val initialIndex = opzioni.indexOf(valoreAttuale).toFloat()
    val sliderPos = if (initialIndex >= 0f) initialIndex else 0f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icona,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = mioBluScuro
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = testo, fontSize = 16.sp, modifier = Modifier.width(80.dp), lineHeight = 18.sp)

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {

            val sliderColors = SliderDefaults.colors(
                activeTrackColor = mioBluScuro,
                inactiveTrackColor = Color(0xFFEBE6ED),
                activeTickColor = mioGialloOcra,
                inactiveTickColor = mioBluScuro
            )

            Slider(
                value = sliderPos,
                onValueChange = { nuovaPos ->
                    val index = nuovaPos.roundToInt()
                    onValueChange(opzioni[index])
                },
                valueRange = 0f..3f,
                steps = 2,
                colors = sliderColors,
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .border(width = 2.dp, color = mioBluScuro, shape = CircleShape)
                            .background(color = mioGialloOcra, shape = CircleShape)
                    )
                },
                track = { sliderState ->
                    SliderDefaults.Track(
                        colors = sliderColors,
                        sliderState = sliderState,
                        thumbTrackGapSize = 0.dp
                    )
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)
                    .offset(y = (-8).dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                opzioni.forEachIndexed { index, label ->
                    val isSelected = sliderPos.roundToInt() == index
                    val displayLabel = if (label == "Indifferente") "Ind." else label

                    Text(
                        text = displayLabel,
                        fontSize = 11.sp,
                        color = if (isSelected) mioGialloOcra else mioBluScuro,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
@Composable
fun BottoneCaratteristica(
    testo: String,
    selezionato: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (selezionato) mioGialloOcra else mioBluScuro
    val textColor = if (selezionato) mioBluScuro else Color.White

    Button(
        onClick = onClick,
        modifier = modifier.height(45.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        Text(
            text = testo,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
    }
}