package gui

import DTO.D_tipoUtente
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.progettoingsw.R
import gui.template.myBottomNavBar_Admin
import gui.template.myBottomNavBar_Sottoadmin
import gui.template.myButton
import navigation.Screen

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProfiloAdminScreen(username: String, navController: NavController, ruolo: Int) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(mioBackgroung),
        containerColor = mioBackgroung,
        topBar = {
            CenterAlignedTopAppBar(
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
            when (ruolo) {
                2 -> myBottomNavBar_Sottoadmin(navController, username)
                1 -> myBottomNavBar_Admin(navController, username)
            }
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
            myButton({navController.navigate(Screen.ModificaPasswordScreen.withArgs(username, D_tipoUtente.ADMIN))}, "Modifica Password")
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}