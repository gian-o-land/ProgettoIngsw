package gui


import DTO.D_stato
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import gui.template.myBottomNavBar
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.progettoingsw.R
import com.google.android.gms.maps.model.LatLng
import connection.scaricaUtente
import navigation.Screen
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import connection.scaricaPrenotazioniUtente
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun UtenteScreen(emailUtente:String,navController : NavController) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val locationRoma = LatLng(41.9028, 12.4964)
    val locationMilano = LatLng(45.4642, 9.1900)
    val locationNapoli = LatLng(40.8518, 14.2681)

    var nomeUtente by remember { mutableStateOf(connection.SessionManager.leggiNome(context) ?: "") }
    var cognomeUtente by remember { mutableStateOf(connection.SessionManager.leggiCognome(context) ?: "") }

    var isLoading by remember { mutableStateOf(nomeUtente.isEmpty()) }

    LaunchedEffect(emailUtente) {
        val datiAggiornati = scaricaUtente(emailUtente)

        if (datiAggiornati != null) {
            // Aggiorniamo le variabili a schermo
            nomeUtente = datiAggiornati.nome
            cognomeUtente = datiAggiornati.cognome

            // Salviamo i dati freschi in memoria
            connection.SessionManager.salvaNomeCognome(context, datiAggiornati.nome, datiAggiornati.cognome)
        }

        // Spegniamo il caricamento a schermo intero
        isLoading = false
    }

    // 1. GESTORE PERMESSI NOTIFICHE (Per Android 13+)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    // AVVIO DEL LAVORATORE IN BACKGROUND APP CHIUSA (WORKMANAGER)
    LaunchedEffect(emailUtente) {
        // Creiamo una richiesta per eseguire il controllo periodicamente
        val workRequest = PeriodicWorkRequestBuilder<connection.NotificheWorker>(
            15, TimeUnit.MINUTES // Minimo consentito da Android
        ).build()

        // Diciamo ad Android di metterlo in coda (KEEP significa che non lo duplica se esiste già)
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "ControlloNotificheServer",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    // AVVIO DEL LAVORATORE IN BACKGROUND APP APERTA
    LaunchedEffect(emailUtente) {
        GestoreNotifiche.caricaStato(context)

        while (true) {
            val prenotazioni = scaricaPrenotazioniUtente(emailUtente)
            val notificheChiuse = prenotazioni.count { it.stato != D_stato.ATTESA }

            val notificheGiaViste = GestoreNotifiche.conteggioNotificheLette + GestoreNotifiche.conteggioNotificheNonLette.value

            //Se ci sono meno notifiche sul server rispetto a quelle salvate...
            if (notificheChiuse < notificheGiaViste) {
                // Eseguiamo l'auto-correzione al ribasso!
                GestoreNotifiche.sincronizzaCancellazioni(context, notificheChiuse)
            }
            // SE INVECE CI SONO NUOVE NOTIFICHE DAL SERVER, SUONIAMO!
            else if (notificheChiuse > notificheGiaViste) {

                // Salviamo in memoria
                GestoreNotifiche.salvaNuoveNotifiche(context, notificheChiuse)

                // Lanciamo la notifica
                mostraNotificaAndroid(
                    context = context,
                    titolo = "Aggiornamento Prenotazione",
                    messaggio = "Un agente ha risposto a una tua richiesta di visita."
                )
            }

            // Pausa veloce di soli 10 secondi!
            delay(10000L)
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = mioBluScuro)
        }
    } else {
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
                    val testoTitolo = if (nomeUtente.isNotEmpty()) {
                        "Ciao,\n$nomeUtente $cognomeUtente"
                    } else {
                        "Ciao,\nCaricamento..."
                    }

                    Text(text=testoTitolo,
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
                myBottomNavBar(navController, emailUtente)
            }

        ) { innerPadding ->

            // Appena si apre la pagina utente, chiediamo il permesso per le notifiche
            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            Column(
                modifier = Modifier
                    .padding(innerPadding)

            ) {
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Incomincia a cercare la tua futura casa",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    //.padding(vertical = 20.dp),
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { navController.navigate(Screen.FiltriScreen.route) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = mioBackgroung,
                        contentColor = mieScritte,
                    ),
                    border = BorderStroke(1.dp, Color.Gray),
                    shape = RectangleShape,
                    modifier = Modifier.padding(horizontal = 35.dp)
                        .width(400.dp)
                        .height(45.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Cerca per comune o città",
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Filtra la tua ricerca", textAlign = TextAlign.Center, fontSize = 17.sp)
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Row(modifier = Modifier
                    //.fillMaxWidth(),
                    .padding(start = 30.dp), // Padding esterno per allineamento
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween){


                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Grafico crescente",
                        tint = mioGialloOcra,
                        modifier = Modifier.size(24.dp)
                            .background(color = mioBluScuro, shape = RoundedCornerShape(8.dp))

                    )
                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Ricerche frequenti",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,

                        textAlign = TextAlign.Left,
                        color = Color.Black
                    )


                }
                Spacer(modifier = Modifier.height(15.dp))

                // ROW SCORRIBILE IN ORIZZONTALE

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        //.padding(start = 30.dp, end = 30.dp)
                        .horizontalScroll(scrollState),

                    horizontalArrangement = Arrangement.spacedBy(10.dp),

                    ) {
                    Spacer(modifier = Modifier.width(20.dp))

                    Card(
                        onClick = {navController.navigate(Screen.RicercaMappaScreen.withLocation(
                            locationNapoli, emailUtente
                        ))},
                        modifier = Modifier
                            .width(280.dp)
                            .height(165.dp),

                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(width = 1.dp, color = Color.Black),
                        colors = CardDefaults.cardColors(containerColor = mioBluScuro),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier
                            .fillMaxWidth(),
                            // verticalAlignment = Alignment.CenterVertically,
                            // horizontalArrangement = Arrangement.Absolute.Center
                        ){
                            Image(painterResource(
                                id = R.drawable.anteprima_napoli),
                                contentDescription = "anteprima napoli",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(text = "Cerca a NAPOLI", color = Color.White, fontSize = 15.sp, modifier = Modifier.padding(10.dp) )
                        }
                    }


                    Card(
                        onClick = {navController.navigate(Screen.RicercaMappaScreen.withLocation(
                            locationMilano, emailUtente
                        ))},
                        modifier = Modifier
                            .width(280.dp)
                            .height(165.dp),

                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(width = 1.dp, color = Color.Black),
                        colors = CardDefaults.cardColors(containerColor = mioBluScuro),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier
                            .fillMaxWidth(),
                            // verticalAlignment = Alignment.CenterVertically,
                            // horizontalArrangement = Arrangement.Absolute.Center
                        ){
                            Image(painterResource(
                                id = R.drawable.anteprima_milano),
                                contentDescription = "anteprima milano",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(text = "Cerca a MILANO", color = Color.White, fontSize = 15.sp, modifier = Modifier.padding(10.dp) )
                        }
                    }

                    Card(
                        onClick = {navController.navigate(Screen.RicercaMappaScreen.withLocation(
                            locationRoma, emailUtente
                        ))},
                        modifier = Modifier
                            .width(280.dp)
                            .height(165.dp),

                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(width = 1.dp, color = Color.Black),
                        colors = CardDefaults.cardColors(containerColor = mioBluScuro),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier
                            .fillMaxWidth(),
                            // verticalAlignment = Alignment.CenterVertically,
                            // horizontalArrangement = Arrangement.Absolute.Center
                        ){
                            Image(painterResource(
                                id = R.drawable.anteprima_roma),
                                contentDescription = "anteprima roma",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(text = "Cerca a ROMA", color = Color.White, fontSize = 15.sp,modifier = Modifier.padding(10.dp) )
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))

                }
                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        navController.navigate(Screen.RicercaMappaScreen.withLocation(
                            locationRoma, emailUtente))
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = mioGialloOcra,
                        contentColor = mioBluScuro
                    ),
                    modifier = Modifier.height(50.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 80.dp)
                ) {
                    Text("Cerca sulla mappa", fontSize = 17.sp)
                }
            }

        }
    }
}

