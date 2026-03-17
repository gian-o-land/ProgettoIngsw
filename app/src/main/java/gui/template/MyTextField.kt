package gui.template

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.VisualTransformation
import gui.mieScritte
import gui.mioBackgroung
import gui.mioBluScuro

@Composable
fun MyTextField(myModifier: Modifier, myLabel: String, myValue:String, myOnValueChange: (String) -> Unit) {

    OutlinedTextField(
        value = myValue,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = mioBackgroung,
            focusedBorderColor = mioBluScuro,
            unfocusedBorderColor = Color.Gray,
            unfocusedTextColor = mieScritte,
            focusedTextColor = mieScritte,
            focusedLabelColor = mioBluScuro,
            unfocusedLabelColor = Color.Gray,
            cursorColor=Color.Black
        ),
        label = {
            Text(myLabel)
        },
        onValueChange = myOnValueChange,
        singleLine = true,
        modifier = myModifier
    )

}

@Composable
fun MyTextField_Password(myModifier: Modifier, myLabel: String, myValue:String, myOnValueChange: (String) -> Unit) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = myValue,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = mioBackgroung,
            focusedBorderColor = mioBluScuro,
            unfocusedBorderColor = Color.Gray,
            unfocusedTextColor = mieScritte,
            focusedTextColor = mieScritte,
            focusedLabelColor = mioBluScuro,
            unfocusedLabelColor = Color.Gray,
            cursorColor=Color.Black
        ),
        label = {
            Text(myLabel)
        },
        onValueChange = myOnValueChange,
        visualTransformation = if (!passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = {
                passwordVisible = !passwordVisible
            }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (passwordVisible) "Nascondi password" else "Mostra password",
                    tint = Color.Gray
                )
            }
        },
        singleLine = true,
        modifier = myModifier
    )

}