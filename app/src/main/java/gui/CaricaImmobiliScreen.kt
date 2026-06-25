package gui

import DTO.D_foto
import DTO.ImmagineDTO
import DTO.InserzioneDTO
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.progettoingsw.R
import androidx.compose.material3.Text
import gui.template.MyButton
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.ui.platform.LocalContext
import connection.inviaImmagineAServer
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import connection.client
import connection.inviaInserzioneAlServer
import gui.template.MyButtonErrore
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun CaricaImmobiliScreen(navController: NavController, username: String) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // --- IL GESTORE DELLO SNACKBAR ---
    val snackbarHostState = remember { SnackbarHostState() }

    // --- VARIABILI DI STATO (Campi di testo finali) ---
    var textFieldCitta by remember { mutableStateOf("") }
    var textFieldComune by remember { mutableStateOf("") }
    var textFieldIndirizzo by remember { mutableStateOf("") }
    var textFieldDescrizione by remember { mutableStateOf("") }

    // --- VARIABILI DI STATO (Per i menu e valori in alto) ---
    var categoria by remember { mutableStateOf("-") }
    var prezzo by remember { mutableStateOf("") }
    var quadratura by remember { mutableStateOf("") }
    var locali by remember { mutableStateOf("") }
    var bagni by remember { mutableStateOf("") }
    var piano by remember { mutableStateOf("-") }
    var arredamento by remember { mutableStateOf("-") }

    // --- VARIABILE CLASSE ENERGETICA ---
    var classeEnergeticaScelta by remember { mutableStateOf("G") }

    // --- VARIABILI CARATTERISTICHE BOOLEANE ---
   /*
    var ascensore by remember { mutableStateOf(false) }
    var cucina by remember { mutableStateOf(false) }
    var piscina by remember { mutableStateOf(false) }
    var cantina by remember { mutableStateOf(false) }
    var balcone by remember { mutableStateOf(false) }
    var terrazzo by remember { mutableStateOf(false) }
*/
    var statoAscensore by remember { mutableStateOf(false) }
    var statoCucina by remember { mutableStateOf(false) }
    var statoPiscina by remember { mutableStateOf(false) }
    var statoCantina by remember { mutableStateOf(false) }
    var statoBalcone by remember { mutableStateOf(false) }
    var statoTerrazzo by remember { mutableStateOf(false) }
    // --- VARIABILI FOTO (MAX 10) ---
    var fotoSelezionate by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var linkS3Ottenuti by remember { mutableStateOf<List<String>>(emptyList()) }

    // --- VARIABILI PLANIMETRIA (SINGOLA) ---
    var planimetriaSelezionata by remember { mutableStateOf<Uri?>(null) }
    var linkPlanimetriaS3 by remember { mutableStateOf<String?>(null) }

    // --- STATI DI CARICAMENTO ED ERRORE ---
    var isUploading by remember { mutableStateOf(false) }
    var showErrors by remember { mutableStateOf(false) }

    // SELETTORE MULTIPLO FOTO
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val combinazione = (fotoSelezionate + uris).distinct()
            fotoSelezionate = combinazione.take(10)
            if (showErrors && fotoSelezionate.isNotEmpty()) showErrors = false
        }
    }

    // SELETTORE SINGOLO PLANIMETRIA
    val planimetriaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            planimetriaSelezionata = uri
            if (showErrors) showErrors = false
        }
    }

    if (isUploading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = mioBluScuro)
        }
    } else{
        Scaffold(
            modifier = Modifier.fillMaxSize().background(color = Color.White),
            containerColor = Color.White,

            // --- SNACKBAR PER GLI ERRORI ---
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
                        containerColor = mioBluScuro
                    ),
                    title = {
                        Text("Carica Immobile", fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                    },
                    navigationIcon = {
                        Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo", modifier = Modifier.height(50.dp).width(50.dp))
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

                // --- RIGHE CON CONTROLLO ERRORE E AGGIORNAMENTO STATO ---
                RigaCliccabileConErrore(
                    icona = Icons.Default.LocationCity, testo = "Categoria",
                    valoreAttuale = categoria, opzioni = listOf("In vendita","In affitto"),
                    isError = showErrors && categoria == "-",
                    onValoreCambiato = { nuovaCategoria -> categoria = nuovaCategoria }
                )
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                val labelPrezzo = if (categoria == "In affitto") "Prezzo / mese" else "Prezzo"

                RigaValoreConErrore(
                    icona = Icons.Default.AttachMoney,
                    testo = labelPrezzo,
                    valoreAttuale = prezzo,
                    isError = showErrors && prezzo.isBlank(),
                    onValoreCambiato = { nuovoPrezzo -> prezzo = nuovoPrezzo },
                    suffisso = "€"
                )
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                RigaValoreConErrore(
                    icona = Icons.Default.Home, testo = "Quadratura",
                    valoreAttuale = quadratura,
                    isError = showErrors && quadratura.isBlank(),
                    onValoreCambiato = { nuovaQuadratura -> quadratura = nuovaQuadratura },
                    suffisso="m²"
                )
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                RigaValoreConErrore(
                    icona = Icons.Default.Dashboard, testo = "Locali",
                    valoreAttuale = locali,
                    isError = showErrors && locali.isBlank(),
                    onValoreCambiato = { nuoviLocali -> locali = nuoviLocali }
                )
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                RigaValoreConErrore(
                    icona = Icons.Default.Dashboard, testo = "Bagni",
                    valoreAttuale = bagni,
                    isError = showErrors && bagni.isBlank(),
                    onValoreCambiato = { nuoviBagni -> bagni = nuoviBagni }
                )
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                RigaCliccabileConErrore(
                    icona = Icons.Default.Menu, testo = "Piano",
                    valoreAttuale = piano, opzioni=  listOf("1", "2", "3", "4", "5", "6", "7", "8"),
                    isError = showErrors && piano == "-",
                    onValoreCambiato = { nuovoPiano -> piano = nuovoPiano }
                )
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                RigaSliderAgente(
                    icona = Icons.Default.FlashOn,
                    testo = "Classe\nenergetica",
                    valoreAttuale = classeEnergeticaScelta,
                    onValueChange = { nuovaClasseEnergetica -> classeEnergeticaScelta = nuovaClasseEnergetica }
                )
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                RigaCliccabileConErrore(
                    icona = Icons.Default.Chair, testo = "Arredamento",
                    valoreAttuale = arredamento, opzioni = listOf("Completo", "Parziale", "No"),
                    isError = showErrors && arredamento == "-",
                    onValoreCambiato = { nuovoArredamento -> arredamento = nuovoArredamento }
                )
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                // --- CARATTERISTICHE ---
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("Altre caratteristiche", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        BottoneCaratteristica(testo = "Ascensore", selezionato = statoAscensore, onClick = { statoAscensore = !statoAscensore }, modifier = Modifier.weight(1f))
                        BottoneCaratteristica(testo = "Cucina", selezionato = statoCucina, onClick = { statoCucina = !statoCucina }, modifier = Modifier.weight(1f))
                        BottoneCaratteristica(testo = "Piscina", selezionato = statoPiscina, onClick = { statoPiscina = !statoPiscina }, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        BottoneCaratteristica(testo = "Cantina", selezionato = statoCantina, onClick = { statoCantina = !statoCantina }, modifier = Modifier.weight(1f))
                        BottoneCaratteristica(testo = "Balcone", selezionato = statoBalcone, onClick = { statoBalcone = !statoBalcone }, modifier = Modifier.weight(1f))
                        BottoneCaratteristica(testo = "Terrazzo", selezionato = statoTerrazzo, onClick = { statoTerrazzo = !statoTerrazzo }, modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))


                MyButtonErrore(
                    myClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    testo = "Inserisci foto (${fotoSelezionate.size}/10)",
                    isError = showErrors && fotoSelezionate.isEmpty()
                )

                if (fotoSelezionate.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(fotoSelezionate) { uri ->
                            Box(modifier = Modifier.size(100.dp)) {
                                AsyncImage(
                                    model = uri, contentDescription = "Anteprima", contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
                                )
                                Icon(
                                    imageVector = Icons.Default.Close, contentDescription = "Rimuovi foto", tint = Color.White,
                                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(24.dp)
                                        .background(Color.Black.copy(alpha = 0.6f), shape = CircleShape)
                                        .clickable { fotoSelezionate = fotoSelezionate.filter { it != uri } }.padding(4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // BOTTONE E ANTEPRIMA PLANIMETRIA (SINGOLA)
                MyButtonErrore(
                    myClick = { planimetriaPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    testo = if (planimetriaSelezionata == null) "Inserisci planimetria" else "Planimetria (1/1)",
                    isError = showErrors && planimetriaSelezionata == null
                )

                if (planimetriaSelezionata != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.size(100.dp)) {
                        AsyncImage(
                            model = planimetriaSelezionata, contentDescription = "Anteprima Planimetria", contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
                        )
                        Icon(
                            imageVector = Icons.Default.Close, contentDescription = "Rimuovi planimetria", tint = Color.White,
                            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(24.dp)
                                .background(Color.Black.copy(alpha = 0.6f), shape = CircleShape)
                                .clickable { planimetriaSelezionata = null }.padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- TEXT FIELDS FINALI ---
                OutlinedTextField(
                    value = textFieldCitta, onValueChange = { textFieldCitta = it }, label = { Text("Città") },
                    isError = showErrors && textFieldCitta.isBlank(),
                    modifier = Modifier.padding(horizontal = 35.dp).fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = textFieldComune, onValueChange = { textFieldComune = it }, label = { Text("Comune") },
                    isError = showErrors && textFieldComune.isBlank(),
                    modifier = Modifier.padding(horizontal = 35.dp).fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = textFieldIndirizzo, onValueChange = { textFieldIndirizzo = it }, label = { Text("Indirizzo") },
                    isError = showErrors && textFieldIndirizzo.isBlank(),
                    modifier = Modifier.padding(horizontal = 35.dp).fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // --- CAMPO DESCRIZIONE (Auto-espandibile) ---
                OutlinedTextField(
                    value = textFieldDescrizione,
                    onValueChange = { textFieldDescrizione = it },
                    label = { Text("Descrizione dell'immobile") },
                    isError = showErrors && textFieldDescrizione.isBlank(),
                    modifier = Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                    singleLine = false,
                    minLines = 1
                )

                Spacer(modifier = Modifier.height(24.dp))

                // --- BOTTONE FINALE CON CONTROLLO GLOBALE E CHIAMATA KTOR ---
                MyButton(
                    myClick = {
                        val menuValidi = categoria != "-" && piano != "-" && arredamento != "-"
                        val campiValidi = prezzo.isNotBlank() && quadratura.isNotBlank() && locali.isNotBlank() && bagni.isNotBlank()

                        val testiValidi = textFieldCitta.isNotBlank() && textFieldComune.isNotBlank() &&
                                textFieldIndirizzo.isNotBlank() && textFieldDescrizione.isNotBlank()

                        val fotoValide = fotoSelezionate.isNotEmpty()
                        val planimetriaValida = planimetriaSelezionata != null

                        if (!(menuValidi && campiValidi && testiValidi && fotoValide && planimetriaValida)) {
                            showErrors = true
                            coroutineScope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar("Compila tutti i campi in rosso e inserisci le immagini mancanti!")
                            }
                        } else {
                            showErrors = false

                            coroutineScope.launch {
                                isUploading = true

                                val indirizzoCompleto = "$textFieldIndirizzo, $textFieldComune, $textFieldCitta"
                                val coordinate = cercaCoordinateConVerifica(context, indirizzoCompleto, textFieldCitta, textFieldComune)

                                // SE LE COORDINATE NON VENGONO TROVATE: MOSTRA ERRORE E BLOCCA TUTTO
                                if (coordinate == null) {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar("Indirizzo non trovato sulla mappa. Scrivi correttamente città, comune e indirizzo.")
                                    isUploading = false
                                    return@launch
                                }

                                // Se le ha trovate, le salviamo per dopo
                                val latReale = coordinate.latitude
                                val lonReale = coordinate.longitude

                                // 1. GENERIAMO L'ID IN ANTICIPO
                                val idGenerato = generaIdInserzione()

                                // 2. CARICA TUTTE LE FOTO DELL'IMMOBILE SU S3 E CREA I DTO
                                val urlsScaricati = mutableListOf<String>()
                                val listaImmaginiDTO = mutableListOf<ImmagineDTO>()
                                var ciSonoErrori = false

                                for (uri in fotoSelezionate) {
                                    val urlS3 = inviaImmagineAServer(context, uri)
                                    if (urlS3 != null) {
                                        urlsScaricati.add(urlS3)
                                        listaImmaginiDTO.add(
                                            ImmagineDTO(
                                                url = urlS3,
                                                tipologia = D_foto.FOTO,
                                                idInserzione = idGenerato
                                            )
                                        )
                                    } else {
                                        ciSonoErrori = true
                                    }
                                }

                                // 3. CARICA LA PLANIMETRIA SU S3 E CREA IL DTO
                                if (planimetriaSelezionata != null) {
                                    val urlPlanimetriaS3 = inviaImmagineAServer(context, planimetriaSelezionata!!)
                                    if (urlPlanimetriaS3 != null) {
                                        linkPlanimetriaS3 = urlPlanimetriaS3
                                        listaImmaginiDTO.add(
                                            ImmagineDTO(
                                                url = urlPlanimetriaS3,
                                                tipologia = D_foto.PLANIMETRIA,
                                                idInserzione = idGenerato
                                            )
                                        )
                                    } else {
                                        ciSonoErrori = true
                                    }
                                }

                                // 4. VERIFICA FINALE UPLOAD E CREAZIONE OGGETTO
                                if (!ciSonoErrori && urlsScaricati.isNotEmpty()) {
                                    linkS3Ottenuti = urlsScaricati

                                    val nuovaInserzione = InserzioneDTO(
                                        idInserzione = idGenerato,
                                        quadratura = quadratura.toIntOrNull() ?: 0,
                                        nBagni = bagni.toIntOrNull() ?: 0,
                                        nLocali = locali.toIntOrNull() ?: 0,
                                        piano = piano.toIntOrNull() ?: 0,
                                        prezzo = prezzo.toFloatOrNull() ?: 0f,

                                        categoria = mappaCategoria(categoria),
                                        arredamento = mappaArredamento(arredamento),
                                        classeEnergetica = mappaClasseEnergetica(classeEnergeticaScelta),

                                        balcone = statoBalcone,
                                        ascensore = statoAscensore,
                                        terrazzo = statoTerrazzo,
                                        cantina = statoCantina,
                                        piscina = statoPiscina,
                                        cucina = statoCucina,

                                        citta = textFieldCitta,
                                        comune = textFieldComune,
                                        indirizzo = textFieldIndirizzo,
                                        descrizione = textFieldDescrizione,
                                        agenteCreatore = username,

                                        latitudine = latReale,
                                        longitudine = lonReale,

                                        immagini = listaImmaginiDTO
                                    )

                                    android.util.Log.d("UPLOAD", "Foto caricate: $linkS3Ottenuti")
                                    android.util.Log.d("UPLOAD", "Planimetria: $linkPlanimetriaS3")
                                    android.util.Log.d("INSERZIONE", "Oggetto creato con successo: $nuovaInserzione")

                                    // 5. CHIAMATA A KTOR
                                    val caricamentoDbOk = inviaInserzioneAlServer(client = client, nuovaInserzione, urlsScaricati, linkPlanimetriaS3)

                                    if (caricamentoDbOk) {
                                        Toast.makeText(context, "Immobile pubblicato con successo!", Toast.LENGTH_LONG).show()
                                        navController.popBackStack()
                                    } else {
                                        snackbarHostState.showSnackbar("Impossibile salvare l'inserzione sul database.")
                                    }

                                } else {
                                    snackbarHostState.showSnackbar("Errore di rete durante il caricamento delle immagini.")
                                }
                                isUploading = false
                            }
                        }
                    },
                    testo = "Carica"
                )

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RigaSliderAgente(
    icona: ImageVector,
    testo: String,
    valoreAttuale: String,
    onValueChange: (String) -> Unit
) {
    val opzioni = listOf("Bassa", "Media", "Alta")

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
                valueRange = 0f..2f,
                steps = 1,
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

                    Text(
                        text = label,
                        fontSize = 11.sp,
                        color = if (isSelected) mioGialloOcra else mioBluScuro,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}