package gui

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.graphics.RectangleShape
import gui.template.myCardInserzionePlaceholder
import androidx.compose.foundation.layout.*

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

import coil.compose.AsyncImage // Importante per le foto reali!
import DTO.InserzioneDTO
import androidx.compose.material3.HorizontalDivider
import com.example.progettoingsw.R
@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.simpleVerticalScrollbar(
    state: LazyListState,
    width: Dp = 4.dp,
    color: Color = Color.Gray
): Modifier = composed {

    drawWithContent {
        drawContent()

        val firstVisibleElementIndex = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index
        val needDrawScrollbar = state.layoutInfo.totalItemsCount > state.layoutInfo.visibleItemsInfo.size

        // Disegna solo se c'è contenuto da scrollare
        if (needDrawScrollbar && firstVisibleElementIndex != null) {
            val visibleItemsInfo = state.layoutInfo.visibleItemsInfo

            // 1. Calcola l'altezza media degli elementi visibili per una stima stabile
            val averageItemSize = visibleItemsInfo.sumOf { it.size } / visibleItemsInfo.size.toFloat()

            // 2. Stima l'altezza totale del contenuto (pixel)
            val estimatedContentHeight = averageItemSize * state.layoutInfo.totalItemsCount

            // 3. Calcola l'altezza della viewport (la card visibile)
            val viewportHeight = size.height

            // 4. Calcola quanto deve essere alto il "pomello" della scrollbar
            val scrollbarHeight = (viewportHeight / estimatedContentHeight) * viewportHeight

            // 5. Calcola la posizione esatta in pixel (offset + indice * altezza media)
            val scrollOffset = state.firstVisibleItemIndex * averageItemSize + state.firstVisibleItemScrollOffset

            // 6. Proietta la posizione sulla barra
            val scrollbarOffsetY = (scrollOffset / estimatedContentHeight) * viewportHeight

            // Disegna la barra
            drawRoundRect(
                color = color,
                topLeft = Offset(size.width - width.toPx(), scrollbarOffsetY),
                size = Size(width.toPx(), scrollbarHeight),
                //alpha = alpha, // Rimuovi questo parametro se vuoi che sia sempre visibile
                cornerRadius = CornerRadius(width.toPx() / 2, width.toPx() / 2)
            )
        }
    }
}

@Composable
fun animatedHeight(
    lista: MutableList<String>,
    isExpanded: Boolean,
    headerHeight: Dp,
    itemHeight: Dp
): Dp{
    val maxItemsToShow = 3
    val visibleItemsCount = minOf(lista.size, maxItemsToShow)

    val expandedHeight = headerHeight + (itemHeight * visibleItemsCount)
    val collapsedHeight = headerHeight

    val animatedHeight by animateDpAsState(
        targetValue = if (isExpanded) expandedHeight else collapsedHeight,
        label = "CardHeightAnimation"
    )
    return animatedHeight
}

@Composable
fun ShowPopup_Sottoadmin(
    selected: String?,
    onSelectedChange: (String?) -> Unit,
    listaAgenti: List<String>,
    itemHeight: Dp,
    // --- MODIFICA: Riceviamo la mappa che collega ogni agente alle sue inserzioni DTO ---
    mappaInserzioniAgenti: Map<String, List<InserzioneDTO>>,
    itemHeightImmobile: Dp
) {
    val liststateSottoadminPopup = rememberLazyListState()
    val maxItemsToShow = 3
    val visibleItemsCount = if(listaAgenti.isEmpty()) 1 else minOf(listaAgenti.size, maxItemsToShow)
    val heightColumnPopup = itemHeight * visibleItemsCount

    // Stato per gestire l'apertura del popup dell'agente cliccato
    var popupAgenteSelection by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = { onSelectedChange(null) }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = mioBackgroung),
            border = BorderStroke(2.dp, mioBluScuro),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            // Header del Popup
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(mioBluScuro),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Agenti creati da:\n$selected",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = mieiTitoli,
                    modifier = Modifier.padding(start = 15.dp)
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Chiudi",
                    tint = mioGialloOcra,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .height(25.dp)
                        .width(25.dp)
                        .clickable { onSelectedChange(null) }
                )
            }

            // Lista degli Agenti del Sottoadmin
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heightColumnPopup),
                colors = CardDefaults.cardColors(containerColor = mioBackgroung),
                shape = RectangleShape,
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                if (listaAgenti.isEmpty() || listaAgenti.contains("Nessun agente creato")) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Nessun agente trovato", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        state = liststateSottoadminPopup,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 2.dp)
                            .simpleVerticalScrollbar(state = liststateSottoadminPopup)
                    ) {
                        items(listaAgenti) { agente ->
                            HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.Black)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .clickable {
                                        // Salviamo l'agente cliccato per aprire il suo popup
                                        popupAgenteSelection = agente
                                    },
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = agente,
                                    fontSize = 16.sp,
                                    color = mieScritte,
                                    modifier = Modifier.padding(start = 15.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- IL SECONDO POPUP (QUELLO DEGLI AGENTI) ---
    if (popupAgenteSelection != null) {
        ShowPopup_Agente(
            selected = popupAgenteSelection,
            onSelectedChange = { nuovoValore ->
                popupAgenteSelection = nuovoValore
            },
            // FIX: Passiamo la lista di DTO corretta filtrando per l'agente selezionato
            // Specifichiamo <InserzioneDTO> per non far arrabbiare il compilatore!
            listaImmobili = mappaInserzioniAgenti[popupAgenteSelection] ?: emptyList<InserzioneDTO>(),
            itemHeight = itemHeightImmobile
        )
    }
}




@Composable
fun ShowPopup_Agente(
    selected: String?,
    onSelectedChange: (String?) -> Unit,
    listaImmobili: List<InserzioneDTO>, // MODIFICATO: Ora riceve la lista di DTO
    itemHeight: Dp
){
    val liststateAgentePopup = rememberLazyListState()

    // Decidiamo quanto spazio deve occupare il popup
    // Se non ci sono immobili, mostriamo un'altezza minima per il testo "Vuoto"
    val maxItemsToShow = 2
    val visibleItemsCount = if(listaImmobili.isEmpty()) 1 else minOf(listaImmobili.size, maxItemsToShow)
    val heightColumnPopup = itemHeight * visibleItemsCount

    Dialog(onDismissRequest = { onSelectedChange(null) }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = mioBackgroung),
            border = BorderStroke(2.dp, mioBluScuro),
            elevation = CardDefaults.cardElevation(8.dp)
        ){
            // --- HEADER BLU ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp) // Leggermente più alto per far stare bene il testo su due righe
                    .background(mioBluScuro),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Inserzioni di:\n$selected",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = mieiTitoli,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(start = 15.dp)
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Chiudi",
                    tint = mioGialloOcra,
                    modifier = Modifier
                        .padding(end = 15.dp)
                        .size(28.dp)
                        .clickable { onSelectedChange(null) }
                )
            }

            // --- CORPO DEL POPUP (LISTA) ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heightColumnPopup),
                colors = CardDefaults.cardColors(containerColor = mioBackgroung),
                shape = RectangleShape
            ) {
                if (listaImmobili.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Nessun immobile caricato", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        state = liststateAgentePopup,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .simpleVerticalScrollbar(liststateAgentePopup)
                    ) {
                        items(listaImmobili) { immobile ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(itemHeight),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // CHIAMATA ALLA CARD REALE
                                MyCardInserzioneReale(
                                    immobile = immobile,
                                    modifier = Modifier
                                        .height(itemHeight - 20.dp)
                                        .width(260.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyCardInserzioneReale(
    immobile: InserzioneDTO,
    modifier: Modifier = Modifier
) {
    // Prendiamo l'URL della prima immagine (Copertina)
    val urlFoto = immobile.immagini.firstOrNull()?.url

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(0.5.dp, Color.LightGray)
    ) {
        Column {
            // IMMAGINE REALE DA SERVER
            AsyncImage(
                model = urlFoto,
                contentDescription = "Foto immobile",
                placeholder = painterResource(R.drawable.immobile_esempio_1), // Mentre carica
                error = painterResource(R.drawable.immobile_esempio_1),       // Se l'URL è rotto
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                contentScale = ContentScale.Crop
            )

            // DATI REALI (Indirizzo e Prezzo)
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = immobile.indirizzo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    color = Color.Black
                )
                Text(
                    text = "${immobile.prezzo} €",
                    fontSize = 12.sp,
                    color = mioBluScuro,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}