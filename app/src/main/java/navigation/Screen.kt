package navigation

import android.net.Uri
import com.google.android.gms.maps.model.LatLng

sealed class Screen(val route: String) {
    data object LoginScreen: Screen("login_screen")

    data object RegisterScreen: Screen("register_screen")

    data object UtenteScreen : Screen("utente_screen?mailUtente={mailUtente}") {

        // Funzione helper per navigare verso questa pagina
        fun withMailUtente(mailUtente: String): String {
            return "utente_screen?mailUtente=${Uri.encode(mailUtente)}"
        }
    }
    data object AdminScreen: Screen("admin_screen?usernameAdmin={usernameAdmin}"){
        fun withMailAdmin(usernameAdmin: String): String{
            return "admin_screen?usernameAdmin=${Uri.encode(usernameAdmin)}"
        }
    }
    data object SottoadminScreen: Screen("sottoadmin_screen?usernameSottoadmin={usernameSottoadmin}"){
        fun withMailSottoadmin(usernameSottoadmin: String): String{
            return "sottoadmin_screen?usernameSottoadmin=${Uri.encode(usernameSottoadmin)}"
        }
    }
    data object AgenteScreen: Screen("agente_screen?mailAgente={mailAgente}"){
        fun withMailAgente(mailAgente: String): String{
            return "agente_screen?mailAgente=${Uri.encode(mailAgente)}"
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
    data object PrenotazioneScreen: Screen("prenotazione_screen")

    data object NotificheScreen: Screen("notifiche_screen")
    data object ProfiloScreen: Screen("profilo_screen")
    data object ModificaCredenzialiScreen: Screen("modifica_credenziali_screen")
    data object ModificaPasswordScreen: Screen("modifica_password_screen")
    data object ProfiloAdminScreen: Screen("profilo_admin_screen?username={username}") {
        // Funzione helper per navigare verso questa pagina passando l'username
        fun withUsername(username: String): String {
            return "profilo_admin_screen?username=${Uri.encode(username)}"
        }
    }




}
