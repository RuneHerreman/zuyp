package be.runeherreman.zuyp.data.workers.geofencing

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HydrationReminderScheduler @Inject constructor(
    private val workManager: WorkManager,
) {
    fun start(hangoutId: UUID) {
        val request = PeriodicWorkRequestBuilder<HydrationReminderWorker>(1, TimeUnit.HOURS)
            .setInputData(workDataOf(HydrationReminderWorker.KEY_HANGOUT_ID to hangoutId.toString()))
            .build()

        workManager.enqueueUniquePeriodicWork(
            workName(hangoutId.toString()),
            ExistingPeriodicWorkPolicy.KEEP,  // don't restart the hourly clock if already running
            request,
        )
    }

    fun stop(hangoutId: String) = workManager.cancelUniqueWork(workName(hangoutId))
    fun stop(hangoutId: UUID)   = stop(hangoutId.toString())

    companion object {
        fun workName(hangoutId: String) = "hydration_$hangoutId"
    }
}
