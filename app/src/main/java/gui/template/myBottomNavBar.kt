package gui.template

import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.getValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.ui.unit.dp
import gui.mioBluScuro
import gui.mioGialloOcra
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState // Importante per l'evidenziazione automatica
import navigation.Screen
import androidx.navigation.NavDestination.Companion.hierarchy

@Composable
fun myBottomNavBar(navController: NavController) {

    data class BottomNavItem(
        val label: String,
        val icon: ImageVector,
        val route: String
    )

    val items = listOf(
        BottomNavItem("Cerca", Icons.Default.Search, Screen.UtenteScreen.route),
        BottomNavItem("Notifiche", Icons.Default.Notifications, Screen.NotificheScreen.route),
        BottomNavItem("Profilo", Icons.Default.Person, Screen.ProfiloScreen.route)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = mioBluScuro,
        modifier = Modifier.clip(
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            )
        )
    ) {
        items.forEach { item ->

            val isSelected = currentDestination?.hierarchy?.any {
                it.route == item.route
            } == true
            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = mioGialloOcra,
                    indicatorColor = mioGialloOcra,
                    unselectedIconColor = Color.White,
                    unselectedTextColor = Color.White
                ),
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },

                // Usa la variabile calcolata automaticamente
                selected = isSelected,

                // 3. LOGICA DI NAVIGAZIONE
                onClick = {
                    if (isSelected) return@NavigationBarItem // Blocca il reload se sei già qui!

                    navController.navigate(item.route) {
                        popUpTo(Screen.UtenteScreen.route) {
                            saveState = true
                        }

                        // Queste servono per non duplicare le pagine
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun myBottomNavBar_Admin(navController: NavController, username: String) {

    data class BottomNavItem(
        val label: String,
        val icon: ImageVector,
        val route: String
    )
    val items = listOf(
        BottomNavItem("Crea", Icons.Default.AddCircleOutline, Screen.AdminScreen.withMailAdmin(username)),
        BottomNavItem("Profilo", Icons.Default.Person, Screen.ProfiloAdminScreen.withUsernameAndRole(username, 1)) // 1 = Admin
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(containerColor= mioBluScuro,
        modifier =
            Modifier.clip(
                shape= RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                )
            )
    ) {

        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.route?.substringBefore("?") == item.route.substringBefore("?")
            } == true
            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = mioGialloOcra,
                    indicatorColor = mioGialloOcra,
                    unselectedIconColor = Color.White,
                    unselectedTextColor = Color.White
                ),
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                // Label (testo sotto)
                label = { Text(item.label) },

                // Logica di selezione visiva
                selected = isSelected,
                onClick = {
                    if (isSelected) return@NavigationBarItem // Blocca il reload se sei già qui!

                    navController.navigate(item.route) {
                        popUpTo(Screen.AdminScreen.route) {
                            saveState = true
                        }

                        // Queste servono per non duplicare le pagine
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }

}

@Composable
fun myBottomNavBar_Sottoadmin(navController: NavController, username: String) {

    data class BottomNavItem(
        val label: String,
        val icon: ImageVector,
        val route: String
    )
    val items = listOf(
        BottomNavItem("Crea", Icons.Default.AddCircleOutline, Screen.SottoadminScreen.withMailSottoadmin(username)),
        BottomNavItem("Profilo", Icons.Default.Person, Screen.ProfiloAdminScreen.withUsernameAndRole(username, 2)) // 2 = Sottoadmin
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(containerColor= mioBluScuro,
        modifier =
            Modifier.clip(
                shape= RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                )
            )
    ) {

        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.route?.substringBefore("?") == item.route.substringBefore("?")
            } == true
            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = mioGialloOcra,
                    indicatorColor = mioGialloOcra,
                    unselectedIconColor = Color.White,
                    unselectedTextColor = Color.White
                ),
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                // Label (testo sotto)
                label = { Text(item.label) },

                // Logica di selezione visiva
                selected = isSelected,
                onClick = {
                    if (isSelected) return@NavigationBarItem // Blocca il reload se sei già qui!

                    navController.navigate(item.route) {
                        popUpTo(Screen.SottoadminScreen.route) {
                            saveState = true
                        }

                        // Queste servono per non duplicare le pagine
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }

}

@Composable
fun myBottomNavBar_Agente(navController: NavController, username: String) {

    data class BottomNavItem(
        val label: String,
        val icon: ImageVector,
        val route: String
    )

    val items = listOf(
        BottomNavItem("Immobili", Icons.Default.Apartment, Screen.AgenteScreen.withMailAgente(username)),
        BottomNavItem("Notifiche", Icons.Default.Notifications, "notificheAgente_route"),
        BottomNavItem("Profilo", Icons.Default.Person, Screen.ProfiloAgenteScreen.withEmail(username = username))
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(containerColor= mioBluScuro,
        modifier =
            Modifier.clip(
                shape= RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                )
            )
    ) {

        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.route?.substringBefore("?") == item.route.substringBefore("?")
            } == true
            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = mioGialloOcra,
                    indicatorColor = mioGialloOcra,
                    unselectedIconColor = Color.White,
                    unselectedTextColor = Color.White
                ),
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                // Label (testo sotto)
                label = { Text(item.label) },

                // Logica di selezione visiva
                selected = isSelected,
                onClick = {
                    if (isSelected) return@NavigationBarItem // Blocca il reload se sei già qui!

                    navController.navigate(item.route) {
                        popUpTo(Screen.AgenteScreen.route) {
                            saveState = true
                        }

                        // Queste servono per non duplicare le pagine
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}