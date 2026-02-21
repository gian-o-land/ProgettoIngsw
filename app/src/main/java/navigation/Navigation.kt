package navigation

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
import gui.NotificheAgenteScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.LoginScreen.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ){

        composable(route = Screen.LoginScreen.route){
            LoginScreen(painterResource(id = R.drawable.logo),"Logo", navController)
        }

        composable(route = Screen.RegisterScreen.route){
            RegisterScreen(painterResource(id = R.drawable.logo),"Logo", navController)
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
            route = Screen.RicercaMappaScreen.route + "?lat={lat}&lng={lng}",
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

            RicercaMappaScreen(
                location = locationObj,
                navController = navController
            )
        }
        composable(route = Screen.RicercaScreen.route){
            RicercaScreen(navController)
        }

        composable(
            route = Screen.InserzioneScreen.route + "?idInserzione={idInserzione}",
            arguments = listOf(navArgument("idInserzione"){
                type = NavType.StringType;
                defaultValue = "<IdInserzione>"
                nullable = true
            })
        ){ entry->
            InserzioneScreen(idInserzione = entry.arguments?.getString("idInserzione")?:"<IdInserzione>", navController=navController)
        }
        composable(route = Screen.PrenotazioneScreen.route){
            PrenotazioneScreen(navController)
        }
        composable(route = Screen.NotificheScreen.route){
           NotificheScreen(navController)
        }
        composable(route = Screen.ProfiloScreen.route){
            ProfiloScreen(navController)
        }
        composable(route = Screen.ModificaCredenzialiScreen.route){
            ModificaCredenzialiScreen(navController)
        }
        composable(route = Screen.ModificaPasswordScreen.route){
            ModificaPasswordScreen(navController)
        }
        composable(route = Screen.FiltriScreen.route){
            FiltriScreen(navController)
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
    }
}
