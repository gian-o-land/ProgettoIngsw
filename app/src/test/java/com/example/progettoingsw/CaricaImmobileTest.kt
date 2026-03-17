package com.example.progettoingsw

import org.junit.Assert.assertEquals
import org.junit.Test

// 1. Logica estratta dalla UI
class GestoreCaricamentoImmobile {

    val indirizziValidi = listOf("Via Roma 15, Pomigliano D'Arco, Napoli", "Via Michelangelo Buonarroti 8, Castello di Cisterna, Napoli")

    // Simula il comportamento della libreria Geocoder di Google
    private fun verificaIndirizzoSuMappa(citta: String, comune: String, indirizzo: String): Boolean {
        if (!indirizziValidi.contains("$indirizzo, $comune, $citta")) return false
        return true
    }

    fun verificaCaricamento(
        categoria: String, piano: String, arredamento: String,
        prezzo: String, quadratura: String, locali: String, bagni: String,
        haFoto: Boolean, haPlanimetria: Boolean,
        citta: String, comune: String, indirizzo: String, descrizione: String
    ): String {

        // Regola 1: Controllo Menu
        val menuValidi = categoria != "-" && piano != "-" && arredamento != "-"
        if (!menuValidi) return "ERRORE_MENU_NON_SELEZIONATO"

        // Regola 2: Controllo Campi Numerici
        val campiValidi = prezzo.isNotBlank() && quadratura.isNotBlank() && locali.isNotBlank() && bagni.isNotBlank()
        if (!campiValidi) return "ERRORE_CAMPI_NUMERICI_VUOTI"

        // Regola 3: Controllo Testi
        val testiValidi = citta.isNotBlank() && comune.isNotBlank() && indirizzo.isNotBlank() && descrizione.isNotBlank()
        if (!testiValidi) return "ERRORE_TESTI_VUOTI"

        // Regola 4: Controllo Immagini
        if (!haFoto) return "ERRORE_FOTO_MANCANTI"
        if (!haPlanimetria) return "ERRORE_PLANIMETRIA_MANCANTE"

        // Regola 5: Controllo Esistenza Geografica (Viene eseguito solo se i testi ci sono)
        val mappaValida = verificaIndirizzoSuMappa(citta, comune, indirizzo)
        if (!mappaValida) return "ERRORE_INDIRIZZO_NON_TROVATO"

        return "SUCCESSO"
    }
}

// 2. I Test xUnit basati sulla tabella WR-ECT
class CaricaImmobileTest {

    private val gestore = GestoreCaricamentoImmobile()

    @Test
    fun T1_tuttiDatiValidi_restituisceSuccesso() {
        val risultato = gestore.verificaCaricamento(
            "Vendita", "1", "Completo",
            "100000", "100", "4", "2",
            true, true,
            "Napoli", "Pomigliano D'Arco", "Via Roma 15", "Bellissimo appartamento"
        )
        assertEquals("SUCCESSO", risultato)
    }

    @Test
    fun T2_menuNonSelezionato_restituisceErroreMenu() {
        // Single Fault: Piano non selezionato ("-")
        val risultato = gestore.verificaCaricamento(
            "Vendita", "-", "Completo",
            "100000", "100", "4", "2",
            true, true,
            "Napoli", "Pomigliano D'Arco", "Via Roma 15", "Bellissimo appartamento"
        )
        assertEquals("ERRORE_MENU_NON_SELEZIONATO", risultato)
    }

    @Test
    fun T3_prezzoVuoto_restituisceErroreCampiNumerici() {
        // Single Fault: Prezzo vuoto
        val risultato = gestore.verificaCaricamento(
            "Vendita", "1", "Completo",
            "", "100", "4", "2",
            true, true,
            "Napoli", "Pomigliano D'Arco", "Via Roma 15", "Bellissimo appartamento"
        )
        assertEquals("ERRORE_CAMPI_NUMERICI_VUOTI", risultato)
    }

    @Test
    fun T4_nessunaFoto_restituisceErroreFoto() {
        // Single Fault: haFoto = false
        val risultato = gestore.verificaCaricamento(
            "Vendita", "1", "Completo",
            "100000", "100", "4", "2",
            false, true,
            "Napoli", "Pomigliano D'Arco", "Via Roma 15", "Bellissimo appartamento"
        )
        assertEquals("ERRORE_FOTO_MANCANTI", risultato)
    }

    @Test
    fun T5_nessunaPlanimetria_restituisceErrorePlanimetria() {
        // Single Fault: haPlanimetria = false
        val risultato = gestore.verificaCaricamento(
            "Vendita", "1", "Completo",
            "100000", "100", "4", "2",
            true, false,
            "Napoli", "Pomigliano D'Arco", "Via Roma 15", "Bellissimo appartamento"
        )
        assertEquals("ERRORE_PLANIMETRIA_MANCANTE", risultato)
    }

    @Test
    fun T6_descrizioneVuota_restituisceErroreTesti() {
        // Single Fault: Descrizione vuota
        val risultato = gestore.verificaCaricamento(
            "Vendita", "1", "Completo",
            "100000", "100", "4", "2",
            true, true,
            "Napoli", "Pomigliano D'Arco", "Via Roma 15", ""
        )
        assertEquals("ERRORE_TESTI_VUOTI", risultato)
    }

    @Test
    fun T7_indirizzoInesistente_restituisceErroreMappa() {
        // Single Fault: Mappa rifiuta la città ("Atlantide")
        val risultato = gestore.verificaCaricamento(
            "Vendita", "1", "Completo",
            "100000", "100", "4", "2",
            true, true,
            "Sinnoh", "Giubilopoli", "Percorso 203", "Bellissimo trilocale"
        )
        assertEquals("ERRORE_INDIRIZZO_NON_TROVATO", risultato)
    }
}