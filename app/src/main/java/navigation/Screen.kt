package navigation

import android.net.Uri
import com.google.android.gms.maps.model.LatLng

sealed class Screen(val route: String) {
    data object LoginScreen: Screen("login_screen")

    data object RegisterScreen: Screen("register_screen")

    data object UtenteScreen: Screen("utente_screen"){
        fun withMailUtente(mailUtente: String): String{
            return "$route?mailUtente=${Uri.encode(mailUtente)}"//return "$route/${Uri.encode(mailUtente)}"
        }
    }

    data object AdminScreen: Screen("admin_screen"){
        fun withMailAdmin(usernameAdmin: String): String{
            return "$route?usernameAdmin=${Uri.encode(usernameAdmin)}"
        }
    }
    data object MappaScreen : Screen("mappa_screen") {

        // 1. Questa variabile serve al NavHost per sapere COSA aspettarsi
        // Corrisponde a: "mappa_screen?lat={lat}&lng={lng}"
        val routeWithArgs = "$route?lat={lat}&lng={lng}"

        // 2. Questa funzione serve al NavController per sapere DOVE andare
        fun withLocation(location: LatLng): String {
            // Estraiamo i double e li mettiamo nella stringa
            return "$route?lat=${location.latitude}&lng=${location.longitude}"
        }
    }
    data object RicercaScreen: Screen("ricerca_screen")
    data object RicercaMappaScreen : Screen("ricerca_mappa_screen") {

        // 1. Questa variabile serve al NavHost per sapere COSA aspettarsi
        // Corrisponde a: "mappa_screen?lat={lat}&lng={lng}"
        val routeWithArgs = "$route?lat={lat}&lng={lng}"

        // 2. Questa funzione serve al NavController per sapere DOVE andare
        fun withLocation(location: LatLng): String {
            // Estraiamo i double e li mettiamo nella stringa
            return "$route?lat=${location.latitude}&lng=${location.longitude}"
        }

    }

    data object InserzioneScreen: Screen("inserzione_screen"){
        fun withIdInserzione(idInserzione: String): String{
            return "$route?idInserzione=${Uri.encode(idInserzione)}"//return "$route/${Uri.encode(mailUtente)}"
        }
    }
    data object FiltriScreen: Screen("filtri_screen")

}
