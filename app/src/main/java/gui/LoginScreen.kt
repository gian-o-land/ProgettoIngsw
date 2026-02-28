package gui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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

@Composable
fun LoginScreen(painter: Painter, descrizione: String?, navController : NavController){

    val coroutineScope= rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var textFieldEmail by remember {
        mutableStateOf("")
    }
    var textFieldPassword by remember {
        mutableStateOf("")
    }

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
                    modifier = Modifier.height(180.dp))
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
            Button(
                onClick = {
                    navController.navigate(Screen.AdminScreen.route)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = mioBackgroung,
                    contentColor = mieScritte,
                ),
                border = BorderStroke(1.dp, Color.Gray),
                shape = RectangleShape,
                modifier = Modifier.padding(horizontal = 35.dp)
                    .width(400.dp)
                    .height(45.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(25.dp))
                    Text("Accedi con Google", textAlign = TextAlign.Center, fontSize = 17.sp)
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
                              navController.navigate(Screen.UtenteScreen.withMailUtente(textFieldEmail))

                        }else if (risposta==1){
                            navController.navigate(Screen.AdminScreen.withMailAdmin(textFieldEmail))
                        }else if (risposta==2){
                            navController.navigate(Screen.SottoadminScreen.withMailSottoadmin(textFieldEmail))

                        }else if (risposta==3){
                            navController.navigate(Screen.AgenteScreen.withMailAgente(textFieldEmail))

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