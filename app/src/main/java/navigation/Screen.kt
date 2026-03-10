package navigation

import DTO.D_tipoUtente
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
    data object AgenteScreen: Screen("agente_screen?username={username}"){
        fun withMailAgente(username: String?): String{
            return "agente_screen?username=${Uri.encode(username)}"
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
        // Corrisponde a: "ricerca_mappa_screen?lat={lat}&lng={lng}&email={email}"
        val routeWithArgs = "$route?lat={lat}&lng={lng}&email={email}"

        // 2. Questa funzione serve al NavController per sapere DOVE andare
        fun withLocation(location: LatLng, email: String): String {
            // Estraiamo i double e l'email e li mettiamo nella stringa
            return "$route?lat=${location.latitude}&lng=${location.longitude}&email=$email"
        }

    }

    data object InserzioneScreen: Screen("inserzione_screen"){
        fun withIdInserzione(idInserzione: String): String{
            return "$route?idInserzione=${Uri.encode(idInserzione)}"//return "$route/${Uri.encode(mailUtente)}"
        }
    }

    data object InserzioneAgenteScreen: Screen("inserzione_agente_screen")

    data object FiltriScreen: Screen("filtri_screen?email={email}") {
        fun withMailUtente(email: String): String {
            return "filtri_screen?email=${Uri.encode(email)}"
        }
    }
    data object PrenotazioneScreen: Screen("prenotazione_screen")

    data object NotificheScreen: Screen("notifiche_screen?email={email}") {
        fun withMailUtente(email: String): String {
            return "notifiche_screen?email=${Uri.encode(email)}"
        }
    }      data object ProfiloScreen: Screen("profilo_screen?email={email}") {
        fun withMail(email: String): String {
            return "profilo_screen?email=${Uri.encode(email)}"
        }
    }
    data object ModificaCredenzialiScreen: Screen("modifica_credenziali_screen")
    data object ModificaPasswordScreen : Screen("modifica_password_screen?identificativo={identificativo}&tipoUtente={tipoUtente}") {

        fun withArgs(identificativo: String, tipoUtente: D_tipoUtente): String {
            // Codifichiamo l'identificativo (specialmente utile se è un'email con la @)
            val idCodificato = Uri.encode(identificativo)
            // Trasformiamo l'enum in stringa e lo codifichiamo per sicurezza
            val tipoCodificato = Uri.encode(tipoUtente.name)

            return "modifica_password_screen?identificativo=$idCodificato&tipoUtente=$tipoCodificato"
        }
    }
    data object ProfiloAdminScreen: Screen("profilo_admin_screen?username={username}&ruolo={ruolo}") {
        fun withUsernameAndRole(username: String, ruolo: Int): String {
            return "profilo_admin_screen?username=${Uri.encode(username)}&ruolo=$ruolo"
        }
    }
    data object ProfiloAgenteScreen: Screen("profilo_agente_screen?username={username}") {
        fun withEmail(username: String): String {
            return "profilo_agente_screen?username=${Uri.encode(username)}"
        }
    }
    data object ModificaCredenzialiAgenteScreen: Screen("modifica_credenziali_agente_screen?username={username}") {
        fun withMailAgente(username: String): String {
            return "modifica_credenziali_agente_screen?username=${Uri.encode(username)}"
        }
    }
    data object ModificaCredenzialiUtenteScreen: Screen("modifica_credenziali_utente_screen?email={email}") {
        fun withMailUtente(email: String): String {
            return "modifica_credenziali_utente_screen?email=${Uri.encode(email)}"
        }
    }
    data object CaricaImmobiliScreen: Screen("carica_immobili_screen?username={username}"){
        fun withUsernameAgente(username: String): String{
            return "carica_immobili_screen?username=${Uri.encode(username)}"
        }
    }
    data object NotificheAgenteScreen: Screen("notifiche_agente_screen?username={username}"){
        fun withUsernameAgente(username: String): String{
            return "notifiche_agente_screen?username=${Uri.encode(username)}"
        }
    }

    data object NewUtenteGoogleScreen: Screen("new_utente_google_screen?email={email}"){
        fun withEmailGoogle(email: String): String{
            return "new_utente_google_screen?email=${Uri.encode(email)}"
        }
    }

}
