package gui

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
import gui.template.myButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(usernameAdmin : String, navController : NavController){
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(mioBackgroung),
        containerColor = mioBackgroung,
        topBar = { CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor =  mioBluScuro
            ),
            title = {
                Text(text="Ciao,\n" + usernameAdmin,
                            fontWeight = FontWeight.Bold,
                            color = mieiTitoli,
                            textAlign = TextAlign.Center)
            },
            navigationIcon = {
                Image(
                painter = painterResource(id = R.drawable.logo),
                "Logo",
                modifier = Modifier.height(50.dp).width(50.dp)
                )
            })
        },
        bottomBar = { myBottomNavBar_Admin() }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Spacer(Modifier.height(30.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Crea\ni tuoi collaboratori",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = mieScritte,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(140.dp))
            myButton({}, "Crea Sottoadmin")
            Spacer(Modifier.height(60.dp))
            myButton({}, "Crea Agente")
        }
    }
}