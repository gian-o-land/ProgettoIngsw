package gui.template

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gui.ShowPopup_Agente
import gui.ShowPopup_Sottoadmin
import gui.mieScritte
import gui.mieiTitoli
import gui.mioBackgroung
import gui.mioBluScuro
import gui.simpleVerticalScrollbar

@Composable
fun MyExpandableCard_Sottoadmin(
    animatedHeight: Dp,
    headerHeight: Dp,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    listState: LazyListState,
    lista: List<String>,
    titolo: String,
    listaPopup: List<String>,
    itemHeight: Dp,
    listaImmobili: Map<String, Int>,
    itemHeightImmobile: Dp
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
                // Se nuovoValore è null (chiusura), aggiorniamo lo stato locale
                popupSelection = nuovoValore
            },
            listaAgenti = listaPopup,
            itemHeight = itemHeight,
            listaImmobili = listaImmobili,
            itemHeightImmobile = itemHeightImmobile
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
    lista: List<String>,
    titolo: String,
    listaPopup: Map<String, Int>,
    itemHeight: Dp
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
    if (popupSelection != null){
        ShowPopup_Agente(
            selected = popupSelection,
            onSelectedChange = { nuovoValore ->
                // Se nuovoValore è null (chiusura), aggiorniamo lo stato locale
                popupSelection = nuovoValore
            },
            listaImmobili = listaPopup,
            itemHeight = itemHeight
        )
    }
}