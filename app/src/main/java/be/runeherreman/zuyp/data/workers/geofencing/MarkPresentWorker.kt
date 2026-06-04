package be.runeherreman.zuyp.data.workers.geofencing

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import be.runeherreman.zuyp.data.fake.data.CurrentUser
import be.runeherreman.zuyp.domain.useCases.hangouts.MarkPresentUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.util.UUID

class MarkPresentWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface Dependencies {
        fun markPresentUseCase(): MarkPresentUseCase
        fun hydrationReminderScheduler(): HydrationReminderScheduler
    }

    override suspend fun doWork(): Result {
        val hangoutId = inputData.getString(KEY_HANGOUT_ID)
            ?.let(UUID::fromString) ?: return Result.failure()

        val deps = EntryPointAccessors.fromApplication(applicationContext, Dependencies::class.java)
        val markedPresent = deps.markPresentUseCase()(hangoutId, CurrentUser.id)
        if (markedPresent) deps.hydrationReminderScheduler().start(hangoutId)

        return Result.success()
    }

    companion object {
        const val KEY_HANGOUT_ID = "hangoutId"
    }
}
