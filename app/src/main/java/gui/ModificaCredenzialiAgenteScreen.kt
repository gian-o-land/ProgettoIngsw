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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.progettoingsw.R
import gui.template.myButton
import gui.template.myTextField
import DTO.source.CommonPhonePrefixes
import DTO.source.DefaultPhonePrefix

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ModificaCredenzialiAgenteScreen(navController : NavController, username: String) {
    var textFieldTelefonoModificato by remember {
        mutableStateOf("")
    }
    var textFieldEmailModificato by remember {
        mutableStateOf("")
    }
    var prefixDropdownExpanded by remember { mutableStateOf(false) }
    var selectedPrefix by remember { mutableStateOf(DefaultPhonePrefix) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        containerColor =  Color.White,
        topBar = { CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor =  mioBluScuro
            ),
            title = {
                Text(text="Impostazioni Profilo",
                    fontWeight = FontWeight.Bold,
                    color = mieiTitoli,
                    textAlign = TextAlign.Center
                )

            },
            navigationIcon = {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    "Logo",
                    modifier = Modifier.height(50.dp).width(50.dp)
                )
            }
        )

        },
        bottomBar = {
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)

        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Modifica le tue\ninformazioni credenziali qui!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = mieScritte,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            myTextField(
                Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                "Email",
                textFieldEmailModificato,
                {emailModificata -> textFieldEmailModificato = emailModificata}
            )
            Spacer(modifier = Modifier.height(10.dp))

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
                        modifier = Modifier
                            .background(mioBackgroung)
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
                    value = textFieldTelefonoModificato,
                    onValueChange = { textFieldTelefonoModificato = it },
                    label = { Text("Numero") },
                    modifier = Modifier
                        .fillMaxWidth()
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

            Spacer(modifier = Modifier.height(40.dp))
            myButton({}, "Conferma")
        }
    }
}