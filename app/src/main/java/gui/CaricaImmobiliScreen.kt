package gui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType

import com.google.android.gms.maps.model.LatLng
import gui.template.myButton
import gui.template.myTextField
import navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaricaImmobiliScreen(navController: NavController, username: String ) {

    var textFieldCitta by remember {
        mutableStateOf("")
    }
    var textFieldComune by remember {
        mutableStateOf("")
    }
    var textFieldIndirizzo by remember {
        mutableStateOf("")
    }
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
                        text = "Carica Immobile",
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


            RigaCliccabile(
                icona = Icons.Default.LocationCity, // Categoria
                testo = "Categoria",
                valoreAttuale = "-",
                opzioni = listOf("In vendita","In affitto")
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            RigaValore(
                icona = Icons.Default.AttachMoney, // Prezzo
                testo = "Prezzo"
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            RigaValore(
                icona = Icons.Default.Home, // Quadratura
                testo = "Quadratura"
            )
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            RigaValore(
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
                valoreAttuale = "-",
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
                valoreAttuale = "-",
                opzioni = listOf("Completo", "Parziale", "No")
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
                        LatLng(41.9028, 12.4964) )// APRIRA' LA MAPPA CON I FILTRI APPLICATI (ZOOMATA SU TUTTA L'ITALIA) per ora roma
                },
                testo = "Inserisci foto"
            )
            Spacer(modifier = Modifier.height(24.dp))

            myButton(
                myClick = {
                    Screen.RicercaMappaScreen.withLocation(
                        LatLng(41.9028, 12.4964) )// APRIRA' LA MAPPA CON I FILTRI APPLICATI (ZOOMATA SU TUTTA L'ITALIA) per ora roma
                },
                testo = "Inserisci planimetria"
            )
            Spacer(modifier = Modifier.height(24.dp))

            myTextField(
                Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                "Città",
                textFieldCitta,
                {inputCitta -> textFieldCitta = inputCitta}
            )
            myTextField(
                Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                "Comune",
                textFieldComune,
                {inputComune -> textFieldComune = inputComune}
            )
            myTextField(
                Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                "Indirizzo",
                textFieldIndirizzo,
                {inputIndirizzo -> textFieldIndirizzo = inputIndirizzo}
            )
            Spacer(modifier = Modifier.height(24.dp))

            myButton(
                myClick = {
                    Screen.RicercaMappaScreen.withLocation(
                        LatLng(41.9028, 12.4964) )// APRIRA' LA MAPPA CON I FILTRI APPLICATI (ZOOMATA SU TUTTA L'ITALIA) per ora roma
                },
                testo = "Carica"
            )
        }
    }
}
@Composable
fun RigaValore(icona: ImageVector, testo: String) {
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

        BoxInputMinMax("Valore")

    }
}





@Composable
fun BoxInputValore(label: String) {
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

