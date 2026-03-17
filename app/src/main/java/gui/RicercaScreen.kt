package gui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.Box
import navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RicercaScreen(navController : NavController) {

    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(true) }
    val searchHistory = listOf("Roma", "Milano", "Napoli")

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        containerColor =  Color.White,
        topBar ={ Box(modifier = Modifier.fillMaxSize()) {
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                onSearch = {
                    active = false
                    println("Cercando: $it")

                    navController.navigate(Screen.FiltriScreen.route)

                },
                active = active,
                onActiveChange = { active = it },
                placeholder = { Text("Cerca città o luogo...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Cerca")
                },
                trailingIcon = {
                    if (active) {
                        IconButton(
                            onClick = {
                            if (query.isNotEmpty()) query = "" else active = false
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Chiudi")
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(if (!active) 16.dp else 0.dp)
            ) {
                searchHistory.forEach { item ->
                    ListItem(
                        headlineContent = { Text(item) },
                        leadingContent = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.clickable {
                            query = item
                            active = false
                        }
                    )
                }
            }
        }
            },


        bottomBar = {
        }
    ) {

    }
}



