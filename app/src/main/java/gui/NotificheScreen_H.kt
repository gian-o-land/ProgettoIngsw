package gui

import androidx.compose.runtime.mutableStateOf
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.progettoingsw.R
import android.app.PendingIntent
import android.content.Intent
import main.MainActivity

fun mostraNotificaAndroid(context: Context, titolo: String, messaggio: String) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = "canale_prenotazioni"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, "Aggiornamenti Prenotazioni", NotificationManager.IMPORTANCE_HIGH).apply {
            description = "Notifiche per l'accettazione o il rifiuto delle prenotazioni"
        }
        notificationManager.createNotificationChannel(channel)
    }

    //CREIAMO L'INTENT PER APRIRE L'APP
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    // Il PendingIntent è l'involucro che diamo alla notifica
    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.logo)
        .setContentTitle(titolo)
        .setContentText(messaggio)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true) // Fa scomparire la notifica dopo averla premuta

    val notificaId = System.currentTimeMillis().toInt()
    notificationManager.notify(notificaId, builder.build())
}

// Oggetto globale che vive per tutta la durata dell'app
object GestoreNotifiche {
    var haNuoveNotifiche = mutableStateOf(false)
    var conteggioNotificheLette = 0
    private var isInizializzato = false
    var conteggioNotificheNonLette = mutableStateOf(0)

    // 1. Legge la memoria all'avvio dell'app
    fun caricaStato(context: Context) {
        if (!isInizializzato) {
            val prefs = context.getSharedPreferences("NotifichePrefs", Context.MODE_PRIVATE)
            conteggioNotificheLette = prefs.getInt("conteggio", 0)
            haNuoveNotifiche.value = prefs.getBoolean("pallino", false)
            isInizializzato = true
            conteggioNotificheNonLette.value = prefs.getInt("NonLette", 0)
        }
    }

    // 2. Salva il nuovo conteggio e accende il pallino
    fun salvaNuoveNotifiche(context: Context, conteggio: Int) {
        conteggioNotificheNonLette.value = conteggio - conteggioNotificheLette
        haNuoveNotifiche.value = true

        val prefs = context.getSharedPreferences("NotifichePrefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean("pallino", true)
            .putInt("NonLette", conteggioNotificheNonLette.value)
            .apply()
    }

    // 3. Spegne il pallino quando apri la pagina notifiche
    fun azzeraPallino(context: Context) {
        conteggioNotificheLette += conteggioNotificheNonLette.value
        conteggioNotificheNonLette.value = 0
        haNuoveNotifiche.value = false

        val prefs = context.getSharedPreferences("NotifichePrefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putInt("conteggio", conteggioNotificheLette)
            .putBoolean("pallino", false)
            .putInt("NonLette", 0)
            .apply()

        //Spazza via tutte le notifiche dalla tendina di Android!
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    // 4. Sincronizza in caso di cancellazioni dal database
    fun sincronizzaCancellazioni(context: Context, notificheTotaliServer: Int) {
        val totaleSalvato = conteggioNotificheLette + conteggioNotificheNonLette.value

        // Se le notifiche sul server sono MENO di quelle che ci ricordiamo...
        if (notificheTotaliServer < totaleSalvato) {

            // ...abbassiamo i nostri contatori per pareggiare il conto!
            if (notificheTotaliServer >= conteggioNotificheNonLette.value) {
                // Riduciamo solo il contatore di quelle già lette
                conteggioNotificheLette = notificheTotaliServer - conteggioNotificheNonLette.value
            } else {
                // Hanno cancellato così tanto che dobbiamo azzerare quelle lette
                conteggioNotificheLette = 0
                conteggioNotificheNonLette.value = notificheTotaliServer
            }

            // Se non ci sono più notifiche non lette, spegniamo il pallino
            if (conteggioNotificheNonLette.value == 0) {
                haNuoveNotifiche.value = false
            }

            // Salviamo la correzione nella memoria fissa del telefono
            val prefs = context.getSharedPreferences("NotifichePrefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putInt("conteggio", conteggioNotificheLette)
                .putInt("NonLette", conteggioNotificheNonLette.value)
                .putBoolean("pallino", haNuoveNotifiche.value)
                .apply()
        }
    }
}

object GestoreNotificheAgente {
    var haNuoveNotifiche = mutableStateOf(false)
    var conteggioNotificheLette = 0
    private var isInizializzato = false
    var conteggioNotificheNonLette = mutableStateOf(0)

    fun caricaStato(context: Context) {
        if (!isInizializzato) {
            val prefs = context.getSharedPreferences("NotificheAgentePrefs", Context.MODE_PRIVATE)
            conteggioNotificheLette = prefs.getInt("conteggio", 0)
            haNuoveNotifiche.value = prefs.getBoolean("pallino", false)
            conteggioNotificheNonLette.value = prefs.getInt("NonLette", 0)
            isInizializzato = true
        }
    }

    fun salvaNuoveNotifiche(context: Context, conteggio: Int) {
        conteggioNotificheNonLette.value = conteggio - conteggioNotificheLette
        haNuoveNotifiche.value = true
        context.getSharedPreferences("NotificheAgentePrefs", Context.MODE_PRIVATE).edit()
            .putBoolean("pallino", true)
            .putInt("NonLette", conteggioNotificheNonLette.value).apply()
    }

    fun azzeraPallino(context: Context) {
        conteggioNotificheLette += conteggioNotificheNonLette.value
        conteggioNotificheNonLette.value = 0
        haNuoveNotifiche.value = false
        context.getSharedPreferences("NotificheAgentePrefs", Context.MODE_PRIVATE).edit()
            .putInt("conteggio", conteggioNotificheLette)
            .putBoolean("pallino", false)
            .putInt("NonLette", 0).apply()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    fun sincronizzaCancellazioni(context: Context, notificheTotaliServer: Int) {
        val totaleSalvato = conteggioNotificheLette + conteggioNotificheNonLette.value
        if (notificheTotaliServer < totaleSalvato) {
            if (notificheTotaliServer >= conteggioNotificheNonLette.value) {
                conteggioNotificheLette = notificheTotaliServer - conteggioNotificheNonLette.value
            } else {
                conteggioNotificheLette = 0
                conteggioNotificheNonLette.value = notificheTotaliServer
            }
            if (conteggioNotificheNonLette.value == 0) haNuoveNotifiche.value = false

            context.getSharedPreferences("NotificheAgentePrefs", Context.MODE_PRIVATE).edit()
                .putInt("conteggio", conteggioNotificheLette)
                .putInt("NonLette", conteggioNotificheNonLette.value)
                .putBoolean("pallino", haNuoveNotifiche.value).apply()
        }
    }
}