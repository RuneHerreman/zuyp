package be.runeherreman.zuyp.data.workers

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.hardware.camera2.CameraManager
import android.media.AudioAttributes
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import be.runeherreman.zuyp.MainActivity
import be.runeherreman.zuyp.R
import be.runeherreman.zuyp.data.messaging.NotificationMessage
import be.runeherreman.zuyp.data.receivers.JoinHangoutReceiver
import be.runeherreman.zuyp.ui.alert.ZuypAlertActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object NotificationHelper {

    const val CHANNEL_HANGOUT    = "zuyp_hangout_invites"
    const val CHANNEL_ZUYP_ALERT = "zuyp_alerts"
    const val ZUYP_ALERT_ID      = 1

    fun createNotificationChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)

        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_HANGOUT, "Hangout Invites", NotificationManager.IMPORTANCE_HIGH)
        )

        val alertSoundUri = "android.resource://${context.packageName}/raw/zuyp_alert".toUri()
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ZUYP_ALERT, "Zuyp Alerts", NotificationManager.IMPORTANCE_HIGH).apply {
                setSound(
                    alertSoundUri,
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }
        )
    }

    fun handle(context: Context, currentUserId: String, message: NotificationMessage) {
        when (message) {
            is NotificationMessage.HangoutInvite  -> showHangoutInvite(context, currentUserId, message)
            is NotificationMessage.ZuypAlert      -> {
                showZuypAlert(context, message)
                CoroutineScope(Dispatchers.IO).launch { flashFlashlight(context) }
            }
            is NotificationMessage.HangoutJoined  -> showHangoutJoined(context, message)
        }
    }

    private fun showHangoutInvite(context: Context, currentUserId: String, message: NotificationMessage.HangoutInvite) {
        val manager      = context.getSystemService(NotificationManager::class.java)
        val notifId      = message.hangoutId.hashCode()

        val joinIntent = PendingIntent.getBroadcast(
            context, notifId,
            Intent(context, JoinHangoutReceiver::class.java).apply {
                putExtra("hangoutId",       message.hangoutId)
                putExtra("userId",          currentUserId)
                putExtra("notificationId",  notifId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val details = buildString {
            append("📍 ${message.locationName}\n🕐 ${message.startDate}")
            message.weather?.let { append("\n$it") }
        }

        manager.notify(
            notifId,
            NotificationCompat.Builder(context, CHANNEL_HANGOUT)
                .setContentTitle("Invite: ${message.title}")
                .setContentText("📍 ${message.locationName} · ${message.startDate}")
                .setStyle(NotificationCompat.BigTextStyle().bigText(details))
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(buildOpenHangoutIntent(context, message.hangoutId, notifId))
                .addAction(0, "Join", joinIntent)
                .setAutoCancel(true)
                .build()
        )
    }

    @SuppressLint("FullScreenIntentPolicy")
    private fun showZuypAlert(context: Context, message: NotificationMessage.ZuypAlert) {
        val manager     = context.getSystemService(NotificationManager::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, ZuypAlertActivity::class.java).apply {
                putExtra("hangoutId",    message.hangoutId)
                putExtra("title",        message.title)
                putExtra("locationName", message.locationName)
                putExtra("startDate",    message.startDate)
                putExtra("weather",      message.weather)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        manager.notify(
            ZUYP_ALERT_ID,
            NotificationCompat.Builder(context, CHANNEL_ZUYP_ALERT)
                .setContentTitle("⚠ ${message.title}")
                .setContentText("📍 ${message.locationName} · ${message.startDate}")
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(pendingIntent, true)
                .setAutoCancel(true)
                .build()
        )
    }

    private fun showHangoutJoined(context: Context, message: NotificationMessage.HangoutJoined) {
        val manager = context.getSystemService(NotificationManager::class.java)
        val notifId = (message.hangoutId + "_joined").hashCode()

        manager.notify(
            notifId,
            NotificationCompat.Builder(context, CHANNEL_HANGOUT)
                .setContentTitle("${message.username} joined ${message.hangoutName}")
                .setContentText("📍 ${message.location}")
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(buildOpenHangoutIntent(context, message.hangoutId, notifId))
                .setAutoCancel(true)
                .build()
        )
    }

    private fun buildOpenHangoutIntent(context: Context, hangoutId: String, notifId: Int): PendingIntent =
        PendingIntent.getActivity(
            context, notifId,
            Intent(context, MainActivity::class.java).apply {
                putExtra(NotificationWorker.EXTRA_HANGOUT_ID, hangoutId)
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    private suspend fun flashFlashlight(context: Context) {
        val cameraManager = context.getSystemService(CameraManager::class.java)
        val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return
        repeat(6) { i ->
            cameraManager.setTorchMode(cameraId, i % 2 == 0)
            delay(300)
        }
        cameraManager.setTorchMode(cameraId, false)
    }
}
