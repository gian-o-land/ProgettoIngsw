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
import connection.scaricaTutteLeInserzioni
import gui.template.myBottomNavBar_Agente
import navigation.Screen
import androidx.compose.runtime.setValue
import gui.template.myCardInserzione
import kotlinx.serialization.encodeToString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgenteScreen(username: String, navController: NavController) {
    val listState = rememberLazyListState()

    // --- VARIABILI DI STATO PER I DATI REALI ---
    var listaInserzioniAgente by remember { mutableStateOf<List<InserzioneDTO>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // --- SCARICAMENTO DATI AL CARICAMENTO DELLA PAGINA ---
    LaunchedEffect(username) {
        isLoading = true
        // Scarichiamo tutte le inserzioni dal database
        val tutteLeInserzioni = scaricaTutteLeInserzioni()

        // Filtriamo SOLO quelle create da questo specifico agente
        listaInserzioniAgente = tutteLeInserzioni.filter { it.agenteCreatore == username }

        isLoading = false
    }

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
                    containerColor = Color(0xFF000080) // mioBluScuro
                ),
                title = {
                    Text(
                        text = "Ciao,\n$username",
                        fontWeight = FontWeight.Bold,
                        color = Color.White, // mieiTitoli
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
            myBottomNavBar_Agente(navController = navController, username)
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
                    .height(60.dp), // Un po' più alto per dare respiro
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "I tuoi Immobili:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.Black, // mieScritte
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }

            // --- CONTROLLO STATO: CARICAMENTO, VUOTO O LISTA PIENA ---
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF000080)) // mioBluScuro
                }
            } else if (listaInserzioniAgente.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                    contentPadding = PaddingValues(bottom = 80.dp) // Spazio per il FAB
                ) {
                    items(listaInserzioniAgente) { inserzione ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp), // Spazio tra una card e l'altra
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // --- USIAMO LA TUA CARD REALE! ---
                            myCardInserzione(
                                inserzione = inserzione,
                                myModifier = Modifier
                                    .height(200.dp)
                                    .width(320.dp), // Leggermente più larga per far respirare i testi
                                onClick = {
                                    // Stessa identica logica di navigazione con lo "zainetto" usata nella mappa
                                    val json = kotlinx.serialization.json.Json.encodeToString(inserzione)
                                    navController.currentBackStackEntry?.savedStateHandle?.set("inserzione_json", json)
                                    navController.navigate(Screen.InserzioneScreen.route)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}