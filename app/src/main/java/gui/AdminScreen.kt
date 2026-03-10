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
import androidx.navigation.NavController
import com.example.progettoingsw.R
import gui.template.myBottomNavBar_Admin
import gui.template.myButton
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import connection.scaricaInserzioniAgente
import connection.scaricaListaAgenti
import connection.scaricaListaSottoadmin
import gui.template.MyExpandableCard_Agenti
import gui.template.MyExpandableCard_Sottoadmin
import navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(usernameAdmin : String, navController : NavController, agenziaAdmin: String){

    // 1. Liste
    val listaSottoadmin = remember { mutableStateListOf<String>() }
    val listaAgenti = remember { mutableStateListOf<String>() }

    // 2. Mappe per i Popup
    val mappaAgentiDeiSottoadmin = remember { mutableStateMapOf<String, List<String>>() }

    // --- MODIFICA 1: La mappa ora contiene direttamente le liste di InserzioneDTO! ---
    val mappaInserzioniDegliAgenti = remember { mutableStateMapOf<String, List<InserzioneDTO>>() }

    val listStateSottoadmin = rememberLazyListState()
    val listStateAgenti = rememberLazyListState()

    // 3. LAUNCHED EFFECT: Popoliamo tutto dal DB.
    LaunchedEffect(usernameAdmin) {
        val sottoadminScaricati = scaricaListaSottoadmin(usernameAdmin)
        val agentiScaricati = scaricaListaAgenti(usernameAdmin)

        // --- POPOLIAMO I SOTTOADMIN E I LORO AGENTI ---
        if (sottoadminScaricati.isNotEmpty()) {
            listaSottoadmin.clear()
            listaSottoadmin.addAll(sottoadminScaricati)

            sottoadminScaricati.forEach { nomeSottoadmin ->
                val agentiDelSotto = scaricaListaAgenti(nomeSottoadmin)
                mappaAgentiDeiSottoadmin[nomeSottoadmin] = if (agentiDelSotto.isNotEmpty()) agentiDelSotto else listOf("Nessun agente creato")
            }
        } else {
            listaSottoadmin.clear()
            listaSottoadmin.add("Nessun sottoadmin creato")
        }

        // --- POPOLIAMO GLI AGENTI E I LORO IMMOBILI REALI ---
        if (agentiScaricati.isNotEmpty()) {
            listaAgenti.clear()
            listaAgenti.addAll(agentiScaricati)

            agentiScaricati.forEach { nomeAgente ->
                // Scarichiamo i dati REALI dal database tramite i DTO
                val inserzioniVere = scaricaInserzioniAgente(nomeAgente)

                // --- MODIFICA 2: Assegniamo direttamente la lista di DTO alla mappa ---
                // Se è vuota, passerà semplicemente una lista vuota (emptyList) che il tuo popup saprà gestire
                mappaInserzioniDegliAgenti[nomeAgente] = inserzioniVere
            }
        } else {
            listaAgenti.clear()
            listaAgenti.add("Nessun agente creato")
        }
    }

    var isExpandedSottoadmin by remember { mutableStateOf(false) }
    val headerHeight = 35.dp
    val itemHeight = 51.dp
    val animatedHeightSottoadmin = animatedHeight(listaSottoadmin, isExpandedSottoadmin, headerHeight, itemHeight)

    var isExpandedAgenti by remember { mutableStateOf(false) }
    val animatedHeightAgenti = animatedHeight(listaAgenti, isExpandedAgenti, headerHeight, itemHeight)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(mioBackgroung),
        containerColor = mioBackgroung,
        topBar = { CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor =  mioBluScuro
            ),
            title = {
                Text(text= "Ciao,\n$usernameAdmin",
                    fontWeight = FontWeight.Bold,
                    color = mieiTitoli,
                    textAlign = TextAlign.Center)
            },
            navigationIcon = {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    "Logo",
                    modifier = Modifier.height(50.dp).width(50.dp)
                )
            })
        },
        bottomBar = { myBottomNavBar_Admin(navController, usernameAdmin, agenziaAdmin) }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                MyExpandableCard_Sottoadmin(
                    animatedHeight = animatedHeightSottoadmin,
                    headerHeight = headerHeight,
                    isExpanded = isExpandedSottoadmin,
                    onExpandedChange = {nuovoValore -> isExpandedSottoadmin = nuovoValore},
                    listState = listStateSottoadmin,
                    lista = listaSottoadmin,
                    titolo = "I tuoi Sottoadmin",
                    listaPopup = mappaAgentiDeiSottoadmin,
                    itemHeight = itemHeight,

                    // --- MODIFICA QUI: Cambiato nome parametro e passata la mappa dei DTO ---
                    mappaInserzioniAgenti = mappaInserzioniDegliAgenti,

                    itemHeightImmobile = 200.dp
                )
            }
            Spacer(Modifier.height(10.dp))
            myButton({
                navController.navigate(Screen.CreazioneSottoadminScreen.withArgs(usernameAdmin, agenziaAdmin))
            }, "Crea Sottoadmin")
            Spacer(Modifier.height(60.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                MyExpandableCard_Agenti(
                    animatedHeight = animatedHeightAgenti,
                    headerHeight = headerHeight,
                    isExpanded = isExpandedAgenti,
                    onExpandedChange = { nuovoValore -> isExpandedAgenti = nuovoValore},
                    listState = listStateAgenti,
                    lista = listaAgenti,
                    titolo = "I tuoi Agenti",
                    // --- MODIFICA 3: Passiamo la mappa con le vere InserzioneDTO! ---
                    listaPopup = mappaInserzioniDegliAgenti,
                    itemHeight = 200.dp
                )
            }
            Spacer(Modifier.height(10.dp))
            myButton(
                myClick = {
                    navController.navigate(Screen.CreazioneAgenteScreen.withArgs(usernameAdmin, agenziaAdmin))
                },
                testo = "Crea Agente"
            )
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}