package gui

import DTO.D_foto
import DTO.ImmagineDTO
import DTO.Inserzione
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

    // --- IL GESTORE DELLO SNACKBAR ---
    val snackbarHostState = remember { SnackbarHostState() }

    // --- VARIABILI DI STATO (Campi di testo finali) ---
    var textFieldCitta by remember { mutableStateOf("") }
    var textFieldComune by remember { mutableStateOf("") }
    var textFieldIndirizzo by remember { mutableStateOf("") }
    var textFieldDescrizione by remember { mutableStateOf("") } // <--- NUOVA VARIABILE PER LA DESCRIZIONE

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
                    containerColor = Color(0xFF000080) // mioBluScuro
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

            RigaSlider(
                icona = Icons.Default.FlashOn, testo = "Classe\nenergetica"
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
                    BottoneCaratteristica("Ascensore", Modifier.weight(1f))
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
            Spacer(modifier = Modifier.height(8.dp))

            // --- CAMPO DESCRIZIONE (Auto-espandibile) ---
            OutlinedTextField(
                value = textFieldDescrizione,
                onValueChange = { textFieldDescrizione = it },
                label = { Text("Descrizione dell'immobile") },
                isError = showErrors && textFieldDescrizione.isBlank(),
                modifier = Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                singleLine = false, // Magia: permette di andare a capo!
                minLines = 1        // Parte con altezza di una riga
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- BOTTONE FINALE CON CONTROLLO GLOBALE E CHIAMATA KTOR ---
            myButton(
                myClick = {
                    val menuValidi = categoria != "-" && piano != "-" && arredamento != "-"
                    val campiValidi = prezzo.isNotBlank() && quadratura.isNotBlank() && locali.isNotBlank() && bagni.isNotBlank()

                    // Aggiunto il controllo anche per la descrizione!
                    val testiValidi = textFieldCitta.isNotBlank() && textFieldComune.isNotBlank() &&
                            textFieldIndirizzo.isNotBlank() && textFieldDescrizione.isNotBlank()

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
                        // TUTTO OK: Nascondi gli errori e inizia il processo
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

                                val nuovaInserzione = Inserzione(
                                    idInserzione = idGenerato,
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
                                    descrizione = textFieldDescrizione, // <--- ECCO LA DESCRIZIONE AGGIUNTA QUI!
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
                                    // navController.popBackStack() // Torna alla home
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