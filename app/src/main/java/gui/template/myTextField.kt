package gui.template

import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButtonDefaults.Icon

@Composable
fun myTextField(myModifier: Modifier,myLabel: String, myValue:String,myOnValueChange: (String) -> Unit) {

        OutlinedTextField(
            value = myValue,
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
                Text(myLabel)
            },
            onValueChange = myOnValueChange,
            singleLine = true,
            modifier = myModifier
        )

}
/*
@Composable
fun myTextField_Icon(myModifier: Modifier, myLabel: String, myValue:String, myOnValueChange: (String) -> Unit, myIcon: ImageVector) {

    OutlinedTextField(
        value = myValue,
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
            Text(myLabel)
        },
        onValueChange = myOnValueChange,
        singleLine = true,
        modifier = myModifier,
        trailingIcon ={
            Icon(
                imageVector=myIcon
            )
        }
    )


}*/