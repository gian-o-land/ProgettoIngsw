package gui.template

import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.ui.unit.dp
import gui.mioBluScuro
import gui.mioGialloOcra
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState // Importante per l'evidenziazione automatica
import navigation.Screen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
@Composable
fun myBottomNavBar(navController: NavController) {

    data class BottomNavItem(
        val label: String,
        val icon: ImageVector,
        val route: String
    )

    // Assicurati che queste stringhe ("utente_screen", etc.) siano identiche
    // a quelle che hai definito nel tuo NavHost
    val items = listOf(
        BottomNavItem("Cerca", Icons.Default.Search, Screen.UtenteScreen.route),
        BottomNavItem("Notifiche", Icons.Default.Notifications, Screen.NotificheScreen.route),
        BottomNavItem("Profilo", Icons.Default.Person, "profilo_screen")
    )

    // 2. LOGICA AUTOMATICA: Capiamo dove siamo guardando il NavController
    // (Sostituisce la variabile selectedItemIndex)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
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
            // Controlla se la rotta dell'item è uguale alla rotta attuale
            //val isSelected = currentRoute == item.route

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
                        // INVECE DI 'findStartDestination', DICIAMOGLI ESPLICITAMENTE
                        // DI TORNARE ALLA TUA HOME INTERNA ("Cerca")
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
fun myBottomNavBar_Admin() {

    data class BottomNavItem(
        val label: String,
        val icon: ImageVector
    )
    val items = listOf(
        BottomNavItem("Crea", Icons.Default.AddCircleOutline),
        BottomNavItem("Profilo", Icons.Default.Person)
    )

    var selectedItemIndex by remember { mutableIntStateOf(0) }

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

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(

                    selectedIconColor = Color.Black,

                    selectedTextColor = mioGialloOcra,

                    indicatorColor = mioGialloOcra,//.copy(alpha = 0.1f),

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
                selected = selectedItemIndex == index,
                onClick = {
                    selectedItemIndex = index
                    // Qui in futuro metterai: navController.navigate(...)
                }
            )
        }
    }

}