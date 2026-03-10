package gui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.example.progettoingsw.R
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.outlined.*
import coil.compose.AsyncImage

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun InserzioneAgenteScreen(
    inserzione: InserzioneDTO
) {
    // --- FILTRIAMO LE IMMAGINI DAL DATABASE ---
    val fotoList = inserzione.immagini.filter { it.tipologia == D_foto.FOTO }.map { it.url }
    val planimetriaUrl = inserzione.immagini.firstOrNull { it.tipologia == D_foto.PLANIMETRIA }?.url

    val pagerState = rememberPagerState(pageCount = { fotoList.size })
    Scaffold(
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
                        color = mieiTitoli,
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

                // 1. Controlliamo se la categoria contiene la parola "AFFITTO" (ignorando maiuscole/minuscole)
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
}