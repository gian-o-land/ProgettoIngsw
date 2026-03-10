package DTO

import kotlinx.serialization.Serializable

@Serializable
data class AdminDTO(
    val username: String,
    val ruolo: D_ruolo,
    val adminCreatore: String?, // Nullable perché la tua FK "admin_creatore" può essere NULL
    val nomeAgenzia: String?    // Nullable come definito nel tuo DB
)