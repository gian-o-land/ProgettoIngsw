package DTO

import kotlinx.serialization.Serializable

@Serializable
data class UtenteDTO(
    val email: String,
    val telefono: String,
    val password: String,
    val nomeCognome: String,
    val selectedPrefix: String
)