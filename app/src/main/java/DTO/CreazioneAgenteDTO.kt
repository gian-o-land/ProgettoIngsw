package DTO

import kotlinx.serialization.Serializable

@Serializable
data class CreazioneAgenteDTO(
    val email: String,
    val nome: String,
    val cognome: String,
    val telefono: String,
    val prefisso: String,
    val password: String,
    val nomeAgenzia: String,
    val adminCreatore: String?

    )