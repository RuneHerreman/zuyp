package be.runeherreman.zuyp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import be.runeherreman.zuyp.data.local.room.database.DatabaseSeeder
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
    }
}
