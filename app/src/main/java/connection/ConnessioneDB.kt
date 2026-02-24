package connection

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

val client = HttpClient(Android)
val urlServer = "https://onrushing-alyssa-nonbillable.ngrok-free.dev"  // da cambiare ogni volta se cambia quando riavvio ngrok

suspend fun faiTestConnessione(): String {
    return try {
        // 3. Facciamo la chiamata e aspettiamo la risposta
        val risposta: HttpResponse = client.get(urlServer)

        // 4. Leggiamo il testo che il server ci ha mandato
        risposta.bodyAsText()
    } catch (e: Exception) {
        "Errore di rete: ${e.message}"
    } finally {
        client.close() // Chiudiamo sempre il client alla fine
    }
}