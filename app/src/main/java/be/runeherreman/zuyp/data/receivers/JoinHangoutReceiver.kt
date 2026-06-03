package be.runeherreman.zuyp.data.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import be.runeherreman.zuyp.data.local.room.entity.hangouts.AttendanceStatus
import be.runeherreman.zuyp.domain.useCases.hangouts.UpdateAttendanceUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class JoinHangoutReceiver : BroadcastReceiver() {
    @Inject
    lateinit var updateAttendanceUseCase: UpdateAttendanceUseCase

    override fun onReceive(context: Context, intent: Intent) {
        val hangoutId = intent.getStringExtra("hangoutId") ?: return
        val userId = intent.getStringExtra("userId") ?: return
        val notificationId = intent.getIntExtra("notificationId", 0)

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                updateAttendanceUseCase(
                    hangoutId = UUID.fromString(hangoutId),
                    userId = UUID.fromString(userId),
                    attendaceStatus = AttendanceStatus.GOING,
                )
                context.getSystemService(NotificationManager::class.java)
                    .cancel(notificationId)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
