package DTO
import kotlinx.serialization.Serializable

@Serializable
data class Account_amministrativoDTO(
    val username: String,
    val password: String,
    val ruolo: D_ruolo,
    val adminCreatore: String?)