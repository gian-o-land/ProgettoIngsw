package DTO

import kotlinx.serialization.Serializable
//AdminDTO
@Serializable
data class AdminDTO(
    val username: String,
    val ruolo: D_ruolo,
    val adminCreatore: String?,
    val nomeAgenzia: String?
)