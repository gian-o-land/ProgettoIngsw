package gui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import gui.template.myBottomNavBar
import DTO.D_stato
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import navigation.Screen
import connection.scaricaPrenotazioniUtente
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import connection.scaricaInserzione
import connection.scaricaAgente
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import androidx.compose.ui.text.style.TextDecoration

data class Notifica(
    val indirizzo: String,
    val comune: String,
    val contenuto: String,
    val stato: D_stato,
    val codiceInserzione: String,
    val mailAgente: String,
    val telefonoAgente: String,
    val dataOra: String
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NotificheScreen(navController : NavController, email : String) {
    val scrollState = rememberLazyListState()

    val context = LocalContext.current

    var listaNotifiche by remember { mutableStateOf<List<Notifica>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // 🔥 2. Variabile per controllare quale notifica è stata cliccata
    var popupSelection by remember { mutableStateOf<Notifica?>(null) }

    LaunchedEffect(email) {
        GestoreNotifiche.azzeraPallino(context, email)
        val prenotazioni = scaricaPrenotazioniUtente(email)

        listaNotifiche = coroutineScope {
            prenotazioni.map { p ->
                async {
                    val inserzione = scaricaInserzione(p.codiceInserzione)
                    val agente = scaricaAgente(p.usernameAgente)

                    val indirizzoImmobile = if (inserzione != null) {
                        inserzione.indirizzo
                    } else {
                        "Indirizzo sconosciuto"
                    }
                    val comuneImmobile = if (inserzione != null) {
                        inserzione.comune
                    } else {
                        "Comune sconosciuto"
                    }
                    val nomeCompletoAgente = if (agente != null) "${agente.nome} ${agente.cognome}" else p.usernameAgente

                    Notifica(
                        indirizzo = indirizzoImmobile,
                        comune = comuneImmobile,
                        contenuto = nomeCompletoAgente,
                        stato = p.stato,
                        codiceInserzione = p.codiceInserzione,
                        // Salviamo anche i dati extra dell'agente
                        mailAgente = agente?.email ?: "Email non disponibile",
                        telefonoAgente = agente?.telefono ?: "Telefono non disponibile",
                        dataOra = p.dataOra
                    )
                }
            }.awaitAll()
        }
        isLoading = false
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        containerColor =  Color.White,
        topBar = { CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor =  mioBluScuro
            ),
            title = {
                Text(text="Notifiche",
                    fontWeight = FontWeight.Bold,
                    color = mieiTitoli,
                    textAlign = TextAlign.Center
                )

            },
            navigationIcon = {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    "Logo",
                    modifier = Modifier.height(50.dp).width(50.dp)
                )
            }
        )

        },
        bottomBar = {
            myBottomNavBar(
                navController = navController,
                email = email
            )
        }

    ) { innerPadding ->
        Card(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = mioBackgroung
            )
        ) {
            if (isLoading){
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = mioBluScuro)
                }
            } else if (listaNotifiche.isEmpty()){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Non hai effettuato prenotazione!",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Gestisci le\ntue prenotazione",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = mieScritte,
                        textAlign = TextAlign.Center)
                }
                LazyColumn(
                    modifier = Modifier
                        .background(mioBackgroung)
                        .fillMaxWidth(),
                    state = scrollState
                ) {
                    items(listaNotifiche.reversed()){ notifica ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clickable{
                                    popupSelection = notifica
                                },
                            shape = RoundedCornerShape(30.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = mioBackgroung
                            ),
                            border = BorderStroke(1.dp, Color.Black),
                            elevation = CardDefaults.cardElevation(defaultElevation = 9.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(16.dp)) {

                                    // 1. TESTO DEL TITOLO MULTI-COLORE
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
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp
                                    )

                                    // 2. TESTO DEL CONTENUTO MULTI-COLORE
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(color = mioBluScuro)) {
                                                append("Richiesta visita con agente:\n")
                                            }
                                            withStyle(style = SpanStyle(color = Color.Black)) {
                                                append(notifica.contenuto) // Questo ora è il nome!
                                            }
                                        },
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                Text(
                                    text = notifica.stato.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = when(notifica.stato) {
                                        D_stato.ACCETTATA -> Color(0xFF4CAF50)
                                        D_stato.RIFIUTATA -> Color.Red
                                        D_stato.ATTESA -> Color.DarkGray
                                    },
                                    modifier = Modifier.padding(end = 10.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
            }
        }
        if (popupSelection != null) {
            PopupNotificaUtente(
                selected = popupSelection,
                onSelectedChange = { popupSelection = it },
                navController = navController,
                emailUtente = email
            )
        }
    }
}

@Composable
fun PopupNotificaUtente(
    selected: Notifica?,
    onSelectedChange: (Notifica?) -> Unit,
    navController: NavController,
    emailUtente: String
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Ci serve per poter lanciare il download dell'inserzione al click del bottone!
    val coroutineScope = rememberCoroutineScope()

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

            // Usiamo Start per allineare i testi a sinistra e non al centro
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.Start) {

                // 1. VIA (Etichetta in blu scuro, valore in nero)
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = mioBluScuro, fontWeight = FontWeight.Bold)) { append("Via: ") }
                        withStyle(style = SpanStyle(color = Color.Black)) { append(selected?.indirizzo ?: "") }
                        withStyle(style = SpanStyle(color = Color.Black)) { append(", ") }
                        withStyle(style = SpanStyle(color = Color.Black)) { append(selected?.comune ?: "") }
                    },
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                // 2. DATA E ORA (Formattata per essere più leggibile)
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = mioBluScuro, fontWeight = FontWeight.Bold)) { append("Data visita: ") }
                        withStyle(style = SpanStyle(color = Color.Black)) {
                            append(selected?.dataOra?.replace("T", " alle ") ?: "")
                        }
                    },
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                // 3. EMAIL AGENTE
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = mioBluScuro, fontWeight = FontWeight.Bold)) { append("Email Agente: ") }
                        withStyle(style = SpanStyle(color = Color.DarkGray)) { append(selected?.mailAgente ?: "") }
                    },
                    fontSize = 15.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))

                // 4. TEL AGENTE
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = mioBluScuro, fontWeight = FontWeight.Bold)) { append("Tel Agente: ") }
                        withStyle(style = SpanStyle(
                            color = mioGialloOcra,
                            textDecoration = TextDecoration.Underline
                        )) {
                            append(selected?.telefonoAgente ?: "")
                        }
                    },
                    fontSize = 15.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable{
                            selected?.telefonoAgente?.let { numero ->
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:$numero")
                                }
                                context.startActivity(intent)
                            }
                        }
                )

                Spacer(modifier = Modifier.height(20.dp))

                //VAI ALL'INSERZIONE
                Button(
                    onClick = {
                        selected?.codiceInserzione?.let { idInserzione ->
                            coroutineScope.launch {
                                // 1. Scarichiamo l'inserzione completa in background
                                val inserzioneCompleta = scaricaInserzione(idInserzione)

                                if (inserzioneCompleta != null) {
                                    // 2. Passiamo l'oggetto alla pagina Inserzione tramite lo zainetto
                                    val json = kotlinx.serialization.json.Json.encodeToString(inserzioneCompleta)
                                    navController.currentBackStackEntry?.savedStateHandle?.set("inserzione_json", json)
                                    navController.currentBackStackEntry?.savedStateHandle?.set("email_utente", emailUtente)

                                    // 3. Spegniamo il popup e viaggiamo!
                                    onSelectedChange(null)
                                    navController.navigate(Screen.InserzioneScreen.route)
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = mioBluScuro, contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Vedi Inserzione", fontWeight = FontWeight.Bold)
                }

                // TASTO DI RIPRENOTAZIONE (Se lo stato è RIFIUTATA)
                if (selected?.stato == D_stato.RIFIUTATA) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            onSelectedChange(null) // Chiude il popup

                            // Reindirizza l'utente direttamente alla schermata di prenotazione per QUELLA inserzione
                            selected.codiceInserzione.let { idInserzione ->
                                val rotta = "${Screen.PrenotazioneScreen.route}?email=$emailUtente&idInserzione=${idInserzione}"
                                navController.navigate(rotta)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White, // Lo facciamo bianco con bordo rosso per differenziarlo
                            contentColor = Color.Red
                        ),
                        border = BorderStroke(1.dp, Color.Red),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Prenota di nuovo", fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}