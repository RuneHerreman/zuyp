package be.runeherreman.zuyp

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import be.runeherreman.zuyp.data.fake.data.FakeUsers
import be.runeherreman.zuyp.data.workers.NotificationWorker
import be.runeherreman.zuyp.ui.permissions.AppPermission
import be.runeherreman.zuyp.ui.permissions.toAndroidPermission
import be.runeherreman.zuyp.ui.theme.ZuypTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        requestFullScreenIntentPermission()
        startNotificationWorker()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestPermissionLauncher.launch(AppPermission.NOTIFICATION.toAndroidPermission())
        setContent {
            ZuypApp()
        }
    }

    private fun requestFullScreenIntentPermission() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        if (!notificationManager.canUseFullScreenIntent()) {
            val intent = Intent(
                Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT,
                Uri.parse("package:$packageName"),
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