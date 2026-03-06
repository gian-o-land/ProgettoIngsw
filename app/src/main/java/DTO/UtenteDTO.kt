package DTO

import kotlinx.serialization.Serializable

@Serializable
data class UtenteDTO(
    val email: String,
    val telefono: String,
    val password: String,
    val nome: String,
    val cognome: String,
    val selectedPrefix: String
)