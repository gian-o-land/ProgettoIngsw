package DTO
import kotlinx.serialization.Serializable

@Serializable
data class ImmagineDTO(
    val url: String,
    val tipologia: D_foto, //planimetria o foto esterni-interni
    val idInserzione: String)