package DTO

import kotlinx.serialization.Serializable

@Serializable
data class PrenotazioneDTO(val idPrenotazione: String, val dataOra: String, val contenuto: String, val stato: D_stato,
                           val mailUtente: String, val codiceInserzione: String, val usernameAgente: String)