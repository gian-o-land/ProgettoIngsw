package DTO

import kotlinx.serialization.Serializable

@Serializable
data class AgenteDTO(
    val username: String,
    val nome: String,
    val cognome: String,
    val telefono: String,
    val password: String,
    val email: String,
    val adminCreatore: String?,
    val selectedPrefix: String,
    val nomeAgenzia: String
)