package gui.template

import DTO.InserzioneDTO
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import gui.ShowPopup_Agente
import gui.ShowPopup_Sottoadmin
import gui.mieScritte
import gui.mieiTitoli
import gui.mioBackgroung
import gui.mioBluScuro
import gui.simpleVerticalScrollbar
import kotlin.collections.emptyMap


@Composable
fun MyExpandableCard_Sottoadmin(
    animatedHeight: Dp,
    headerHeight: Dp,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    listState: LazyListState,
    lista: List<String>, // Nomi dei Sottoadmin
    titolo: String,
    listaPopup: Map<String, List<String>>, // Mappa Sottoadmin -> Suoi Agenti
    itemHeight: Dp,
    // --- MODIFICA 1: Cambiato il tipo per accettare la mappa dei DTO reali ---
    mappaInserzioniAgenti: Map<String, List<InserzioneDTO>>,
    itemHeightImmobile: Dp,
    navController: NavController,
    isLoading: Boolean
){
    var popupSelection by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier
            .height(animatedHeight)
            .width(300.dp),
        colors = CardDefaults.cardColors(
            containerColor = mioBackgroung,
        ),
        border = BorderStroke(2.dp, mioBluScuro)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(mioBluScuro)
                .height(headerHeight)
                .clickable{
                    if (!isLoading){
                        onExpandedChange(!isExpanded)
                    }
                          },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = titolo,
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp,
                color = mieiTitoli,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 15.dp)
            )
            if (isLoading) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier
                        .padding(end = 15.dp)
                        .size(24.dp), // Lo facciamo piccolino per stare bene nella riga
                    color = mieiTitoli,
                    strokeWidth = 2.5.dp
                )
            } else {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Espandi",
                    tint = mieiTitoli,
                    modifier = Modifier.padding(end = 10.dp)
                )
            }
        }
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 2.dp)
                .simpleVerticalScrollbar(state = listState)
        ) {
            items(lista){ itemLista ->
                Row(
                    modifier = Modifier.height(1.dp)
                        .fillMaxWidth()
                        .background(Color.Black)
                ) { }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .height(50.dp)
                        .clickable {
                            popupSelection = itemLista
                        },
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = itemLista,
                        fontSize = 16.sp,
                        color = mieScritte,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                }
            }
        }
    }

    if (popupSelection != null) {
        ShowPopup_Sottoadmin(
            selected = popupSelection,
            onSelectedChange = { nuovoValore ->
                popupSelection = nuovoValore
            },
            listaAgenti = listaPopup[popupSelection] ?: listOf("Nessun agente trovato"),
            itemHeight = itemHeight,
            // --- MODIFICA 2: Usiamo il nome parametro corretto "mappaInserzioniAgenti" ---
            mappaInserzioniAgenti = mappaInserzioniAgenti,
            itemHeightImmobile = itemHeightImmobile,
            navController = navController
        )
    }
}
@Composable
fun MyExpandableCard_Agenti(
    animatedHeight: Dp,
    headerHeight: Dp,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    listState: LazyListState,
    lista: List<String>, // Nomi degli agenti
    titolo: String,
    listaPopup: Map<String, List<InserzioneDTO>>, // Mappa Agente -> Sue Inserzioni
    itemHeight: Dp,
    navController: NavController,
    isLoading: Boolean
){
    var popupSelection by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier
            .height(animatedHeight)
            .width(300.dp),
        colors = CardDefaults.cardColors(containerColor = mioBackgroung),
        border = BorderStroke(2.dp, mioBluScuro)
    ) {
        // Header della Card (Cliccabile per espandere)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(mioBluScuro)
                .height(headerHeight)
                .clickable {
                    if (!isLoading){
                        onExpandedChange(!isExpanded)
                    }
                           },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = titolo,
                color = mieiTitoli,
                modifier = Modifier.padding(start = 15.dp)
            )
            if (isLoading) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier
                        .padding(end = 15.dp)
                        .size(24.dp), // Lo facciamo piccolino per stare bene nella riga
                    color = mieiTitoli,
                    strokeWidth = 2.5.dp
                )
            } else {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Espandi",
                    tint = mieiTitoli,
                    modifier = Modifier.padding(end = 10.dp)
                )
            }
        }

        // Lista degli Agenti
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(lista) { nomeAgente ->
                HorizontalDivider(color = Color.Black, thickness = 0.5.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clickable { popupSelection = nomeAgente }, // Cliccando l'agente si apre il popup
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = nomeAgente,
                        color = mieScritte,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                }
            }
        }
    }

    // --- IL POPUP ---
    if (popupSelection != null) {
        ShowPopup_Agente(
            selected = popupSelection,
            onSelectedChange = { popupSelection = it },
            // FIX ERRORE: Specifichiamo il tipo <InserzioneDTO>
            listaImmobili = listaPopup[popupSelection] ?: emptyList<InserzioneDTO>(),
            itemHeight = itemHeight,
            navController = navController
        )
    }
}

@Composable
fun MyExpandableCard_Prenotazioni(
    animatedHeight: Dp,
    headerHeight: Dp,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    listState: LazyListState,
    lista: List<String>,
    titolo: String,
    navController: NavController
){
    Card(
        modifier = Modifier
            .height(animatedHeight)
            .width(300.dp),
        colors = CardDefaults.cardColors(
            containerColor = mioBackgroung,
        ),
        border = BorderStroke(2.dp, mioBluScuro)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(mioBluScuro)
                .height(headerHeight)
                .clickable{ onExpandedChange(!isExpanded)},
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = titolo,
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp,
                color = mieiTitoli,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 15.dp)
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Espandi",
                tint = mieiTitoli,
                modifier = Modifier.padding(end = 10.dp)
            )
        }
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 2.dp)
                .simpleVerticalScrollbar(
                    state = listState
                )
        ) {
            items(lista){ itemLista ->
                Row(
                    modifier = Modifier.height(1.dp)
                        .fillMaxWidth()
                        .background(Color.Black)
                ) { }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .height(50.dp)
                        .clickable {
                        },
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = itemLista,
                        fontSize = 16.sp,
                        color = mieScritte,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                }
            }
        }
    }
}