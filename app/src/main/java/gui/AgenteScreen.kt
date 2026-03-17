package gui

import DTO.InserzioneDTO
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import gui.template.MyBottomNavBar_Agente
import navigation.Screen
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import gui.template.MyCardInserzione
import kotlinx.serialization.encodeToString
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import connection.scaricaAgente
import connection.scaricaInserzioniAgente
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgenteScreen(username: String, navController: NavController) {
    val listState = rememberLazyListState()
    var listaInserzioniAgente by remember { mutableStateOf<List<InserzioneDTO>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current
    var nomeAgenzia by remember { mutableStateOf(connection.SessionManager.leggiAgenzia(context) ?: "") }

    var isLoadingAgenzia by remember { mutableStateOf(nomeAgenzia.isEmpty()) }

    // Chiamata al server all'apertura della schermata
    LaunchedEffect(username) {
        // Scarichiamo i dati aggiornati dal server
        val agenteDati = scaricaAgente(username)

        if (agenteDati != null) {
            // Aggiorniamo la variabile a schermo
            nomeAgenzia = agenteDati.nomeAgenzia

            // Salviamo l'agenzia nella memoria fissa per i futuri accessi istantanei
            connection.SessionManager.salvaSessione(context, username, 3, agenteDati.nomeAgenzia)
        }

        // Alla fine, spegniamo SEMPRE il caricamento!
        isLoadingAgenzia = false
    }

    // --- SCARICAMENTO DATI AL CARICAMENTO DELLA PAGINA ---
    LaunchedEffect(username) {
        isLoading = true
        listaInserzioniAgente = scaricaInserzioniAgente(username)

        isLoading = false
    }

    LaunchedEffect(username) {
        GestoreNotifiche.caricaStato(context, username)

        while (true) {
            val prenotazioni = connection.scaricaPrenotazioniAgente(username)

            //SCUDO PROTEZIONE BACKGROUND: Se la lista è vuota (blocco rete OS), saltiamo il giro
            if (prenotazioni.isEmpty()) {
                delay(10000L)
                continue
            }

            //L'agente viene notificato SOLO per quelle in ATTESA!
            val notificheInAttesa = prenotazioni.count { it.stato == DTO.D_stato.ATTESA }
            val notificheGiaViste = GestoreNotifiche.conteggioNotificheLette + GestoreNotifiche.conteggioNotificheNonLette.value

            if (notificheInAttesa < notificheGiaViste) {
                GestoreNotifiche.sincronizzaCancellazioni(context, notificheInAttesa, username)
            } else if (notificheInAttesa > notificheGiaViste) {
                GestoreNotifiche.salvaNuoveNotifiche(context, notificheInAttesa, username)

                mostraNotificaAndroid(
                    context = context,
                    titolo = "Nuova Prenotazione!",
                    messaggio = "Un utente ha richiesto una visita per un tuo immobile."
                )
            }
            delay(10000L)
        }
    }

    // AVVIO DEL LAVORATORE IN BACKGROUND PER L'AGENTE (APP CHIUSA)
    LaunchedEffect(username) {
        // Creiamo una richiesta per eseguire il controllo ogni 15 minuti
        val workRequest = PeriodicWorkRequestBuilder<connection.NotificheWorkerAgente>(
            15, TimeUnit.MINUTES
        ).build()

        // Lo mettiamo in coda assicurandoci che abbia un NOME UNICO
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "ControlloNotificheAgenteServer",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    if (isLoadingAgenzia) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = mioBluScuro)
        }
    } else {
        Scaffold(
            floatingActionButton = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    ExtendedFloatingActionButton(
                        onClick = { navController.navigate(Screen.CaricaImmobiliScreen.withUsernameAgente(username)) },
                        containerColor = mioGialloOcra,
                        contentColor =mioBluScuro,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Apartment, contentDescription = "Carica Immobile")
                        Text("Carica Immobile")
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White),
            containerColor = Color.White,
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = mioBluScuro
                    ),
                    title = {
                        Text(
                            text = "Ciao,\n$username",
                            fontWeight = FontWeight.Bold,
                            color = mieiTitoli,
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .height(50.dp)
                                .width(50.dp)
                        )
                    }
                )
            },
            bottomBar = {
                MyBottomNavBar_Agente(navController = navController, username)
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "La tua ageniza: $nomeAgenzia",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = mieScritte,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "I tuoi Immobili:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = mieScritte,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                }

                // --- CONTROLLO STATO: CARICAMENTO, VUOTO O LISTA PIENA ---
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = mioBluScuro)
                    }
                } else if (listaInserzioniAgente.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Non hai ancora caricato immobili.\nPremi 'Carica Immobile' per iniziare!",
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(listaInserzioniAgente) { inserzione ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // --- USIAMO LA TUA CARD REALE! ---
                                MyCardInserzione(
                                    inserzione = inserzione,
                                    myModifier = Modifier
                                        .height(200.dp)
                                        .width(320.dp),
                                    onClick = {
                                        val json = kotlinx.serialization.json.Json.encodeToString(inserzione)
                                        navController.currentBackStackEntry?.savedStateHandle?.set("inserzione_json", json)
                                        navController.navigate(Screen.InserzioneAgenteScreen.route)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}