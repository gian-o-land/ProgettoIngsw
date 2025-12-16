package gui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.window.Dialog
import gui.template.myExpandableCard_Agenti
import gui.template.myExpandableCard_Sottoadmin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(usernameAdmin : String, navController : NavController){
    var selectedSottoadmin by remember { mutableStateOf<String?>(null) }
    var selectedAgente by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    //Card Sottoadmin
    val listState_Sottoadmin = rememberLazyListState()
    val listaSottoadmin = remember {
        mutableStateListOf(
            "Primo@sottoadmin",
            "Secondo@sottoadmin",
            "Terzo@sottoadmin",
            "Quarto@sottoadmin",
            "Quinto@sottoadmin"
        )
    }
    var isExpanded_Sottoadmin by remember { mutableStateOf(false) }
    val headerHeight = 35.dp
    val itemHeight = 51.dp // 50dp (riga) + 1dp (divisore)
    val animatedHeight_Sottoadmin = animatedHeight(listaSottoadmin, isExpanded_Sottoadmin, headerHeight, itemHeight)

    //Card Agenti
    val listState_Agenti = rememberLazyListState()
    val listaAgenti = remember {
        mutableStateListOf(
            "Primo@Agenti",
            "Secondo@Agenti",
            "Terzo@Agenti",
            "Quarto@Agenti",
            "Quinto@Agenti"
        )
    }
    var isExpanded_Agenti by remember { mutableStateOf(false) }
    val animatedHeight_Agenti = animatedHeight(listaAgenti, isExpanded_Agenti, headerHeight, itemHeight)

    //PopUp Sottoadmin
    val listState_Sottoadmin_PopUp = rememberLazyListState()
    val maxItemsToShow = 3
    val visibleItemsCount = minOf(listaAgenti.size, maxItemsToShow)
    val height_Column_PopUp = itemHeight * visibleItemsCount
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
                Text(text="Ciao,\n" + usernameAdmin,
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
                myExpandableCard_Sottoadmin(
                    animatedHeight = animatedHeight_Sottoadmin,
                    headerHeight = headerHeight,
                    isExpanded = isExpanded_Sottoadmin,
                    onExpandedChange = {nuovoValore -> isExpanded_Sottoadmin = nuovoValore},
                    onSelectedChange = {nuovoSelected -> selectedSottoadmin = nuovoSelected},
                    listState = listState_Sottoadmin,
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
                myExpandableCard_Agenti(
                    animatedHeight = animatedHeight_Agenti,
                    headerHeight = headerHeight,
                    isExpanded = isExpanded_Agenti,
                    onExpandedChange = {nuovoValore -> isExpanded_Agenti = nuovoValore},
                    onSelectedChange = {nuovoSelected -> selectedAgente = nuovoSelected},
                    listState = listState_Agenti,
                    lista = listaAgenti,
                    titolo = "I tuoi Agenti"
                )
            }
            Spacer(Modifier.height(10.dp))
            myButton({}, "Crea Agente")
            Spacer(modifier = Modifier.height(30.dp))
        }
        /*if (selectedSottoadmin != null) {
            Dialog(onDismissRequest = {
                selectedSottoadmin = null
            }) {
                // Il contenuto del Popup
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = mioBackgroung
                    ),
                    border = BorderStroke(2.dp, mioBluScuro),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(mioBluScuro),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Agenti creati da:\n$selectedSottoadmin",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = mieiTitoli,
                            modifier = Modifier.padding(start = 15.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Chiudi",
                            tint = mioGialloOcra,
                            modifier = Modifier.padding(end = 10.dp)
                                .height(25.dp)
                                .width(25.dp)
                                .clickable{selectedSottoadmin = null}
                        )
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(height_Column_PopUp),
                        colors = CardDefaults.cardColors(
                            containerColor = mioBackgroung
                        ),
                        shape = RectangleShape,
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        LazyColumn(
                            state = listState_Sottoadmin_PopUp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 2.dp)
                                .simpleVerticalScrollbar(
                                    state = listState_Sottoadmin_PopUp
                                )
                        ) {
                            items(listaAgenti){ agenti ->
                                Row(
                                    modifier = Modifier.height(1.dp)
                                        .fillMaxWidth()
                                        .background(Color.Black)
                                ) { }
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .height(50.dp),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = agenti,
                                        fontSize = 16.sp,
                                        color = mieScritte,
                                        modifier = Modifier.padding(start = 15.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }*/
        if (selectedAgente != null) {
            Dialog(onDismissRequest = { selectedAgente = null }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = mioBackgroung
                    ),
                    border = BorderStroke(2.dp, mioBluScuro),
                    elevation = CardDefaults.cardElevation(8.dp)
                ){
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(mioBluScuro),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Inserzioni create da:\n$selectedAgente",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = mieiTitoli,
                            modifier = Modifier.padding(start = 15.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Chiudi",
                            tint = mioGialloOcra,
                            modifier = Modifier.padding(end = 10.dp)
                                .height(25.dp)
                                .width(25.dp)
                                .clickable{selectedAgente = null}
                        )
                    }

                }
            }
        }
    }
}