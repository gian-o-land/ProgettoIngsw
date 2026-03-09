package navigation

import DTO.InserzioneDTO
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.progettoingsw.R
import gui.LoginScreen
import gui.RegisterScreen
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.android.gms.maps.model.LatLng
import gui.AdminScreen
import gui.FiltriScreen
import gui.InserzioneScreen
import gui.MappaScreen
import gui.ModificaCredenzialiScreen
import gui.ModificaPasswordScreen
import gui.NotificheScreen
import gui.RicercaMappaScreen
import gui.RicercaScreen
import gui.UtenteScreen
import gui.PrenotazioneScreen
import gui.ProfiloScreen
import gui.SottoadminScreen
import gui.AgenteScreen
import gui.CaricaImmobiliScreen
import gui.ProfiloAdminScreen
import gui.ProfiloAgenteScreen
import gui.ModificaCredenzialiAgenteScreen
import gui.ModificaCredenzialiUtenteScreen
import gui.NewUtenteGoogleScreen
import gui.NotificheAgenteScreen
import gui.mioBluScuro
import androidx.compose.ui.platform.LocalContext
import connection.SessionManager


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation() {
    val navController = rememberNavController()

    val context = LocalContext.current

    // 1. Leggiamo dalla memoria a lungo termine
    val emailSalvata = SessionManager.leggiEmail(context)
    val ruoloSalvato = SessionManager.leggiRuolo(context)

    // 2. Decidiamo dinamicamente la schermata di partenza!
    val schermataDiPartenza = if (emailSalvata != null && ruoloSalvato != -1) {
        when (ruoloSalvato) {
            0 -> Screen.UtenteScreen.withMailUtente(emailSalvata)
            1 -> Screen.AdminScreen.withMailAdmin(emailSalvata)
            2 -> Screen.SottoadminScreen.withMailSottoadmin(emailSalvata)
            3 -> Screen.AgenteScreen.withMailAgente(emailSalvata)
            else -> Screen.LoginScreen.route
        }
    } else {
        Screen.LoginScreen.route // Nessuno salvato, si va al login!
    }

    NavHost(
        navController = navController,
        startDestination = schermataDiPartenza,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ){

        composable(route = Screen.LoginScreen.route){
            LoginScreen(painterResource(id = R.drawable.logo_lettering),"Logo", navController)
        }

        composable(route = Screen.RegisterScreen.route){
            RegisterScreen(painterResource(id = R.drawable.logo_lettering),"Logo", navController)
        }

        composable(
            route = Screen.UtenteScreen.route,

            arguments = listOf(
                navArgument("mailUtente") {
                    type = NavType.StringType
                    defaultValue = "<NomeCognomeUtente>" // Valore se arrivi dalla Navbar (senza parametri)
                    nullable = true
                }
            )
        ) { entry ->
            // Recupera l'argomento
            val mailRecuperata = entry.arguments?.getString("mailUtente") ?: "<NomeCognomeUtente>"

            // Passalo alla schermata
            UtenteScreen(
                navController = navController,
                emailUtente = mailRecuperata
            )
        }

        composable(
            route = Screen.AdminScreen.route,

            arguments = listOf(
                navArgument("usernameAdmin") {
                    type = NavType.StringType
                    defaultValue = "<UsernameAdmin>" // Valore se arrivi dalla Navbar (senza parametri)
                    nullable = true
                }
            )
        ) { entry ->
            // Recupera l'argomento
            val usernameRecuperato = entry.arguments?.getString("usernameAdmin") ?: "<UsernameAdmin>"

            // Passalo alla schermata
            AdminScreen(
                navController = navController,
                usernameAdmin = usernameRecuperato
            )
        }

        composable(
            route = Screen.SottoadminScreen.route,

            arguments = listOf(
                navArgument("usernameSottoadmin") {
                    type = NavType.StringType
                    defaultValue = "<UsernameSottoadmin>" // Valore se arrivi dalla Navbar (senza parametri)
                    nullable = true
                }
            )
        ) { entry ->
            // Recupera l'argomento
            val usernameRecuperato = entry.arguments?.getString("usernameSottoadmin") ?: "<UsernameSottoadmin>"

            // Passalo alla schermata
            SottoadminScreen(
                navController = navController,
                usernameSottoadmin = usernameRecuperato
            )
        }

        composable(
            route = Screen.AgenteScreen.route,

            arguments = listOf(
                navArgument("username") {
                    type = NavType.StringType
                    defaultValue = "<UsernameAgente>" // Valore se arrivi dalla Navbar (senza parametri)
                    nullable = true
                }
            )
        ) { entry ->
            // Recupera l'argomento
            val usernameRecuperato = entry.arguments?.getString("username") ?: "<UsernameAgente>"

            // Passalo alla schermata
            AgenteScreen(
                navController = navController,
                username = usernameRecuperato
            )
        }

        composable(
            route = Screen.MappaScreen.route + "?lat={lat}&lng={lng}",
            arguments = listOf(
                navArgument("lat") {// non supporta tipo double quindi uso prima string e poi lo converto
                    type = NavType.StringType
                    defaultValue = "0.0"
                },
                navArgument("lng") {
                    type = NavType.StringType
                    defaultValue = "0.0"
                }
            )
        ) { entry ->
            val lat = entry.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
            val lng = entry.arguments?.getString("lng")?.toDoubleOrNull() ?: 0.0

            val locationObj = LatLng(lat, lng)

            MappaScreen(
                location = locationObj,
                navController = navController
            )
        }

        composable(route = Screen.RicercaScreen.route){
            RicercaScreen(navController)
        }
        composable(
            // Aggiungiamo &email={email} alla rotta
            route = Screen.RicercaMappaScreen.route + "?lat={lat}&lng={lng}&email={email}",
            arguments = listOf(
                navArgument("lat") {
                    type = NavType.StringType
                    defaultValue = "0.0"
                },
                navArgument("lng") {
                    type = NavType.StringType
                    defaultValue = "0.0"
                },
                // Aggiungiamo l'argomento per l'email
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = "" // Valore di default in caso mancasse
                }
            )
        ) { entry ->
            val lat = entry.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
            val lng = entry.arguments?.getString("lng")?.toDoubleOrNull() ?: 0.0

            // Estraiamo l'email dagli argomenti
            val emailUtente = entry.arguments?.getString("email") ?: ""

            val locationObj = LatLng(lat, lng)

            RicercaMappaScreen(
                location = locationObj,
                navController = navController,
                email = emailUtente
            )
        }
        composable(route = Screen.RicercaScreen.route){
            RicercaScreen(navController)
        }

        composable(route = Screen.InserzioneScreen.route) {
            // 1. Peschiamo sia il JSON che l'EMAIL dallo "zainetto" della pagina precedente
            val json = navController.previousBackStackEntry?.savedStateHandle?.get<String>("inserzione_json")
            val emailUtente = navController.previousBackStackEntry?.savedStateHandle?.get<String>("email_utente")

            // Controlliamo che ci siano entrambi prima di aprire la pagina
            if (json != null && emailUtente != null) {
                // 2. Ritrasformiamo il JSON in oggetto e mostriamo la pagina
                val inserzioneOggetto = kotlinx.serialization.json.Json.decodeFromString<InserzioneDTO>(json)

                // Passiamo l'oggetto e l'email alla funzione!
                InserzioneScreen(
                    navController = navController,
                    inserzione = inserzioneOggetto,
                    email = emailUtente
                )
            } else {
                // 3. Schermata di emergenza (se manca il JSON o manca l'email)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = mioBluScuro,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
        }
        composable(
            // Definiamo la rotta con i due parametri nell'URL
            route = Screen.PrenotazioneScreen.route + "?email={email}&idInserzione={idInserzione}",
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("idInserzione") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { entry ->
            val emailRecuperata = entry.arguments?.getString("email") ?: ""
            val idInserzioneRecuperato = entry.arguments?.getString("idInserzione") ?: ""

            PrenotazioneScreen(
                navController = navController,
                email = emailRecuperata,
                idInserzione = idInserzioneRecuperato
            )
        }
        composable(
            route = Screen.NotificheScreen.route,
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = "<EmailUtente>"
                    nullable = true
                }
            )
        ) { entry ->
            val emailRecuperata = entry.arguments?.getString("email") ?: "<EmailUtente>"

            NotificheScreen(
                navController = navController,
                email = emailRecuperata
            )
        }

        composable(route = Screen.ModificaCredenzialiScreen.route){
            ModificaCredenzialiScreen(navController)
        }
        composable(route = Screen.ModificaPasswordScreen.route){
            ModificaPasswordScreen(navController)
        }
        composable(
            route = Screen.FiltriScreen.route,
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = "<EmailUtente>"
                    nullable = true
                }
            )
        ) { entry ->
            val emailRecuperata = entry.arguments?.getString("email") ?: "<EmailUtente>"

            FiltriScreen(
                navController = navController,
                email = emailRecuperata
            )
        }
        composable(
            route = Screen.ProfiloAdminScreen.route,
            arguments = listOf(
                navArgument("username") {
                    type = NavType.StringType
                    defaultValue = "<Username>"
                    nullable = true
                },
                navArgument("ruolo") {
                    type = NavType.IntType // Usiamo IntType
                    defaultValue = 1 // Valore di default: Admin
                }
            )
        ) { entry ->
            val usernameRecuperato = entry.arguments?.getString("username") ?: "<Username>"
            // Usiamo getInt per recuperare il ruolo
            val ruoloRecuperato = entry.arguments?.getInt("ruolo") ?: 1

            ProfiloAdminScreen(
                username = usernameRecuperato,
                ruolo = ruoloRecuperato,
                navController = navController
            )
        }
        composable(
            route = Screen.ProfiloAgenteScreen.route,
            arguments = listOf(
                navArgument("username") {
                    type = NavType.StringType
                    defaultValue = "<UsernameAgente>"
                    nullable = true
                }
            )
        ) { entry ->
            val usernameRecuperato = entry.arguments?.getString("username") ?: "<UsernameAgente>"

            ProfiloAgenteScreen(
                navController = navController,
                username = usernameRecuperato
            )
        }
        composable(
            route = Screen.ModificaCredenzialiAgenteScreen.route,
            arguments = listOf(
                navArgument("username") {
                    type = NavType.StringType
                    defaultValue = "<UsernameAgente>"
                    nullable = true
                }
            )
        ) { entry ->
            val usernameRecuperato = entry.arguments?.getString("username") ?: "<UsernameAgente>"

            ModificaCredenzialiAgenteScreen(
                navController = navController,
                username = usernameRecuperato
            )
        }
        composable(
            route = Screen.ProfiloScreen.route,
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = "<EmailUtente>"
                    nullable = true
                }
            )
        ) { entry ->
            val emailRecuperata = entry.arguments?.getString("email") ?: "<EmailUtente>"

            ProfiloScreen(
                navController = navController,
                email = emailRecuperata
            )
        }
        composable(
            route = Screen.ModificaCredenzialiUtenteScreen.route,
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = "<EmailUtente>"
                    nullable = true
                }
            )
        ) { entry ->
            val emailRecuperata = entry.arguments?.getString("email") ?: "<EmailUtente>"

            ModificaCredenzialiUtenteScreen(
                navController = navController,
                emailOriginale = emailRecuperata
            )
        }
        composable(
            route = Screen.NotificheAgenteScreen.route,
            arguments = listOf(
                navArgument("username") {
                    type = NavType.StringType
                    defaultValue = "<UsernameAgente>"
                    nullable = true
                }
            )
        ) { entry ->
            val usernameRecuperato = entry.arguments?.getString("username") ?: "<UsernameAgente>"

            NotificheAgenteScreen(
                navController = navController,
                username = usernameRecuperato
            )
        }

        composable(
            route = Screen.CaricaImmobiliScreen.route,
            arguments = listOf(
                navArgument("username") {
                    type = NavType.StringType
                    defaultValue = "<UsernameAgente>"
                    nullable = true
                }
            )
        ) { entry ->
            val usernameRecuperato = entry.arguments?.getString("username") ?: "<UsernameAgente>"

            CaricaImmobiliScreen(
                navController = navController,
                username = usernameRecuperato
            )
        }

        composable(
            route = Screen.NewUtenteGoogleScreen.route,

            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = "<NomeCognomeUtente>"
                    nullable = true
                }
            )
        ) { entry ->
            // Recupera l'argomento
            val mailRecuperata = entry.arguments?.getString("email") ?: "<NomeCognomeUtente>"

            // Passalo alla schermata
            NewUtenteGoogleScreen(
                navController = navController,
                email = mailRecuperata
            )
        }
    }
}
