package gui


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import gui.template.myBottomNavBar
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.progettoingsw.R
import com.google.android.gms.maps.model.LatLng
import navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun UtenteScreen(emailUtente:String,navController : NavController) {
    var textFieldRicerca by remember {
        mutableStateOf("")
    }

    val scrollState = rememberScrollState()
    val locationRoma = LatLng(41.9028, 12.4964)
    val locationMilano = LatLng(45.4642, 9.1900)
    val locationNapoli = LatLng(40.8518, 14.2681)
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
                Text(text="Ciao,\n" + emailUtente,
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
            myBottomNavBar(navController, emailUtente)
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)

        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Incomincia a cercare la tua futura casa",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 20.dp),
                //.padding(vertical = 20.dp),
                textAlign = TextAlign.Center,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { navController.navigate(Screen.FiltriScreen.route) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = mioBackgroung,
                    contentColor = mieScritte,
                ),
                border = BorderStroke(1.dp, Color.Gray),
                shape = RectangleShape,
                modifier = Modifier.padding(horizontal = 35.dp)
                    .width(400.dp)
                    .height(45.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Cerca per comune o città",
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Text("Cerca per comune o città", textAlign = TextAlign.Center, fontSize = 17.sp)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(modifier = Modifier
                //.fillMaxWidth(),
                .padding(start = 30.dp), // Padding esterno per allineamento
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween){


                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = "Grafico crescente",
                    tint = mioGialloOcra,
                    modifier = Modifier.size(24.dp)
                        .background(color = mioBluScuro, shape = RoundedCornerShape(8.dp))

                )
                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Ricerche frequenti",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,

                    textAlign = TextAlign.Left,
                    color = Color.Black
                )


            }
            Spacer(modifier = Modifier.height(15.dp))

            // ROW SCORRIBILE IN ORIZZONTALE

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    //.padding(start = 30.dp, end = 30.dp)
                    .horizontalScroll(scrollState),

                horizontalArrangement = Arrangement.spacedBy(10.dp),

                ) {
                Spacer(modifier = Modifier.width(20.dp))

                Card(
                    onClick = {navController.navigate(Screen.RicercaMappaScreen.withLocation(
                        locationNapoli, emailUtente
                    ))},
                    modifier = Modifier
                        .width(280.dp)
                        .height(165.dp),

                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(width = 1.dp, color = Color.Black),
                    colors = CardDefaults.cardColors(containerColor = mioBluScuro),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                        Column(modifier = Modifier
                            .fillMaxWidth(),
                           // verticalAlignment = Alignment.CenterVertically,
                           // horizontalArrangement = Arrangement.Absolute.Center
                        ){
                            Image(painterResource(
                                id = R.drawable.anteprima_napoli),
                                contentDescription = "anteprima napoli",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(text = "Cerca a NAPOLI", color = Color.White, fontSize = 15.sp, modifier = Modifier.padding(10.dp) )
                        }
                    }


                Card(
                    onClick = {navController.navigate(Screen.RicercaMappaScreen.withLocation(
                        locationMilano, emailUtente
                    ))},
                    modifier = Modifier
                        .width(280.dp)
                        .height(165.dp),

                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(width = 1.dp, color = Color.Black),
                    colors = CardDefaults.cardColors(containerColor = mioBluScuro),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier
                        .fillMaxWidth(),
                        // verticalAlignment = Alignment.CenterVertically,
                        // horizontalArrangement = Arrangement.Absolute.Center
                    ){
                        Image(painterResource(
                            id = R.drawable.anteprima_milano),
                            contentDescription = "anteprima milano",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(text = "Cerca a MILANO", color = Color.White, fontSize = 15.sp, modifier = Modifier.padding(10.dp) )
                    }
                }

                Card(
                    onClick = {navController.navigate(Screen.RicercaMappaScreen.withLocation(
                        locationRoma, emailUtente
                    ))},
                    modifier = Modifier
                        .width(280.dp)
                        .height(165.dp),

                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(width = 1.dp, color = Color.Black),
                    colors = CardDefaults.cardColors(containerColor = mioBluScuro),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier
                        .fillMaxWidth(),
                        // verticalAlignment = Alignment.CenterVertically,
                        // horizontalArrangement = Arrangement.Absolute.Center
                    ){
                        Image(painterResource(
                            id = R.drawable.anteprima_roma),
                            contentDescription = "anteprima roma",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(text = "Cerca a ROMA", color = Color.White, fontSize = 15.sp,modifier = Modifier.padding(10.dp) )
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))

                }
            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    navController.navigate(Screen.RicercaMappaScreen.withLocation(
                        locationRoma, emailUtente))
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = mioGialloOcra,
                    contentColor = mioBluScuro
                ),
                modifier = Modifier.height(50.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 80.dp)
            ) {
                Text("Cerca sulla mappa", fontSize = 17.sp)
            }
            }

        }
    }

