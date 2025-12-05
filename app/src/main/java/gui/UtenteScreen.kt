package gui

import android.R.attr.content
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import gui.template.myBottomNavBar
import gui.template.myTextField
import model.Utente
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.ui.text.input.PasswordVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun UtenteScreen(emailUtente:String,navController : NavController) {
    var textFieldRicerca by remember {
        mutableStateOf("")
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        topBar = { CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor =  Color(0xFF00409C)
            ),
            title = {
                Text("Ciao, " + emailUtente)
            }
        )
            Column(
                modifier = Modifier.fillMaxWidth(),
                //verticalAlignment = Alignment.CenterVertically,
                //horizontalArrangement = Arrangement.Absolute.Center
            ) {
                Spacer(modifier = Modifier.height(120.dp))
                Text(
                    text = "Incomincia a cercare la tua futura casa",
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 20.dp),
                        //.padding(vertical = 20.dp),
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
               // Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = textFieldRicerca,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF04198C),
                        unfocusedBorderColor = Color.Gray,
                        unfocusedTextColor = Color.Black,
                        focusedTextColor = Color.Black,
                        focusedLabelColor = Color(0xFF04198C),
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = Color.Black
                    ),
                    label = {
                        Text("Reinserisci password")
                    },
                    onValueChange = {
                        textFieldRicerca = it
                    },
                    singleLine = true,
                    modifier = Modifier.padding(horizontal = 35.dp)
                        .fillMaxWidth(),
                    trailingIcon = {
                        // QUESTO È IL BLOCCO COMPOSABLE
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Cerca"
                        )
                    }
                )


            }
         },
        bottomBar = {
            myBottomNavBar()
        }

    ) { innerPadding -> // 2. L'innerPadding è obbligatorio!

        // 3. TUTTO IL CONTENUTO VISIBILE DEVE ESSERE RACCHIUSO IN UN UNICO COMPONENTE DI LAYOUT, E QUI SERVE UNA COLUMN
        Column(
            modifier = Modifier
                // Applica il padding fornito dallo Scaffold per evitare che gli elementi
                // vengano coperti da topBar o bottomBar.
                .padding(innerPadding)
                .fillMaxSize()
        ) {}
    }
}
/*
@Preview
@Composable
fun SimpleComposablePreview() {
    UtenteScreen(
    )
}*/