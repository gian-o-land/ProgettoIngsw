package gui

import DTO.D_tipoUtente
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.progettoingsw.R
import connection.SessionManager
import gui.template.myBottomNavBar_Agente
import gui.template.myButton
import navigation.Screen

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProfiloAgenteScreen(navController : NavController, username: String) {
    var mostraDialogConferma by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
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
            myBottomNavBar_Agente(
                navController = navController,
                username = username
            )
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
                Text(text = "Modifica le tue\ninformazioni personali",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = mieScritte,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            myButton({navController.navigate(Screen.ModificaCredenzialiAgenteScreen.withMailAgente(username))}, "Modifica Credenziali")
            Spacer(modifier = Modifier.height(30.dp))
            myButton({navController.navigate(Screen.ModificaPasswordScreen.withArgs(username, D_tipoUtente.AGENTE))}, "Modifica Password")
            Spacer(modifier = Modifier.height(30.dp))
            myButton(
                {mostraDialogConferma = true},
                "Esci (Logout)"
            )
        }
        if (mostraDialogConferma) {
            Dialog(
                onDismissRequest = {
                    mostraDialogConferma = false
                },
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = mioBackgroung
                    ),
                    border = BorderStroke(2.dp, mioBluScuro),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(mioBluScuro),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Logout",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = mieiTitoli,
                            modifier = Modifier.padding(start = 15.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Chiudi",
                            tint = mioGialloOcra,
                            modifier = Modifier.padding(end = 10.dp)
                                .height(25.dp)
                                .width(25.dp)
                                .clickable { mostraDialogConferma = false }
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sei sicuro di voler uscire?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = mieScritte
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {mostraDialogConferma = false},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = mioGialloOcra,
                                contentColor = mioBluScuro
                            ),
                            modifier = Modifier.height(50.dp)
                                .width(150.dp)
                                .padding(start = 15.dp)
                        ) {
                            Text("Annulla", fontSize = 17.sp)
                        }
                        Button(
                            onClick = {
                                //Spegniamo il popup
                                mostraDialogConferma = false

                                SessionManager.eseguiLogout(context)
                                navController.navigate(Screen.LoginScreen.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = mioGialloOcra,
                                contentColor = mioBluScuro
                            ),
                            modifier = Modifier.height(50.dp)
                                .width(150.dp)
                                .padding(end = 15.dp)
                        ) {
                            Text("Conferma", fontSize = 17.sp)
                        }
                    }
                }

            }
        }
    }
}