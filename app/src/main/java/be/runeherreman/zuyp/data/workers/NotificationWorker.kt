package be.runeherreman.zuyp.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class NotificationWorker(
    context: Context,
    params: WorkerParameters
): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {

    }
}