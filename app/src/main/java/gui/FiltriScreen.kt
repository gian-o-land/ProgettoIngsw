package gui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.progettoingsw.R

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Icon
import androidx.compose.material3.Text

import androidx.compose.ui.text.TextStyle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.ui.text.input.KeyboardType
import com.google.android.gms.maps.model.LatLng
import gui.template.myButton
import navigation.Screen
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltriScreen(navController: NavController, email: String) {
    Scaffold(
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
                        text = "Filtri",
                        fontWeight = FontWeight.Bold,
                        color = mieiTitoli,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .height(50.dp)
                            .width(50.dp)
                    )
                }
            )
        },
        bottomBar = {
            /*
            myBottomNavBar(
                navController = navController
            )

             */
        }
    ) { innerPadding -> // QUI INIZIA IL CONTENUTO DELLO SCAFFOLD

        Column(
            modifier = Modifier
                .padding(innerPadding) // Applica il padding qui!
                .fillMaxSize()         // Assicura che la colonna prenda tutto lo spazio
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- TITOLO ---
            Text(
                text = "Imposta i filtri\nper la tua ricerca",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // --- LISTA FILTRI ---

            RigaCliccabile(
                icona = Icons.Default.LocationCity, // Categoria
                testo = "Categoria",
                valoreAttuale = "Indifferente",
                opzioni = listOf("Indifferente","In vendita","In affitto")
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            RigaMinMax(
                icona = Icons.Default.AttachMoney, // Prezzo
                testo = "Prezzo"
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            RigaMinMax(
                icona = Icons.Default.Home, // Quadratura
                testo = "Quadratura"
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            RigaMinMax(
                icona = Icons.Default.Dashboard, // Locali
                testo = "Locali"
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            RigaBottoni(
                icona = Icons.Default.Bathtub, // Bagni
                testo = "Bagni",
                opzioni = listOf("1", "2", "3", ">3")
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            RigaCliccabile(
                icona = Icons.Default.Menu, // Piano (usa un'icona adatta per le scale)
                testo = "Piano",
                valoreAttuale = "Indifferente",
                opzioni=  listOf("1", "2", "3", "4", "5", "6", "7", "8")
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            RigaSlider(
                icona = Icons.Default.FlashOn, // Classe energetica
                testo = "Classe\nenergetica"
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            RigaCliccabile(
                icona = Icons.Default.Chair, // Arredamento
                testo = "Arredamento",
                valoreAttuale = "Indifferente",
                opzioni = listOf("Indifferente","Completo", "Parziale", "No")
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            // --- ALTRE CARATTERISTICHE ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Altre caratteristiche",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Griglia di bottoni (2 righe da 3)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BottoneCaratteristica(testo = "Ascensore", modifier = Modifier.weight(1f))
                    BottoneCaratteristica(testo = "Cucina", modifier = Modifier.weight(1f))
                    BottoneCaratteristica(testo = "Piscina", modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BottoneCaratteristica(testo = "Cantina", modifier = Modifier.weight(1f))
                    BottoneCaratteristica(testo = "Balcone", modifier = Modifier.weight(1f))
                    BottoneCaratteristica(testo = "Terrazzo", modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            myButton(
                myClick = {
                    Screen.RicercaMappaScreen.withLocation(
                        LatLng(41.9028, 12.4964), email )// APRIRA' LA MAPPA CON I FILTRI APPLICATI (ZOOMATA SU TUTTA L'ITALIA) per ora roma
                },
                testo = "Cerca"
            )
        }
    }
}

@Composable
fun RigaCliccabile(
    icona: ImageVector,
    testo: String,
    valoreAttuale: String,
    opzioni: List<String>
) {
    var menuEspanso by remember { mutableStateOf(false) }
    var selezioneAttuale by remember { mutableStateOf(valoreAttuale) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { menuEspanso = true }
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- PARTE SINISTRA ---
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icona,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = mioBluScuro // <--- ICONA PRINCIPALE BLU SCURO
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = testo, fontSize = 16.sp)
        }

        // --- PARTE DESTRA ---
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = selezioneAttuale, fontSize = 16.sp, color = Color.DarkGray)
                val iconaFreccia = if (menuEspanso) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight
                Icon(
                    imageVector = iconaFreccia,
                    contentDescription = null,
                    tint = mioBluScuro // <--- ICONA FRECCIA BLU SCURO
                )
            }

            // --- MENU A TENDINA ---
            DropdownMenu(
                expanded = menuEspanso,
                onDismissRequest = { menuEspanso = false },
                modifier = Modifier
                    .heightIn(max = 192.dp)
                    .background(Color.White)

            ) {
                opzioni.forEachIndexed { index, opzione ->
                    DropdownMenuItem(
                        text = { Text(text = opzione, color = Color.Black) },
                        onClick = {
                            selezioneAttuale = opzione
                            menuEspanso = false
                        }
                    )
                    if (index < opzioni.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RigaMinMax(icona: ImageVector, testo: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icona,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = mioBluScuro // <--- ICONA PRINCIPALE BLU SCURO
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = testo, fontSize = 16.sp, modifier = Modifier.weight(1f))

        BoxInputMinMax("Min")
        Spacer(modifier = Modifier.width(8.dp))
        BoxInputMinMax("Max")
    }
}





@Composable
fun BoxInputMinMax(label: String) {
    // Variabile di stato che salva ciò che l'utente digita
    var testoInserito by remember { mutableStateOf("") }

    BasicTextField(
        value = testoInserito,

        // 1. FILTRO: Accetta solo i numeri
        onValueChange = { nuovoTesto ->
            testoInserito = nuovoTesto.filter { it.isDigit() }
        },

        singleLine = true,
        textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),

        // 2. TASTIERA NUMERICA
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),

        // --- MODIFICA ESTETICA QUI ---
        modifier = Modifier
            .width(90.dp)
            .height(45.dp)
            // Imposta lo sfondo bianco con gli angoli arrotondati
            .background(Color.White, RoundedCornerShape(4.dp))
            // Aggiunge il bordino nero spesso 1.dp
            .border(1.dp, Color.Black, RoundedCornerShape(4.dp)),
        // -----------------------------

        decorationBox = { innerTextField ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // Etichetta (Min / Max)
                Text(text = label, fontSize = 10.sp, color = Color.DarkGray)

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Box(modifier = Modifier.weight(1f)) {
                        // Placeholder "Input"
                        if (testoInserito.isEmpty()) {
                            Text(text = "Input", fontSize = 14.sp, color = Color.Gray)
                        }
                        // Testo digitato
                        innerTextField()
                    }

                    // Icona "X" per cancellare
                    if (testoInserito.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Cancella",
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { testoInserito = "" },
                            tint = mioBluScuro
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun RigaBottoni(icona: ImageVector, testo: String, opzioni: List<String>) {
    var selezionato by remember { mutableStateOf(opzioni[0]) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icona,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = mioBluScuro
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = testo, fontSize = 16.sp, modifier = Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            opzioni.forEach { opzione ->
                val isSelected = selezionato == opzione
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        // 1. CAMBIO SFONDO: Giallo ocra se selezionato, altrimenti Blu scuro
                        .background(if (isSelected) mioGialloOcra else mioBluScuro)
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp)) // Opzionale: puoi togliere il bordo o metterlo dello stesso colore dello sfondo
                        .clickable { selezionato = opzione },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = opzione,
                        fontSize = 14.sp,
                        // 2. CAMBIO COLORE TESTO: Blu scuro se selezionato, altrimenti Bianco
                        color = if (isSelected) mioBluScuro else Color.White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RigaSlider(icona: ImageVector, testo: String) {
    var sliderPos by remember { mutableFloatStateOf(0f) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icona,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = mioBluScuro
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = testo, fontSize = 16.sp, modifier = Modifier.width(80.dp), lineHeight = 18.sp)

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {

            // 1. Definiamo i colori qui per poterli riutilizzare
            val sliderColors = SliderDefaults.colors(
                activeTrackColor = mioBluScuro,
                inactiveTrackColor = Color(0xFFEBE6ED),
                activeTickColor = mioGialloOcra,
                inactiveTickColor = mioBluScuro
            )

            Slider(
                value = sliderPos,
                onValueChange = { sliderPos = it },
                valueRange = 0f..2f,
                steps = 1,
                colors = sliderColors,

                // --- PALLINO PERSONALIZZATO ---
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .border(width = 2.dp, color = mioBluScuro, shape = CircleShape)
                            .background(color = mioGialloOcra, shape = CircleShape)
                    )
                },

                // --- LINEA PERSONALIZZATA (RIMUOVE IL BUCO) ---
                track = { sliderState ->
                    SliderDefaults.Track(
                        colors = sliderColors,
                        sliderState = sliderState,
                        thumbTrackGapSize = 0.dp // <--- QUESTO ELIMINA IL "BUCO" BIANCO!
                    )
                }
            )

            // --- ETICHETTE SOTTO LO SLIDER ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)
                    .offset(y = (-8).dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val opzioni = listOf("Bassa", "Media", "Alta")

                opzioni.forEachIndexed { index, label ->
                    val isSelected = sliderPos.roundToInt() == index

                    Text(
                        text = label,
                        fontSize = 12.sp,
                        color = if (isSelected) mioGialloOcra else mioBluScuro,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
@Composable
fun BottoneCaratteristica(testo: String, modifier: Modifier = Modifier) {
    var selezionato by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            // 1. CAMBIO SFONDO: Giallo Ocra se acceso, Blu Scuro se spento
            .background(if (selezionato) mioGialloOcra else mioBluScuro)
            .clickable { selezionato = !selezionato },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = testo,
            fontSize = 14.sp,
            // 2. CAMBIO COLORE TESTO: Blu Scuro se acceso, Bianco se spento
            color = if (selezionato) mioBluScuro else Color.White,
            fontWeight = if (selezionato) FontWeight.Bold else FontWeight.Normal // (Opzionale) Grassetto quando selezionato
        )
    }
}