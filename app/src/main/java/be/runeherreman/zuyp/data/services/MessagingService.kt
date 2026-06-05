package be.runeherreman.zuyp.data.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import be.runeherreman.zuyp.R
import be.runeherreman.zuyp.data.fake.data.CurrentUser
import be.runeherreman.zuyp.data.geofence.GeofenceSyncCoordinator
import be.runeherreman.zuyp.data.messaging.MessageConsumer
import be.runeherreman.zuyp.data.messaging.NotificationMessage
import be.runeherreman.zuyp.data.workers.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MessagingService : Service() {

    @Inject lateinit var messageConsumer: MessageConsumer
    @Inject lateinit var geofenceSyncCoordinator: GeofenceSyncCoordinator

    private val job   = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, buildForegroundNotification())

        // Start message broker consumer
        val userId = CurrentUser.id.toString()
        messageConsumer.onMessageReceived = { raw ->
            scope.launch {
                val message = NotificationMessage.fromJson(raw)
                if (message != null) {
                    NotificationHelper.handle(this@MessagingService, userId, message)
                } else {
                    Log.w(TAG, "Unknown message: $raw")
                }
            }
        }
        messageConsumer.startConsuming(userId)

        // Start geofence coordinator
        geofenceSyncCoordinator.start(scope)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onDestroy() {
        messageConsumer.stopConsuming()
        job.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildForegroundNotification(): Notification {
        getSystemService(NotificationManager::class.java).createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Zuyp Service", NotificationManager.IMPORTANCE_LOW)
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Zuyp")
            .setContentText("Listening for hangout alerts")
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setOngoing(true)
            .setSilent(true)
            .setTimeoutAfter(5_000L)
            .build()
    }

    companion object {
        private const val NOTIFICATION_ID = 9999
        private const val CHANNEL_ID      = "zuyp_service"
        private const val TAG             = "MessagingService"
    }
}
