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
            // MODIFICA QUI: Usa direttamente la route dell'oggetto.
            // NON aggiungere + "?mailUtente={mailUtente}" se l'hai già messo nella classe Screen!
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
            route = Screen.AdminScreen.route + "?usernameAdmin={usernameAdmin}",
            arguments = listOf(navArgument("usernameAdmin"){
                type = NavType.StringType;
                defaultValue = "<UsernameAdmin>"
                nullable = true
            })
        ){ entry ->
            AdminScreen(usernameAdmin = entry.arguments?.getString("usernameAdmin")?:"<UsernameAdmin>", navController)
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
    }
}
