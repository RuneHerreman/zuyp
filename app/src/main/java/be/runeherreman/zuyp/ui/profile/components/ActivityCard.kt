package be.runeherreman.zuyp.ui.profile.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.runeherreman.zuyp.domain.model.Hangout
import java.time.format.DateTimeFormatter
import java.util.Locale

private val CARD_WIDTH = 232.dp
private val monthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.getDefault())

@Composable
fun ActivityCard(
    hangout: Hangout,
    accentColor: Color,
    accentContainer: Color,
    onAccentContainer: Color,
    modifier: Modifier = Modifier,
    onClick: (Hangout) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.96f else 1f, label = "cardScale")

    Card(
        onClick = { onClick(hangout) },
        interactionSource = interactionSource,
        modifier = modifier
            .width(CARD_WIDTH)
            .graphicsLayer { scaleX = scale; scaleY = scale },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DateMedallion(
                month = hangout.startDate.format(monthFormatter).uppercase(Locale.getDefault()),
                day = hangout.startDate.dayOfMonth.toString(),
                accentContainer = accentContainer,
                onAccentContainer = onAccentContainer
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = hangout.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = accentColor
                    )
                    Text(
                        text = hangout.locationName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = accentColor
            )
        }
    }
}

@Composable
private fun DateMedallion(
    month: String,
    day: String,
    accentContainer: Color,
    onAccentContainer: Color
) {
    // Trim the built-in font padding so the month sits snug above the day.
    val tight = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.Both
    )
    val noFontPadding = PlatformTextStyle(includeFontPadding = false)

    Box(
        modifier = Modifier
            .size(52.dp)
            .background(accentContainer, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            // Negative spacing eats the font's empty leading so the month sits
            // right on top of the day (no glyphs live in that gap, so nothing clips).
            verticalArrangement = Arrangement.spacedBy((-4).dp)
        ) {
            Text(
                text = month,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    lineHeight = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    platformStyle = noFontPadding,
                    lineHeightStyle = tight
                ),
                color = onAccentContainer.copy(alpha = 0.75f)
            )
            Text(
                text = day,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold,
                    platformStyle = noFontPadding,
                    lineHeightStyle = tight
                ),
                color = onAccentContainer
            )
        }
    }
}
