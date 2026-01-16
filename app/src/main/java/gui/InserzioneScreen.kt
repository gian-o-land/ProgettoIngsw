package gui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Balcony
import androidx.compose.material.icons.outlined.Bathtub
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Chair
import androidx.compose.material.icons.outlined.Elevator
import androidx.compose.material.icons.outlined.SquareFoot
import androidx.compose.material.icons.outlined.Stairs
import androidx.compose.material.icons.outlined.ViewQuilt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import gui.template.myBottomNavBar
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import android.content.ClipboardManager
import android.content.Context
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import navigation.Screen

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun InserzioneScreen(idInserzione: String, navController : NavController) { // ANDRA' PASSATO PER PARAMETRO ANCHE UN OGGETTO DI TIPO "INSERZIONE"


    //var listaImmaginiImmobile by remember { mutableStateOf<List<String>>(emptyList()) }

    val listaImmaginiImmobile = mapOf(
        "immobile_esempio_1" to R.drawable.immobile_esempio_1,
        "immobile_esempio_1_1" to R.drawable.immobile_esempio_1_1,
        "immobile_esempio_2" to R.drawable.immobile_esempio_2,
        "immobile_esempio_3" to R.drawable.immobile_esempio_3,
        "immobile_esempio_4" to R.drawable.immobile_esempio_4,
        "immobile_esempio_5" to R.drawable.immobile_esempio_5
    )
    val pagerState = rememberPagerState(pageCount = { listaImmaginiImmobile.size })
    val scrollState = rememberScrollState()
    var mostraPopupContatti by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val numeroTelefono= "338123456"
    Scaffold(
        floatingActionButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(50.dp)
            ) {

                // --- BOTTONE 1 (In alto): Filtri ---
                ExtendedFloatingActionButton(
                    onClick = {
                        mostraPopupContatti = true
                    },
                    containerColor = mioGialloOcra,
                    contentColor = mioBluScuro,
                    shape = CircleShape

                ) {
                    Icon(Icons.Default.Call, contentDescription = "Chiama")
                    Text("Chiama")
                }

                // --- BOTTONE 2 (In basso): Mia Posizione ---
                ExtendedFloatingActionButton( // Versione Standard (più grande)
                    onClick = {
                        navController.navigate(Screen.PrenotazioneScreen.route)

                    },
                    containerColor = mioGialloOcra,
                    contentColor = mioBluScuro,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = "Prenota")
                    Text("Prenota")

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
                        text = "stai visualizzando:\n" + idInserzione,
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
            myBottomNavBar(navController = navController)
        }

    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopEnd // Allinea l'indicatore in alto a destra
                ) {
                    HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = 0.dp), // Lascia intravedere le card laterali
                        pageSpacing = 10.dp, // Spazio tra le card
                        modifier = Modifier.fillMaxWidth()
                    ) { pageIndex ->// servirà quando gli passero la lista delle immagini dell'inserzione
                        Card(
                            modifier = Modifier
                                .fillMaxWidth() // La larghezza è gestita dal Pager
                                .height(180.dp),
                            shape = RectangleShape,
                            colors = CardDefaults.cardColors(containerColor = Color.Red),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Text(text = "foto " + pageIndex)
                        }

                    }
                    if (listaImmaginiImmobile.isNotEmpty()) {
                        Surface(
                            modifier = Modifier
                                .padding(
                                    top = 10.dp,
                                    end = 45.dp
                                ), // Padding per staccarlo dai bordi (45dp per compensare il padding del pager)
                            //shape = RoundedCornerShape(50),
                            color = Color.Black.copy(alpha = 0.6f),
                            contentColor = Color.White
                        ) {
                            Text(
                                // +1 perché l'indice parte da 0
                                text = "${pagerState.currentPage + 1} di ${listaImmaginiImmobile.size}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                }
                val prezzoImmobile = 150000
                Text(
                    text = "€" + prezzoImmobile,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                )
                val indirizzoImmobile = "via delle rose, 123"
                Text(
                    text = indirizzoImmobile,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth() // La larghezza è gestita dal Pager
                        .padding(horizontal = 10.dp)
                        .wrapContentHeight(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.Black),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "Descrizione",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                    val descrizioneImmobile =
                        "descrizione molto bella e molto prolissa di questa inestimabile casa molto bella e molto prolissa di questa inestimabile casa molto bella e molto prolissa di questa inestimabile casa"
                    Text(
                        text = descrizioneImmobile,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)

                    )

                }


                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .wrapContentHeight(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.Black),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "Caratteristiche",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )

                    Row(
                        modifier = Modifier
                            .wrapContentHeight(),
                    ) {
                        Column() {
                            Row() {
                                Icon(
                                    imageVector = Icons.Outlined.Apartment,
                                    contentDescription = "Categoria",
                                    tint = Color.Black,
                                    modifier = Modifier.size(40.dp)

                                )
                                Column() {
                                    Text(
                                        text = "Categoria",
                                        fontSize = 16.sp,
                                    )
                                    Text(
                                        text = "In Vendita",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            Row() {
                                Icon(
                                    imageVector = Icons.Outlined.SquareFoot,
                                    contentDescription = "Quadratura",
                                    tint = Color.Black,
                                    modifier = Modifier.size(40.dp)

                                )

                                Column() {
                                    Text(
                                        text = "Quadratura",
                                        fontSize = 16.sp,
                                    )
                                    Text(
                                        text = "PLACEHOLDER",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            Row() {

                                Icon(
                                    imageVector = Icons.Outlined.Bathtub,
                                    contentDescription = "Numero Bagni",
                                    tint = Color.Black,
                                    modifier = Modifier.size(40.dp)
                                )
                                Column() {
                                    Text(
                                        text = "Numero Bagni",
                                        fontSize = 16.sp,
                                    )
                                    Text(
                                        text = "PLACEHOLDER",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Row() {
                                Icon(
                                    imageVector = Icons.Outlined.ViewQuilt,
                                    contentDescription = "Numero Locali",
                                    tint = Color.Black,
                                    modifier = Modifier.size(40.dp)
                                )
                                Column() {
                                    Text(
                                        text = "Numero Locali",
                                        fontSize = 16.sp,
                                    )
                                    Text(
                                        text = "PLACEHOLDER",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            Row() {

                                Icon(
                                    imageVector = Icons.Outlined.Stairs,
                                    contentDescription = "Scale",
                                    tint = Color.Black,
                                    modifier = Modifier.size(40.dp)
                                )
                                Column() {
                                    Text(
                                        text = "Scale",
                                        fontSize = 16.sp,
                                    )
                                    Text(
                                        text = "PLACEHOLDER",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                        }

                        Spacer(
                            modifier = Modifier.width(30.dp)
                        )

                        Column(
                        ) {
                            Row() {
                                Icon(
                                    imageVector = Icons.Outlined.Chair,
                                    contentDescription = "Arredamento",
                                    tint = Color.Black,
                                    modifier = Modifier.size(40.dp)
                                )
                                Column() {
                                    Text(
                                        text = "Arredamento",
                                        fontSize = 16.sp,
                                    )
                                    Text(
                                        text = "PLACEHOLDER",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }

                            }
                            Row() {
                                Icon(
                                    imageVector = Icons.Outlined.Balcony,
                                    contentDescription = "Balcone",
                                    tint = Color.Black,
                                    modifier = Modifier.size(40.dp)
                                )
                                Column() {
                                    Text(
                                        text = "Balcone",
                                        fontSize = 16.sp,
                                    )
                                    Text(
                                        text = "PLACEHOLDER",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Row() {
                                Icon(
                                    imageVector = Icons.Outlined.Elevator,
                                    contentDescription = "Ascensore",
                                    tint = Color.Black,
                                    modifier = Modifier.size(40.dp)
                                )
                                Column() {
                                    Text(
                                        text = "Ascensore",
                                        fontSize = 16.sp,
                                    )
                                    Text(
                                        text = "PLACEHOLDER",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            Row() {
                                Icon(
                                    imageVector = Icons.Outlined.Bolt,
                                    contentDescription = "Classe Energetica",
                                    tint = Color.Black,
                                    modifier = Modifier.size(40.dp)
                                )
                                Column() {
                                    Text(
                                        text = "Classe Energetica",
                                        fontSize = 16.sp,
                                    )
                                    Text(
                                        text = "PLACEHOLDER",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .wrapContentHeight(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.Black),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column() {
                        Text(
                            text = "Planimetria",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth() // La larghezza è gestita dal Pager
                                .wrapContentHeight(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Red),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Image(
                                painterResource(
                                    id = R.drawable.planimetria_1
                                ),
                                contentDescription = "planimetria",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                    }

                }

                Spacer(
                    modifier = Modifier.height(90.dp)
                )
            }

        }
        }
    if (mostraPopupContatti) {
        AlertDialog(
            onDismissRequest = {
                // Cosa succede se clicchi fuori dal popup (lo chiudiamo)
                mostraPopupContatti = false
            },
            icon = {
                Icon(Icons.Default.ContactPhone, contentDescription = null)
            },
            title = {
                Text(text = "Contatta l'inserzionista")
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Puoi chiamare il seguente numero:")
                    Spacer(modifier = Modifier.height(10.dp))

                    // IL NUMERO DI TELEFONO IN GRANDE
                    Text(
                        text = "+39 333 1234567", // Qui metterai inserzione.telefono
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = mioBluScuro
                    )
                }
            },

            confirmButton = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Spazio tra i bottoni
                ) {
                    // 1. Tasto COPIA (Stile leggero)
                    OutlinedButton(
                        onClick = {
                            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Numero Telefono", numeroTelefono)
                            clipboardManager.setPrimaryClip(clip)
                            //Toast.makeText(context, "Copiato!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Copia")
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$numeroTelefono")
                            }
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Verde
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chiama")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostraPopupContatti = false
                    }
                ) {
                    Text("Chiudi")
                }
            }


        )
    }
    }










/*
Column {
    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 30.dp), // Lascia intravedere le card laterali
        pageSpacing = 10.dp, // Spazio tra le card
        modifier = Modifier.fillMaxWidth()
    ) { pageIndex ->
        val immagine = listaImmaginiImmobile[pageIndex]
        Card(
            modifier = Modifier
                .fillMaxWidth() // La larghezza è gestita dal Pager
                .height(165.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White), // Usa i tuoi colori
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column {
                Image(
                    painter = painterResource(immagine),
                    "immobile",
                    modifier = Modifier.height(700.dp).width(400.dp)
                )

            }
        }


    }


}

}
        */