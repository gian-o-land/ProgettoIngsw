package model

import java.time.LocalDateTime

class Prenotazione(private var idPrenotazione: String, private var dataOra: LocalDateTime, private var contenuto: String, private var stato: D_stato,
                   private var mailUtente: String, private var codiceInserzione: String, private var usernameAgente: String) {

    // Getter
    fun getIdPrenotazione(): String {
        return idPrenotazione
    }

    fun getDataOra(): LocalDateTime {
        return dataOra
    }

    fun getContenuto(): String {
        return contenuto
    }

    fun getStato(): D_stato {
        return stato
    }

    fun getMailUtente(): String {
        return mailUtente
    }

    fun getCodiceInserzione(): String {
        return codiceInserzione
    }

    fun getUsernameAgente(): String {
        return usernameAgente
    }

    // Setter
    fun setIdPrenotazione(newId: String) {
        idPrenotazione = newId
    }

    fun setDataOra(newDataOra: LocalDateTime) {
        dataOra = newDataOra
    }

    fun setContenuto(newContenuto: String) {
        contenuto = newContenuto
    }

    fun setStato(newStato: D_stato) {
        stato = newStato
    }

    fun setMailUtente(newMailUtente: String) {
        mailUtente = newMailUtente
    }

    fun setCodiceInserzione(newInserzione: String) {
        codiceInserzione = newInserzione
    }

    fun setUsernameAgente(newAgente: String) {
        usernameAgente = newAgente
    }
}