package DTO
import kotlinx.serialization.Serializable

@Serializable
data class ImmagineDTO(
    val nomeFile: String,
    val formato: D_formato,
    val dimensione: Float,
    val categoria: D_foto, //planimetria o foto esterni-interni
    val codiceInserzione: String)