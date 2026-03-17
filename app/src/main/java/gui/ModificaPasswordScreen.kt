package gui
import DTO.D_tipoUtente
import DTO.ModificaPasswordDTO
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import gui.template.MyTextField_Password
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.progettoingsw.R
import gui.template.MyButton

import android.widget.Toast
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

import androidx.compose.ui.platform.LocalContext
import connection.aggiornaPasswordServer

import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModificaPasswordScreen(
    navController: NavController,
    identificativoCaratteristico: String,
    tipoUtente: D_tipoUtente
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // --- STATO PER LO SNACKBAR ---
    val snackbarHostState = remember { SnackbarHostState() }

    // --- VARIABILI FORM ---
    var textFieldVecchiaPassword by remember { mutableStateOf("") }
    var textFieldPasswordModificata by remember { mutableStateOf("") }
    var textFieldRipetiPasswordModificata by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(color = Color.White),
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = mioBluScuro),
                title = {
                    Text("Impostazioni Profilo", fontWeight = FontWeight.Bold, color = mieiTitoli, textAlign = TextAlign.Center)
                },
                navigationIcon = {
                    Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo", modifier = Modifier.height(50.dp).width(50.dp))
                }
            )
        },
        // --- SNACKBAR AGGANCIATO ALLO SCAFFOLD ---
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(horizontal = 35.dp, vertical = 20.dp)
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFFB00020),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Modifica le tue\ninformazioni credenziali qui!",
                fontWeight = FontWeight.Bold, fontSize = 20.sp, color = mieScritte, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(30.dp))

            // --- TESTO VECCHIA PASSWORD ---
            MyTextField_Password(
                myModifier = Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                myLabel = "Vecchia Password",
                myValue = textFieldVecchiaPassword,
                myOnValueChange = { textFieldVecchiaPassword = it }
            )
            Spacer(modifier = Modifier.height(15.dp))

            // --- TESTO NUOVA PASSWORD ---
            MyTextField_Password(
                myModifier = Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                myLabel = "Nuova Password",
                myValue = textFieldPasswordModificata,
                myOnValueChange = { textFieldPasswordModificata = it }
            )
            Spacer(modifier = Modifier.height(15.dp))

            // --- TESTO RIPETI NUOVA PASSWORD ---
            MyTextField_Password(
                myModifier = Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                myLabel = "Ripeti Nuova Password",
                myValue = textFieldRipetiPasswordModificata,
                myOnValueChange = { textFieldRipetiPasswordModificata = it }
            )
            Spacer(modifier = Modifier.height(40.dp))

            if (isLoading) {
                CircularProgressIndicator(color = mioBluScuro)
            } else {
                MyButton(
                    myClick = {
                        // campi vuoti e coincidenza
                        if (textFieldVecchiaPassword.isBlank() || textFieldPasswordModificata.isBlank() || textFieldRipetiPasswordModificata.isBlank()) {
                            Toast.makeText(context, "Compila tutti i campi", Toast.LENGTH_SHORT).show()
                            return@MyButton
                        }
                        if (textFieldPasswordModificata != textFieldRipetiPasswordModificata) {
                            Toast.makeText(context, "Le nuove password non coincidono", Toast.LENGTH_SHORT).show()
                            return@MyButton
                        }

                        // Controllo stringente SOLO sulla nuova password
                        val isValidLength = textFieldPasswordModificata.length > 8
                        val hasUpperCase = textFieldPasswordModificata.any { it.isUpperCase() }
                        val hasLowerCase = textFieldPasswordModificata.any { it.isLowerCase() }
                        val hasDigit = textFieldPasswordModificata.any { it.isDigit() }
                        val hasSpecialChar = textFieldPasswordModificata.any { !it.isLetterOrDigit() }

                        if (!isValidLength || !hasUpperCase || !hasLowerCase || !hasDigit || !hasSpecialChar) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "La nuova password deve avere > 8 caratteri, una maiuscola, una minuscola, un numero e un carattere speciale.",
                                    duration = SnackbarDuration.Short
                                )
                            }
                            return@MyButton
                        }

                        // Chiamata al server con il DTO
                        scope.launch {
                            isLoading = true
                            val dto = ModificaPasswordDTO(
                                identificativo = identificativoCaratteristico,
                                tipoUtente = tipoUtente,
                                vecchiaPassword = textFieldVecchiaPassword,
                                nuovaPassword = textFieldPasswordModificata
                            )

                            val successo = aggiornaPasswordServer(dto)
                            isLoading = false

                            if (successo) {
                                Toast.makeText(context, "Password aggiornata con successo!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Errore: Vecchia password errata o problema server.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    testo = "Conferma"
                )
            }
        }
    }
}