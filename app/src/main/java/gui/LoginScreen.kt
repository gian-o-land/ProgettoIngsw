package gui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.progettoingsw.R
import org.intellij.lang.annotations.JdkConstants

@Composable
fun LoginScreen(painter: Painter, descrizione: String?){
    var textFieldState1 by remember {
        mutableStateOf("")
    }
    var textFieldState2 by remember {
        mutableStateOf("")
    }

    Column (modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)
    ){
        Card(modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
            shape = RoundedCornerShape(
                topStart = 0.dp,    // Angolo in alto a sinistra: dritto
                topEnd = 0.dp,      // Angolo in alto a destra: dritto
                bottomStart = 18.dp, // Angolo in basso a sinistra: arrotondato
                bottomEnd = 18.dp   // Angolo in basso a destra: arrotondato
            ),
            colors = CardDefaults.cardColors(Color(0xFF00409C))
        ){
            Row(modifier = Modifier
                .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Center
            ){
                Image(painter = painter, contentDescription = descrizione)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Accedi per trovare la casa dei tuoi sogni",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 40.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF000000),
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
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 154.dp)
                .fillMaxWidth())
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = textFieldState1,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF04198C),
                unfocusedBorderColor = Color.Gray,
                unfocusedTextColor = Color.Black,
                focusedTextColor = Color.Black,
                focusedLabelColor = Color(0xFF04198C),
                unfocusedLabelColor = Color.Gray,
                cursorColor=Color.Black
            ),
            label = {
                Text("Email/Username")
            },
            onValueChange = {
                textFieldState1 = it
            },
            singleLine = true,
            modifier = Modifier.padding(horizontal = 35.dp)
        )
        Spacer(modifier = Modifier.height(15.dp))
        OutlinedTextField(
            value = textFieldState2,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF04198C),
                unfocusedBorderColor = Color.Gray,
                unfocusedTextColor = Color.Black,
                focusedTextColor = Color.Black,
                focusedLabelColor = Color(0xFF04198C),
                unfocusedLabelColor = Color.Gray,
                cursorColor=Color.Black

            ),
            label = {
                Text("Password")
            },
            onValueChange = {
                textFieldState2 = it
            },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.padding(horizontal = 35.dp)
        )
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            {},
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF2CB05),
                contentColor = Color(0xFF000000)
            ),
            modifier = Modifier.height(50.dp)
                .fillMaxWidth()
                .padding(horizontal = 80.dp)
        ) {
            Text("Accedi", fontSize = 17.sp)
        }
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Non hai un account?",
            color = Color.Black,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 17.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            {},
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF2CB05),
                contentColor = Color(0xFF000000)
            ),
            modifier = Modifier.height(50.dp)
                .fillMaxWidth()
                .padding(horizontal = 80.dp)
        ) {
            Text("Registrati", fontSize = 17.sp)
        }
    }
}