package gui


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
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import gui.template.myBottoneOrario
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Date
import java.util.Locale


import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun PrenotazioneScreen(navController: NavController) {
    var orarioSelezionato by remember { mutableStateOf<LocalTime?>(null) }
    var prenotazioneEffettuata by remember { mutableStateOf(false) }

    // --- LOGICA DATE CALENDARIO ---
    val adessoMillis = System.currentTimeMillis()
    val unGiornoMillis = 86400000L
    val limiteMillis = adessoMillis + (unGiornoMillis * 14)

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = null,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis in (adessoMillis - unGiornoMillis)..limiteMillis
            }
            override fun isSelectableYear(year: Int): Boolean = true
        }
    )

    val giornoSelezionato = datePickerState.selectedDateMillis
    val abilitaConfermaPrenotazione = giornoSelezionato != null && orarioSelezionato != null

    val dataLeggibile = remember(giornoSelezionato) {
        if (giornoSelezionato != null) {
            SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).format(Date(giornoSelezionato))
        } else "Nessuna data selezionata"
    }

    // --- LOGICA METEO COMPLETAMENTE DISACCOPPIATA ---
    // Ricorda che meteoDelGiornoSelezionato ora restituisce un oggetto InfoMeteo
    val meteoDelGiornoSelezionato = usaMeteo(
        latitude = -22.9068,  // Es. Rio de Janeiro
        longitude = -43.1729,
        giornoSelezionatoMillis = giornoSelezionato,
        fornitore = OpenMeteoAdapter()
    )

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
                        contentDescription = "Logo",
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
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Seleziona la data della visita",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 10.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = mioBluScuro),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides mioBluScuro
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {

                                val tipografiaBase = MaterialTheme.typography
                                MaterialTheme(
                                    typography = tipografiaBase.copy(
                                        bodyLarge = tipografiaBase.bodyLarge.copy(
                                            fontSize = tipografiaBase.bodyLarge.fontSize * 1.0f
                                        ),
                                        bodySmall = tipografiaBase.bodySmall.copy(
                                            fontSize = tipografiaBase.bodySmall.fontSize * 1.0f
                                        )
                                    )
                                ) {
                                    DatePicker(
                                        state = datePickerState,
                                        title = null,
                                        headline = null,
                                        showModeToggle = false,
                                        modifier = Modifier.fillMaxWidth(),
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
                                }

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .width(200.dp)
                                        .height(60.dp)
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }
                                        ) {}
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = "Seleziona la fascia oraria",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                myBottoneOrario(id = LocalTime.of(10, 30), text = "09:00 - 11:00", selezionato = orarioSelezionato) { orarioSelezionato = LocalTime.of(10, 30) }
                myBottoneOrario(id = LocalTime.of(12, 30), text = "11:00 - 13:00", selezionato = orarioSelezionato) { orarioSelezionato = LocalTime.of(12, 30) }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                myBottoneOrario(id = LocalTime.of(14, 30), text = "14:00 - 16:00", selezionato = orarioSelezionato) { orarioSelezionato = LocalTime.of(14, 30) }
                myBottoneOrario(id = LocalTime.of(16, 30), text = "16:00 - 18:00", selezionato = orarioSelezionato) { orarioSelezionato = LocalTime.of(16, 30) }
            }

            // --- RIGA CON IL RISULTATO E L'ICONA DEL METEO ---
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Event, contentDescription = null, tint = mioBluScuro)
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = when {
                        giornoSelezionato != null && orarioSelezionato != null -> "Hai scelto: $dataLeggibile \nalle ore $orarioSelezionato"
                        giornoSelezionato != null -> " $dataLeggibile (seleziona orario)"
                        else -> "Scegli prima una data dal calendario"
                    },
                    modifier = Modifier.weight(1f)
                )

                // MOSTRA L'ICONA DEL METEO E LE TEMPERATURE
                if (meteoDelGiornoSelezionato != null) {
                    val (iconaMeteo, coloreMeteo) = getIconaEColore(meteoDelGiornoSelezionato.condizione)

                    // Raggruppo l'icona e il testo in una colonna per tenerli vicini e centrati
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = iconaMeteo,
                            contentDescription = "Meteo",
                            tint = coloreMeteo,
                            modifier = Modifier.size(28.dp) // Ridotto leggermente per far spazio al testo
                        )

                        // Mostra Temp Minima e Massima sotto l'icona
                        Text(
                            text = "${meteoDelGiornoSelezionato.tempMin}° / ${meteoDelGiornoSelezionato.tempMax}°",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            if (prenotazioneEffettuata) {
                AlertDialog(
                    onDismissRequest = { prenotazioneEffettuata = false },
                    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
                    containerColor = mioBluScuro,
                    textContentColor = mioGialloOcra,
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Prenotazione effettuata\ncon successo!",
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = {
                                    prenotazioneEffettuata = false
                                    navController.navigate("UtenteScreen") {
                                        popUpTo("UtenteScreen") { inclusive = true }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = mioGialloOcra, contentColor = mioBluScuro),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Torna alla Home")
                            }
                        }
                    },
                    confirmButton = {}, dismissButton = {}
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { prenotazioneEffettuata = true },
                enabled = abilitaConfermaPrenotazione,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = mioGialloOcra,
                    contentColor = mioBluScuro
                ),
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 80.dp)
            ) {
                Text("Conferma Prenotazione", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}