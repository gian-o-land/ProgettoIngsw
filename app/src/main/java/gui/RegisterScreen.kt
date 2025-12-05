package gui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.typography
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import gui.template.myTextField
import gui.template.myTextField_Password
import model.source.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(painter: Painter, descrizione: String?, navController: NavController) {
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var nome by remember {
        mutableStateOf("")
    }
    var cognome by remember {
        mutableStateOf("")
    }
    var telefono  by remember {
        mutableStateOf("")
    }
    var testPassword by remember {
        mutableStateOf("")
    }
    val scrollState = rememberScrollState()
    var prefixDropdownExpanded by remember {
        mutableStateOf(false)
    }
    var selectedPrefix by remember {
        mutableStateOf(DefaultPhonePrefix) // Inizia con il prefisso di default
    }


    Column(
        modifier = Modifier.fillMaxSize()
            .background(mioBluScuro)
            .imePadding()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            colors = CardDefaults.cardColors(mioBluScuro)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                Image(painter = painter, contentDescription = descrizione)
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(color = mioBluScuro),
            shape = RoundedCornerShape(bottomEnd = 0.dp, bottomStart = 0.dp, topStart = 15.dp, topEnd = 15.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(color = mioBackgroung)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Registrazione",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = mieScritte,
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 40.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                myTextField(
                    Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                    "Email",
                    email,
                    {nuovoTesto -> email = nuovoTesto}
                )
                Spacer(modifier = Modifier.height(20.dp))
                myTextField(
                    Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                    "Nome",
                    nome,
                    {nuovoTesto -> nome = nuovoTesto}
                )
                Spacer(modifier = Modifier.height(20.dp))
                myTextField(
                    Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                    "Cognome",
                    cognome,
                    {nuovoTesto -> cognome = nuovoTesto}
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.Top) {
                    ExposedDropdownMenuBox(
                        expanded = prefixDropdownExpanded,
                        onExpandedChange = { prefixDropdownExpanded = !prefixDropdownExpanded },
                        modifier = Modifier.padding(start = 35.dp, end = 10.dp)
                    ) {
                        OutlinedTextField(
                            value = "${selectedPrefix.flagEmoji}${selectedPrefix.displayName}",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pref.") },
                            singleLine = true,
                            modifier = Modifier
                                .menuAnchor()
                                .width(130.dp),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = prefixDropdownExpanded)
                            },
                            textStyle = typography.bodyLarge,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = mioBluScuro,
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = mieScritte,
                                unfocusedTextColor = mieScritte,
                                unfocusedContainerColor = mioBackgroung,
                                focusedLabelColor = mioBluScuro,
                                unfocusedLabelColor = Color.Gray,
                                cursorColor = Color.Black,
                                focusedTrailingIconColor = Color.Gray,
                                unfocusedTrailingIconColor = Color.Gray
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = prefixDropdownExpanded,
                            onDismissRequest = { prefixDropdownExpanded = false },
                            modifier = Modifier.background(mioBackgroung)
                                .border(
                                width = 0.5.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(4.dp)
                            )
                        ) {
                            CommonPhonePrefixes.forEachIndexed { index, prefix ->
                                DropdownMenuItem(
                                    text = { Text("${prefix.flagEmoji} ${prefix.displayName}") },
                                    onClick = {
                                        selectedPrefix = prefix
                                        prefixDropdownExpanded = false
                                    }
                                )
                                if (index < CommonPhonePrefixes.lastIndex) {
                                    HorizontalDivider(
                                        color = Color.Black,
                                        thickness = 0.5.dp
                                    )
                                }
                            }
                        }
                    }
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Numero") },
                        modifier = Modifier.fillMaxWidth()
                            .padding(end = 35.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = typography.bodyLarge,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = mioBluScuro,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = mieScritte,
                            unfocusedTextColor = mieScritte,
                            unfocusedContainerColor = mioBackgroung,
                            focusedLabelColor = mioBluScuro,
                            unfocusedLabelColor = Color.Gray,
                            cursorColor = Color.Black
                        )
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                myTextField_Password(
                    Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                    "Password",
                    password,
                    {nuovoTesto -> password = nuovoTesto}
                )
                Spacer(modifier = Modifier.height(20.dp))
                myTextField_Password(
                    Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                    "Reinserisci password",
                    testPassword,
                    {nuovoTesto -> testPassword = nuovoTesto}
                )
                Spacer(modifier = Modifier.height(40.dp))
                Button(
                    {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = mioGialloOcra,
                        contentColor = mieScritte
                    ),
                    modifier = Modifier.height(50.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 80.dp)
                ) {
                    Text("Registrati", fontSize = 17.sp)
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

