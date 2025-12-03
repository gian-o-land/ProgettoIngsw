package gui

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
import gui.template.myTextField

@Composable

fun UtenteScreen() {
    var textFieldRicerca by remember {
        mutableStateOf("")
    }
    Column (modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(
                topStart = 0.dp,    // Angolo in alto a sinistra: dritto
                topEnd = 0.dp,      // Angolo in alto a destra: dritto
                bottomStart = 18.dp, // Angolo in basso a sinistra: arrotondato
                bottomEnd = 18.dp   // Angolo in basso a destra: arrotondato
            ),
            colors = CardDefaults.cardColors(Color(0xFF00409C))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                Spacer(modifier = Modifier.height(100.dp))

                Text(
                    text = "Ciao, *usernameUtente* ", //aggiungere variabile usernameUtente
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }
        }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                Text(
                    text = "Incomincia a cercare la tua futura casa", //aggiungere variabile usernameUtente
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(vertical= 20.dp),
                    textAlign = TextAlign.Center,
                    color= Color.Black
                )
            }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.Center
        ) {

            myTextField(myModifier= Modifier.padding(35.dp).fillMaxWidth(), myLabel="Cerca",myValue=textFieldRicerca,myOnValueChange= { nuovoValore ->
                textFieldRicerca = nuovoValore
            })
        }
    }
    }
@Preview
@Composable
fun SimpleComposablePreview() {
    UtenteScreen(
    )
}