package com.example.progettoingsw

import org.junit.Assert.assertEquals
import org.junit.Test

// 1. Logica di validazione
class GestoreFiltri {

    private val cittaValide = listOf("Roma", "Milano", "Napoli", "Torino", "Firenze")

    fun verificaFiltri(
        localita: String,
        prezzoMin: String, prezzoMax: String,
        quadMin: String, quadMax: String,
        localiMin: String, localiMax: String
    ): String {

        // 1. Controlli Località
        if (localita.isEmpty()) return "ERRORE_LOCALITA_VUOTA"
        if (!cittaValide.contains(localita)) return "ERRORE_LOCALITA_INESISTENTE"

        // 2. Controlli Range: L'errore scatta SOLO se entrambi ci sono, ma sono invertiti
        val pMin = prezzoMin.toIntOrNull()
        val pMax = prezzoMax.toIntOrNull()
        if (pMin != null && pMax != null && pMin > pMax) return "ERRORE_PREZZO_MIN_MAX"

        val qMin = quadMin.toIntOrNull()
        val qMax = quadMax.toIntOrNull()
        if (qMin != null && qMax != null && qMin > qMax) return "ERRORE_QUADRATURA_MIN_MAX"

        val lMin = localiMin.toIntOrNull()
        val lMax = localiMax.toIntOrNull()
        if (lMin != null && lMax != null && lMin > lMax) return "ERRORE_LOCALI_MIN_MAX"

        // Se è vuoto, o se c'è solo min, o se c'è solo max, o se min < max, va tutto bene!
        return "SUCCESSO"
    }
}

// 2. Test xUnit basati sulla tabella WR-ECT
class FiltriRicercaTest {

    private val gestore = GestoreFiltri()

    @Test
    fun T1_filtriMistiVuotiEMaxMin_restituisceSuccesso() {
        // Quad Max è vuoto, Locali Min è vuoto. Deve funzionare!
        val risultato = gestore.verificaFiltri("Roma", "100", "500", "50", "", "", "3")
        assertEquals("SUCCESSO", risultato)
    }

    @Test
    fun T2_filtriPrezzoEntrambiVuoti_restituisceSuccesso() {
        // I prezzi sono entrambi vuoti. Deve funzionare!
        val risultato = gestore.verificaFiltri("Milano", "", "", "50", "100", "1", "3")
        assertEquals("SUCCESSO", risultato)
    }

    @Test
    fun T3_localitaVuota_restituisceErroreLocalita() {
        // Single Fault: Manca la città
        val risultato = gestore.verificaFiltri("", "100", "500", "50", "100", "1", "3")
        assertEquals("ERRORE_LOCALITA_VUOTA", risultato)
    }

    @Test
    fun T4_localitaInesistente_restituisceErroreLocalita() {
        // Single Fault: Città finta
        val risultato = gestore.verificaFiltri("Giubilopoli", "100", "500", "50", "100", "1", "3")
        assertEquals("ERRORE_LOCALITA_INESISTENTE", risultato)
    }

    @Test
    fun T5_prezzoMinoreMaggioreMassimo_restituisceErrorePrezzo() {
        // Single Fault: Prezzi invertiti
        val risultato = gestore.verificaFiltri("Roma", "500", "100", "50", "100", "1", "3")
        assertEquals("ERRORE_PREZZO_MIN_MAX", risultato)
    }

    @Test
    fun T6_quadraturaMinoreMaggioreMassima_restituisceErroreQuadratura() {
        // Single Fault: Quadratura invertita
        val risultato = gestore.verificaFiltri("Roma", "100", "500", "100", "50", "1", "3")
        assertEquals("ERRORE_QUADRATURA_MIN_MAX", risultato)
    }

    @Test
    fun T7_localiMinoreMaggioreMassimo_restituisceErroreLocali() {
        // Single Fault: Locali invertiti
        val risultato = gestore.verificaFiltri("Roma", "100", "500", "50", "100", "3", "1")
        assertEquals("ERRORE_LOCALI_MIN_MAX", risultato)
    }
}