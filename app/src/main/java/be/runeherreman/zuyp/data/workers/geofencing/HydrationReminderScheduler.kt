package be.runeherreman.zuyp.data.workers.geofencing

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.UUID
import java.util.concurrent.TimeUnit
import be.runeherreman.zuyp.domain.geofence.HydrationScheduler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HydrationReminderScheduler @Inject constructor(
    private val workManager: WorkManager,
) : HydrationScheduler {
    override fun start(hangoutId: UUID) {
        val request = PeriodicWorkRequestBuilder<HydrationReminderWorker>(1, TimeUnit.HOURS)
            .setInputData(workDataOf(HydrationReminderWorker.KEY_HANGOUT_ID to hangoutId.toString()))
            .build()

        workManager.enqueueUniquePeriodicWork(
            workName(hangoutId.toString()),
            ExistingPeriodicWorkPolicy.KEEP,  // don't restart the hourly clock if already running
            request,
        )
    }

    override fun stop(hangoutId: UUID) { workManager.cancelUniqueWork(workName(hangoutId.toString())) }

    companion object {
        fun workName(hangoutId: String) = "hydration_$hangoutId"
    }
}
