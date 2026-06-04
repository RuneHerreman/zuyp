package be.runeherreman.zuyp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import be.runeherreman.zuyp.data.workers.NotificationWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var pendingHangoutId by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        pendingHangoutId = extractHangoutId(intent)
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

    private fun extractHangoutId(intent: Intent): String? {
        intent.getStringExtra(NotificationWorker.EXTRA_HANGOUT_ID)?.let { return it }
        if (intent.action == Intent.ACTION_VIEW) {
            return intent.data?.lastPathSegment
        }
        return null
    }
}
