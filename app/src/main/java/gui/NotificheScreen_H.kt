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
    var conteggioNotificheNonLette = mutableStateOf(0)

    // Funzione per ottenere il nome del file basato sull'utente e sul tipo
    private fun getPrefsName(id: String): String = "NotifichePrefs_$id"

    fun caricaStato(context: Context, id: String) {
        val prefs = context.getSharedPreferences(getPrefsName(id), Context.MODE_PRIVATE)
        conteggioNotificheLette = prefs.getInt("conteggio", 0)
        haNuoveNotifiche.value = prefs.getBoolean("pallino", false)
        conteggioNotificheNonLette.value = prefs.getInt("NonLette", 0)
    }

    fun salvaNuoveNotifiche(context: Context, conteggioTotaleServer: Int, id: String) {
        conteggioNotificheNonLette.value = conteggioTotaleServer - conteggioNotificheLette
        haNuoveNotifiche.value = true

        context.getSharedPreferences(getPrefsName(id), Context.MODE_PRIVATE).edit()
            .putBoolean("pallino", true)
            .putInt("NonLette", conteggioNotificheNonLette.value)
            .apply()
    }

    fun azzeraPallino(context: Context, id: String) {
        conteggioNotificheLette += conteggioNotificheNonLette.value
        conteggioNotificheNonLette.value = 0
        haNuoveNotifiche.value = false

        context.getSharedPreferences(getPrefsName(id), Context.MODE_PRIVATE).edit()
            .putInt("conteggio", conteggioNotificheLette)
            .putBoolean("pallino", false)
            .putInt("NonLette", 0)
            .commit()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    fun sincronizzaCancellazioni(context: Context, notificheTotaliServer: Int, id: String) {
        val totaleSalvato = conteggioNotificheLette + conteggioNotificheNonLette.value
        if (notificheTotaliServer < totaleSalvato) {
            if (notificheTotaliServer >= conteggioNotificheNonLette.value) {
                conteggioNotificheLette = notificheTotaliServer - conteggioNotificheNonLette.value
            } else {
                conteggioNotificheLette = 0
                conteggioNotificheNonLette.value = notificheTotaliServer
            }
            haNuoveNotifiche.value = conteggioNotificheNonLette.value > 0

            context.getSharedPreferences(getPrefsName(id), Context.MODE_PRIVATE).edit()
                .putInt("conteggio", conteggioNotificheLette)
                .putInt("NonLette", conteggioNotificheNonLette.value)
                .putBoolean("pallino", haNuoveNotifiche.value)
                .apply()
        }
    }
}