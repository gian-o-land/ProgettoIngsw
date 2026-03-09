package gui

import DTO.D_stato
import DTO.PrenotazioneDTO
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NotificheAgenteScreen(navController: NavController, username: String){
    val scrollState = rememberLazyListState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var listaPrenotazioni by remember { mutableStateOf<List<PrenotazioneDTO>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var popupSelection by remember { mutableStateOf<PrenotazioneDTO?>(null) }

    // Ricarica la lista (usata all'avvio e dopo un aggiornamento)
    fun ricaricaDati() {
        coroutineScope.launch {
            listaPrenotazioni = scaricaPrenotazioniAgente(username)
            isLoading = false
        }
    }

    LaunchedEffect(username) {
        GestoreNotificheAgente.azzeraPallino(context)
        ricaricaDati()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        containerColor =  Color.White,
        topBar = { CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor =  mioBluScuro),
            title = {
                Text(text="Notifiche",
                    fontWeight = FontWeight.Bold,
                    color = mieiTitoli)
                    },
            navigationIcon = {
                Image(painterResource(id = R.drawable.logo),
                    "Logo",
                    modifier = Modifier
                        .height(50.dp)
                        .width(50.dp)
                )
            }
        )},
        bottomBar = { myBottomNavBar_Agente(navController, username) }
    ) { innerPadding ->
        Card(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = mioBackgroung
            )
        ) {
            if(isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = mioBluScuro)
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Gestisci le richieste\ndi prenotazione",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = mieScritte,
                        textAlign = TextAlign.Center
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .background(mioBackgroung)
                        .fillMaxWidth(),
                    state = scrollState
                ) {
                    items(listaPrenotazioni){ prenotazione ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable{ popupSelection = prenotazione },
                            shape = RoundedCornerShape(30.dp),
                            colors = CardDefaults.cardColors(containerColor = mioBackgroung),
                            border = BorderStroke(1.dp, Color.Black),
                            elevation = CardDefaults.cardElevation(defaultElevation = 9.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "Visita ${prenotazione.idPrenotazione}",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = prenotazione.contenuto,
                                        fontSize = 14.sp,
                                        color = Color.DarkGray,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                // Mostriamo lo stato attuale colorato!
                                Text(
                                    text = prenotazione.stato.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = when(prenotazione.stato) {
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

        if (popupSelection != null){
            PopupNotifica(
                selected = popupSelection,
                onSelectedChange = { popupSelection = it },
                onStatoAggiornato = { ricaricaDati() } // Ricarichiamo la pagina dopo il click!
            )
        }
    }
}

@Composable
fun PopupNotifica(
    selected: PrenotazioneDTO?,
    onSelectedChange: (PrenotazioneDTO?) -> Unit,
    onStatoAggiornato: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Dialog(onDismissRequest = { onSelectedChange(null) }) {
        Card(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            colors = CardDefaults.cardColors(containerColor = mioBackgroung),
            border = BorderStroke(2.dp, mioBluScuro),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(50.dp).background(mioBluScuro),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Dettaglio Visita",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = mieiTitoli,
                    modifier = Modifier.padding(start = 15.dp)
                )
                Icon(
                    Icons.Default.Close,
                    "Chiudi",
                    tint = mioGialloOcra,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .height(25.dp)
                        .width(25.dp)
                        .clickable { onSelectedChange(null) }
                )
            }

            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Immobile: ${selected?.codiceInserzione}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Utente: ${selected?.mailUtente}",
                    fontSize = 15.sp
                )
                Text(
                    text = "Data proposta: ${selected?.dataOra?.replace("T", " alle ")}",
                    fontSize = 15.sp,
                    color = Color.DarkGray
                )
            }

            // MOSTRA I BOTTONI SOLO SE LO STATO È "ATTESA"! Altrimenti l'ha già gestita.
            if (selected?.stato == D_stato.ATTESA) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                // 1. Manda l'aggiornamento al DB
                                aggiornaStatoPrenotazione(selected.idPrenotazione, D_stato.ACCETTATA)
                                // 2. Chiude il popup
                                onSelectedChange(null)
                                // 3. Aggiorna la lista
                                onStatoAggiornato()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Verde!
                    ) {
                        Text(
                        "Accetta",
                        fontSize = 17.sp,
                        color = Color.White
                        )
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                aggiornaStatoPrenotazione(selected.idPrenotazione, D_stato.RIFIUTATA)
                                onSelectedChange(null)
                                onStatoAggiornato()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red) // Rosso!
                    ) {
                        Text(
                            "Rifiuta",
                            fontSize = 17.sp,
                            color = Color.White
                        )
                    }
                }
            } else {
                // Messaggio se l'ha già gestita
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 15.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Hai già elaborato questa richiesta.",
                        color = Color.Gray,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }
}