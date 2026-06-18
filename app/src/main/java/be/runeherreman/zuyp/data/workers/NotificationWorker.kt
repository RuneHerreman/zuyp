package be.runeherreman.zuyp.data.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import be.runeherreman.zuyp.data.messaging.NotificationMessage

class NotificationWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val userId = inputData.getString(KEY_USER_ID) ?: return Result.failure()
        val raw    = inputData.getString(KEY_RAW_MESSAGE) ?: return Result.failure()

        val message = NotificationMessage.fromJson(raw)
        if (message != null) {
            NotificationHelper.handle(applicationContext, userId, message)
        } else {
            Log.w("NotificationWorker", "Unknown message: $raw")
        }

        return Result.success()
    }

    companion object {
        const val WORK_NAME        = "notification_worker"
        const val KEY_USER_ID      = "userId"
        const val KEY_RAW_MESSAGE  = "rawMessage"
        const val EXTRA_HANGOUT_ID = "hangoutId"
    }
}
