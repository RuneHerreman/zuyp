package be.runeherreman.zuyp.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import be.runeherreman.zuyp.ui.theme.errorContainerLight
import be.runeherreman.zuyp.ui.theme.onErrorContainerLight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.data.local.room.entity.AttendanceStatus
import coil.compose.AsyncImage
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HangoutCard(
    hangout: Hangout,
    phrases: List<String>,
    friendAttendees: List<User> = emptyList(),
    modifier: Modifier = Modifier,
    onLocationClick: (Hangout) -> Unit = { _ -> },
    onClick: (Hangout) -> Unit = {}
) {
    val formattedDate =
        hangout.startDate.format(DateTimeFormatter.ofPattern("MMM d yyyy", Locale.getDefault()))

    val goingAttendees = remember(hangout.attendees) {
        hangout.attendees.filter { it.attendanceStatus == AttendanceStatus.GOING }
    }

    val attendeeText = remember(goingAttendees) {
        if (goingAttendees.isEmpty()) {
            phrases.random()
        } else {
            "${goingAttendees.size} going"
        }
    }

    Card(
        onClick = { onClick(hangout) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Title + visibility badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = hangout.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (hangout.private) {
                    PrivateBadge(modifier = Modifier.padding(start = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date and location grouped
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                InfoRow(icon = Icons.Filled.CalendarToday, text = formattedDate)
                InfoRow(
                    icon = Icons.Filled.LocationOn,
                    text = hangout.locationName,
                    onClick = { onLocationClick(hangout) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Attendance count + stacked avatars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoRow(icon = Icons.Filled.Group, text = attendeeText)
                if (friendAttendees.isNotEmpty()) {
                    StackedAvatars(attendees = friendAttendees)
                }
            }
        }
    }
}

@Composable
private fun PrivateBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(errorContainerLight),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Private",
            style = MaterialTheme.typography.labelSmall,
            color = onErrorContainerLight,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun StackedAvatars(
    attendees: List<User>,
    modifier: Modifier = Modifier
) {
    val display = attendees.take(3)
    val avatarSize = 28.dp
    val overlap = 10.dp
    val extraCount = (display.size - 1).coerceAtLeast(0).toFloat()
    val totalWidth = avatarSize + (avatarSize - overlap) * extraCount

    Box(modifier = modifier.size(width = totalWidth, height = avatarSize)) {
        display.forEachIndexed { index, user ->
            val initials = remember(user.name) {
                user.name
                    .trim()
                    .split(" ")
                    .filter { it.isNotBlank() }
                    .take(2)
                    .joinToString("") { it.first().uppercaseChar().toString() }
                    .ifBlank { "?" }
            }
            Box(
                modifier = Modifier
                    .offset(x = (avatarSize - overlap) * index.toFloat())
                    .size(avatarSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (user.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = user.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)
                            .fillMaxSize()
                    )
                } else {
                    Text(
                        text = initials,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    text: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = if (onClick != null) {
            Modifier.clickable(onClick = onClick)
        } else {
            Modifier
        },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
