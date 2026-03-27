package connection

import android.content.Context

object SessionManager {
    private const val NOME_FILE_PREFERENZE = "DietiSession"
    private const val CHIAVE_EMAIL = "email_salvata"
    private const val CHIAVE_USERNAME = "username_salvato"
    private const val CHIAVE_RUOLO = "ruolo_salvato"
    private const val CHIAVE_NOME = "nome_salvato"
    private const val CHIAVE_COGNOME = "cognome_salvato"
    private const val CHIAVE_AGENZIA = "agenzia_salvata"




    // 1. Salva i dati quando fai il login
    fun salvaSessioneUtente(context: Context, email: String?, ruolo: Int) {
        val prefs = context.getSharedPreferences(NOME_FILE_PREFERENZE, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(CHIAVE_EMAIL, email)
            .putInt(CHIAVE_RUOLO, ruolo)
            .apply() // .apply() salva in background senza bloccare l'app
    }
    fun salvaSessione(context: Context, username: String?, ruolo: Int, agenzia: String) {
        val prefs = context.getSharedPreferences(NOME_FILE_PREFERENZE, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(CHIAVE_USERNAME, username)
            .putInt(CHIAVE_RUOLO, ruolo)
            .putString(CHIAVE_AGENZIA, agenzia)
            .apply() // .apply() salva in background senza bloccare l'app
    }

    fun salvaSessioneAgente(context: Context, username: String?, email: String?, ruolo: Int, agenzia: String) {
        val prefs = context.getSharedPreferences(NOME_FILE_PREFERENZE, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(CHIAVE_USERNAME, username)
            .putString(CHIAVE_EMAIL, email)
            .putInt(CHIAVE_RUOLO, ruolo)
            .putString(CHIAVE_AGENZIA, agenzia)
            .apply() // .apply() salva in background senza bloccare l'app
    }

    // 2. Legge l'email (Restituisce null se non hai mai fatto l'accesso)
    fun leggiEmail(context: Context): String? {
        val prefs = context.getSharedPreferences(NOME_FILE_PREFERENZE, Context.MODE_PRIVATE)
        return prefs.getString(CHIAVE_EMAIL, null)
    }

    fun leggiUsername(context: Context): String? {
        val prefs = context.getSharedPreferences(NOME_FILE_PREFERENZE, Context.MODE_PRIVATE)
        return prefs.getString(CHIAVE_USERNAME, null)
    }

    // 3. Legge il ruolo (Restituisce -1 se non hai mai fatto l'accesso)
    fun leggiRuolo(context: Context): Int {
        val prefs = context.getSharedPreferences(NOME_FILE_PREFERENZE, Context.MODE_PRIVATE)
        return prefs.getInt(CHIAVE_RUOLO, -1)
    }

    // 4. Cancella tutto quando fai il Logout
    fun eseguiLogout(context: Context) {
        val prefs = context.getSharedPreferences(NOME_FILE_PREFERENZE, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
    // Funzione per salvare nome e cognome
    fun salvaNomeCognome(context: Context, nome: String, cognome: String) {
        val prefs = context.getSharedPreferences(NOME_FILE_PREFERENZE, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(CHIAVE_NOME, nome)
            .putString(CHIAVE_COGNOME, cognome)
            .apply()
    }


    // Leggi il nome
    fun leggiNome(context: Context): String? {
        val prefs = context.getSharedPreferences(NOME_FILE_PREFERENZE, Context.MODE_PRIVATE)
        return prefs.getString(CHIAVE_NOME, null)
    }
    fun leggiAgenzia(context: Context): String? {
        val prefs = context.getSharedPreferences(NOME_FILE_PREFERENZE, Context.MODE_PRIVATE)
        return prefs.getString(CHIAVE_AGENZIA, null)
    }

    // Leggi il cognome
    fun leggiCognome(context: Context): String? {
        val prefs = context.getSharedPreferences(NOME_FILE_PREFERENZE, Context.MODE_PRIVATE)
        return prefs.getString(CHIAVE_COGNOME, null)
    }
}