package gui

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
import gui.template.myBottomNavBar_Admin
import gui.template.myButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import gui.template.MyExpandableCard_Agenti
import gui.template.MyExpandableCard_Sottoadmin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(usernameAdmin : String, navController : NavController){
    val scrollState = rememberScrollState()

    //Card Sottoadmin
    val listStateSottoadmin = rememberLazyListState()
    val listaSottoadmin = remember {
        mutableStateListOf(
            "Primo@sottoadmin",
            "Secondo@sottoadmin",
            "Terzo@sottoadmin",
            "Quarto@sottoadmin",
            "Quinto@sottoadmin"
        )
    }
    var isExpandedSottoadmin by remember { mutableStateOf(false) }
    val headerHeight = 35.dp
    val itemHeight = 51.dp // 50dp (riga) + 1dp (divisore)
    val animatedHeightSottoadmin = animatedHeight(listaSottoadmin, isExpandedSottoadmin, headerHeight, itemHeight)

    //Card Agenti
    val listStateAgenti = rememberLazyListState()
    val listaAgenti = remember {
        mutableStateListOf(
            "Primo@Agenti",
            "Secondo@Agenti",
            "Terzo@Agenti",
            "Quarto@Agenti",
            "Quinto@Agenti"
        )
    }
    var isExpandedAgenti by remember { mutableStateOf(false) }
    val animatedHeightAgenti = animatedHeight(listaAgenti, isExpandedAgenti, headerHeight, itemHeight)

    val indirizzo = "via sal cazzo, Piscitielli, Ita"
    //popup agente
    val listaImmobili = mapOf(
            "Via:$indirizzo\nquadratura: 30m^2\nprezzo: 200000€" to R.drawable.immobile_esempio_1,
            "immobile_esempio_2" to R.drawable.immobile_esempio_2,
            "immobile_esempio_3" to R.drawable.immobile_esempio_3,
            "immobile_esempio_4" to R.drawable.immobile_esempio_4,
            "immobile_esempio_5" to R.drawable.immobile_esempio_5
    )

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
        bottomBar = { myBottomNavBar_Admin() }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Crea\ni tuoi collaboratori",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = mieScritte,
                    textAlign = TextAlign.Center
                )
            }
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
                    listaPopup = listaAgenti,
                    itemHeight = itemHeight
                )
            }
            Spacer(Modifier.height(10.dp))
            myButton({}, "Crea Sottoadmin")
            Spacer(Modifier.height(60.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                MyExpandableCard_Agenti(
                    animatedHeight = animatedHeightAgenti,
                    headerHeight = headerHeight,
                    isExpanded = isExpandedAgenti,
                    onExpandedChange = {nuovoValore -> isExpandedAgenti = nuovoValore},
                    listState = listStateAgenti,
                    lista = listaAgenti,
                    titolo = "I tuoi Agenti",
                    listaPopup = listaImmobili,
                    itemHeight = 200.dp
                )
            }
            Spacer(Modifier.height(10.dp))
            myButton({}, "Crea Agente")
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}