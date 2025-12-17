package gui.template

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

@Composable
fun myCardInserzione(
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