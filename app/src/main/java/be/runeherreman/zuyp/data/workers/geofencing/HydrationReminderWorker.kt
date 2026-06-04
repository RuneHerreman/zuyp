package be.runeherreman.zuyp.data.workers.geofencing

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import be.runeherreman.zuyp.data.workers.NotificationHelper
import be.runeherreman.zuyp.domain.useCases.hangouts.GetHangoutByIdUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.time.LocalDateTime

class HydrationReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface Dependencies {
        fun getHangoutByIdUseCase(): GetHangoutByIdUseCase
        fun hydrationReminderScheduler(): HydrationReminderScheduler
    }

    override suspend fun doWork(): Result {
        val hangoutId = inputData.getString(MarkPresentWorker.KEY_HANGOUT_ID)
            ?: return Result.failure()

        val deps = EntryPointAccessors.fromApplication(applicationContext, Dependencies::class.java)
        val hangout = deps.getHangoutByIdUseCase()(hangoutId) ?: return Result.success()

        if (hangout.endDate.isBefore(LocalDateTime.now())) {
            deps.hydrationReminderScheduler().stop(hangoutId)
            return Result.success()
        }

        NotificationHelper.showHydrationReminder(applicationContext, hangoutId)
        return Result.success()
    }
}
