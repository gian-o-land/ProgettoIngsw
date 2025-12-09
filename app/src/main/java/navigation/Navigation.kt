package navigation

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
import gui.MappaScreen
import gui.RicercaMappaScreen
import gui.RicercaScreen
import gui.UtenteScreen
import java.util.Map.entry

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
            route = Screen.UtenteScreen.route + "?mailUtente={mailUtente}",//route = Screen.UtenteScreen.route + "/{mailUtente}",
            arguments = listOf(navArgument("mailUtente"){
                type = NavType.StringType;
                defaultValue = "<NomeCognomeUtente>"
                nullable = true
            })
        ){ entry->
            UtenteScreen(emailUtente = entry.arguments?.getString("mailUtente")?:"<NomeCognomeUtente>", navController=navController)
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
        composable(route = Screen.RicercaMappaScreen.route){
            RicercaMappaScreen(navController)
        }
    }
}
