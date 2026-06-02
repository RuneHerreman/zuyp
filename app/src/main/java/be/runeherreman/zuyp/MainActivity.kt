package be.runeherreman.zuyp

import android.Manifest
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
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import be.runeherreman.zuyp.data.fake.data.CurrentUser
import be.runeherreman.zuyp.data.workers.NotificationWorker
import be.runeherreman.zuyp.domain.useCases.users.SetUserLocationPreferenceUseCase
import be.runeherreman.zuyp.domain.useCases.users.SetUserNotificationPreferenceUseCase
import be.runeherreman.zuyp.ui.permissions.AppPermission
import be.runeherreman.zuyp.ui.permissions.toAndroidPermissions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor(
    private val setNotificationPreference: SetUserNotificationPreferenceUseCase,
    private val setLocationPreference: SetUserLocationPreferenceUseCase
) : ComponentActivity() {

    private var pendingHangoutId by mutableStateOf<String?>(null)

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        requestFullScreenIntentPermission()
        persistPermissionResult(result)
        startNotificationWorker()
    }

    private fun persistPermissionResult(result: Map<String, Boolean>) {
        val notificationsGranted = result[Manifest.permission.POST_NOTIFICATIONS] == true
        val locationGranted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                result[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        lifecycleScope.launch {
            setNotificationPreference(notificationsGranted)
            setLocationPreference(locationGranted)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        pendingHangoutId = extractHangoutId(intent)
        val permissions = (
                AppPermission.NOTIFICATION.toAndroidPermissions()
                        +
                AppPermission.LOCATION.toAndroidPermissions()).toTypedArray()
        requestPermissionsLauncher.launch(permissions)
        setContent {
            ZuypApp(
                initialHangoutId = pendingHangoutId,
                onHangoutConsumed = { pendingHangoutId = null }
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingHangoutId = extractHangoutId(intent)
    }

    /**
     * Resolves the hangout id from either a notification tap
     * ([NotificationWorker.EXTRA_HANGOUT_ID]) or a shared deep link
     * (https://zuyp.app/hangout/<id> or zuyp://hangout/<id>).
     */
    private fun extractHangoutId(intent: Intent): String? {
        intent.getStringExtra(NotificationWorker.EXTRA_HANGOUT_ID)?.let { return it }
        if (intent.action == Intent.ACTION_VIEW) {
            return intent.data?.lastPathSegment
        }
        return null
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
            .setInputData(workDataOf(NotificationWorker.KEY_USER_ID to CurrentUser.id.toString()))
            .build()
        WorkManager.getInstance(this).enqueueUniqueWork(
            NotificationWorker.WORK_NAME,
            androidx.work.ExistingWorkPolicy.REPLACE,
            request,
        )
    }
}
