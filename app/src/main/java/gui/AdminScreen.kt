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
import androidx.compose.ui.Alignment
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(usernameAdmin : String, navController : NavController){
    val listState = rememberLazyListState()

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
                Card(
                    modifier = Modifier
                        .height(200.dp)
                        .width(300.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = mioBackgroung,
                    ),
                    border = BorderStroke(2.dp, mioBluScuro)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(mioBluScuro)
                            .height(35.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "I tuoi Sottoadmin",
                            fontWeight = FontWeight.Normal,
                            fontSize = 17.sp,
                            color = mieiTitoli,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(start = 15.dp)
                        )
                    }
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        val listaSottoadmin = mutableListOf(
                            "Primo@sottoadmin",
                            "Secondo@sottoadmin",
                            "Terzo@sottoadmin",
                            "Quarto@sottoadmin",
                            "Quinto@sottoadmin"
                        )
                        items(listaSottoadmin){ sottoadmin ->
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
                                    text = sottoadmin,
                                    fontSize = 16.sp,
                                    color = mieScritte,
                                    modifier = Modifier.padding(start = 15.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            myButton({}, "Crea Sottoadmin")
            Spacer(Modifier.height(60.dp))
            myButton({}, "Crea Agente")
        }
    }
}