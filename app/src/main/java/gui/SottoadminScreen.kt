package gui

import DTO.InserzioneDTO
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.progettoingsw.R
import gui.template.myBottomNavBar_Sottoadmin
import gui.template.myButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import connection.scaricaInserzioniAgente
import connection.scaricaListaAgenti
import gui.template.MyExpandableCard_Agenti
import navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SottoadminScreen(
    usernameSottoadmin: String,
    navController: NavController,
    agenziaAdmin: String
) {
    // Dimensioni standard per coerenza grafica
    val headerHeight = 35.dp
    val itemHeight = 51.dp

    // 1. STATO DEI DATI
    val listStateAgenti = rememberLazyListState()
    val listaAgenti = remember { mutableStateListOf<String>() }

    // Mappa che collega ogni Agente alle sue inserzioni reali (DTO)
    val mappaInserzioniDegliAgenti = remember { mutableStateMapOf<String, List<InserzioneDTO>>() }

    // 2. RECUPERO DATI DAL SERVER
    LaunchedEffect(usernameSottoadmin) {
        // Scarichiamo gli agenti creati da questo Sottoadmin
        val agentiScaricati = scaricaListaAgenti(usernameSottoadmin)

        if (agentiScaricati.isNotEmpty()) {
            listaAgenti.clear()
            listaAgenti.addAll(agentiScaricati)

            // Per ogni agente scaricato, recuperiamo le sue inserzioni dal DB
            agentiScaricati.forEach { nomeAgente ->
                val inserzioniVere = scaricaInserzioniAgente(nomeAgente)
                mappaInserzioniDegliAgenti[nomeAgente] = inserzioniVere
            }
        } else {
            listaAgenti.clear()
            listaAgenti.add("Nessun agente creato")
        }
    }

    // Stati per l'animazione della card
    var isExpandedAgenti by remember { mutableStateOf(false) }
    val animatedHeightAgenti = animatedHeight(listaAgenti, isExpandedAgenti, headerHeight, itemHeight)

    Scaffold(
        modifier = Modifier.fillMaxSize().background(mioBluScuro),
        containerColor = mioBluScuro,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = mioBluScuro),
                title = {
                    Text(
                        text = "Ciao,\n$usernameSottoadmin",
                        fontWeight = FontWeight.Bold,
                        color = mieiTitoli,
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
        },
        bottomBar = {
            myBottomNavBar_Sottoadmin(navController, usernameSottoadmin, agenziaAdmin)
        }
    ) { innerPadding ->
        // Foglio bianco arrotondato che riempie lo schermo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .background(
                    color = mioBackgroung,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .padding(bottom = innerPadding.calculateBottomPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(30.dp))

            Text(
                text = "Gestione\nCollaboratori",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = mieScritte,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(30.dp))

            // CARD ESPANDIBILE AGENTI
            MyExpandableCard_Agenti(
                animatedHeight = animatedHeightAgenti,
                headerHeight = headerHeight,
                isExpanded = isExpandedAgenti,
                onExpandedChange = { nuovoValore -> isExpandedAgenti = nuovoValore },
                listState = listStateAgenti,
                lista = listaAgenti,
                titolo = "I tuoi Agenti",
                // Passiamo la mappa di DTO: l'errore Argument Mismatch sparisce qui!
                listaPopup = mappaInserzioniDegliAgenti,
                itemHeight = 200.dp
            )

            Spacer(Modifier.height(20.dp))

            // BOTTONE CREAZIONE
            myButton(
                myClick = {
                    navController.navigate(Screen.CreazioneAgenteScreen.withArgs(usernameSottoadmin, agenziaAdmin))
                },
                testo = "Crea Agente"
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}