package connection

import DTO.AgenteDTO
import DTO.InserzioneDTO
import DTO.UtenteDTO
import DTO.source.PhonePrefix
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.coroutines.CancellationException
import android.content.Context
import android.net.Uri
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import io.ktor.client.request.delete
import kotlinx.coroutines.NonCancellable

val client = HttpClient(Android) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}
val urlServer = "https://idvhx-101-56-160-180.a.free.pinggy.link"  // da cambiare ogni volta se cambia quando riavvio pinggy


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

    } catch (e: CancellationException) {
        // Se Compose cancella la coroutine, glielo lasciamo fare senza bloccarlo
        throw e
    } catch (e: Exception) {
        // Così l'app scriverà l'errore in rosso nella console di Android Studio (Logcat)!
        android.util.Log.e("ERRORE_CONNESSIONE", "Impossibile collegarsi al server: ${e.message}", e)
        -1
    }
}

suspend fun connessioneLoginGoogle(idTokenGoogle: String): Int {
    return try {
        // Facciamo una POST alla rotta del backend pensata solo per Google
        val risposta: HttpResponse = client.post("$urlServer/login/google") {
            setBody(
                FormDataContent(
                    Parameters.build {
                        // Inviamo il token crittografato al server
                        append("idToken", idTokenGoogle)
                    }
                )
            )
        }

        // Il tuo server Ktor (dopo aver controllato il token sul DB) ti risponderà
        // con un numero:
        //  0 -> Utente normale (nuovo o esistente) ammesso
        // -1 -> Errore, token falso, oppure tentativo di accesso bloccato
        val testoRisposta = risposta.bodyAsText()
        testoRisposta.toIntOrNull() ?: -1

    } catch (e: Exception) {
        android.util.Log.e("ERRORE_CONNESSIONE", "Impossibile collegarsi al server (Google): ${e.message}", e)
        -1
    }
}


// Passiamo il Context per poter leggere il file dalla memoria del telefono
suspend fun inviaImmagineAServer(context: Context, uri: Uri): String? {
    return try {
        // 1. Apriamo il file selezionato e lo trasformiamo in un array di byte
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: return null

        // 2. Creiamo una richiesta "Multipart" (come i form dei siti web con allegati)
        val risposta = client.submitFormWithBinaryData(
            url = "$urlServer/upload/immagine",
            formData = formData {
                append("image", bytes, Headers.build {
                    // Diciamo al server che è un'immagine
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"foto_immobile.jpg\"")
                })
            }
        )

        // 3. Il backend ci risponderà con l'URL pubblico di Amazon S3 o con "-1"
        val risultato = risposta.bodyAsText()
        if (risultato == "-1") null else risultato

    } catch (e: Exception) {
        android.util.Log.e("UPLOAD_S3", "Errore invio immagine: ${e.message}", e)
        null
    }
}
suspend fun scaricaUtente(email: String): UtenteDTO? {
    return try {
        // Usiamo GET invece di POST, l'email è già nell'URL!
        val risposta: HttpResponse = client.get("$urlServer/utente/$email")

        // Se il server ha risposto con "OK"
        if (risposta.status == HttpStatusCode.OK) {
            return risposta.body()
        } else {
            // Se risponde NotFound (o altro), l'utente non c'è
            return null
        }

    } catch (e: Exception) {
        android.util.Log.e("ERRORE_RECUPERO", "Errore: ${e.message}", e)
        return null
    }
}


@Serializable
data class InserzioneRequest(
    val inserzione: InserzioneDTO,
    val urlFoto: List<String>,
    val urlPlanimetria: String?
)

suspend fun inviaInserzioneAlServer(
    client: HttpClient,
    inserzione: InserzioneDTO,
    urlFoto: MutableList<String>,
    urlPlanimetria: String?
): Boolean {
    return try {
        // 1. "Impacchettiamo" i tre dati in un unico oggetto
        val pacchettoDati = InserzioneRequest(
            inserzione = inserzione,
            urlFoto = urlFoto,
            urlPlanimetria = urlPlanimetria
        )

        val risposta: HttpResponse = client.post("$urlServer/inserzioni") {
            contentType(ContentType.Application.Json)
            // 2. Passiamo il pacchetto unico al setBody
            setBody(pacchettoDati)
        }

        if (risposta.status.isSuccess()) {
            android.util.Log.d("API_INSERZIONE", "Inserzione caricata con successo su PostgreSQL!")
            true
        } else {
            android.util.Log.e("API_INSERZIONE", "Errore dal server: ${risposta.status}")
            false
        }
    } catch (e: Exception) {
        android.util.Log.e("API_INSERZIONE", "Errore di connessione: ${e.message}")
        false
    }
}

suspend fun scaricaTutteLeInserzioni(): List<InserzioneDTO> {
    return try {
        // Facciamo una richiesta GET alla rotta che ci restituirà il JSON con tutte le case
        val risposta: HttpResponse = client.get("$urlServer/inserzioni")

        if (risposta.status == HttpStatusCode.OK) {
            // Ktor converte magicamente l'array JSON in una List<Inserzione>
            risposta.body()
        } else {
            android.util.Log.e("API_MAPPA", "Errore dal server: ${risposta.status}")
            emptyList()
        }
    } catch (e: Exception) {
        android.util.Log.e("API_MAPPA", "Impossibile scaricare le inserzioni: ${e.message}")
        emptyList()
    }
}

suspend fun scaricaAgente(username: String): AgenteDTO? {
    return withContext(Dispatchers.IO) {
        try {
            // Usa il tuo indirizzo IP corretto al posto di 10.0.2.2 o quello che stai usando
            val response = client.get("$urlServer/agente/$username")

            if (response.status.isSuccess()) {
                val agente = response.body<AgenteDTO>()
                android.util.Log.d("API_AGENTE", "Agente scaricato: ${agente.username}")
                agente
            } else {
                android.util.Log.e("API_AGENTE", "Errore dal server: ${response.status}")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("API_AGENTE", "Errore di connessione: ${e.message}")
            null
        }
    }
}

suspend fun completaProfiloGoogle(utente: UtenteDTO): Boolean {
    return try {
        val risposta: HttpResponse = client.post("$urlServer/completaUtenteGoogle") {
            // Diciamo a Ktor che stiamo inviando un JSON
            contentType(ContentType.Application.Json)
            // Impacchettiamo l'intero DTO nel corpo della richiesta
            setBody(utente)
        }

        // Se il server risponde con OK (200), l'aggiornamento è andato a buon fine
        if (risposta.status.isSuccess()) {
            android.util.Log.d("API_GOOGLE_UPDATE", "Profilo aggiornato con successo!")
            true
        } else {
            android.util.Log.e("API_GOOGLE_UPDATE", "Errore dal server: ${risposta.status}")
            false
        }
    } catch (e: Exception) {
        android.util.Log.e("API_GOOGLE_UPDATE", "Errore di connessione: ${e.message}")
        false
    }
}

suspend fun eliminaUtenteGoogleIncompleto(email: String) {
    // withContext(NonCancellable) rende questa coroutine "immortale" finché non ha finito
    withContext(Dispatchers.IO + NonCancellable) {
        try {
            val risposta = client.delete("$urlServer/eliminaUtenteGoogle/$email")
            if (risposta.status.isSuccess()) {
                android.util.Log.d("API_DELETE", "Utente fantasma $email eliminato.")
            }
        } catch (e: Exception) {
            android.util.Log.e("API_DELETE", "Errore eliminazione utente: ${e.message}")
        }
    }
}