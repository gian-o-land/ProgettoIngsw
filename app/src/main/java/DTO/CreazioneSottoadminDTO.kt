package DTO

import kotlinx.serialization.Serializable

@Serializable
data class CreazioneSottoadminDTO(
    val password: String,
    val nomeAgenzia: String,
    val adminCreatore: String
)