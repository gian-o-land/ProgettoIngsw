package gui


import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun UtenteScreen(emailUtente:String,navController : NavController) {
    var textFieldRicerca by remember {
        mutableStateOf("")
    }
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        containerColor =  Color.White,
        topBar = { CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor =  Color(0xFF00409C)
            ),
            title = {
                Text(text="Ciao, " + emailUtente,
                    fontWeight = FontWeight.Bold,
                    color = mieiTitoli
                )

            }
        )
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Spacer(modifier = Modifier.height(120.dp))
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
                OutlinedTextField(
                    value = textFieldRicerca,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF04198C),
                        unfocusedBorderColor = Color.Gray,
                        unfocusedTextColor = Color.Black,
                        focusedTextColor = Color.Black,
                        focusedLabelColor = Color(0xFF04198C),
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = Color.Black
                    ),
                    label = {
                        Text("Cerca")
                    },
                    onValueChange = {
                        textFieldRicerca = it
                    },
                    singleLine = true,
                    modifier = Modifier.padding(horizontal = 35.dp)
                        .fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Cerca",
                        )
                    }
                )

                Spacer(modifier = Modifier.height(15.dp))

                Row(modifier = Modifier
                    //.fillMaxWidth(),
                    .padding(start = 30.dp), // Padding esterno per allineamento
                    verticalAlignment = Alignment.CenterVertically,
                    // Distribuisce gli elementi, mettendo il Text a sinistra e l'Icona a destra
                    horizontalArrangement = Arrangement.SpaceBetween){


                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Grafico crescente",
                        tint = mioGialloOcra,
                        modifier = Modifier.size(24.dp)
                            .background(color = mioBluScuro, shape = RoundedCornerShape(8.dp))

                    )
                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                    text = "Ricerche frequenti",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,

                    textAlign = TextAlign.Left,
                    color = Color.Black
                )


                }

                // ROW SCORRIBILE IN ORIZZONTALE

                Row(
                    // Riempi la larghezza del contenitore e aggiungi un padding verticale esterno
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .horizontalScroll(scrollState),

                    horizontalArrangement = Arrangement.spacedBy(10.dp),

                    ) {
                    Card(
                        modifier = Modifier
                            // Forza la larghezza per assicurare che sia scorrevole
                            .width(150.dp)
                            .height(100.dp),

                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Red),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        // Contenuto della Card
                        Box(
                            modifier = Modifier.fillMaxSize().padding(10.dp)
                        ) {
                            Text(text = "PROVA", color = Color.White)
                        }
                    }

                    Card(
                        modifier = Modifier
                            .width(150.dp)
                            .height(100.dp),

                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Red),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        // Contenuto della Card
                        Box(
                            modifier = Modifier.fillMaxSize().padding(10.dp)
                        ) {
                            Text(text = "PROVA", color = Color.White)
                        }
                    }

                    Card(
                        modifier = Modifier
                            // Forza la larghezza per assicurare che sia scorrevole
                            .width(150.dp)
                            .height(100.dp),

                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Red),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        // Contenuto della Card
                        Box(
                            modifier = Modifier.fillMaxSize().padding(10.dp)
                        ) {
                            Text(text = "PROVA", color = Color.White)
                        }
                    }

                }
            }
         },
        bottomBar = {
            myBottomNavBar()
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier

                .padding(innerPadding)
                .fillMaxSize()
        ) {}
    }
}
/*
@Preview
@Composable
fun SimpleComposablePreview() {
    UtenteScreen(
    )
}*/