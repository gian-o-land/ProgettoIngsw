package connection

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
val client = HttpClient(Android)
val urlServer = "https://onrushing-alyssa-nonbillable.ngrok-free.dev"  // da cambiare ogni volta se cambia quando riavvio ngrok


suspend fun connessioneLogin(identificativo: String, passwordInserita: String): Int {
    return try {
        // 1. Facciamo una richiesta POST all'indirizzo del login
        val risposta: HttpResponse = client.post("$urlServer/login") {

            // 2. Impacchettiamo i dati esattamente come se fosse un form di un sito web
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("identificativo", identificativo)
                        append("password", passwordInserita)
                    }
                )
            )
        }

        // 3. Leggiamo la risposta del server (che sarà "0", "1", "2", "3" o "-1")
        val testoRisposta = risposta.bodyAsText()

        // 4. Convertiamo la stringa in un numero intero.
        // Se per qualche motivo il server risponde con una parola strana, toIntOrNull()
        // restituisce null, e l'operatore "?: -1" lo trasforma in -1 come sicurezza.
        testoRisposta.toIntOrNull() ?: -1

    } catch (e: Exception) {
        // Così l'app scriverà l'errore in rosso nella console di Android Studio (Logcat)!
        android.util.Log.e("ERRORE_CONNESSIONE", "Impossibile collegarsi al server: ${e.message}", e)
        -1
    }
}