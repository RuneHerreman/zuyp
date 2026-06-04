package be.runeherreman.zuyp

import android.app.Application
import android.content.Intent
import be.runeherreman.zuyp.data.local.room.database.DatabaseSeeder
import be.runeherreman.zuyp.data.services.MessagingService
import be.runeherreman.zuyp.data.workers.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltAndroidApp
class ZuypApplication : Application() {

    @Inject lateinit var databaseSeeder: DatabaseSeeder

    override fun onCreate() {
        super.onCreate()
        runBlocking { databaseSeeder.seedIfNeeded() }
        NotificationHelper.createNotificationChannels(this)
        startForegroundService(Intent(this, MessagingService::class.java))
    }
}
