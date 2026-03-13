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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import gui.GestoreNotifiche

@Composable
fun myBottomNavBar(navController: NavController, email : String) {

    data class BottomNavItem(
        val label: String,
        val icon: ImageVector,
        val route: String
    )

    val items = listOf(
        BottomNavItem("Cerca", Icons.Default.Search, Screen.UtenteScreen.withMailUtente(email)),
        BottomNavItem("Notifiche", Icons.Default.Notifications, Screen.NotificheScreen.withMailUtente(email)),
        BottomNavItem("Profilo", Icons.Default.Person, Screen.ProfiloScreen.withMail(email))
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

            // --- NUOVA LOGICA DI SELEZIONE ---
            val isSelected = currentDestination?.hierarchy?.any { destination ->
                // Prendiamo la rotta attuale e la puliamo dai parametri
                val baseCurrentRoute = destination.route?.substringBefore("?")?.substringBefore("/")
                // Prendiamo la rotta dell'item e la puliamo dai parametri
                val baseItemRoute = item.route.substringBefore("?")?.substringBefore("/")

                // Confrontiamo solo i nomi puliti (es. "utente_screen" == "utente_screen")
                baseCurrentRoute == baseItemRoute
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
                    if (item.label == "Notifiche" && GestoreNotifiche.haNuoveNotifiche.value) {
                        //MOSTRA IL PALLINO ROSSO!
                        BadgedBox(
                            badge = { Badge { Text("${GestoreNotifiche.conteggioNotificheNonLette.value}") } }
                        ) {
                            Icon(imageVector = item.icon, contentDescription = item.label)
                        }
                    } else {
                        // Icona normale senza pallino
                        Icon(imageVector = item.icon, contentDescription = item.label)
                    }
                },
                label = { Text(item.label) },
                selected = isSelected,
                onClick = {
                    if (isSelected) return@NavigationBarItem // Blocca il reload se sei già qui!

                    navController.navigate(item.route) {
                        popUpTo(Screen.UtenteScreen.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun myBottomNavBar_Admin(navController: NavController, username: String, agenziaAdmin: String) {

    data class BottomNavItem(
        val label: String,
        val icon: ImageVector,
        val route: String
    )
    val items = listOf(
        BottomNavItem("Crea", Icons.Default.AddCircleOutline, Screen.AdminScreen.withArgs(username,agenziaAdmin)),
        BottomNavItem("Profilo", Icons.Default.Person, Screen.ProfiloAdminScreen.withArgs(username, 1, agenziaAdmin)) // 1 = Admin
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
            // --- MODIFICA CRUCIALE QUI ---
            // Tagliamo sia i parametri query (?) sia i parametri path (/)
            val isSelected = currentDestination?.hierarchy?.any { navDestination ->
                val routeTemplate = navDestination.route ?: ""
                val radiceAttuale = routeTemplate.substringBefore("?").substringBefore("/")
                val radiceItem = item.route.substringBefore("?").substringBefore("/")

                radiceAttuale == radiceItem && radiceAttuale.isNotEmpty()
            } == true
            // -----------------------------

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
fun myBottomNavBar_Sottoadmin(navController: NavController, username: String, agenziaAdmin: String) {

    data class BottomNavItem(
        val label: String,
        val icon: ImageVector,
        val route: String
    )
    val items = listOf(
        BottomNavItem("Crea", Icons.Default.AddCircleOutline, Screen.SottoadminScreen.withArgs(username, agenziaAdmin)),
        BottomNavItem("Profilo", Icons.Default.Person, Screen.ProfiloAdminScreen.withArgs(username, 2, agenziaAdmin)) // 2 = Sottoadmin
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
            // --- MODIFICA DI SICUREZZA ---
            // Tagliamo in modo robusto sia i "?" che i "/"
            val isSelected = currentDestination?.hierarchy?.any { navDestination ->
                val routeTemplate = navDestination.route ?: ""
                val radiceAttuale = routeTemplate.substringBefore("?").substringBefore("/")
                val radiceItem = item.route.substringBefore("?").substringBefore("/")

                radiceAttuale == radiceItem && radiceAttuale.isNotEmpty()
            } == true
            // -----------------------------

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
        BottomNavItem("Notifiche", Icons.Default.Notifications, Screen.NotificheAgenteScreen.withUsernameAgente(username)),
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
                    if (item.label == "Notifiche" && GestoreNotifiche.haNuoveNotifiche.value) {
                        BadgedBox(
                            badge = { Badge { Text("${GestoreNotifiche.conteggioNotificheNonLette.value}") } }
                        ) { Icon(imageVector = item.icon, contentDescription = item.label) }
                    } else {
                        Icon(imageVector = item.icon, contentDescription = item.label)
                    }
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