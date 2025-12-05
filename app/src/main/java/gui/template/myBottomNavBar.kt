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
import androidx.compose.ui.unit.dp
@Composable
fun myBottomNavBar() {

    data class BottomNavItem(
        val label: String,
        val icon: ImageVector
    )
    val items = listOf(
        BottomNavItem("Cerca", Icons.Default.Search),
        BottomNavItem("Notifiche", Icons.Default.Notifications),
        BottomNavItem("Profilo", Icons.Default.Person)
    )

    var selectedItemIndex by remember { mutableIntStateOf(0) }

            NavigationBar(containerColor=Color(0xFF00409C),
                modifier =
                    Modifier.clip(
                        shape= RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                )
                    )
            ) {

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        colors = NavigationBarItemDefaults.colors(

                            selectedIconColor = Color.Black,

                            selectedTextColor = Color(0xFFF2CB05),

                            indicatorColor = Color(0xFFF2CB05),//.copy(alpha = 0.1f),

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