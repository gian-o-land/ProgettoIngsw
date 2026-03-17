package gui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import connection.scaricaUtente
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import com.example.progettoingsw.R
import gui.template.myButton
import gui.template.myTextField
import DTO.source.CommonPhonePrefixes
import DTO.source.DefaultPhonePrefix
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import connection.modificaCredenzialiUtente


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModificaCredenzialiUtenteScreen(navController: NavController, emailOriginale: String) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // --- VARIABILI DI STATO ---
    var textFieldNomeModificato by remember { mutableStateOf("") }
    var textFieldCognomeModificato by remember { mutableStateOf("") }
    var textFieldEmailModificato by remember { mutableStateOf("") }
    var textFieldTelefonoModificato by remember { mutableStateOf("") }

    var prefixDropdownExpanded by remember { mutableStateOf(false) }
    var selectedPrefix by remember { mutableStateOf(DefaultPhonePrefix) }

    // --- STATI DI CARICAMENTO ---
    var isFetching by remember { mutableStateOf(true) }
    var isUpdating by remember { mutableStateOf(false) }

    // --- CARICAMENTO DATI ---
    LaunchedEffect(emailOriginale) {
        isFetching = true
        val utente = scaricaUtente(emailOriginale)
        if (utente != null) {
            textFieldNomeModificato = utente.nome
            textFieldCognomeModificato = utente.cognome
            textFieldEmailModificato = utente.email
            textFieldTelefonoModificato = utente.telefono

            val prefissoDB = utente.selectedPrefix.trim()

            val prefissoCorrispondente = CommonPhonePrefixes.find { prefissoOggetto ->
                val prefissoPulito = prefissoOggetto.displayName.replace("(", "").replace(")", "").trim()
                prefissoPulito == prefissoDB
            }

            if (prefissoCorrispondente != null) {
                selectedPrefix = prefissoCorrispondente
            }

        } else {
            Toast.makeText(context, "Impossibile caricare i dati attuali", Toast.LENGTH_SHORT).show()
        }
        isFetching = false
    }

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
                        text = "Impostazioni Profilo",
                        fontWeight = FontWeight.Bold,
                        color = mieiTitoli,
                        textAlign = TextAlign.Center
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
        }
    ) { innerPadding ->

        if (isFetching) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = mioBluScuro)
            }
        } else {
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Modifica le tue\ninformazioni credenziali qui!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = mieScritte,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))

                // --- CAMPI DI TESTO ---
                myTextField(
                    Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                    "Nome",
                    textFieldNomeModificato,
                    { textFieldNomeModificato = it }
                )
                Spacer(modifier = Modifier.height(10.dp))

                myTextField(
                    Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                    "Cognome",
                    textFieldCognomeModificato,
                    { textFieldCognomeModificato = it }
                )
                Spacer(modifier = Modifier.height(10.dp))

                myTextField(
                    Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                    "Email",
                    textFieldEmailModificato,
                    { textFieldEmailModificato = it }
                )
                Spacer(modifier = Modifier.height(10.dp))

                // --- PREFISSO E TELEFONO ---
                Row(verticalAlignment = Alignment.Top) {
                    ExposedDropdownMenuBox(
                        expanded = prefixDropdownExpanded,
                        onExpandedChange = { prefixDropdownExpanded = !prefixDropdownExpanded },
                        modifier = Modifier.padding(start = 35.dp, end = 10.dp)
                    ) {
                        OutlinedTextField(
                            value = "${selectedPrefix.flagEmoji}${selectedPrefix.displayName}",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pref.") },
                            singleLine = true,
                            modifier = Modifier.menuAnchor().width(130.dp),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = prefixDropdownExpanded) },
                            textStyle = MaterialTheme.typography.bodyLarge,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = mioBluScuro, unfocusedBorderColor = Color.Gray,
                                focusedTextColor = mieScritte, unfocusedTextColor = mieScritte,
                                unfocusedContainerColor = mioBackgroung, focusedLabelColor = mioBluScuro,
                                unfocusedLabelColor = Color.Gray, cursorColor = Color.Black,
                                focusedTrailingIconColor = Color.Gray, unfocusedTrailingIconColor = Color.Gray
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = prefixDropdownExpanded,
                            onDismissRequest = { prefixDropdownExpanded = false },
                            modifier = Modifier.background(mioBackgroung).border(width = 0.5.dp, color = Color.Black, shape = RoundedCornerShape(4.dp))
                        ) {
                            CommonPhonePrefixes.forEachIndexed { index, prefix ->
                                DropdownMenuItem(
                                    text = { Text("${prefix.flagEmoji} ${prefix.displayName}") },
                                    onClick = {
                                        selectedPrefix = prefix
                                        prefixDropdownExpanded = false
                                    }
                                )
                                if (index < CommonPhonePrefixes.lastIndex) {
                                    HorizontalDivider(color = Color.Black, thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                    OutlinedTextField(
                        value = textFieldTelefonoModificato,
                        onValueChange = { textFieldTelefonoModificato = it },
                        label = { Text("Numero") },
                        modifier = Modifier.fillMaxWidth().padding(end = 35.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = mioBluScuro, unfocusedBorderColor = Color.Gray,
                            focusedTextColor = mieScritte, unfocusedTextColor = mieScritte,
                            unfocusedContainerColor = mioBackgroung, focusedLabelColor = mioBluScuro,
                            unfocusedLabelColor = Color.Gray, cursorColor = Color.Black
                        )
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // --- BOTTONE DI SALVATAGGIO ---
                myButton(
                    myClick = {
                        // REGOLA 1: Controllo campi vuoti
                        if (textFieldEmailModificato.isBlank() ||
                            textFieldTelefonoModificato.isBlank() ||
                            textFieldNomeModificato.isBlank() ||
                            textFieldCognomeModificato.isBlank()
                        ) {
                            Toast.makeText(context, "Compila tutti i campi", Toast.LENGTH_SHORT).show()
                            return@myButton // Blocca l'esecuzione
                        }

                        // REGOLA 2: Controllo lunghezza telefono (esattamente 10 cifre)
                        if (textFieldTelefonoModificato.length != 10) {
                            Toast.makeText(context, "Il numero di telefono non valido", Toast.LENGTH_SHORT).show()
                            return@myButton
                        }

                        // REGOLA 3: Controllo formato email (deve contenere @ e .)
                        val emailRegex = "^.+@.+\\..+$".toRegex()
                        if (!textFieldEmailModificato.matches(emailRegex)) {
                            Toast.makeText(context, "Indirizzo email non valido", Toast.LENGTH_SHORT).show()
                            return@myButton
                        }

                        // Se supera tutti i controlli, procediamo.
                        coroutineScope.launch {
                            isUpdating = true

                            val prefissoPulito = selectedPrefix.displayName
                                .replace("(", "")
                                .replace(")", "")
                                .trim()

                            // Chiamiamo la funzione per l'UTENTE
                            val successo = modificaCredenzialiUtente(
                                emailOriginale = emailOriginale, // Passiamo la vecchia mail per trovarlo nel DB
                                nuovoNome = textFieldNomeModificato,
                                nuovoCognome = textFieldCognomeModificato,
                                nuovaEmail = textFieldEmailModificato,
                                nuovoTelefono = textFieldTelefonoModificato,
                                nuovoPrefisso = prefissoPulito
                            )

                            if (successo) {
                                Toast.makeText(context, "Profilo aggiornato con successo!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Errore durante l'aggiornamento.", Toast.LENGTH_SHORT).show()
                            }
                            isUpdating = false
                        }
                    },
                    testo = if (isUpdating) "Salvataggio..." else "Conferma"
                )
            }
        }
    }
}