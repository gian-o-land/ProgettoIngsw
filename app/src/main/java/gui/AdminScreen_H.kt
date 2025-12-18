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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.graphics.RectangleShape
import gui.template.myCardInserzione

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
    itemHeight: Dp
){
    val liststateSottoadminPopup = rememberLazyListState()
    val maxItemsToShow = 3
    val visibleItemsCount = minOf(listaAgenti.size, maxItemsToShow)
    val heightColumnPopup = itemHeight * visibleItemsCount

    Dialog(onDismissRequest = {
        onSelectedChange(null)
    }) {
        // Il contenuto del Popup
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = mioBackgroung
            ),
            border = BorderStroke(2.dp, mioBluScuro),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
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
                    modifier = Modifier.padding(end = 10.dp)
                        .height(25.dp)
                        .width(25.dp)
                        .clickable{onSelectedChange(null)}
                )
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heightColumnPopup),
                colors = CardDefaults.cardColors(
                    containerColor = mioBackgroung
                ),
                shape = RectangleShape,
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                LazyColumn(
                    state = liststateSottoadminPopup,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 2.dp)
                        .simpleVerticalScrollbar(
                            state = liststateSottoadminPopup
                        )
                ) {
                    items(listaAgenti){ agenti ->
                        Row(
                            modifier = Modifier.height(1.dp)
                                .fillMaxWidth()
                                .background(Color.Black)
                        ) { }
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .height(50.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = agenti,
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

@Composable
fun ShowPopup_Agente(
    selected: String?,
    onSelectedChange: (String?) -> Unit,
    listaImmobili: Map<String, Int>,
    itemHeight: Dp
){
    val liststateAgentePopup = rememberLazyListState()
    val maxItemsToShow = 1
    val visibleItemsCount = minOf(listaImmobili.size, maxItemsToShow)
    val heightColumnPopup = itemHeight * visibleItemsCount

    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = mioBackgroung
            ),
            border = BorderStroke(2.dp, mioBluScuro),
            elevation = CardDefaults.cardElevation(8.dp)
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(mioBluScuro),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Inserzioni create da:\n$selected",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = mieiTitoli,
                    modifier = Modifier.padding(start = 15.dp)
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Chiudi",
                    tint = mioGialloOcra,
                    modifier = Modifier.padding(end = 10.dp)
                        .height(25.dp)
                        .width(25.dp)
                        .clickable{onSelectedChange(null)}
                )
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heightColumnPopup),
                colors = CardDefaults.cardColors(
                    containerColor = mioBackgroung
                ),
                shape = RectangleShape,
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                LazyColumn(
                    state = liststateAgentePopup,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 2.dp)
                        .simpleVerticalScrollbar(
                            state = liststateAgentePopup
                        )
                ) {
                    items(listaImmobili.entries.toList()){ immobile ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .height(200.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            myCardInserzione(
                                myModifier = Modifier
                                    .height(180.dp)
                                    .width(250.dp),
                                immobile = immobile
                            )
                        }
                    }
                }
            }
        }
    }
}