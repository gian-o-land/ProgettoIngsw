package gui.template

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalTime
import gui.mioGialloOcra
import gui.mioBluScuro

@Composable
fun myButton(myClick :() -> Unit, testo: String){

    Button(
        onClick = myClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = mioGialloOcra,
            contentColor = mioBluScuro
        ),
        modifier = Modifier.height(50.dp)
            .fillMaxWidth()
            .padding(horizontal = 80.dp)
    ) {
        Text(testo, fontSize = 17.sp)
    }
}

@Composable

fun RowScope.myBottoneOrario(
    id: java.time.LocalTime?,
    text: String,
    selezionato: LocalTime?,
    onClick: () -> Unit
) {
    // Calcoliamo se QUESTO bottone è quello attivo
    val isSelected = selezionato == id

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .weight(1f) // Fai in modo che i bottoni occupino lo stesso spazio (50% e 50%)
            .height(50.dp),
        shape = CircleShape,

        // GESTIONE COLORI ATTIVO/DISATTIVO
        colors = ButtonDefaults.outlinedButtonColors(
            // Se selezionato -> Giallo Ocra, Altrimenti -> Trasparente (o Bianco)
            containerColor = if (isSelected) mioGialloOcra else mioBluScuro,

            // Se selezionato -> Bianco, Altrimenti -> Blu Scuro (o Grigio)
            contentColor = if (isSelected) mioBluScuro else Color.White
        ),
        // GESTIONE BORDO
        border = BorderStroke(
            width = if (isSelected) 0.dp else 1.dp, // Togli il bordo se è pieno
            color = if (isSelected) Color.Transparent else Color.Gray
        )
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}