package be.runeherreman.zuyp.data.workers.geofencing

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import be.runeherreman.zuyp.data.workers.NotificationHelper
import be.runeherreman.zuyp.domain.usecases.hangouts.GetHangoutByIdUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDateTime

@HiltWorker
class HydrationReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val getHangoutByIdUseCase: GetHangoutByIdUseCase,
    private val hydrationReminderScheduler: HydrationReminderScheduler,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val hangoutId = inputData.getString(KEY_HANGOUT_ID)
            ?: return Result.failure()

        val hangout = getHangoutByIdUseCase(hangoutId) ?: return Result.success()

        // auto-cancel when the hangout is over
        if (hangout.endDate.isBefore(LocalDateTime.now())) {
            hydrationReminderScheduler.stop(hangoutId)
            return Result.success()
        }

        NotificationHelper.showHydrationReminder(applicationContext, hangoutId)
        return Result.success()
    }

    companion object {
        const val KEY_HANGOUT_ID = "hangoutId"
    }
}
