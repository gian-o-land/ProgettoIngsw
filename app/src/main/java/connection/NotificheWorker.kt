package connection

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import DTO.D_stato
import gui.GestoreNotifiche
import gui.mostraNotificaAndroid

class NotificheWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // 1. Capiamo chi è l'utente leggendo la memoria fissa (perché l'app è chiusa!)
        val emailUtente = SessionManager.leggiEmail(applicationContext) ?: return Result.success()

        val ruolo = SessionManager.leggiRuolo(applicationContext)
        if (ruolo != 0) return Result.success()

        // Se non c'è nessuno loggato, fermiamo il lavoro

        // 2. Carichiamo lo stato delle notifiche lette
        GestoreNotifiche.caricaStato(applicationContext, emailUtente)

        try {
            GestoreNotifiche.caricaStato(applicationContext, emailUtente)

            // 3. Facciamo la chiamata al server
            val prenotazioni = scaricaPrenotazioniUtente(emailUtente)

            //SCUDO PROTEZIONE: Evita che il worker azzeri la memoria per un errore temporaneo
            if (prenotazioni.isEmpty()) {
                return Result.success()
            }

            val notificheChiuse = prenotazioni.count { it.stato != D_stato.ATTESA }

            val notificheGiaViste = GestoreNotifiche.conteggioNotificheLette + GestoreNotifiche.conteggioNotificheNonLette.value

            //AUTO-CORREZIONE SE IL SERVER HA MENO NOTIFICHE
            if (notificheChiuse < notificheGiaViste) {
                GestoreNotifiche.sincronizzaCancellazioni(applicationContext, notificheChiuse, emailUtente)
            }
            // 4. Se invece ci sono novità reali, salviamo e spariamo la notifica!
            else if (notificheChiuse > notificheGiaViste) {
                GestoreNotifiche.salvaNuoveNotifiche(applicationContext, notificheChiuse, emailUtente)

                mostraNotificaAndroid(
                    context = applicationContext,
                    titolo = "Aggiornamento Prenotazione",
                    messaggio = "Un agente ha risposto a una tua richiesta di visita."
                )
            }
            return Result.success()
        } catch (_: Exception) {
            return Result.retry() // Se non c'è internet, riprova più tardi
        }
    }
}

class NotificheWorkerAgente(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // 1. Capiamo chi è l'agente leggendo la memoria fissa (il SessionManager usa "leggiEmail" anche per l'username)
        val usernameAgente = SessionManager.leggiEmail(applicationContext) ?: return Result.success()

        val ruolo = SessionManager.leggiRuolo(applicationContext)
        if (ruolo != 3) return Result.success()

        // Se non c'è nessuno loggato, fermiamo il lavoro

        // 2. Carichiamo lo stato delle notifiche dalla cassaforte dell'AGENTE
        GestoreNotifiche.caricaStato(applicationContext, usernameAgente)

        try {
            GestoreNotifiche.caricaStato(applicationContext, usernameAgente)

            // 3. Scarichiamo le prenotazioni dell'agente
            val prenotazioni = scaricaPrenotazioniAgente(usernameAgente)

            //SCUDO PROTEZIONE
            if (prenotazioni.isEmpty()) {
                return Result.success()
            }

            // L'agente è interessato SOLO a quelle in ATTESA!
            val notificheInAttesa = prenotazioni.count { it.stato == D_stato.ATTESA }

            // Calcoliamo quante notifiche in attesa conoscevamo già
            val notificheGiaViste = GestoreNotifiche.conteggioNotificheLette + GestoreNotifiche.conteggioNotificheNonLette.value

            // 4. AUTO-CORREZIONE
            if (notificheInAttesa < notificheGiaViste) {
                GestoreNotifiche.sincronizzaCancellazioni(applicationContext, notificheInAttesa, usernameAgente)
            }
            // 5. NUOVE NOTIFICHE REALI
            else if (notificheInAttesa > notificheGiaViste) {
                GestoreNotifiche.salvaNuoveNotifiche(applicationContext, notificheInAttesa, usernameAgente)

                // Spariamo la notifica sul telefono
                mostraNotificaAndroid(
                    context = applicationContext,
                    titolo = "Nuova Prenotazione!",
                    messaggio = "Un utente ha richiesto una visita per un tuo immobile."
                )
            }
            return Result.success()
        } catch (_: Exception) {
            return Result.retry() // Se non c'è internet, riprova più tardi
        }
    }
}