package gui

import DTO.CreazioneSottoadminDTO
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import gui.template.MyButton
import gui.template.MyTextField_Password
import com.example.progettoingsw.R
import connection.creaNuovoSottoadmin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreazioneSottoadminScreen(
    navController: NavController,
    usernameAdmin: String,
    agenziaAdmin: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Solo la password! L'username lo calcola Ktor.
    var password by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(mioBluScuro),
        containerColor = mioBluScuro,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = mioBluScuro),
                title = {
                    Text("Nuovo Sottoadmin", fontWeight = FontWeight.Bold, color = mieiTitoli)
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        "Logo",
                        modifier = Modifier.height(50.dp).width(50.dp)
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .background(
                    color = mioBackgroung,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Inserisci i dati\ndel nuovo Sottoadmin",
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
            Spacer(modifier = Modifier.height(40.dp))

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(horizontal = 35.dp, vertical = 5.dp)
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFFB00020),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
            }

            MyTextField_Password(Modifier.padding(horizontal = 35.dp).fillMaxWidth(), "Password", password) { password = it }
            Spacer(modifier = Modifier.height(50.dp))

            if (isLoading) {
                CircularProgressIndicator(color = mioBluScuro)
            } else {
                MyButton(
                    testo = "Crea Sottoadmin",
                    myClick = {
                        if (password.isBlank()) {
                            Toast.makeText(context, "Inserisci una password", Toast.LENGTH_SHORT).show()
                            return@MyButton
                        }

                        val isValidLength = password.length > 8
                        val hasUpperCase = password.any { it.isUpperCase() }
                        val hasLowerCase = password.any { it.isLowerCase() }
                        val hasDigit = password.any { it.isDigit() }
                        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

                        if (!isValidLength || !hasUpperCase || !hasLowerCase || !hasDigit || !hasSpecialChar) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "La password deve avere > 8 caratteri, una maiuscola, una minuscola, un numero e un carattere speciale.",
                                    duration = SnackbarDuration.Short
                                )
                            }
                            return@MyButton
                        }

                        scope.launch {
                            isLoading = true

                            val nuovoSottoadmin = CreazioneSottoadminDTO(
                                password = password,
                                nomeAgenzia = agenziaAdmin,
                                adminCreatore = usernameAdmin
                            )

                            // usernameGenerato sarà "Sadmin001" (o null in caso di errore)
                            val usernameGenerato = creaNuovoSottoadmin(nuovoSottoadmin)
                            isLoading = false

                            if (usernameGenerato != null) {
                                // Mostriamo all'Admin l'username appena creato, così può comunicarlo al dipendente!
                                Toast.makeText(context, "Sottoadmin creato: $usernameGenerato", Toast.LENGTH_LONG).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Errore di connessione o del server.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}