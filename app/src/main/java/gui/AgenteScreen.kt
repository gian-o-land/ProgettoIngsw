package gui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import gui.template.myBottomNavBar_Agente
import gui.template.myCardInserzione

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgenteScreen(emailAgente: String, navController : NavController){
    val scrollState = rememberScrollState()
    val listState = rememberLazyListState()

    val indirizzo = "via sal cazzo, Piscitielli, Ita"
    val listaImmobili = mapOf(
        "Via:$indirizzo\nquadratura: 30m^2\nprezzo: 200000€" to R.drawable.immobile_esempio_1,
        "immobile_esempio_2" to R.drawable.immobile_esempio_2,
        "immobile_esempio_3" to R.drawable.immobile_esempio_3,
        "immobile_esempio_4" to R.drawable.immobile_esempio_4,
        "immobile_esempio_5" to R.drawable.immobile_esempio_5
    )
    Scaffold(
        floatingActionButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                ExtendedFloatingActionButton(
                    onClick = {},
                    containerColor = mioGialloOcra,
                    contentColor = mioBluScuro,
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
                        text = "Ciao,\n$emailAgente",
                        fontWeight = FontWeight.Bold,
                        color = mieiTitoli,
                        textAlign = TextAlign.Center
                    )

                },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        "Logo",
                        modifier = Modifier
                            .height(50.dp)
                            .width(50.dp)
                    )
                }
            )
        },
        bottomBar = {
            myBottomNavBar_Agente(navController = navController)
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Lista Immobili:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = mieScritte,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 30.dp))
            }
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 2.dp)
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 70.dp)
            ) {
                items(listaImmobili.entries.toList()){ immobile ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .height(220.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        myCardInserzione(
                            myModifier = Modifier
                                .height(200.dp)
                                .width(300.dp),
                            immobile = immobile
                        )
                    }
                }
            }
        }
    }
}