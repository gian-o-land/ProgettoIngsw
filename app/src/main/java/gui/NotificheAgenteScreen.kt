package gui

import DTO.D_stato
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.progettoingsw.R
import gui.template.myBottomNavBar_Agente
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import connection.aggiornaStatoPrenotazione
import connection.scaricaPrenotazioniAgente
import kotlinx.coroutines.launch
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.encodeToString
import navigation.Screen
import androidx.compose.ui.text.style.TextDecoration

data class NotificaAgente(
    val idPrenotazione: String,
    val indirizzo: String,
    val comune: String,
    val contenuto: String,
    val stato: D_stato,
    val codiceInserzione: String,
    val mailUtente: String,
    val telefonoUtente: String,
    val dataOra: String
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NotificheAgenteScreen(navController: NavController, username: String) {
    val scrollState = rememberLazyListState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Usiamo la nuova classe NotificaAgente!
    var listaNotifiche by remember { mutableStateOf<List<NotificaAgente>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var popupSelection by remember { mutableStateOf<NotificaAgente?>(null) }

    // Ricarica la lista (scaricando in parallelo i dati dell'utente)
    fun ricaricaDati() {
        isLoading = true
        coroutineScope.launch {
            val prenotazioni = scaricaPrenotazioniAgente(username)

            listaNotifiche = coroutineScope {
                prenotazioni.map { p ->
                    async {
                        // Scarichiamo inserzione e UTENTE
                        val inserzione = connection.scaricaInserzione(p.codiceInserzione)
                        val utente = connection.scaricaUtente(p.mailUtente)

                        val indirizzoImmobile = if (inserzione != null) {
                            "${inserzione.indirizzo}"
                        } else {
                            "Indirizzo sconosciuto"
                        }
                        val comuneImmobile = if (inserzione != null) {
                            inserzione.comune
                        } else {
                            "Comune sconosciuto"
                        }

                        val nomeCompletoUtente = if (utente != null) "${utente.nome} ${utente.cognome}" else p.mailUtente

                        NotificaAgente(
                            idPrenotazione = p.idPrenotazione,
                            indirizzo = indirizzoImmobile,
                            contenuto = nomeCompletoUtente,
                            stato = p.stato,
                            codiceInserzione = p.codiceInserzione,
                            mailUtente = utente?.email ?: p.mailUtente,
                            telefonoUtente = utente?.telefono ?: "Telefono non disponibile",
                            dataOra = p.dataOra,
                            comune = comuneImmobile
                        )
                    }
                }.awaitAll()
            }
            isLoading = false
        }
    }

    LaunchedEffect(username) {
        GestoreNotificheAgente.azzeraPallino(context)
        ricaricaDati()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(color = Color.White),
        containerColor = Color.White,
        topBar = { CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = mioBluScuro),
            title = { Text("Notifiche", fontWeight = FontWeight.Bold, color = mieiTitoli) },
            navigationIcon = { Image(painterResource(id = R.drawable.logo), "Logo", modifier = Modifier.height(50.dp).width(50.dp)) }
        )},
        bottomBar = { myBottomNavBar_Agente(navController, username) }
    ) { innerPadding ->
        Card(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = mioBackgroung)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = mioBluScuro)
                }
            } else if (listaNotifiche.isEmpty()){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Non sono presenti notifiche!",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 20.dp), horizontalArrangement = Arrangement.Center) {
                    Text("Gestisci le richieste\ndi prenotazione", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = mieScritte, textAlign = TextAlign.Center)
                }
                LazyColumn(modifier = Modifier.background(mioBackgroung).fillMaxWidth(), state = scrollState) {
                    items(listaNotifiche.reversed()) { notifica ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable { popupSelection = notifica },
                            shape = RoundedCornerShape(30.dp),
                            colors = CardDefaults.cardColors(containerColor = mioBackgroung),
                            border = BorderStroke(1.dp, Color.Black),
                            elevation = CardDefaults.cardElevation(defaultElevation = 9.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                                    // 1. VIA IN DUE COLORI
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(color = mioBluScuro)) {
                                                append("Visita in \n")
                                            }
                                            withStyle(style = SpanStyle(color = Color.Black)) {
                                                append("${notifica.indirizzo}\n") // Questo ora è l'indirizzo!
                                            }
                                            withStyle(style = SpanStyle(color = Color.Black)) {
                                                append(notifica.comune)
                                            }
                                        },
                                        fontWeight = FontWeight.SemiBold, fontSize = 16.sp
                                    )
                                    // 2. NOME UTENTE IN DUE COLORI
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(color = mioBluScuro)) { append("Richiesta da:\n") }
                                            withStyle(style = SpanStyle(color = Color.Black)) { append(notifica.contenuto) }
                                        },
                                        fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                Text(
                                    text = notifica.stato.toString(),
                                    fontWeight = FontWeight.Bold, fontSize = 17.sp,
                                    color = when(notifica.stato) {
                                        D_stato.ACCETTATA -> Color(0xFF4CAF50)
                                        D_stato.RIFIUTATA -> Color.Red
                                        D_stato.ATTESA -> Color.DarkGray
                                    },
                                    modifier = Modifier.padding(end = 15.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
            }
        }

        if (popupSelection != null) {
            PopupNotificaAgente(
                selected = popupSelection,
                onSelectedChange = { popupSelection = it },
                onStatoAggiornato = { ricaricaDati() },
                navController = navController
            )
        }
    }
}

@Composable
fun PopupNotificaAgente(
    selected: NotificaAgente?,
    onSelectedChange: (NotificaAgente?) -> Unit,
    onStatoAggiornato: () -> Unit,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Dialog(onDismissRequest = { onSelectedChange(null) }) {
        Card(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(2.dp, mioBluScuro),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(50.dp).background(mioBluScuro),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dettaglio Visita", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = mieiTitoli, modifier = Modifier.padding(start = 15.dp))
                Icon(Icons.Default.Close, "Chiudi", tint = mioGialloOcra, modifier = Modifier.padding(end = 10.dp).height(25.dp).width(25.dp).clickable { onSelectedChange(null) })
            }

            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.Start) {
                // 1. VIA
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = mioBluScuro, fontWeight = FontWeight.Bold)) { append("Via: ") }
                        withStyle(style = SpanStyle(color = Color.Black)) { append(selected?.indirizzo ?: "") }
                        withStyle(style = SpanStyle(color = Color.Black)) { append(", ") }
                        withStyle(style = SpanStyle(color = Color.Black)) { append(selected?.comune ?: "") }
                    }, fontSize = 16.sp, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                // 2. DATA E ORA
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = mioBluScuro, fontWeight = FontWeight.Bold)) { append("Data visita: ") }
                        withStyle(style = SpanStyle(color = Color.Black)) { append(selected?.dataOra?.replace("T", " alle ") ?: "") }
                    }, fontSize = 16.sp, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                // 3. EMAIL UTENTE
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = mioBluScuro, fontWeight = FontWeight.Bold)) { append("Email Utente: ") }
                        withStyle(style = SpanStyle(color = Color.DarkGray)) { append(selected?.mailUtente ?: "") }
                    }, fontSize = 15.sp, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))

                // 4. TEL UTENTE
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = mioBluScuro, fontWeight = FontWeight.Bold)) { append("Tel Utente: ") }
                        withStyle(style = SpanStyle(
                            color = mioGialloOcra,
                            textDecoration = TextDecoration.Underline
                        )) {
                            append(selected?.telefonoUtente ?: "")
                        }
                    },
                    fontSize = 15.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable{
                            selected?.telefonoUtente?.let { numero ->
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:$numero")
                                }
                                context.startActivity(intent)
                            }
                        }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // MOSTRA I BOTTONI ACCETTA/RIFIUTA SOLO SE LO STATO È "ATTESA"
                if (selected?.stato == D_stato.ATTESA) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(
                            modifier = Modifier.width(130.dp).padding(start = 15.dp),
                            onClick = {
                                coroutineScope.launch {
                                    aggiornaStatoPrenotazione(selected.idPrenotazione, D_stato.ACCETTATA)
                                    onSelectedChange(null)
                                    onStatoAggiornato()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Verde!
                        ) { Text("Accetta", fontSize = 17.sp, color = Color.White) }

                        Button(
                            modifier = Modifier.width(130.dp).padding(end = 15.dp),
                            onClick = {
                                coroutineScope.launch {
                                    aggiornaStatoPrenotazione(selected.idPrenotazione, D_stato.RIFIUTATA)
                                    onSelectedChange(null)
                                    onStatoAggiornato()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red) // Rosso!
                        ) { Text("Rifiuta", fontSize = 17.sp, color = Color.White) }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text("Hai già elaborato questa richiesta.", color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // TASTO VAI ALL'INSERZIONE
                Button(
                    onClick = {
                        selected?.codiceInserzione?.let { idInserzione ->
                            coroutineScope.launch {
                                val inserzioneCompleta = connection.scaricaInserzione(idInserzione)
                                if (inserzioneCompleta != null) {
                                    val json = kotlinx.serialization.json.Json.encodeToString(inserzioneCompleta)
                                    navController.currentBackStackEntry?.savedStateHandle?.set("inserzione_json", json)

                                    onSelectedChange(null)
                                    navController.navigate(Screen.InserzioneAgenteScreen.route)
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = mioBluScuro, contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Vedi Inserzione", fontWeight = FontWeight.Bold) }
            }
        }
    }
}