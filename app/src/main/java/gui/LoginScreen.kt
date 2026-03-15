package gui

import androidx.annotation.OptIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.progettoingsw.R
import connection.connessioneLogin
import gui.template.myButton
import gui.template.myTextField
import gui.template.myTextField_Password
import kotlinx.coroutines.launch
import navigation.Screen
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import androidx.media3.common.util.Log
import connection.connessioneLoginGoogle
import androidx.compose.ui.res.stringResource
import androidx.media3.common.util.UnstableApi
import connection.SessionManager
import connection.scaricaAdmin
import connection.scaricaAgente
import connection.scaricaUtente

@OptIn(UnstableApi::class)
@Composable
fun LoginScreen(painter: Painter, descrizione: String?, navController : NavController){

    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope= rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarHostState2 = remember { SnackbarHostState() }

    var textFieldEmail by remember {
        mutableStateOf("")
    }
    var textFieldPassword by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current

    val WEB_CLIENT_ID = stringResource(id = R.string.web_client_id)

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = mioBluScuro)
        }
    } else {
        Column (modifier = Modifier
            .fillMaxSize()
            .background(color = mioBluScuro)
        ){
            Card(modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
                colors = CardDefaults.cardColors(mioBluScuro)
            ){
                Row(modifier = Modifier
                    .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.Center
                ){
                    Image(
                        painter = painter,
                        contentDescription = descrizione,
                        modifier = Modifier.height(160.dp).padding(top = 30.dp))
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(color = mioBluScuro),
                shape = RoundedCornerShape(bottomEnd = 0.dp, bottomStart = 0.dp, topStart = 15.dp, topEnd = 15.dp),
                colors = CardDefaults.cardColors(Color.White)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "Accedi per trovare la casa dei tuoi sogni",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = mieScritte,
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 40.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                SnackbarHost(
                    hostState = snackbarHostState2,
                    modifier = Modifier.padding(horizontal = 35.dp, vertical = 5.dp)
                ) { data ->
                    androidx.compose.material3.Snackbar(
                        snackbarData = data,
                        containerColor = Color(0xFFB00020), // Colore rosso errore scuro
                        contentColor = Color.White
                    )
                }
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val credentialManager = CredentialManager.create(context)

                            // INCOLLA QUI IL TUO WEB CLIENT ID (creato su Google Cloud)
                            val googleIdOption = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId(WEB_CLIENT_ID)
                                .setAutoSelectEnabled(true)
                                .build()

                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()

                            try {
                                // 1. Fa apparire il popup nativo di Google
                                val result = credentialManager.getCredential(context, request)
                                val credential = result.credential

                                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                                    // ✨ ECCO IL PASSAGGIO CHIAVE: Recuperiamo la mail! ✨
                                    // La proprietà ".id" di GoogleIdTokenCredential contiene l'email dell'utente
                                    val mailUtente = googleIdTokenCredential.id

                                    // Estraiamo anche l'idToken crittografato da mandare al backend
                                    val tokenSicuro = googleIdTokenCredential.idToken

                                    // Invia il token al tuo server tramite la funzione in connessioneDB
                                    val livelloAccesso = connessioneLoginGoogle(tokenSicuro)

                                    // Gestisce la navigazione in base alla risposta del server
                                    when (livelloAccesso) {
                                        0 -> {
                                            Log.d("LOGIN_GOOGLE", "Accesso riuscito per: $mailUtente")

                                            val dati = scaricaUtente(mailUtente)
                                            if (dati?.telefono == "0000000000" && dati.isGoogle){
                                                navController.navigate(Screen.NewUtenteGoogleScreen.withEmailGoogle(mailUtente))
                                            } else{
                                                SessionManager.salvaSessioneUtente(context, mailUtente, 0)

                                                navController.navigate(Screen.UtenteScreen.withMailUtente(mailUtente)) {
                                                    popUpTo(0)
                                                }
                                            }
                                        }
                                        else -> {
                                            Log.e("LOGIN_GOOGLE", "Accesso negato (Admin/Agente o errore del server)")
                                            coroutineScope.launch {
                                                snackbarHostState2.showSnackbar("Errore di connessione")
                                            }
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("LOGIN_GOOGLE", "Login annullato o fallito: ${e.message}")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = mioBackgroung,
                        contentColor = mieScritte,
                    ),
                    border = BorderStroke(1.dp, Color.Gray),
                    shape = RectangleShape,
                    modifier = Modifier
                        .padding(horizontal = 35.dp)
                        .width(400.dp)
                        .height(45.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.google_logo),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(25.dp))
                        Text(
                            text = "Accedi con Google",
                            textAlign = TextAlign.Center,
                            fontSize = 17.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "oppure",
                    color = mieScritte,
                    modifier = Modifier.padding(horizontal = 154.dp)
                        .fillMaxWidth())
                Spacer(modifier = Modifier.height(10.dp))
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(horizontal = 35.dp, vertical = 5.dp)
                ) { data ->
                    androidx.compose.material3.Snackbar(
                        snackbarData = data,
                        containerColor = Color(0xFFB00020), // Colore rosso errore scuro
                        contentColor = Color.White
                    )
                }
                myTextField(
                    Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                    "Email/Username",
                    textFieldEmail,
                    {nuovoTesto -> textFieldEmail = nuovoTesto}
                )
                Spacer(modifier = Modifier.height(15.dp))
                myTextField_Password(
                    Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                    "Password",
                    textFieldPassword,
                    {nuovoTesto -> textFieldPassword = nuovoTesto}
                )
                Spacer(modifier = Modifier.height(30.dp))
                myButton(
                    myClick = {
                        coroutineScope.launch {
                            val risposta = connessioneLogin(textFieldEmail, textFieldPassword)
                            if (risposta==0){
                                SessionManager.salvaSessioneUtente(context, textFieldEmail, 0)

                                navController.navigate(Screen.UtenteScreen.withMailUtente(textFieldEmail)){
                                    popUpTo(0)
                                }

                            }else if (risposta == 1) {
                                // 1. Attiviamo la rotellina di caricamento
                                isLoading = true

                                // 2. Scarichiamo i dati dell'Admin dal server
                                val adminScaricato = scaricaAdmin(textFieldEmail)

                                // 3. Estraiamo il nome dell'agenzia (se è null, mettiamo un valore di fallback sicuro)
                                val agenziaAdmin = adminScaricato?.nomeAgenzia ?: "Nessuna Agenzia"

                                // 4. Salviamo la sessione
                                SessionManager.salvaSessione(context, textFieldEmail, 1,agenziaAdmin)

                                // 5. Spegniamo il caricamento e navighiamo passando la variabile corretta!
                                isLoading = false
                                navController.navigate(Screen.AdminScreen.withArgs(textFieldEmail, agenziaAdmin)) {
                                    popUpTo(0)
                                }
                            }else if (risposta==2){

                                isLoading = true

                                val sottoadminScaricato = scaricaAdmin(textFieldEmail)
                                val agenziaSottoadmin = sottoadminScaricato?.nomeAgenzia ?: "Nessuna Agenzia"

                                SessionManager.salvaSessione(context, textFieldEmail, 2, agenziaSottoadmin)
                                isLoading = false

                                navController.navigate(Screen.SottoadminScreen.withArgs(textFieldEmail, agenziaSottoadmin)){
                                    popUpTo(0)
                                }

                            }else if (risposta==3){
                                isLoading = true
                                val agente = scaricaAgente(textFieldEmail)
                                val agenziaAgente = agente?.nomeAgenzia ?: "Nessuna Agenzia"

                                SessionManager.salvaSessione(context, agente?.username, 3, agenziaAgente)

                                isLoading = false
                                navController.navigate(Screen.AgenteScreen.withMailAgente(agente?.username)){
                                    popUpTo(0)
                                }

                            }else{
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Credenziali non valide")
                                }
                                textFieldEmail = ""
                                textFieldPassword = ""
                            }

                        }


                    },
                    testo = "Accedi"
                )
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "Non hai un account?",
                    color = mieScritte,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                myButton(
                    myClick = {
                        navController.navigate(Screen.RegisterScreen.route)
                    },
                    testo = "Registrati"
                )
            }
        }
    }
}