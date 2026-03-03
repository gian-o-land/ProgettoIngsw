package gui

import DTO.D_arred
import DTO.D_catInser
import DTO.D_energ
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


fun generaIdInserzione(): String {
    // Usiamo lettere maiuscole e numeri per evitare confusione tra 'l' minuscola e '1'
    val caratteriConsentiti = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    return (1..6)
        .map { caratteriConsentiti.random() }
        .joinToString("")
}

fun mappaCategoria(valoreUI: String): D_catInser {
    return when (valoreUI) {
        "In vendita" -> D_catInser.VENDITA
        "In affitto" -> D_catInser.AFFITTO
        else -> D_catInser.VENDITA // Valore di sicurezza
    }
}

fun mappaArredamento(valoreUI: String): D_arred {
    return when (valoreUI) {
        "Completo" -> D_arred.COMPLETO
        "Parziale" -> D_arred.PARZIALE
        "No" -> D_arred.NO
        else -> D_arred.NO
    }
}

fun mappaClasseEnergetica(valoreUI: String): D_energ {
    // Se il tuo slider restituisce lettere (es. "A", "G") o parole intere, le gestiamo entrambe
    return when (valoreUI.uppercase()) {
        "ALTA"-> D_energ.ALTA
        "MEDIA"-> D_energ.MEDIA
        "BASSA"-> D_energ.BASSA
        else -> D_energ.BASSA
    }
}
@Composable
fun RigaCliccabileConErrore(
    icona: ImageVector,
    testo: String,
    valoreAttuale: String,
    opzioni: List<String>,
    isError: Boolean = false,                  // <--- AGGIUNTO: Riceve il segnale di errore
    onValoreCambiato: (String) -> Unit = {}    // <--- AGGIUNTO: Comunica la scelta alla schermata
) {
    var menuEspanso by remember { mutableStateOf(false) }

    // --- COLORI DINAMICI ---
    // Se c'è un errore diventano rossi, altrimenti usano i tuoi colori originali
    val coloreIcona = if (isError) Color(0xFFB00020) else mioBluScuro
    val coloreTesto = if (isError) Color(0xFFB00020) else Color.Black
    val coloreTestoValore = if (isError) Color(0xFFB00020) else Color.DarkGray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { menuEspanso = true }
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- PARTE SINISTRA ---
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icona,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = coloreIcona // <--- Usa il colore dinamico
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = testo,
                fontSize = 16.sp,
                color = coloreTesto // <--- Usa il colore dinamico
            )
        }

        // --- PARTE DESTRA ---
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Non usa più "selezioneAttuale", ma "valoreAttuale" passato dalla schermata
                Text(
                    text = valoreAttuale,
                    fontSize = 16.sp,
                    color = coloreTestoValore // <--- Usa il colore dinamico
                )
                val iconaFreccia = if (menuEspanso) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight
                Icon(
                    imageVector = iconaFreccia,
                    contentDescription = null,
                    tint = coloreIcona // <--- Usa il colore dinamico
                )
            }

            // --- MENU A TENDINA ---
            DropdownMenu(
                expanded = menuEspanso,
                onDismissRequest = { menuEspanso = false },
                modifier = Modifier
                    .heightIn(max = 192.dp)
                    .background(Color.White)

            ) {
                opzioni.forEachIndexed { index, opzione ->
                    DropdownMenuItem(
                        text = { Text(text = opzione, color = Color.Black) },
                        onClick = {
                            // Quando l'utente clicca, avvisiamo la schermata principale della nuova scelta
                            onValoreCambiato(opzione)
                            menuEspanso = false
                        }
                    )
                    if (index < opzioni.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun RigaValoreConErrore(
    icona: ImageVector,
    testo: String,
    valoreAttuale: String,
    isError: Boolean = false,
    onValoreCambiato: (String) -> Unit = {},
    suffisso: String? = null
) {
    // --- COLORI DINAMICI ---
    val coloreIcona = if (isError) Color(0xFFB00020) else Color(0xFF000080) // Usa la tua variabile mioBluScuro
    val coloreTesto = if (isError) Color(0xFFB00020) else Color.Black

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icona,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = coloreIcona
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = testo,
            fontSize = 16.sp,
            color = coloreTesto,
            modifier = Modifier.weight(1f)
        )

        // Passiamo il suffisso al componente che si occupa di disegnare la casella!
        BoxInputValore(
            valore = valoreAttuale,
            onValoreCambiato = onValoreCambiato,
            isError = isError,
            label = "Valore",
            suffisso = suffisso // <--- AGGIUNTO IL PASSAGGIO DEL SUFFISSO
        )
    }
}




@Composable
fun BoxInputValore(
    label: String,
    valore: String,
    onValoreCambiato: (String) -> Unit,
    isError: Boolean = false,
    suffisso: String? = null               // <--- 1. AGGIUNTO: Riceve il suffisso opzionale
) {
    // --- COLORI DINAMICI PER L'ERRORE ---
    val coloreBordo = if (isError) Color(0xFFB00020) else Color.Black
    val coloreEtichetta = if (isError) Color(0xFFB00020) else Color.DarkGray

    BasicTextField(
        value = valore,
        onValueChange = { nuovoTesto ->
            val soloNumeri = nuovoTesto.filter { it.isDigit() }
            onValoreCambiato(soloNumeri)
        },
        singleLine = true,
        textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),

        modifier = Modifier
            // ⚠️ PICCOLO CONSIGLIO: ho aumentato leggermente la larghezza da 90.dp a 110.dp
            // perché con numeri lunghi + simbolo € + la "X", lo spazio iniziava a stare stretto!
            .width(110.dp)
            .height(45.dp)
            .background(Color.White, RoundedCornerShape(4.dp))
            .border(1.dp, coloreBordo, RoundedCornerShape(4.dp)),

        decorationBox = { innerTextField ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // Etichetta (es. "Valore")
                Text(text = label, fontSize = 10.sp, color = coloreEtichetta)

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Box(modifier = Modifier.weight(1f)) {
                        // Placeholder "Input"
                        if (valore.isEmpty()) {
                            Text(text = "Input", fontSize = 14.sp, color = Color.Gray)
                        }
                        // Testo digitato
                        innerTextField()
                    }

                    // --- 2. IL SUFFISSO MAGICO ---
                    // Lo mostriamo solo se è stato passato e se c'è un valore inserito
                    // (così non si sovrappone visivamente alla scritta "Input")
                    if (suffisso != null && valore.isNotEmpty()) {
                        Text(
                            text = suffisso,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(end = 4.dp) // Piccolo margine prima della X
                        )
                    }

                    // Icona "X" per cancellare
                    if (valore.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Cancella",
                            modifier = Modifier
                                .size(16.dp)
                                .clickable {
                                    onValoreCambiato("")
                                },
                            // Assicurati che 'mioBluScuro' sia accessibile qui,
                            // sennò usa un colore come Color.Gray o coloreBordo
                            tint = Color(0xFF000080)
                        )
                    }
                }
            }
        }
    )
}

