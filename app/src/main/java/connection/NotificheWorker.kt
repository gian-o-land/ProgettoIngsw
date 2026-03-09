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
        val emailUtente = SessionManager.leggiEmail(applicationContext)

        // Se non c'è nessuno loggato, fermiamo il lavoro
        if (emailUtente == null) return Result.success()

        // 2. Carichiamo lo stato delle notifiche lette
        GestoreNotifiche.caricaStato(applicationContext)

        try {
            // 3. Facciamo la chiamata al server
            val prenotazioni = scaricaPrenotazioniUtente(emailUtente)
            val notificheChiuse = prenotazioni.count { it.stato != D_stato.ATTESA }

            // 4. Se ci sono novità, salviamo e spariamo la notifica!
            if (notificheChiuse > GestoreNotifiche.conteggioNotificheLette) {
                GestoreNotifiche.salvaNuoveNotifiche(applicationContext, notificheChiuse)

                mostraNotificaAndroid(
                    context = applicationContext,
                    titolo = "Aggiornamento Prenotazione",
                    messaggio = "Un agente ha risposto a una tua richiesta di visita."
                )
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.retry() // Se non c'è internet, riprova più tardi
        }
    }
}