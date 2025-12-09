package gui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.SearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ListItem
import com.google.android.gms.maps.model.LatLng

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RicercaMappaScreen(navController : NavController) {
    // Stato del testo cercato
    var query by remember { mutableStateOf("") }
    // Stato per sapere se la barra è espansa (sta mostrando i risultati) o no
    var active by remember { mutableStateOf(false) }
    // Lista finta di risultati per l'esempio
    val searchHistory = listOf("Roma", "Milano", "Napoli", "Pizzeria da Michele")
    val locationNapoli = LatLng(40.8518, 14.2681)
    // Usiamo un BOX per sovrapporre la ricerca alla mappa
    Box(modifier = Modifier.fillMaxSize()) {

        // 1. LA MAPPA (Sullo sfondo)
        MappaScreen(location=locationNapoli,navController)
        // Nota: Mettila come primo elemento così sta "sotto"

        // 2. LA BARRA DI RICERCA (Sopra la mappa)
        SearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = {
                active = false // Chiude la lista quando premi invio
                // QUI FAI PARTIRE LA LOGICA DI GEOCODING O NAVIGAZIONE
                println("Cercando: $it")
            },
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text("Cerca città o luogo...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Cerca")
            },
            trailingIcon = {
                // Mostra la X solo se c'è testo
                if (active) {
                    IconButton(onClick = {
                        if (query.isNotEmpty()) query = "" else active = false
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Chiudi")
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.TopCenter) // Posizionata in alto al centro
                // Se non è attiva, diamo un po' di margine dai bordi
                .padding(if (!active) 16.dp else 0.dp)
        ) {
            // 3. IL CONTENUTO DEI SUGGERIMENTI (appare quando active = true)
            searchHistory.forEach { item ->
                ListItem(
                    headlineContent = { Text(item) },
                    leadingContent = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.clickable {
                        query = item
                        active = false // Chiudi la ricerca
                        // Fai partire la navigazione per "item"
                    }
                )
            }
        }
    }
}