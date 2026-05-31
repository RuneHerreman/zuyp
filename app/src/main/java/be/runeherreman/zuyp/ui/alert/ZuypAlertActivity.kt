package be.runeherreman.zuyp.ui.alert

import android.app.NotificationManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.runeherreman.zuyp.data.fake.data.FakeUsers
import be.runeherreman.zuyp.data.workers.NotificationWorker
import be.runeherreman.zuyp.ui.theme.ZuypTheme

private val Black       = Color(0xFF080808)
private val AlertRed    = Color(0xFFE8271A)
private val White       = Color(0xFFFFFFFF)
private val WhiteDim    = Color(0x99FFFFFF)
private val WhiteFaint  = Color(0x14FFFFFF)
private val WhiteBorder = Color(0x1FFFFFFF)

@AndroidEntryPoint
class ZuypAlertActivity : ComponentActivity() {

    private val viewModel: ZuypAlertViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        viewModel.loadFromIntent(
            hangoutId = intent.getStringExtra("hangoutId") ?: "",
            title = intent.getStringExtra("title") ?: "",
            locationName = intent.getStringExtra("locationName") ?: "",
            startDate = intent.getStringExtra("startDate") ?: "",
            weather = intent.getStringExtra("weather"),
        )

        setContent {
            ZuypTheme {
                val uiState by viewModel.uiState.collectAsState()
                ZuypAlertScreen(
                    title = uiState.title,
                    locationName = uiState.locationName,
                    startDate = uiState.startDate,
                    weather = uiState.weather,
                    onDismiss = {
                        getSystemService(NotificationManager::class.java)
                            .cancel(NotificationWorker.ZUYP_ALERT_ID)
                        finish()
                    },
                    onJoin = {
                        viewModel.join(FakeUsers.userKoen.id) {
                            getSystemService(NotificationManager::class.java)
                                .cancel(NotificationWorker.ZUYP_ALERT_ID)
                            finish()
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun ZuypAlertScreen(
    title: String,
    locationName: String,
    startDate: String,
    weather: String?,
    onDismiss: () -> Unit,
    onJoin: () -> Unit,
) {
    val pulse = rememberInfiniteTransition(label = "pulse")
    val dotAlpha by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "dotAlpha",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind { drawRect(Black) },
    ) {
        // Top red stripe
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .drawBehind { drawRect(AlertRed) },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 28.dp),
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Live alert indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .drawBehind { drawRect(AlertRed.copy(alpha = dotAlpha)) },
                )
                Spacer(modifier = Modifier.width(9.dp))
                Text(
                    text = "ZUYP ALERT",
                    color = AlertRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Hangout title — does the heavy lifting
            Text(
                text = title,
                color = White,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 46.sp,
                letterSpacing = (-1.5).sp,
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Hairline divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .drawBehind { drawRect(WhiteBorder) },
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Detail rows — label above value, editorial style
            DetailField(label = "LOCATION", value = locationName)
            Spacer(modifier = Modifier.height(20.dp))
            DetailField(label = "TIME", value = startDate)
            if (weather != null) {
                Spacer(modifier = Modifier.height(20.dp))
                WeatherField(weather = weather)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Join
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .drawBehind { drawRect(AlertRed) }
                    .clickable { onJoin() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "JOIN",
                    color = White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dismiss — ghost, understated
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .border(1.dp, WhiteBorder, RoundedCornerShape(6.dp))
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "DISMISS",
                    color = WhiteDim,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun WeatherField(weather: String) {
    val parts = weather.split(" • ")
    val temperature = parts.getOrNull(0) ?: weather
    val condition = parts.getOrNull(1)
    val styleTip = parts.getOrNull(2)

    Column {
        Text(
            text = "WEATHER",
            color = WhiteDim.copy(alpha = 0.4f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
        )
        Spacer(modifier = Modifier.height(3.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = temperature,
                color = White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.2).sp,
            )
            if (condition != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "·",
                    color = WhiteDim.copy(alpha = 0.3f),
                    fontSize = 17.sp,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = condition,
                    color = WhiteDim,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = (-0.2).sp,
                )
            }
        }
        if (styleTip != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .border(1.dp, WhiteBorder, RoundedCornerShape(4.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Text(
                    text = "👔  $styleTip",
                    color = WhiteDim,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun DetailField(label: String, value: String) {
    Column {
        Text(
            text = label,
            color = WhiteDim.copy(alpha = 0.4f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            color = White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = (-0.2).sp,
        )
    }
}
