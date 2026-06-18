package be.runeherreman.zuyp.data.geofence

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import be.runeherreman.zuyp.data.workers.geofencing.GeofenceSyncWorker
import be.runeherreman.zuyp.domain.geofence.GeofenceSyncScheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeofenceSyncSchedulerImpl @Inject constructor(
    private val workManager: WorkManager,
) : GeofenceSyncScheduler {
    override fun schedulePeriodicSync() {
        workManager.enqueueUniquePeriodicWork(
            GeofenceSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<GeofenceSyncWorker>(15, TimeUnit.MINUTES).build(),
        )
    }
}
