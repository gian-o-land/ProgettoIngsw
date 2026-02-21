package gui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.progettoingsw.R
import gui.template.myBottomNavBar_Agente
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog


data class Notifica(val titolo: String, val contenuto: String)
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NotificheAgenteScreen(navController: NavController, username: String){
    val scrollState = rememberLazyListState()

    // Dati fittizi che riproducono l'immagine
    val listaNotifiche = remember {
        listOf(
            Notifica("Notifica 1", "<<contenuto notifica>>"),
            Notifica("Notifica 2", "<<contenuto notifica>>"),
            Notifica("Notifica 3", "<<contenuto notifica>>"),
            Notifica("Notifica 4", "<<contenuto notifica>>"),
            Notifica("Notifica 5", "<<contenuto notifica>>"),
            Notifica("Notifica 6", "<<contenuto notifica>>"),
            Notifica("Notifica 7", "<<contenuto notifica>>"),
            Notifica("Notifica 8", "<<contenuto notifica>>")
        )
    }
    var popupSelection by remember { mutableStateOf<Notifica?>(null) }

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
                Text(text="Notifiche",
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
            myBottomNavBar_Agente(navController, username)
        }
    ) { innerPadding ->
        Card(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = mioBackgroung
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Gestisci le rischieste\ndi prenotazione",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = mieScritte,
                    textAlign = TextAlign.Center)
            }
            LazyColumn(
                modifier = Modifier
                    .background(mioBackgroung)
                    .fillMaxWidth(),
                state = scrollState
            ) {
                items(listaNotifiche){ notifica ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable{
                                popupSelection = notifica
                            },
                        shape = RoundedCornerShape(30.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = mioBackgroung
                        ),
                        border = BorderStroke(1.dp, Color.Black),
                        elevation = CardDefaults.cardElevation(defaultElevation = 9.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = notifica.titolo,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                            Text(
                                text = notifica.contenuto,
                                fontSize = 14.sp,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
        if (popupSelection != null){
            PopupNotifica(
                popupSelection,
                onSelectedChange = { nuovoValore ->
                    // Se nuovoValore è null (chiusura), aggiorniamo lo stato locale
                    popupSelection = nuovoValore
                }
            )
        }
    }
}

@Composable
fun PopupNotifica(
    selected: Notifica?,
    onSelectedChange: (Notifica?) -> Unit
) {
    Dialog(
        onDismissRequest = {
            onSelectedChange(null)
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp),
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
                    text = "Notifica:\n${selected?.titolo}",
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
                        .clickable { onSelectedChange(null) }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selected?.contenuto}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = mieScritte
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = mioGialloOcra,
                        contentColor = mioBluScuro
                    ),
                    modifier = Modifier.height(50.dp)
                        .width(150.dp)
                        .padding(start = 15.dp)
                ) {
                    Text("Accetta", fontSize = 17.sp)
                }
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = mioGialloOcra,
                        contentColor = mioBluScuro
                    ),
                    modifier = Modifier.height(50.dp)
                        .width(150.dp)
                        .padding(end = 15.dp)
                ) {
                    Text("Rifiuta", fontSize = 17.sp)
                }
            }
        }

    }
}