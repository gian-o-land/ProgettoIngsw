package gui


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import gui.template.myButton
import gui.template.myTextField
import gui.template.myTextField_Password
import DTO.CreazioneAgenteDTO
import androidx.compose.ui.graphics.Color
import connection.creaNuovoAgente
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.text.input.KeyboardType

import DTO.source.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.border

import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import com.example.progettoingsw.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreazioneAgenteScreen(
    navController: NavController,
    usernameAdmin: String,
    agenziaAdmin: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var cognome by remember { mutableStateOf("") }

    // Variabili per il telefono
    var telefono by remember { mutableStateOf("") }
    var prefixDropdownExpanded by remember { mutableStateOf(false) }
    var selectedPrefix by remember { mutableStateOf(DefaultPhonePrefix) }

    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(mioBluScuro),
        containerColor = mioBluScuro,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = mioBluScuro),
                title = {
                    Text("Nuovo Agente", fontWeight = FontWeight.Bold, color = mieiTitoli)
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
        // Rimosso il bottomBar vuoto che causava la striscia blu!
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(horizontal = 35.dp, vertical = 20.dp)
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFFB00020), // Colore rosso errore scuro
                    contentColor = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                // 1. Spingiamo giù il blocco solo per non coprire la TopBar blu
                .padding(top = innerPadding.calculateTopPadding())
                // 2. Coloriamo tutto lo spazio rimanente di bianco (fino in fondo allo schermo!)
                .background(
                    color = mioBackgroung,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                // 3. Aggiungiamo il padding in basso AL CONTENUTO, così non finisce sotto i tasti di Android
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        )  {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Inserisci i dati\ndel nuovo collaboratore",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = mieScritte,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Agenzia: $agenziaAdmin",
                fontSize = 16.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(30.dp))

            myTextField(Modifier.padding(horizontal = 35.dp).fillMaxWidth(), "Nome", nome) { nome = it }
            Spacer(modifier = Modifier.height(20.dp))

            myTextField(Modifier.padding(horizontal = 35.dp).fillMaxWidth(), "Cognome", cognome) { cognome = it }
            Spacer(modifier = Modifier.height(20.dp))

            myTextField(Modifier.padding(horizontal = 35.dp).fillMaxWidth(), "Email", email) { email = it }
            Spacer(modifier = Modifier.height(20.dp))

            // --- SEZIONE TELEFONO ---
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 35.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = prefixDropdownExpanded,
                    onExpandedChange = { prefixDropdownExpanded = !prefixDropdownExpanded },
                    modifier = Modifier.padding(end = 10.dp)
                ) {
                    OutlinedTextField(
                        value = "${selectedPrefix.flagEmoji}${selectedPrefix.displayName}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pref.") },
                        singleLine = true,
                        modifier = Modifier
                            .menuAnchor()
                            .width(130.dp),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = prefixDropdownExpanded)
                        },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = mioBluScuro,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = mieScritte,
                            unfocusedTextColor = mieScritte,
                            unfocusedContainerColor = mioBackgroung,
                            focusedLabelColor = mioBluScuro,
                            unfocusedLabelColor = Color.Gray,
                            cursorColor = Color.Black,
                            focusedTrailingIconColor = Color.Gray,
                            unfocusedTrailingIconColor = Color.Gray
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = prefixDropdownExpanded,
                        onDismissRequest = { prefixDropdownExpanded = false },
                        modifier = Modifier
                            .background(mioBackgroung)
                            .border(
                                width = 0.5.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(4.dp)
                            )
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
                                HorizontalDivider(
                                    color = Color.Black,
                                    thickness = 0.5.dp
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Numero") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = mioBluScuro,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = mieScritte,
                        unfocusedTextColor = mieScritte,
                        unfocusedContainerColor = mioBackgroung,
                        focusedLabelColor = mioBluScuro,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = Color.Black
                    )
                )
            }
            // ------------------------

            Spacer(modifier = Modifier.height(20.dp))

            myTextField_Password(Modifier.padding(horizontal = 35.dp).fillMaxWidth(), "Password", password) { password = it }
            Spacer(modifier = Modifier.height(40.dp))

            if (isLoading) {
                CircularProgressIndicator(color = mioBluScuro)
            } else {
                myButton(
                    testo = "Crea Agente",
                    myClick = {
                        // 1. Controllo base campi vuoti
                        if (email.isBlank() || nome.isBlank() || cognome.isBlank() || telefono.isBlank() || password.isBlank()) {
                            Toast.makeText(context, "Compila tutti i campi", Toast.LENGTH_SHORT).show()
                            return@myButton
                        }

                        // 2. Controllo stringente sulla nuova password spostato QUI!
                        val isValidLength = password.length > 8
                        val hasUpperCase = password.any { it.isUpperCase() }
                        val hasLowerCase = password.any { it.isLowerCase() }
                        val hasDigit = password.any { it.isDigit() }
                        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

                        if (!isValidLength || !hasUpperCase || !hasLowerCase || !hasDigit || !hasSpecialChar) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "La nuova password deve avere > 8 caratteri, una maiuscola, una minuscola, un numero e un carattere speciale.",
                                    duration = SnackbarDuration.Short
                                )
                            }
                            return@myButton // Blocchiamo l'esecuzione se la password non rispetta i criteri
                        }

                        // 3. Se tutto è corretto, facciamo la chiamata al server
                        scope.launch {
                            isLoading = true

                            val nuovoAgente = CreazioneAgenteDTO(
                                email = email.trim(),
                                nome = nome.trim(),
                                cognome = cognome.trim(),
                                telefono = telefono.trim(),
                                prefisso = selectedPrefix.displayName
                                    .replace("(", "")
                                    .replace(")", "")
                                    .trim(),
                                password = password,
                                nomeAgenzia = agenziaAdmin,
                                adminCreatore = usernameAdmin
                            )

                            // usernameGenerato conterrà il testo inviato dal server, es: "Tecnocasa_AG0001"
                            val usernameGenerato = creaNuovoAgente(nuovoAgente)
                            isLoading = false

                            if (usernameGenerato != null) {
                                // Mostriamo nel Toast l'username appena creato!
                                Toast.makeText(context, "Agente creato! Username: $usernameGenerato", Toast.LENGTH_LONG).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Errore: l'email potrebbe essere già in uso o errore di rete.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}