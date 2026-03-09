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

    // 1. Legge la memoria all'avvio dell'app
    fun caricaStato(context: Context) {
        if (!isInizializzato) {
            val prefs = context.getSharedPreferences("NotifichePrefs", Context.MODE_PRIVATE)
            conteggioNotificheLette = prefs.getInt("conteggio", 0)
            haNuoveNotifiche.value = prefs.getBoolean("pallino", false)
            isInizializzato = true
        }
    }

    // 2. Salva il nuovo conteggio e accende il pallino
    fun salvaNuoveNotifiche(context: Context, conteggio: Int) {
        conteggioNotificheLette = conteggio
        haNuoveNotifiche.value = true

        val prefs = context.getSharedPreferences("NotifichePrefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putInt("conteggio", conteggio)
            .putBoolean("pallino", true)
            .apply()
    }

    // 3. Spegne il pallino quando apri la pagina notifiche
    fun azzeraPallino(context: Context) {
        haNuoveNotifiche.value = false
        val prefs = context.getSharedPreferences("NotifichePrefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("pallino", false).apply()
    }
}