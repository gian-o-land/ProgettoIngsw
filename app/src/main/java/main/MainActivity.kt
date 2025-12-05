package main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import com.example.progettoingsw.R
import gui.LoginScreen
import gui.ProgettoIngswTheme
import gui.RegisterScreen
import gui.UtenteScreen
import navigation.Navigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ProgettoIngswTheme {
                Navigation()
            }
        }
    }
}