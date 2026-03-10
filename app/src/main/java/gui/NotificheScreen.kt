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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NotificheScreen(navController : NavController, email : String) {
    val scrollState = rememberLazyListState()

    data class Notifica(val titolo: String, val contenuto: String, val stato: D_stato, val codiceInserzione: String)
    var listaNotifiche by remember { mutableStateOf<List<Notifica>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    // Scarichiamo i dati all'avvio della pagina
    LaunchedEffect(email) {
        GestoreNotifiche.azzeraPallino(context)

        val prenotazioni = scaricaPrenotazioniUtente(email)

        listaNotifiche = prenotazioni.map { p ->
            Notifica(
                titolo = "Visita \n${p.idPrenotazione}",
                contenuto = p.contenuto,
                stato = p.stato,
                codiceInserzione = p.codiceInserzione
            )
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
                                    navController.navigate(Screen.InserzioneScreen.route)
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
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                                ) {
                                    Text(
                                        text = notifica.titolo,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = notifica.contenuto,
                                        fontSize = 14.sp,
                                        color = Color.DarkGray,
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
    }
}