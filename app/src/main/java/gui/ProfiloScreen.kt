package gui


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import gui.template.myButton
import navigation.Screen
import gui.template.MyExpandableCard_Prenotazioni

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProfiloScreen(navController : NavController) {
    val listState = rememberLazyListState()
    val listaPrenotazioni = remember{
        mutableStateListOf(
            "prenotazione 1",
            "prenotazione 2",
            "prenotazione 3",
            "prenotazione 4",
            "prenotazione 5"
        )
    }
    var isExpanded by remember { mutableStateOf(false) }
    val headerHeight = 35.dp
    val itemHeight = 51.dp // 50dp (riga) + 1dp (divisore)
    val animatedHeight = animatedHeight(listaPrenotazioni, isExpanded, headerHeight, itemHeight)

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
                Text(text="Impostazioni Profilo",
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
                navController = navController
            )
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)

        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Modifica le tue\ninformazioni personali",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = mieScritte,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            myButton({navController.navigate(Screen.ModificaCredenzialiScreen.route)}, "Modifica Credenziali")
            Spacer(modifier = Modifier.height(30.dp))
            myButton({navController.navigate(Screen.ModificaPasswordScreen.route)}, "Modifica Password")
            Spacer(modifier = Modifier.height(50.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                MyExpandableCard_Prenotazioni(
                    animatedHeight = animatedHeight,
                    headerHeight = headerHeight,
                    isExpanded = isExpanded,
                    onExpandedChange = {nuovoValore -> isExpanded = nuovoValore},
                    listState = listState,
                    lista = listaPrenotazioni,
                    titolo = "Le tue prenotazioni",
                    navController = navController
                )
            }
        }
    }
}