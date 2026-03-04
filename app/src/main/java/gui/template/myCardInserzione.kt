package gui.template

import DTO.D_foto
import DTO.Inserzione
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gui.mieiTitoli
import gui.mioBluScuro

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

import coil.compose.AsyncImage

@Composable
fun myCardInserzionePlaceholder(
    myModifier: Modifier,
    immobile: Map.Entry<String, Int>
){
    Card(
        modifier = myModifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(2.dp, mioBluScuro)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // PRIMA ROW: 65% dell'altezza
            Row(
                modifier = Modifier
                    .weight(0.65f) // Qui definisci il 65%
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(immobile.value),
                    contentDescription = "Casa",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // SECONDA ROW: 35% dell'altezza
            Row(
                modifier = Modifier
                    .weight(0.35f) // Qui definisci il restante 35%
                    .fillMaxWidth()
                    .background(mioBluScuro),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = immobile.key,
                    fontSize = 16.sp,
                    color = mieiTitoli,
                    modifier = Modifier.padding(start = 15.dp)
                )
            }
        }
    }
}

@Composable
fun myCardInserzione(
    inserzione: Inserzione,       // <--- 1. Riceve l'oggetto vero dal DB
    myModifier: Modifier = Modifier,
    onClick: () -> Unit           // <--- 2. Riceve l'azione da fare al click
) {
    Card(
        modifier = myModifier.clickable { onClick() }, // Rende tutta la card cliccabile
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(2.dp, mioBluScuro) // Assicurati di avere questo colore
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // PRIMA ROW: 65% dell'altezza (FOTO)
            Row(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxWidth()
            ) {
                // Cerchiamo la prima foto
                val primaFotoUrl = inserzione.immagini.firstOrNull { it.tipologia == D_foto.FOTO }?.url

                // Usiamo AsyncImage al posto di Image+painterResource
                AsyncImage(
                    model = primaFotoUrl,
                    contentDescription = "Foto immobile",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // SECONDA ROW: 35% dell'altezza (TESTO)
            Row(
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxWidth()
                    .background(mioBluScuro), // Il tuo sfondo
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.padding(start = 15.dp)) {
                    Text(
                        text = "${inserzione.prezzo} €", // Il testo principale
                        fontSize = 18.sp,
                        color = mieiTitoli, // Usa il tuo colore (o Color.White se mieiTitoli non è visibile)
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${inserzione.citta} • ${inserzione.quadratura} mq", // Sottotitolo
                        fontSize = 13.sp,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}