package navigation

import android.net.Uri

sealed class Screen(val route: String) {
    data object LoginScreen: Screen("login_screen")

    data object RegisterScreen: Screen("register_screen")

    data object UtenteScreen: Screen("utente_screen"){
        fun withMailUtente(mailUtente: String): String{
            return "$route?mailUtente=${Uri.encode(mailUtente)}"//return "$route/${Uri.encode(mailUtente)}"
        }
    }

}