package gui

import DTO.AgenteDTO
import DTO.D_foto
import DTO.InserzioneDTO
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
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Balcony
import androidx.compose.material.icons.outlined.Bathtub
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Chair
import androidx.compose.material.icons.outlined.Elevator
import androidx.compose.material.icons.outlined.SquareFoot
import androidx.compose.material.icons.outlined.Stairs
import androidx.compose.material.icons.outlined.ViewQuilt
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
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import android.content.ClipboardManager
import android.content.Context
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import navigation.Screen
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import connection.scaricaAgente

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun InserzioneScreen(
    navController: NavController,
    inserzione: InserzioneDTO,
    email: String
) {
    val context = LocalContext.current

    // --- FILTRIAMO LE IMMAGINI DAL DATABASE ---
    val fotoList = inserzione.immagini.filter { it.tipologia == D_foto.FOTO }.map { it.url }
    val planimetriaUrl = inserzione.immagini.firstOrNull { it.tipologia == D_foto.PLANIMETRIA }?.url

    val pagerState = rememberPagerState(pageCount = { fotoList.size })

    var agenteTrovato by remember { mutableStateOf<AgenteDTO?>(null) }
    var mostraPopupContatti by remember { mutableStateOf(false) }
    val numeroTelefono = agenteTrovato?.telefono ?: "Numero non disponibile"

    LaunchedEffect(inserzione.agenteCreatore) {
        agenteTrovato = scaricaAgente(inserzione.agenteCreatore)
    }
    val nomeAgente = if (agenteTrovato != null) "${agenteTrovato!!.nome} ${agenteTrovato!!.cognome}" else inserzione.agenteCreatore
    Scaffold(
        floatingActionButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(50.dp)
            ) {
                ExtendedFloatingActionButton(
                    onClick = { mostraPopupContatti = true },
                    containerColor = mioGialloOcra,
                    contentColor = mioBluScuro,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Chiama")
                    Text("Chiama")
                }

                ExtendedFloatingActionButton(
                    onClick = {
                        val rotta = "${Screen.PrenotazioneScreen.route}?email=$email&idInserzione=${inserzione.idInserzione}"
                        navController.navigate(rotta)
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
        modifier = Modifier.fillMaxSize().background(color = Color.White),
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = mioBluScuro
                ),
                title = {
                    Text(
                        text = "Inserzione",
                        fontWeight = FontWeight.Bold,
                        color = Color.White, // mieiTitoli
                        textAlign = TextAlign.Center,
                        fontSize = 17.sp
                    )
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.height(50.dp).width(50.dp)
                    )
                }
            )
        },
        bottomBar = {
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // --- CAROSELLO FOTO ---
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {

                    if (fotoList.isNotEmpty()) {
                        HorizontalPager(
                            state = pagerState,
                            contentPadding = PaddingValues(horizontal = 0.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) { pageIndex ->
                            Card(
                                modifier = Modifier.fillMaxWidth().height(220.dp),
                                shape = RectangleShape,
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                AsyncImage(
                                    model = fotoList[pageIndex],
                                    contentDescription = "Foto Immobile",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.padding(top = 10.dp, end = 10.dp),
                            color = Color.Black.copy(alpha = 0.6f),
                            contentColor = Color.White,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${pagerState.currentPage + 1} di ${fotoList.size}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxWidth().height(220.dp).background(Color.LightGray), contentAlignment = Alignment.Center) {
                            Text("Nessuna foto disponibile")
                        }
                    }
                }

                // --- INTESTAZIONE IMMOBILE (CON CONTROLLO AFFITTO/VENDITA) ---

                val suffissoPrezzo = if (inserzione.categoria.name.contains("AFFITTO", ignoreCase = true)) " / mese" else ""

                // 2. Mostriamo il prezzo dinamico
                Text(
                    text = "${inserzione.prezzo} €$suffissoPrezzo",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = mioBluScuro,
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 4.dp),
                )

                Text(
                    text = "${inserzione.indirizzo}, ${inserzione.citta}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- DESCRIZIONE ---
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp).wrapContentHeight(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "Descrizione",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 6.dp)
                    )
                    Text(
                        text = inserzione.descrizione,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // --- CARATTERISTICHE ---
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp).wrapContentHeight(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "Caratteristiche",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 6.dp)
                    )

                    Row(
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            CaratteristicaRiga(icona = Icons.Outlined.Apartment, titolo = "Categoria", valore = inserzione.categoria.name)
                            CaratteristicaRiga(icona = Icons.Outlined.SquareFoot, titolo = "Quadratura", valore = "${inserzione.quadratura} m²")
                            CaratteristicaRiga(icona = Icons.Outlined.Bathtub, titolo = "Bagni", valore = "${inserzione.nBagni}")
                            CaratteristicaRiga(icona = Icons.Outlined.ViewQuilt, titolo = "Locali", valore = "${inserzione.nLocali}")
                            CaratteristicaRiga(icona = Icons.Outlined.Stairs, titolo = "Piano", valore = "${inserzione.piano}")
                        }

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            CaratteristicaRiga(icona = Icons.Outlined.Chair, titolo = "Arredamento", valore = inserzione.arredamento.name)
                            CaratteristicaRiga(icona = Icons.Outlined.Balcony, titolo = "Balcone", valore = if (inserzione.balcone) "Sì" else "No")
                            CaratteristicaRiga(icona = Icons.Outlined.Elevator, titolo = "Ascensore", valore = if (inserzione.ascensore) "Sì" else "No")
                            CaratteristicaRiga(icona = Icons.Outlined.Bolt, titolo = "Cl. Energetica", valore = inserzione.classeEnergetica.name)
                            CaratteristicaRiga(icona = Icons.Outlined.Pool, titolo = "Piscina", valore = if (inserzione.piscina) "Sì" else "No")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // --- PLANIMETRIA ---
                if (planimetriaUrl != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp).wrapContentHeight(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color.LightGray),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column {
                            Text(
                                text = "Planimetria",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 6.dp)
                            )
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(12.dp).height(200.dp),
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                AsyncImage(
                                    model = planimetriaUrl,
                                    contentDescription = "Planimetria",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize().background(Color.White)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }

    // --- POPUP CONTATTI ---
    if (mostraPopupContatti) {
        Dialog(onDismissRequest = {mostraPopupContatti = false}){
            Card(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(2.dp, mioBluScuro),
                elevation = CardDefaults.cardElevation(8.dp)
            ){
                Row(
                    modifier = Modifier.fillMaxWidth().height(50.dp).background(mioBluScuro),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Contatta l'inserzionista", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = mieiTitoli, modifier = Modifier.padding(start = 15.dp))
                    Icon(Icons.Default.Close, "Chiudi", tint = mioGialloOcra, modifier = Modifier.padding(end = 10.dp).height(25.dp).width(25.dp).clickable { mostraPopupContatti = false })
                }
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally){
                    Row(horizontalArrangement = Arrangement.Center) {
                        Text("Agente: ", fontWeight = FontWeight.Medium, color = mioBluScuro)
                        Text(nomeAgente, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "+39 $numeroTelefono",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = mioBluScuro
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            modifier = Modifier.padding(start = 5.dp).width(130.dp),
                            onClick = {
                                val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Numero Telefono", numeroTelefono)
                                clipboardManager.setPrimaryClip(clip)
                                Toast.makeText(context, "Numero copiato!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = mioGialloOcra),
                            enabled = agenteTrovato != null
                        ) { Text("Copia", color = mioBluScuro) }

                        Button(
                            modifier = Modifier.padding(end = 5.dp).width(130.dp),
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply { data = Uri.parse("tel:$numeroTelefono") }
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = mioGialloOcra),
                            enabled = agenteTrovato != null
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp), tint = mioBluScuro)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Chiama", color = mioBluScuro)
                        }
                    }
                }
            }
        }
    }
}

// --- FUNZIONCINA DI SUPPORTO PER DISEGNARE LE RIGHE DELLE CARATTERISTICHE ---
@Composable
fun CaratteristicaRiga(icona: androidx.compose.ui.graphics.vector.ImageVector, titolo: String, valore: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icona,
            contentDescription = titolo,
            tint = mioBluScuro,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = titolo, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(text = valore, fontSize = 13.sp, color = Color.Gray)
        }
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