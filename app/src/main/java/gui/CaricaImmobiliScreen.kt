package gui

import DTO.D_arred
import DTO.D_catInser
import DTO.D_energ
import DTO.Inserzione
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType

import gui.template.myButton

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import connection.inviaImmagineAServer
import kotlinx.coroutines.launch

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale

import coil.compose.AsyncImage
import connection.client
import connection.inviaInserzioneAlServer
import gui.template.myButtonErrore


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun CaricaImmobiliScreen(navController: NavController, username: String) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // NOTA: Se hai un client Ktor globale, usa quello (es. 'KtorClient.client').
    // Altrimenti, assicurati di avere accesso al client qui dentro per passarlo alla funzione di invio.
    // val clientKtor = tuoClientGlobale

    // --- IL GESTORE DELLO SNACKBAR ---
    val snackbarHostState = remember { SnackbarHostState() }

    // --- VARIABILI DI STATO (Campi di testo finali) ---
    var textFieldCitta by remember { mutableStateOf("") }
    var textFieldComune by remember { mutableStateOf("") }
    var textFieldIndirizzo by remember { mutableStateOf("") }

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
    var ascensore by remember { mutableStateOf(false) }
    var cucina by remember { mutableStateOf(false) }
    var piscina by remember { mutableStateOf(false) }
    var cantina by remember { mutableStateOf(false) }
    var balcone by remember { mutableStateOf(false) }
    var terrazzo by remember { mutableStateOf(false) }

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
            if (showErrors && planimetriaSelezionata != null) showErrors = false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(color = Color.White),
        containerColor = Color.White,

        // --- SNACKBAR PER GLI ERRORI ---
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(horizontal = 35.dp, vertical = 5.dp)
            ) { data ->
                androidx.compose.material3.Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFFB00020), // Colore rosso errore scuro
                    contentColor = Color.White
                )
            }
        },

        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF000080) // Assicurati di usare il tuo 'mioBluScuro' se definito
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
                valoreAttuale = categoria, opzioni = listOf("Vendita","Affitto"),
                isError = showErrors && categoria == "-",
                onValoreCambiato = { nuovaCategoria -> categoria = nuovaCategoria }
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            RigaValoreConErrore(
                icona = Icons.Default.AttachMoney, testo = "Prezzo",
                valoreAttuale = prezzo,
                isError = showErrors && prezzo.isBlank(),
                onValoreCambiato = { nuovoPrezzo -> prezzo = nuovoPrezzo }
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            RigaValoreConErrore(
                icona = Icons.Default.Home, testo = "Quadratura",
                valoreAttuale = quadratura,
                isError = showErrors && quadratura.isBlank(),
                onValoreCambiato = { nuovaQuadratura -> quadratura = nuovaQuadratura }
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

            RigaSlider(
                icona = Icons.Default.FlashOn, testo = "Classe\nenergetica"
                // Assicurati che RigaSlider accetti onValoreCambiato = { nuovaClasse -> classeEnergeticaScelta = nuovaClasse }
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
                    BottoneCaratteristica("Ascensore", Modifier.weight(1f)) // Passa le lambda se il bottone lo supporta
                    BottoneCaratteristica("Cucina", Modifier.weight(1f))
                    BottoneCaratteristica("Piscina", Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BottoneCaratteristica("Cantina", Modifier.weight(1f))
                    BottoneCaratteristica("Balcone", Modifier.weight(1f))
                    BottoneCaratteristica("Terrazzo", Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // ==========================================
            // BOTTONE E GALLERIA FOTO (IMMOBILE)
            // ==========================================
            myButtonErrore(
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

            // ==========================================
            // BOTTONE E ANTEPRIMA PLANIMETRIA (SINGOLA)
            // ==========================================
            myButtonErrore(
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

            Spacer(modifier = Modifier.height(24.dp))

            // --- BOTTONE FINALE CON CONTROLLO GLOBALE E CHIAMATA KTOR ---
            myButton(
                myClick = {
                    val menuValidi = categoria != "-" && piano != "-" && arredamento != "-"
                    val campiValidi = prezzo.isNotBlank() && quadratura.isNotBlank() && locali.isNotBlank() && bagni.isNotBlank()
                    val testiValidi = textFieldCitta.isNotBlank() && textFieldComune.isNotBlank() && textFieldIndirizzo.isNotBlank()
                    val fotoValide = fotoSelezionate.isNotEmpty()
                    val planimetriaValida = planimetriaSelezionata != null

                    // Verifica che TUTTI i campi siano compilati
                    if (!(menuValidi && campiValidi && testiValidi && fotoValide && planimetriaValida)) {
                        showErrors = true
                        coroutineScope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar("Compila tutti i campi in rosso e inserisci le immagini mancanti!")
                        }
                    } else {
                        // TUTTO OK: Nascondi gli errori e inizia l'upload
                        showErrors = false

                        coroutineScope.launch {
                            isUploading = true
                            val urlsScaricati = mutableListOf<String>()
                            var ciSonoErrori = false

                            // 1. CARICA TUTTE LE FOTO DELL'IMMOBILE SU S3
                            for (uri in fotoSelezionate) {
                                val urlS3 = inviaImmagineAServer(context, uri)
                                if (urlS3 != null) urlsScaricati.add(urlS3) else ciSonoErrori = true
                            }

                            // 2. CARICA LA PLANIMETRIA SU S3
                            if (planimetriaSelezionata != null) {
                                val urlPlanimetriaS3 = inviaImmagineAServer(context, planimetriaSelezionata!!)
                                if (urlPlanimetriaS3 != null) {
                                    linkPlanimetriaS3 = urlPlanimetriaS3
                                } else {
                                    ciSonoErrori = true
                                }
                            }

                            // 3. VERIFICA FINALE UPLOAD E CREAZIONE OGGETTO
                            if (!ciSonoErrori && urlsScaricati.isNotEmpty()) {
                                linkS3Ottenuti = urlsScaricati

                                val indirizzoCompleto = "$textFieldIndirizzo, $textFieldComune, $textFieldCitta"
                                var latReale = 0.0
                                var lonReale = 0.0

                                val coordinate = cercaCoordinate(context, indirizzoCompleto) // Assicurati di avere la funzione cercaCoordinate definita

                                if (coordinate != null) {
                                    latReale = coordinate.latitude
                                    lonReale = coordinate.longitude
                                } else {
                                    android.util.Log.w("GEOCODER", "Attenzione: Indirizzo non trovato sulla mappa, salvato come 0.0, 0.0")
                                }

                                val nuovaInserzione = Inserzione(
                                    idInserzione = generaIdInserzione(), // Assicurati di avere la funzione generaIdInserzione()
                                    quadratura = quadratura.toIntOrNull() ?: 0,
                                    nBagni = bagni.toIntOrNull() ?: 0,
                                    nLocali = locali.toIntOrNull() ?: 0,
                                    piano = piano.toIntOrNull() ?: 0,
                                    prezzo = prezzo.toFloatOrNull() ?: 0f,

                                    categoria = mappaCategoria(categoria),
                                    arredamento = mappaArredamento(arredamento),
                                    classeEnergetica = mappaClasseEnergetica(classeEnergeticaScelta),

                                    balcone = balcone,
                                    ascensore = ascensore,
                                    terrazzo = terrazzo,
                                    cantina = cantina,
                                    piscina = piscina,
                                    cucina = cucina,

                                    citta = textFieldCitta,
                                    comune = textFieldComune,
                                    indirizzo = textFieldIndirizzo,
                                    agenteCreatore = username,

                                    // Inseriamo le coordinate appena trovate!
                                    latitudine = latReale,
                                    longitudine = lonReale,

                                )

                                android.util.Log.d("UPLOAD", "Foto caricate: $linkS3Ottenuti")
                                android.util.Log.d("UPLOAD", "Planimetria: $linkPlanimetriaS3")
                                android.util.Log.d("INSERZIONE", "Oggetto creato con successo: $nuovaInserzione")

                                // 4. CHIAMATA A KTOR
                                val caricamentoDbOk = inviaInserzioneAlServer(client =client, nuovaInserzione,urlsScaricati,linkPlanimetriaS3)

                                if (caricamentoDbOk) {
                                    Toast.makeText(context, "Immobile pubblicato con successo!", Toast.LENGTH_LONG).show()
                                    // Opzionale: Torna alla home
                                    // navController.popBackStack()
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
                testo = if (isUploading) "Caricamento in corso..." else "Carica"
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

fun generaIdInserzione(): String {
    // Usiamo lettere maiuscole e numeri per evitare confusione tra 'l' minuscola e '1'
    val caratteriConsentiti = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    return (1..6)
        .map { caratteriConsentiti.random() }
        .joinToString("")
}

fun mappaCategoria(valoreUI: String): D_catInser {
    return when (valoreUI) {
        "Vendita" -> D_catInser.VENDITA
        "Affitto" -> D_catInser.AFFITTO
        else -> D_catInser.VENDITA // Valore di sicurezza
    }
}

fun mappaArredamento(valoreUI: String): D_arred {
    return when (valoreUI) {
        "Completo" -> D_arred.COMPLETO
        "Parziale" -> D_arred.PARZIALE
        "No" -> D_arred.NO
        else -> D_arred.NO
    }
}

fun mappaClasseEnergetica(valoreUI: String): D_energ {
    // Se il tuo slider restituisce lettere (es. "A", "G") o parole intere, le gestiamo entrambe
    return when (valoreUI.uppercase()) {
        "ALTA"-> D_energ.ALTA
        "MEDIA"-> D_energ.MEDIA
        "BASSA"-> D_energ.BASSA
        else -> D_energ.BASSA
    }
}
@Composable
fun RigaCliccabileConErrore(
    icona: ImageVector,
    testo: String,
    valoreAttuale: String,
    opzioni: List<String>,
    isError: Boolean = false,                  // <--- AGGIUNTO: Riceve il segnale di errore
    onValoreCambiato: (String) -> Unit = {}    // <--- AGGIUNTO: Comunica la scelta alla schermata
) {
    var menuEspanso by remember { mutableStateOf(false) }

    // --- COLORI DINAMICI ---
    // Se c'è un errore diventano rossi, altrimenti usano i tuoi colori originali
    val coloreIcona = if (isError) Color(0xFFB00020) else mioBluScuro
    val coloreTesto = if (isError) Color(0xFFB00020) else Color.Black
    val coloreTestoValore = if (isError) Color(0xFFB00020) else Color.DarkGray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { menuEspanso = true }
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- PARTE SINISTRA ---
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icona,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = coloreIcona // <--- Usa il colore dinamico
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = testo,
                fontSize = 16.sp,
                color = coloreTesto // <--- Usa il colore dinamico
            )
        }

        // --- PARTE DESTRA ---
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Non usa più "selezioneAttuale", ma "valoreAttuale" passato dalla schermata
                Text(
                    text = valoreAttuale,
                    fontSize = 16.sp,
                    color = coloreTestoValore // <--- Usa il colore dinamico
                )
                val iconaFreccia = if (menuEspanso) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight
                Icon(
                    imageVector = iconaFreccia,
                    contentDescription = null,
                    tint = coloreIcona // <--- Usa il colore dinamico
                )
            }

            // --- MENU A TENDINA ---
            DropdownMenu(
                expanded = menuEspanso,
                onDismissRequest = { menuEspanso = false },
                modifier = Modifier
                    .heightIn(max = 192.dp)
                    .background(Color.White)

            ) {
                opzioni.forEachIndexed { index, opzione ->
                    DropdownMenuItem(
                        text = { Text(text = opzione, color = Color.Black) },
                        onClick = {
                            // Quando l'utente clicca, avvisiamo la schermata principale della nuova scelta
                            onValoreCambiato(opzione)
                            menuEspanso = false
                        }
                    )
                    if (index < opzioni.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun RigaValoreConErrore(
    icona: ImageVector,
    testo: String,
    valoreAttuale: String,                     // <--- AGGIUNTO: Riceve il testo digitato
    isError: Boolean = false,                  // <--- AGGIUNTO: Riceve il segnale di errore
    onValoreCambiato: (String) -> Unit = {}    // <--- AGGIUNTO: Comunica la digitazione alla schermata
) {
    // --- COLORI DINAMICI ---
    val coloreIcona = if (isError) Color(0xFFB00020) else mioBluScuro
    val coloreTesto = if (isError) Color(0xFFB00020) else Color.Black

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
            tint = coloreIcona // <--- Usa il colore dinamico
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = testo,
            fontSize = 16.sp,
            color = coloreTesto, // <--- Usa il colore dinamico
            modifier = Modifier.weight(1f)
        )

        // ⚠️ ATTENZIONE: Anche BoxInputMinMax deve essere aggiornato!
        BoxInputValore(
            valore = valoreAttuale,
            onValoreCambiato = onValoreCambiato,
            isError = isError,
            label = "Valore"
        )
    }
}




@Composable
fun BoxInputValore(
    label: String,
    valore: String,                        // <--- AGGIUNTO: Riceve il testo dal genitore
    onValoreCambiato: (String) -> Unit,    // <--- AGGIUNTO: Comunica la modifica al genitore
    isError: Boolean = false               // <--- AGGIUNTO: Segnale per far scattare il rosso
) {
    // --- COLORI DINAMICI PER L'ERRORE ---
    val coloreBordo = if (isError) Color(0xFFB00020) else Color.Black
    val coloreEtichetta = if (isError) Color(0xFFB00020) else Color.DarkGray

    BasicTextField(
        // Usiamo la variabile passata dall'esterno, non più lo stato interno
        value = valore,

        // 1. FILTRO: Accetta solo i numeri e avvisa la schermata principale
        onValueChange = { nuovoTesto ->
            val soloNumeri = nuovoTesto.filter { it.isDigit() }
            onValoreCambiato(soloNumeri)
        },

        singleLine = true,
        textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),

        // 2. TASTIERA NUMERICA
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),

        // --- MODIFICA ESTETICA QUI ---
        modifier = Modifier
            .width(90.dp)
            .height(45.dp)
            // Imposta lo sfondo bianco con gli angoli arrotondati
            .background(Color.White, RoundedCornerShape(4.dp))
            // Aggiunge il bordino dinamico: diventa ROSSO se c'è l'errore!
            .border(1.dp, coloreBordo, RoundedCornerShape(4.dp)),
        // -----------------------------

        decorationBox = { innerTextField ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // Etichetta (es. "Valore") colorata di rosso in caso di errore
                Text(text = label, fontSize = 10.sp, color = coloreEtichetta)

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Box(modifier = Modifier.weight(1f)) {
                        // Placeholder "Input"
                        if (valore.isEmpty()) {
                            Text(text = "Input", fontSize = 14.sp, color = Color.Gray)
                        }
                        // Testo digitato
                        innerTextField()
                    }

                    // Icona "X" per cancellare
                    if (valore.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Cancella",
                            modifier = Modifier
                                .size(16.dp)
                                .clickable {
                                    // Avvisiamo il genitore che vogliamo svuotare il campo
                                    onValoreCambiato("")
                                },
                            tint = mioBluScuro // Oppure usa coloreBordo se vuoi che diventi rossa anche la X!
                        )
                    }
                }
            }
        }
    )
}

