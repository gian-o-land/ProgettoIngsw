@file:kotlin.OptIn(ExperimentalMaterial3Api::class)

package gui

import DTO.UtenteDTO
import DTO.source.CommonPhonePrefixes
import DTO.source.DefaultPhonePrefix
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.progettoingsw.R
import gui.template.MyButton
import gui.template.MyTextField
import kotlinx.coroutines.launch
import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.DisposableEffect
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Dispatchers
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import connection.SessionManager

@Composable
@OptIn(ExperimentalMaterial3Api::class, kotlinx.coroutines.DelicateCoroutinesApi::class)
fun NewUtenteGoogleScreen(navController: NavController, email: String){
    var textFieldTelefonoModificato by remember {
        mutableStateOf("")
    }
    var textFieldNomeModificato by remember {
        mutableStateOf("")
    }
    var textFieldCognomeModificato by remember {
        mutableStateOf("")
    }
    var prefixDropdownExpanded by remember { mutableStateOf(false) }
    var selectedPrefix by remember { mutableStateOf(DefaultPhonePrefix) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var isConfermato by remember { mutableStateOf(false) }
    val context = LocalContext.current

    BackHandler {
        coroutineScope.launch {
            connection.eliminaUtenteGoogleIncompleto(email)
            navController.popBackStack()
        }
    }

    val isConfermatoAggiornato by rememberUpdatedState(isConfermato)

    DisposableEffect(Unit) {
        onDispose {
            if (!isConfermatoAggiornato) {
                GlobalScope.launch(Dispatchers.IO) {
                    connection.eliminaUtenteGoogleIncompleto(email)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        containerColor =  Color.White,
        topBar = { CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor =  mioBluScuro
            ),
            title = {
                Text(text="Impostazioni Profilo",
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)

        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Ciao,\n$email\nprima di accedere\ninserici le tue credenziali",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = mieScritte,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            MyTextField(
                Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                "Nome",
                textFieldNomeModificato,
                {nomeModificato -> textFieldNomeModificato = nomeModificato}
            )
            Spacer(modifier = Modifier.height(10.dp))

            MyTextField(
                Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                "Cognome",
                textFieldCognomeModificato,
                {cognomeModificato -> textFieldCognomeModificato = cognomeModificato}
            )
            Spacer(modifier = Modifier.height(10.dp))

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
                        modifier = Modifier
                            .menuAnchor()
                            .width(130.dp),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = prefixDropdownExpanded)
                        },
                        textStyle = typography.bodyLarge,
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
                    value = textFieldTelefonoModificato,
                    onValueChange = { textFieldTelefonoModificato = it },
                    label = { Text("Numero") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 35.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = typography.bodyLarge,
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

            Spacer(modifier = Modifier.height(40.dp))
            MyButton(
                myClick = {
                    // 1. Controllo campi vuoti
                    if (textFieldNomeModificato.isBlank() ||
                        textFieldCognomeModificato.isBlank() ||
                        textFieldTelefonoModificato.isBlank()
                    ) {
                        android.widget.Toast.makeText(context, "Per favore, compila tutti i campi", android.widget.Toast.LENGTH_SHORT).show()
                        return@MyButton
                    }

                    // 2. Controllo lunghezza numero di telefono (esattamente 10 cifre)
                    if (textFieldTelefonoModificato.trim().length != 10) {
                        android.widget.Toast.makeText(context, "Il numero di telefono deve essere di 10 cifre", android.widget.Toast.LENGTH_SHORT).show()
                        return@MyButton
                    }

                    // Se i controlli passano, procediamo con la chiamata al server
                    coroutineScope.launch {
                        val dto = UtenteDTO(
                            email = email,
                            telefono = textFieldTelefonoModificato.trim(),
                            password = "Accesso_Google0",
                            nome = textFieldNomeModificato.trim(),
                            cognome = textFieldCognomeModificato.trim(),
                            selectedPrefix = selectedPrefix.prefix
                        )

                        val successo = connection.completaProfiloGoogle(dto)

                        if (successo) {
                            isConfermato = true
                            SessionManager.salvaSessioneUtente(context, email, 0)
                            navController.navigate(navigation.Screen.UtenteScreen.withMailUtente(email)) {
                                popUpTo(0)
                            }
                        } else {
                            snackbarHostState.showSnackbar("Errore di connessione, per favore riprovare")
                        }
                    }
                },
                testo = "Conferma"
            )
        }
    }
}