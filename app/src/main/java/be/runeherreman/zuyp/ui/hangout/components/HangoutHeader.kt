package be.runeherreman.zuyp.ui.hangout.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.ui.hangout.HangoutUiState
import be.runeherreman.zuyp.ui.components.InfoRow
import java.time.format.DateTimeFormatter

@Composable
fun HangoutHeader(
    hangout: Hangout,
    uiState: HangoutUiState,
    onLocationClick: (Hangout) -> Unit = { _ -> }
) {
    Text(
        text = hangout.title,
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.primary,
        lineHeight = 1.1.em
    )

    Spacer(modifier = Modifier.height(8.dp))

    val dateFormatter = DateTimeFormatter.ofPattern("MMM d yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH'h'mm")

    val isOneDay = hangout.startDate.toLocalDate() == hangout.endDate.toLocalDate()
    InfoRow(
        icon = Icons.Default.CalendarToday,
        text =
            when {
                isOneDay -> "${hangout.startDate.format(dateFormatter)} • ${hangout.startDate.format(timeFormatter)} - ${hangout.endDate.format(timeFormatter)}"
                else -> "${hangout.startDate.format(dateFormatter)} ${hangout.startDate.format(timeFormatter)} - ${hangout.endDate.format(dateFormatter)} ${hangout.endDate.format(timeFormatter)}"
            }
    )
    Spacer(modifier = Modifier.height(4.dp))
    InfoRow(
        icon = Icons.Default.LocationOn,
        text = hangout.locationName,
        onClick = { onLocationClick(hangout) }
    )
    Spacer(modifier = Modifier.height(4.dp))
    if (uiState.isLoadingWeather) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Loading weather...", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        }
    } else {
        InfoRow(
            icon = uiState.weatherIcon,
            text = uiState.weatherPrediction
        )
    }
}


@Composable
fun PrivateBadge() {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "Private",
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
}
