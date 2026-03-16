package com.example.progettoingsw

import org.junit.Assert.assertEquals
import org.junit.Test

// 1. La logica estratta dalla tua UI
class GestoreModificaPassword {
    fun verificaModifica(vecchia: String, nuova: String, conferma: String, passwordDB: String): String {
        // Controllo campi vuoti
        if (vecchia.isEmpty() || nuova.isEmpty() || conferma.isEmpty()) return "ERRORE_CAMPI_VUOTI"

        // Controllo corrispondenza conferma
        if (nuova != conferma) return "ERRORE_CONFERMA_DIVERSA"

        // Controllo formato
        val regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$".toRegex()
        if (!nuova.matches(regex)) return "ERRORE_FORMATO"

        // Controllo password reale (il server verifica la vecchia password)
        if (vecchia != passwordDB) return "ERRORE_VECCHIA_ERRATA"

        // Se arriva fin qui, la password può essere modificata
        return "SUCCESSO"
    }
}

// 2. I Test
class ModificaPasswordTest {

    private val gestore = GestoreModificaPassword()
    private val db = "Vecchia123!"

    @Test
    fun T1_modificaConPasswordDiversa_restituisceSuccesso() {
        assertEquals("SUCCESSO", gestore.verificaModifica("Vecchia123!", "Nuova456!", "Nuova456!", db))
    }

    @Test
    fun T2_modificaConPasswordUguale_restituisceSuccesso() {
        assertEquals("SUCCESSO", gestore.verificaModifica("Vecchia123!", "Vecchia123!", "Vecchia123!", db))
    }

    @Test
    fun T3_vecchiaPasswordVuota_restituisceErroreCampiVuoti() {
        assertEquals("ERRORE_CAMPI_VUOTI", gestore.verificaModifica("", "Nuova456!", "Nuova456!", db))
    }

    @Test
    fun T4_vecchiaPasswordSbagliata_restituisceErroreCorrispondenza() {
        assertEquals("ERRORE_VECCHIA_ERRATA", gestore.verificaModifica("Sbagliata99!", "Nuova456!", "Nuova456!", db))
    }

    @Test
    fun T5_nuovaPasswordVuota_restituisceErroreCampiVuoti() {
        assertEquals("ERRORE_CAMPI_VUOTI", gestore.verificaModifica("Vecchia123!", "", "", db))
    }

    @Test
    fun T6_nuovaPasswordFormatoErrato_restituisceErroreFormato() {
        assertEquals("ERRORE_FORMATO", gestore.verificaModifica("Vecchia123!", "ciao", "ciao", db))
    }

    @Test
    fun T7_confermaDiversaDaNuova_restituisceErroreConferma() {
        assertEquals("ERRORE_CONFERMA_DIVERSA", gestore.verificaModifica("Vecchia123!", "Nuova456!", "Sbagliata789!", db))
    }
}