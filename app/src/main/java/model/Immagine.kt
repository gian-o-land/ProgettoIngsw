package model

class Immagine(private var nomeFile: String,
               private var formato: D_formato,
               private var dimensione: Float,
               private var categoria: D_foto, //planimetria o foto esterni-interni
               private var codiceInserzione: String) {

    fun getNomeFile(): String{
        return nomeFile
    }

    fun setNomeFile(newNomeFile: String){
        nomeFile = newNomeFile
    }

    fun getFormato(): D_formato{
        return formato
    }

    fun setFormato(newFormato: D_formato){
        formato = newFormato
    }

    fun getDimensione(): Float{
        return dimensione
    }

    fun setDimensione(newDimensione: Float){
        dimensione = newDimensione
    }

    fun getCategoria(): D_foto{
        return categoria
    }

    fun setCategoria(newCategoria: D_foto){
        categoria = newCategoria
    }

    fun getCodiceInserzione(): String{
        return codiceInserzione
    }

    fun setCodcieInserzione(newInserzione: String){
        codiceInserzione = newInserzione
    }

}