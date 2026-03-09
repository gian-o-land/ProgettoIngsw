package DTO

import kotlinx.serialization.Serializable

@Serializable
data class ModificaPasswordDTO(
    val identificativo: String, // Sarà l'email o l'username
    val tipoUtente: D_tipoUtente,
    val vecchiaPassword: String,
    val nuovaPassword: String
)