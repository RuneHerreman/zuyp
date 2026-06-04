package be.runeherreman.zuyp

import android.app.Application
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import be.runeherreman.zuyp.data.fake.data.CurrentUser
import be.runeherreman.zuyp.data.local.room.database.DatabaseSeeder
import be.runeherreman.zuyp.data.messaging.MessageConsumer
import be.runeherreman.zuyp.data.workers.NotificationHelper
import be.runeherreman.zuyp.data.workers.NotificationWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltAndroidApp
class ZuypApplication : Application() {

    @Inject lateinit var databaseSeeder: DatabaseSeeder
    @Inject lateinit var messageConsumer: MessageConsumer

    override fun onCreate() {
        super.onCreate()
        runBlocking { databaseSeeder.seedIfNeeded() }

        NotificationHelper.createNotificationChannels(this)

        messageConsumer.onMessageReceived = { raw ->
            WorkManager.getInstance(this).enqueue(
                OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInputData(workDataOf(
                        NotificationWorker.KEY_USER_ID     to CurrentUser.id.toString(),
                        NotificationWorker.KEY_RAW_MESSAGE to raw
                    ))
                    .build()
            )
        }
        messageConsumer.startConsuming(CurrentUser.id.toString())
    }
}
