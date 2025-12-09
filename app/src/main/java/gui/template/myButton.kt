package gui.template

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gui.mieScritte
import gui.mioGialloOcra

@Composable
fun myButton(myClick : () -> Unit, testo: String){

    Button(
        onClick = myClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = mioGialloOcra,
            contentColor = mieScritte
        ),
        modifier = Modifier.height(50.dp)
            .fillMaxWidth()
            .padding(horizontal = 80.dp)
    ) {
        Text(testo, fontSize = 17.sp)
    }
}