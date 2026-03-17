package gui

import DTO.InserzioneDTO
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import gui.template.MyBottomNavBar_Sottoadmin
import gui.template.MyButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
    val headerHeight = 35.dp
    val itemHeight = 51.dp

    //STATO DEI DATI
    val listStateAgenti = rememberLazyListState()
    val listaAgenti = remember { mutableStateListOf<String>() }

    val mappaInserzioniDegliAgenti = remember { mutableStateMapOf<String, List<InserzioneDTO>>() }

    var isLoadingAgenti by remember { mutableStateOf(true) }

    // RECUPERO DATI DAL SERVER
    LaunchedEffect(usernameSottoadmin) {
        val agentiScaricati = scaricaListaAgenti(usernameSottoadmin)

        if (agentiScaricati.isNotEmpty()) {
            listaAgenti.clear()
            listaAgenti.addAll(agentiScaricati)

            agentiScaricati.forEach { nomeAgente ->
                val inserzioniVere = scaricaInserzioniAgente(nomeAgente)
                mappaInserzioniDegliAgenti[nomeAgente] = inserzioniVere
            }
        } else {
            listaAgenti.clear()
            listaAgenti.add("Nessun agente creato")
        }

        isLoadingAgenti = false
    }

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
            MyBottomNavBar_Sottoadmin(navController, usernameSottoadmin, agenziaAdmin)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .background(
                    color = mioBackgroung
                )
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

            MyExpandableCard_Agenti(
                animatedHeight = animatedHeightAgenti,
                headerHeight = headerHeight,
                isExpanded = isExpandedAgenti,
                onExpandedChange = { nuovoValore -> isExpandedAgenti = nuovoValore },
                listState = listStateAgenti,
                lista = listaAgenti,
                titolo = "I tuoi Agenti",
                listaPopup = mappaInserzioniDegliAgenti,
                itemHeight = 200.dp,
                navController = navController,
                isLoading = isLoadingAgenti
            )

            Spacer(Modifier.height(20.dp))

            MyButton(
                myClick = {
                    navController.navigate(Screen.CreazioneAgenteScreen.withArgs(usernameSottoadmin, agenziaAdmin))
                },
                testo = "Crea Agente"
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}