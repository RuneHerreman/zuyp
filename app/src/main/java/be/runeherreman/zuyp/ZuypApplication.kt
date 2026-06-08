package be.runeherreman.zuyp

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import be.runeherreman.zuyp.data.local.room.database.DatabaseSeeder
import be.runeherreman.zuyp.data.services.MessagingService
import be.runeherreman.zuyp.data.workers.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltAndroidApp
class ZuypApplication : Application(), Configuration.Provider {

    @Inject lateinit var databaseSeeder: DatabaseSeeder
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        runBlocking {
            databaseSeeder.seedIfNeeded()
        }
        NotificationHelper.createNotificationChannels(this)
        try {
            startForegroundService(Intent(this, MessagingService::class.java))
        } catch (e: IllegalStateException) {
            Log.w(TAG, "MessagingService not started: app launched from background (${e.message})")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start MessagingService", e)
        }
    }

    companion object {
        private const val TAG = "ZuypApplication"
    }
}
