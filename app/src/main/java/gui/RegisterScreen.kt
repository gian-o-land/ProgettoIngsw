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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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
            .background(Color.White)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(bottomEnd = 18.dp, bottomStart = 18.dp, topStart = 0.dp, topEnd = 0.dp),
            colors = CardDefaults.cardColors(Color(0xFF00409C))
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(color = Color.White)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Registrazione",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 40.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = email,
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
                    Text("Email")
                },
                onValueChange = {
                    email = it
                },
                singleLine = true,
                modifier = Modifier.padding(horizontal = 35.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = nome,
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
                    Text("Nome")
                },
                onValueChange = {
                    nome = it
                },
                singleLine = true,
                modifier = Modifier.padding(horizontal = 35.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = cognome,
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
                    Text("Cognome")
                },
                onValueChange = {
                    cognome = it
                },
                singleLine = true,
                modifier = Modifier.padding(horizontal = 35.dp)
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
                            focusedBorderColor = Color(0xFF04198C),
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            unfocusedContainerColor = Color.White,
                            focusedLabelColor = Color(0xFF04198C),
                            unfocusedLabelColor = Color.Gray,
                            cursorColor = Color.Black,
                            focusedTrailingIconColor = Color.Gray,
                            unfocusedTrailingIconColor = Color.Gray
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = prefixDropdownExpanded,
                        onDismissRequest = { prefixDropdownExpanded = false }
                    ) {
                        CommonPhonePrefixes.forEach { prefix ->
                            DropdownMenuItem(
                                text = { Text("${prefix.flagEmoji} ${prefix.displayName}") },
                                onClick = {
                                    selectedPrefix = prefix
                                    prefixDropdownExpanded = false
                                }
                            )
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
                        focusedBorderColor = Color(0xFF04198C),
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        unfocusedContainerColor = Color.White,
                        focusedLabelColor = Color(0xFF04198C),
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = Color.Black
                    )
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = password,
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
                    Text("Password")
                },
                onValueChange = {
                    password = it
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(horizontal = 35.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = testPassword,
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
                    testPassword = it
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(horizontal = 35.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(40.dp))
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
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

