package be.runeherreman.zuyp.data.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import be.runeherreman.zuyp.MainActivity
import be.runeherreman.zuyp.data.messaging.MessageConsumer
import be.runeherreman.zuyp.data.messaging.NotificationMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val messageConsumer: MessageConsumer,
) : CoroutineWorker(context, params) {

    private val notificationManager by lazy {
        applicationContext.getSystemService(NotificationManager::class.java)
    }

    override suspend fun doWork(): Result {
        val userId = inputData.getString(KEY_USER_ID) ?: return Result.failure()

        createNotificationChannels()
        // =========================================
        // Add reaction to nofication types here
        // =========================================
        messageConsumer.onMessageReceived = { raw ->
            when (val message = NotificationMessage.fromJson(raw)) {
                is NotificationMessage.HangoutInvite -> showHangoutInviteNotification(message.hangoutId)
                is NotificationMessage.ZuypAlert -> {
                    showZuypAlertNotification(message.hangoutId)
                    CoroutineScope(Dispatchers.IO).launch { flashFlashlight() }
                }
                null -> Log.w("Messagebroker", "Unknown message: $raw")
            }
        }
        messageConsumer.startConsuming(userId)

        while (!isStopped) delay(5_000)

        messageConsumer.stopConsuming()
        return Result.success()
    }

    private fun showHangoutInviteNotification(hangoutId: String) {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_HANGOUT)
            .setContentTitle("New hangout invite")
            .setContentText("You've been invited to a hangout!")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notificationManager.notify(hangoutId.hashCode(), notification)
    }

    private fun showZuypAlertNotification(hangoutId: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("hangoutId", hangoutId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ZUYP_ALERT)
            .setContentTitle("Zuyp Alert!")
            .setContentText("Urgent alert!")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(pendingIntent, true)
            .build()
        notificationManager.notify(ZUYP_ALERT_ID, notification)
    }

    private suspend fun flashFlashlight() {
        val cameraManager = applicationContext.getSystemService(CameraManager::class.java)
        val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return
        repeat(6) { i ->
            cameraManager.setTorchMode(cameraId, i % 2 == 0)
            delay(300)
        }
        cameraManager.setTorchMode(cameraId, false)
    }

    private fun createNotificationChannels() {
        val hangoutChannel = NotificationChannel(
            CHANNEL_HANGOUT,
            "Hangout Invites",
            NotificationManager.IMPORTANCE_DEFAULT,
        )

        val alertSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val alertChannel = NotificationChannel(
            CHANNEL_ZUYP_ALERT,
            "Zuyp Alerts",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            setSound(
                alertSoundUri,
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build(),
            )
        }

        notificationManager.createNotificationChannel(hangoutChannel)
        notificationManager.createNotificationChannel(alertChannel)
    }

    companion object {
        const val WORK_NAME = "notification_worker"
        const val KEY_USER_ID = "userId"
        const val CHANNEL_HANGOUT = "zuyp_hangout_invites"
        const val CHANNEL_ZUYP_ALERT = "zuyp_alerts"
        const val ZUYP_ALERT_ID = 1
    }
}
