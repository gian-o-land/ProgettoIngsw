package navigation

import DTO.D_tipoUtente
import DTO.FiltriRicercaDTO
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
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
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
import gui.CreazioneAgenteScreen
import gui.CreazioneSottoadminScreen
import gui.InserzioneAgenteScreen
import kotlinx.serialization.json.Json


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation() {
    val navController = rememberNavController()

    val context = LocalContext.current

    val emailSalvata = SessionManager.leggiEmail(context)
    val usernameSalvato = SessionManager.leggiUsername(context)
    val ruoloSalvato = SessionManager.leggiRuolo(context)
    val agenziaSalvata = SessionManager.leggiAgenzia(context)


    val schermataDiPartenza = if (emailSalvata != null && ruoloSalvato == 0) {
        Screen.UtenteScreen.withMailUtente(emailSalvata)
    }else if(usernameSalvato != null && ruoloSalvato != -1) {
        when(ruoloSalvato){
            1 -> Screen.AdminScreen.withArgs(usernameSalvato, agenziaSalvata);
            2 -> Screen.SottoadminScreen.withArgs(usernameSalvato, agenziaSalvata);
            3 -> Screen.AgenteScreen.withMailAgente(usernameSalvato);
            else -> Screen.LoginScreen.route
        }
    }else {
        Screen.LoginScreen.route
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
                    defaultValue = "<NomeCognomeUtente>"
                    nullable = true
                }
            )
        ) { entry ->
            val mailRecuperata = entry.arguments?.getString("mailUtente") ?: "<NomeCognomeUtente>"

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
                    defaultValue = "<UsernameAdmin>"
                    nullable = true
                },
                navArgument("agenziaAdmin") {
                    type = NavType.StringType
                    defaultValue = "<NomeAgenzia>"
                    nullable = true
                }
            )
        ) { entry ->
            val usernameRecuperato = entry.arguments?.getString("usernameAdmin") ?: "<UsernameAdmin>"
            val agenziaRecuperata = entry.arguments?.getString("agenziaAdmin") ?: "<NomeAgenzia>"

            AdminScreen(
                navController = navController,
                usernameAdmin = usernameRecuperato,
                agenziaAdmin = agenziaRecuperata
            )
        }

        composable(
            route = Screen.SottoadminScreen.route,
            arguments = listOf(
                navArgument("usernameSottoadmin") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("agenziaAdmin") {
                    type = NavType.StringType
                    defaultValue = "Nessuna Agenzia"
                }
            )
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("usernameSottoadmin") ?: ""
            val agenzia = backStackEntry.arguments?.getString("agenziaAdmin") ?: ""

            SottoadminScreen(
                usernameSottoadmin = username,
                navController = navController,
                agenziaAdmin = agenzia
            )
        }

        composable(
            route = Screen.AgenteScreen.route,

            arguments = listOf(
                navArgument("username") {
                    type = NavType.StringType
                    defaultValue = "<UsernameAgente>"
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
                navArgument("lat") {
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
                location = locationObj
            )
        }

        composable(route = Screen.RicercaScreen.route){
            RicercaScreen(navController)
        }
        composable(
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
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { entry ->
            val lat = entry.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
            val lng = entry.arguments?.getString("lng")?.toDoubleOrNull() ?: 0.0

            val emailUtente = entry.arguments?.getString("email") ?: ""

            val locationObj = LatLng(lat, lng)

            val filtriJson = navController.previousBackStackEntry?.savedStateHandle?.get<String>("filtri_json")

            val filtriAttivi = if (filtriJson != null) {
                Json.decodeFromString<FiltriRicercaDTO>(filtriJson)
            } else {
                FiltriRicercaDTO()
            }

            RicercaMappaScreen(
                location = locationObj,
                navController = navController,
                email = emailUtente,
                filtri = filtriAttivi
            )
        }
        composable(route = Screen.RicercaScreen.route){
            RicercaScreen(navController)
        }

        composable(route = Screen.InserzioneScreen.route) {
            val json =  remember {  navController.previousBackStackEntry?.savedStateHandle?.get<String>("inserzione_json")}
            val emailUtente = remember {  navController.previousBackStackEntry?.savedStateHandle?.get<String>("email_utente")}

            if (json != null && emailUtente != null) {
                val inserzioneOggetto = Json.decodeFromString<InserzioneDTO>(json)

                InserzioneScreen(
                    navController = navController,
                    inserzione = inserzioneOggetto,
                    email = emailUtente
                )
            } else {
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

        composable(
            route = Screen.ModificaPasswordScreen.route,
            arguments = listOf(
                navArgument("identificativo") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("tipoUtente") {
                    type = NavType.StringType
                    defaultValue = D_tipoUtente.UTENTE.name
                }
            )
        ) { backStackEntry ->

            val identificativo = backStackEntry.arguments?.getString("identificativo") ?: ""
            val tipoStringa = backStackEntry.arguments?.getString("tipoUtente") ?: D_tipoUtente.UTENTE.name

            val tipoEnum = try {
                D_tipoUtente.valueOf(tipoStringa)
            } catch (_: Exception) {
                D_tipoUtente.UTENTE
            }

            ModificaPasswordScreen(
                navController = navController,
                identificativoCaratteristico = identificativo,
                tipoUtente = tipoEnum
            )
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
                navArgument("username") { type = NavType.StringType },
                navArgument("ruolo") { type = NavType.IntType },
                navArgument("agenziaAdmin") {
                    type = NavType.StringType
                    defaultValue = "Nessuna Agenzia"
                }
            )
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            val ruolo = backStackEntry.arguments?.getInt("ruolo") ?: 0
            val agenzia = backStackEntry.arguments?.getString("agenziaAdmin") ?: ""

            ProfiloAdminScreen(
                username = username,
                navController = navController,
                ruolo = ruolo,
                agenziaAdmin = agenzia
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

        composable(
            route = Screen.CreazioneAgenteScreen.route,
            arguments = listOf(
                navArgument("usernameAdmin") { type = NavType.StringType; defaultValue = "" },
                navArgument("agenziaAdmin") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val usernameSalvato = backStackEntry.arguments?.getString("usernameAdmin") ?: ""
            val agenziaSalvata = backStackEntry.arguments?.getString("agenziaAdmin") ?: ""

            CreazioneAgenteScreen(
                navController = navController,
                usernameAdmin = usernameSalvato,
                agenziaAdmin = agenziaSalvata
            )
        }

        composable(
            route = Screen.CreazioneSottoadminScreen.route,
            arguments = listOf(
                navArgument("usernameAdmin") { type = NavType.StringType },
                navArgument("agenziaAdmin") { type = NavType.StringType }
            )
        ) { entry ->
            val usernameAdmin = entry.arguments?.getString("usernameAdmin") ?: ""
            val agenziaAdmin = entry.arguments?.getString("agenziaAdmin") ?: ""

            CreazioneSottoadminScreen(
                navController = navController,
                usernameAdmin = usernameAdmin,
                agenziaAdmin = agenziaAdmin
            )
        }

        composable(route = Screen.InserzioneAgenteScreen.route) {
            val jsonInserzione = remember { navController.previousBackStackEntry?.savedStateHandle?.get<String>("inserzione_json") }

            if (jsonInserzione != null) {
                val inserzioneObj = Json.decodeFromString<InserzioneDTO>(jsonInserzione)

                InserzioneAgenteScreen(
                    inserzione = inserzioneObj
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Errore nel caricamento dell'inserzione.")
                }
            }
        }
    }
}
