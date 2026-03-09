package DTO

import kotlinx.serialization.Serializable

@Serializable
data class RegistrazioneUtenteDTO(
    val email: String,
    val nome: String,
    val cognome: String,
    val prefisso: String,
    val telefono: String, // Qui uniremo il prefisso e il numero
    val password: String
)