package com.example.progettoingsw

import org.junit.Assert.assertEquals
import org.junit.Test

// 1. Logica di validazione isolata dalla UI
class GestoreModificaCredenziali {
    fun verificaCredenziali(nome: String, cognome: String, email: String, telefono: String): String {

        // Regola 1: Errore se anche solo uno dei campi è vuoto
        if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
            return "ERRORE_CAMPI_VUOTI"
        }

        // Regola 2: Errore se il numero di telefono non è di 10 cifre
        if (telefono.length != 10) {
            return "ERRORE_TELEFONO_LUNGHEZZA"
        }

        // Regola 3: Errore se l'email non rispetta il formato "%_@_%._%"
        // Usiamo una Regex base che controlla la presenza di caratteri prima di @, tra @ e il punto, e dopo il punto.
        val emailRegex = "^.+@.+\\..+$".toRegex()
        if (!email.matches(emailRegex)) {
            return "ERRORE_FORMATO_EMAIL"
        }

        // Se supera tutti i controlli, restituisce successo (anche se i dati sono uguali ai vecchi)
        return "SUCCESSO"
    }
}

// 2. I Test xUnit basati sulla tabella WR-ECT
class ModificaCredenzialiUtenteTest {

    private val gestore = GestoreModificaCredenziali()

    @Test
    fun T1_modificaTuttiDatiValidi_restituisceSuccesso() {
        // Arrange & Act
        val risultato = gestore.verificaCredenziali("Gianluca", "Podano", "gianpod@email.com", "1234567890")
        // Assert
        assertEquals("SUCCESSO", risultato)
    }

    @Test
    fun T2_datiUgualiAiPrecedenti_restituisceSuccesso() {
        // Ipotizziamo che "Vecchio" siano i dati già presenti nel DB
        val risultato = gestore.verificaCredenziali("Vecchio", "Vecchio", "vecchio@email.com", "0987654321")
        assertEquals("SUCCESSO", risultato)
    }

    @Test
    fun T3_nomeVuoto_restituisceErroreCampiVuoti() {
        // Single Fault: Nome vuoto
        val risultato = gestore.verificaCredenziali("", "Podano", "gianpod@email.com", "1234567890")
        assertEquals("ERRORE_CAMPI_VUOTI", risultato)
    }

    @Test
    fun T4_telefonoVuoto_restituisceErroreCampiVuoti() {
        // Single Fault: Telefono vuoto
        val risultato = gestore.verificaCredenziali("Gianluca", "Podano", "gianpod@email.com", "")
        assertEquals("ERRORE_CAMPI_VUOTI", risultato)
    }

    @Test
    fun T5_telefonoCorto_restituisceErroreTelefono() {
        // Single Fault: Telefono di 3 cifre invece di 10
        val risultato = gestore.verificaCredenziali("Gianluca", "Podano", "gianpod@email.com", "123")
        assertEquals("ERRORE_TELEFONO_LUNGHEZZA", risultato)
    }

    @Test
    fun T6_emailSenzaChiocciolaEPunto_restituisceErroreEmail() {
        // Single Fault: Formato email errato
        val risultato = gestore.verificaCredenziali("Gianluca", "Podano", "gianpodemailcom", "1234567890")
        assertEquals("ERRORE_FORMATO_EMAIL", risultato)
    }
}