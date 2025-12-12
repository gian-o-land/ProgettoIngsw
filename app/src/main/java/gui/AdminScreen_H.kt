package gui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun Modifier.simpleVerticalScrollbar(
    state: LazyListState,
    width: Dp = 4.dp,
    color: Color = Color.Gray
): Modifier = composed {
    val targetAlpha = if (state.isScrollInProgress) 1f else 0f
    // Opzionale: Animazione di dissolvenza. Se la vuoi sempre visibile, imposta alpha a 1f fisso.
    val duration = if (state.isScrollInProgress) 150 else 500

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = duration),
        label = "ScrollbarAlpha"
    )

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