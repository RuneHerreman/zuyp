package be.runeherreman.zuyp.data.workers.geofencing

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import be.runeherreman.zuyp.data.fake.data.CurrentUser
import be.runeherreman.zuyp.domain.useCases.hangouts.MarkPresentUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.UUID

@HiltWorker
class MarkPresentWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val markPresentUseCase: MarkPresentUseCase,
    private val hydrationReminderScheduler: HydrationReminderScheduler,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val hangoutId = inputData.getString(KEY_HANGOUT_ID)
            ?.let(UUID::fromString) ?: return Result.failure()

        val markedPresent = markPresentUseCase(hangoutId, CurrentUser.id)
        Log.d(TAG, "hangout=$hangoutId markedPresent=$markedPresent")

        if (markedPresent) hydrationReminderScheduler.start(hangoutId)

        return Result.success()
    }

    companion object {
        const val KEY_HANGOUT_ID = "hangoutId"
        private const val TAG    = "MarkPresentWorker"
    }
}
