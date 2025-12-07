package gui

import androidx.compose.ui.draw.clipToBounds
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import gui.template.myTextField
import gui.template.myTextField_Password
import model.source.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(painter: Painter, descrizione: String?, navController: NavController) {
    // CONFIGURAZIONE DIMENSIONI HEADER
    val headerMaxHeight = 180.dp // Altezza espansa
    val headerMinHeight = 40.dp  // Altezza collassata (barra blu)

    val density = LocalDensity.current
    val headerMaxHeightPx = with(density) { headerMaxHeight.toPx() }
    val headerMinHeightPx = with(density) { headerMinHeight.toPx() }
    val collapseRangePx = headerMaxHeightPx - headerMinHeightPx

    // STATO UI FORM
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var cognome by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var testPassword by remember { mutableStateOf("") }
    var prefixDropdownExpanded by remember { mutableStateOf(false) }
    var selectedPrefix by remember { mutableStateOf(DefaultPhonePrefix) }

    // STATO SCROLL E ANIMAZIONE
    // Altezza corrente header (parte dal massimo)
    var currentHeaderHeightPx by remember { mutableFloatStateOf(headerMaxHeightPx) }
    // Stato della lista
    val listState = rememberLazyListState()

    // --- LOGICA NESTED SCROLL (CUORE DEL MOVIMENTO)
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                // SCROLL VERSO L'ALTO (COLLASSO)
                if (delta < 0) {
                    val oldHeight = currentHeaderHeightPx
                    val newHeight = (currentHeaderHeightPx + delta).coerceIn(headerMinHeightPx, headerMaxHeightPx)

                    if (oldHeight != newHeight) {
                        currentHeaderHeightPx = newHeight
                        val consumed = newHeight - oldHeight
                        return Offset(0f, consumed)
                    }
                }

                // SCROLL VERSO IL BASSO (ESPANSIONE)
                if (delta > 0) {
                    val isAtTop = listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0

                    if (isAtTop) {
                        val oldHeight = currentHeaderHeightPx
                        val newHeight = (currentHeaderHeightPx + delta).coerceIn(headerMinHeightPx, headerMaxHeightPx)

                        if (oldHeight != newHeight) {
                            currentHeaderHeightPx = newHeight
                            val consumed = newHeight - oldHeight
                            return Offset(0f, consumed)
                        }
                    }
                }
                return Offset.Zero
            }
        }
    }

    // CALCOLI GRAFICI
    val currentHeaderHeightDp = with(density) { currentHeaderHeightPx.toDp() }

    // Tiriamo su la lista per coprire lo spazio lasciato dal padding fisso
    val listTranslationY = -(headerMaxHeightPx - currentHeaderHeightPx)

    // Dissolvenza basata sull'altezza
    val imageAlpha = ((currentHeaderHeightPx - headerMinHeightPx) / collapseRangePx).coerceIn(0f, 1f)


    // UI COMPOSITION
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(mioBluScuro) // Sfondo generale
            .nestedScroll(nestedScrollConnection)
    ) {
        // CONTENUTO (LISTA/FORM)
        LazyColumn(
            state = listState,
            // Il padding è FISSO al massimo, poi trasliamo la lista sopra
            contentPadding = PaddingValues(top = headerMaxHeight),
            modifier = Modifier
                .fillMaxSize()
                // Applichiamo traslazione per seguire la barra
                .graphicsLayer { translationY = listTranslationY }
                // Aumentiamo l'altezza virtuale della lista per evitare il vuoto in fondo
                .layout { measurable, constraints ->
                    val extraHeight = collapseRangePx.roundToInt()
                    val newConstraints = constraints.copy(
                        minHeight = constraints.maxHeight + extraHeight,
                        maxHeight = constraints.maxHeight + extraHeight
                    )
                    val placeable = measurable.measure(newConstraints)
                    layout(placeable.width, constraints.maxHeight) {
                        placeable.place(0, 0)
                    }
                }
        ) {
            // Mettiamo tutto il form in un singolo item della LazyColumn
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = mioBackgroung,
                            shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
                        )
                        .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
                ) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = "Registrazione",
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = mieScritte,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    myTextField(
                        Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                        "Email",
                        email,
                        { nuovoTesto -> email = nuovoTesto }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    myTextField(
                        Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                        "Nome",
                        nome,
                        { nuovoTesto -> nome = nuovoTesto }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    myTextField(
                        Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                        "Cognome",
                        cognome,
                        { nuovoTesto -> cognome = nuovoTesto }
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // --- SEZIONE TELEFONO ---
                    Row(verticalAlignment = Alignment.Top) {
                        ExposedDropdownMenuBox(
                            expanded = prefixDropdownExpanded,
                            onExpandedChange = { prefixDropdownExpanded = !prefixDropdownExpanded },
                            modifier = Modifier.padding(start = 35.dp, end = 10.dp)
                        ) {
                            OutlinedTextField(
                                value = "${selectedPrefix.flagEmoji}${selectedPrefix.displayName}",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Pref.") },
                                singleLine = true,
                                modifier = Modifier
                                    .menuAnchor()
                                    .width(130.dp),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = prefixDropdownExpanded)
                                },
                                textStyle = typography.bodyLarge,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = mioBluScuro,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedTextColor = mieScritte,
                                    unfocusedTextColor = mieScritte,
                                    unfocusedContainerColor = mioBackgroung,
                                    focusedLabelColor = mioBluScuro,
                                    unfocusedLabelColor = Color.Gray,
                                    cursorColor = Color.Black,
                                    focusedTrailingIconColor = Color.Gray,
                                    unfocusedTrailingIconColor = Color.Gray
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = prefixDropdownExpanded,
                                onDismissRequest = { prefixDropdownExpanded = false },
                                modifier = Modifier
                                    .background(mioBackgroung)
                                    .border(
                                        width = 0.5.dp,
                                        color = Color.Black,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            ) {
                                CommonPhonePrefixes.forEachIndexed { index, prefix ->
                                    DropdownMenuItem(
                                        text = { Text("${prefix.flagEmoji} ${prefix.displayName}") },
                                        onClick = {
                                            selectedPrefix = prefix
                                            prefixDropdownExpanded = false
                                        }
                                    )
                                    if (index < CommonPhonePrefixes.lastIndex) {
                                        HorizontalDivider(
                                            color = Color.Black,
                                            thickness = 0.5.dp
                                        )
                                    }
                                }
                            }
                        }
                        OutlinedTextField(
                            value = telefono,
                            onValueChange = { telefono = it },
                            label = { Text("Numero") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 35.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = typography.bodyLarge,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = mioBluScuro,
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = mieScritte,
                                unfocusedTextColor = mieScritte,
                                unfocusedContainerColor = mioBackgroung,
                                focusedLabelColor = mioBluScuro,
                                unfocusedLabelColor = Color.Gray,
                                cursorColor = Color.Black
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    myTextField_Password(
                        Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                        "Password",
                        password,
                        { nuovoTesto -> password = nuovoTesto }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    myTextField_Password(
                        Modifier.padding(horizontal = 35.dp).fillMaxWidth(),
                        "Reinserisci password",
                        testPassword,
                        { nuovoTesto -> testPassword = nuovoTesto }
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    Button(
                        {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = mioGialloOcra,
                            contentColor = mieScritte
                        ),
                        modifier = Modifier
                            .height(50.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 80.dp)
                    ) {
                        Text("Registrati", fontSize = 17.sp)
                    }
                    // Aggiungiamo un extra spazio in fondo per scrollare comodamente
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        // HEADER BAR (SOVRAPPOSTA)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(currentHeaderHeightDp)
                .background(mioBluScuro)
                .align(Alignment.TopCenter)
                .clipToBounds() // Importante per non disegnare l'immagine fuori dal box
        ) {
            // Immagine con dissolvenza
            Image(
                painter = painter,
                contentDescription = descrizione,
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .requiredHeight(headerMaxHeight) // Mantiene la size originale anche quando il box si stringe
                    .graphicsLayer {
                        alpha = imageAlpha
                    }
                    .align(Alignment.BottomCenter) // Mantiene l'immagine centrata in basso mentre si chiude
            )
        }
    }
}

