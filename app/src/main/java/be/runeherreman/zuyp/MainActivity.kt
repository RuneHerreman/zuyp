package be.runeherreman.zuyp

import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import be.runeherreman.zuyp.data.fake.data.FakeUsers
import be.runeherreman.zuyp.data.workers.NotificationWorker
import be.runeherreman.zuyp.ui.permissions.AppPermission
import be.runeherreman.zuyp.ui.permissions.toAndroidPermissions
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.net.toUri

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var pendingHangoutId by mutableStateOf<String?>(null)

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        requestFullScreenIntentPermission()
        startNotificationWorker()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        pendingHangoutId = intent.getStringExtra(NotificationWorker.EXTRA_HANGOUT_ID)
        val permissions = (
                AppPermission.NOTIFICATION.toAndroidPermissions()
                        +
                AppPermission.LOCATION.toAndroidPermissions()).toTypedArray()
        requestPermissionsLauncher.launch(permissions)
        setContent {
            ZuypApp(initialHangoutId = pendingHangoutId)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingHangoutId = intent.getStringExtra(NotificationWorker.EXTRA_HANGOUT_ID)
    }

    private fun requestFullScreenIntentPermission() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        if (!notificationManager.canUseFullScreenIntent()) {
            val intent = Intent(
                Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT,
                "package:$packageName".toUri(),
            )
            startActivity(intent)
        }
    }

    private fun startNotificationWorker() {
        val request = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(workDataOf(NotificationWorker.KEY_USER_ID to FakeUsers.userKoen.id.toString()))
            .build()
        WorkManager.getInstance(this).enqueueUniqueWork(
            NotificationWorker.WORK_NAME,
            androidx.work.ExistingWorkPolicy.REPLACE,
            request,
        )
    }
}
