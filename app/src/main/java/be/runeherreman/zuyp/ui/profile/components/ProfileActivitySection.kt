package be.runeherreman.zuyp.ui.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import be.runeherreman.zuyp.domain.model.Hangout

private val SIDE_PADDING = 16.dp

@Composable
fun ProfileActivitySection(
    title: String,
    icon: ImageVector,
    hangouts: List<Hangout>,
    emptyMessage: String,
    accentColor: Color,
    accentContainer: Color,
    onAccentContainer: Color,
    modifier: Modifier = Modifier,
    onHangoutClick: (Hangout) -> Unit = {}
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            title = title,
            icon = icon,
            count = hangouts.size,
            accentColor = accentColor,
            accentContainer = accentContainer,
            onAccentContainer = onAccentContainer
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (hangouts.isEmpty()) {
            EmptyActivityMessage(
                message = emptyMessage,
                modifier = Modifier.padding(horizontal = SIDE_PADDING)
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = SIDE_PADDING)
            ) {
                items(hangouts, key = { it.id }) { hangout ->
                    ActivityCard(
                        hangout = hangout,
                        accentColor = accentColor,
                        accentContainer = accentContainer,
                        onAccentContainer = onAccentContainer,
                        onClick = onHangoutClick
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: ImageVector,
    count: Int,
    accentColor: Color,
    accentContainer: Color,
    onAccentContainer: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SIDE_PADDING),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(accentContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = onAccentContainer,
                modifier = Modifier.size(18.dp)
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        if (count > 0) {
            Box(
                modifier = Modifier
                    .background(accentColor, CircleShape)
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.surface
                )
            }
        }
    }
}

@Composable
private fun EmptyActivityMessage(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceContainerLow,
                RoundedCornerShape(20.dp)
            )
            .padding(vertical = 28.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
