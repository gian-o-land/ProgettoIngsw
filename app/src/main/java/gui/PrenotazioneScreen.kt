package gui


import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.progettoingsw.R
import gui.template.myBottomNavBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import gui.template.myBottoneOrario
import navigation.Screen
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@OptIn(ExperimentalMaterial3Api::class)

fun PrenotazioneScreen(navController : NavController) {
    var giornoSelezionato by remember { mutableStateOf<Long?>(null) }


    var mostraCalendario by remember { mutableStateOf(false) }

    val dataFormattata = remember(giornoSelezionato) {
        if (giornoSelezionato != null) {
            SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).format(Date(giornoSelezionato!!))
        } else {
            "Nessuna data selezionata"
        }
    }
    var orarioSelezionato by remember { mutableStateOf<LocalTime?>(null) }

    val context = LocalContext.current
    var prenotazioneEffettuata by remember { mutableStateOf(false) }
    var abilitaConfermaPrenotazione by remember { mutableStateOf(false) }

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
                        text = "Prenota una visita",
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
            myBottomNavBar(navController)
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {// --- 1. LOGICA PER BLOCCARE IL PASSATO ---
// Calcoliamo l'inizio della giornata odierna (Mezzanotte)
// per evitare che blocchi "oggi" se sono le 18:00
            val inizioOggi = remember {
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }

// --- 2. STATO DEL CALENDARIO ---
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = null,
                selectableDates = object : SelectableDates {

                    // CORREZIONE: La funzione si chiama 'isSelectableDate' (con la D maiuscola)
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis >= inizioOggi
                    }

                    // (Opzionale) Puoi anche limitare gli anni se vuoi
                    override fun isSelectableYear(year: Int): Boolean {
                        return true // Accetta tutti gli anni nel range
                    }
                }
            )

// --- 3. RECUPERO VARIABILE ---
// Questa è la variabile che userai per il Database o per prenotare
// Contiene il TIMESTAMP (Long) o null
            val giornoSelezionato = datePickerState.selectedDateMillis

// Creiamo una stringa leggibile per l'utente (es. "22/12/2025")
            val dataLeggibile = remember(giornoSelezionato) {
                if (giornoSelezionato != null) {
                    SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).format(Date(giornoSelezionato))
                } else {
                    "Nessuna data selezionata"
                }
            }

// --- 4. UI VISIVA ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp), // Un po' di aria sopra e sotto
               // shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White), // Sfondo bianco pulito
               // elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal=16.dp)
                ) {
                    // Titolo sezione
                    Text(
                        text = "Seleziona la data della visita",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    //Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = mioBluScuro),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides mioBluScuro
                        ){
                            // Il Box principale che fa da contenitore per poter sovrapporre gli elementi
                            Box(modifier = Modifier.fillMaxWidth()) {

                                // 1. IL CALENDARIO (Senza altezze forzate per evitare crash nello scroll)
                                DatePicker(
                                    state = datePickerState,
                                    title = null,
                                    headline = null,
                                    showModeToggle = false,
                                    modifier = Modifier.fillMaxWidth(), // Rimosso .height() fisso
                                    colors = DatePickerDefaults.colors(
                                        containerColor = mioBluScuro,
                                        selectedDayContainerColor = mioGialloOcra,
                                        selectedDayContentColor = mioBluScuro,
                                        todayDateBorderColor = mioGialloOcra,
                                        todayContentColor = mioGialloOcra,
                                        weekdayContentColor = Color.White,
                                        yearContentColor = Color.White,
                                        dayContentColor = Color.White,
                                        disabledDayContentColor = Color.White.copy(alpha = 0.3f),
                                        disabledYearContentColor = mioGialloOcra,
                                        selectedYearContentColor = mioGialloOcra,
                                        navigationContentColor = mioGialloOcra,
                                        subheadContentColor = mioGialloOcra
                                    )
                                )

                                // 2. LO SCUDO TRASPARENTE ANTI-CLICK
                                Box(
                                    modifier = Modifier
                                        // Usa l'allineamento nativo del Box (NIENTE "as Alignment.Horizontal")
                                        .align(Alignment.TopStart)
                                        // Impostiamo una larghezza fissa che copre solo il testo "Mese Anno"
                                        // e lascia scoperte le frecce a destra
                                        .width(200.dp)
                                        // Copriamo solo la prima riga in alto
                                        .height(60.dp)
                                        // Intercettiamo il click in modo "nativo" e sicuro
                                        .clickable(
                                            indication = null, // Rimuove l'animazione dell'onda (ripple)
                                            interactionSource = remember { MutableInteractionSource() }
                                        ) {
                                            // Lasciare vuoto: il click viene consumato qui e non passa sotto
                                        }
                                )
                            }
                        }
                    }


                    //Spacer(modifier = Modifier.height(10.dp))

                }
            }

            Text(
                text = "Seleziona la fascia oraria",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // RIGA 1 (Bottoni 1 e 2)
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 12.dp,vertical=5.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Spazio orizzontale tra i bottoni
            ) {
                myBottoneOrario(
                    id = LocalTime.of(10, 30),
                    text = "09:00 - 11:00", // Ho messo orari realistici, ma puoi scrivere "1"
                    selezionato = orarioSelezionato
                ) { orarioSelezionato = LocalTime.of(10, 30) }
                myBottoneOrario(
                    id = LocalTime.of(12, 30),
                    text = "11:00 - 13:00",
                    selezionato = orarioSelezionato
                ) { orarioSelezionato = LocalTime.of(12, 30) }
            }

            // RIGA 2 (Bottoni 3 e 4)
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 12.dp,vertical=5.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                myBottoneOrario(
                    id = LocalTime.of(14, 30),
                    text = "14:00 - 16:00",
                    selezionato = orarioSelezionato
                ) { orarioSelezionato = LocalTime.of(14, 30) }
                myBottoneOrario(
                    id = LocalTime.of(16, 30),
                    text = "16:00 - 18:00",
                    selezionato = orarioSelezionato
                ) { orarioSelezionato = LocalTime.of(16, 30) }
            }


            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Event, contentDescription = null, tint = mioBluScuro)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when {
                        // CASO 1: Hai sia la Data che l'Orario (Vince su tutto)
                        giornoSelezionato != null && orarioSelezionato != null ->
                            "Hai scelto: $dataLeggibile alle ore $orarioSelezionato"

                        // CASO 2: Hai solo la Data (ma ti manca l'orario)
                        giornoSelezionato != null ->
                            "Hai scelto: $dataLeggibile (seleziona orario)"

                        // CASO 3: Non hai ancora scelto la data
                        else ->
                            "Scegli prima una data dal calendario"
                    }
                )

            }

            if (prenotazioneEffettuata) {
                AlertDialog(
                    onDismissRequest = {
                        // Cosa succede se clicchi fuori dal popup (lo chiudiamo)
                        prenotazioneEffettuata = false
                    },
                    properties = DialogProperties(
                        dismissOnBackPress = false,     // Blocca il tasto "Indietro" del telefono
                        dismissOnClickOutside = false   // Blocca i click sullo sfondo scuro
                    ),
                    containerColor = mioBluScuro,
                    textContentColor = mioGialloOcra,
                    text = {Column(
                        modifier = Modifier.fillMaxWidth(), // Occupa tutta la larghezza
                        horizontalAlignment = Alignment.CenterHorizontally // <--- CENTRA IL CONTENUTO
                    ) {
                        Text("Prenotazione effettuata\ncon successo!",
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(20.dp)) // Spazio tra la scritta e il bottone

                        // --- IL TUO BOTTONE PERSONALIZZATO ---
                        Button(
                            onClick = {
                                prenotazioneEffettuata = false
                                navController.navigate(Screen.UtenteScreen.route) {
                                    popUpTo(Screen.UtenteScreen.route) { inclusive = true }
                                }
                            },
                            // --- COLORI ---
                            colors = ButtonDefaults.buttonColors(
                                containerColor = mioGialloOcra, // <--- CAMBIA QUI LO SFONDO (es. Color.Red, mioBluScuro...)
                                contentColor = mioBluScuro     // <--- CAMBIA QUI IL TESTO (es. Color.Black...)
                            ),
                            shape = RoundedCornerShape(12.dp) // Arrotondamento (opzionale)
                        ) {
                            Text("Torna alla Home")
                        }
                    }
                    },

                    // Lasciamo questi VUOTI perché abbiamo messo il bottone sopra
                    confirmButton = {},
                    dismissButton = {}

                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            if(giornoSelezionato != null && orarioSelezionato != null){
                abilitaConfermaPrenotazione = true
            }
            Button(
                onClick = {
                    prenotazioneEffettuata = true
                },
                enabled = abilitaConfermaPrenotazione,
               content={Text("Conferma Prenotazione")},
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = mioGialloOcra,
                    contentColor = mioBluScuro
                ),
                modifier = Modifier.height(50.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 80.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

        }
    }
}
