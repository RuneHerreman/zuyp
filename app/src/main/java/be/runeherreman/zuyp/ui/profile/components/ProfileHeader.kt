package be.runeherreman.zuyp.ui.profile.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import be.runeherreman.zuyp.domain.model.User
import coil.compose.AsyncImage

/**
 * Bold gradient "social passport" header: oversized tap-to-edit avatar, the
 * name in the serif display font, and playful translucent stat pills.
 */
@Composable
fun ProfileHeader(
    user: User?,
    friendsCount: Int,
    groupsCount: Int,
    eventsCount: Int,
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onFriendsClick: () -> Unit = {},
    onGroupsClick: () -> Unit = {},
    onEventsClick: () -> Unit = {}
) {
    val scheme = MaterialTheme.colorScheme

    // A tonal gradient of the single brand color reads more intentional than
    // primary->tertiary (which are nearly the same indigo): light at the top,
    // deeper toward the bottom for a soft sense of depth.
    val heroBrush = Brush.linearGradient(
        listOf(
            lerp(scheme.primary, Color.White, 0.12f),
            scheme.primary,
            lerp(scheme.primary, Color.Black, 0.28f)
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(heroBrush)
    ) {
        // Playful background blobs for depth.
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .offset(x = 70.dp, y = (-70).dp)
                .clip(CircleShape)
                .background(scheme.onPrimary.copy(alpha = 0.08f))
        )
        Box(
            modifier = Modifier
                .size(130.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-45).dp, y = 45.dp)
                .clip(CircleShape)
                .background(scheme.onPrimary.copy(alpha = 0.06f))
        )

        // Settings lives in the top-right corner as a consistent action.
        CircleIconButton(
            icon = Icons.Default.Settings,
            contentDescription = "Open settings",
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
        )

        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                EditableAvatar(user = user, onEditClick = onEditClick)

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f).padding(end = 36.dp)) {
                    Text(
                        text = user?.name ?: "—",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = scheme.onPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!user?.email.isNullOrBlank()) {
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = scheme.onPrimary.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            StatPills(
                friendsCount = friendsCount,
                groupsCount = groupsCount,
                eventsCount = eventsCount,
                onFriendsClick = onFriendsClick,
                onGroupsClick = onGroupsClick,
                onEventsClick = onEventsClick
            )
        }
    }
}

@Composable
private fun EditableAvatar(user: User?, onEditClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val initials = remember(user?.name) {
        user?.name
            ?.trim()
            ?.split(" ")
            ?.filter { it.isNotBlank() }
            ?.take(2)
            ?.joinToString("") { it.first().uppercaseChar().toString() }
            ?.ifBlank { "?" }
            ?: "?"
    }

    Box {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(scheme.onPrimary.copy(alpha = 0.25f))
                .padding(3.dp)
                .clip(CircleShape)
                .background(scheme.primaryContainer)
                .clickable(onClick = onEditClick),
            contentAlignment = Alignment.Center
        ) {
            if (!user?.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = user.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )
            } else {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = scheme.onPrimaryContainer
                )
            }
        }

        // Little pencil badge — tap the avatar to edit your profile.
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(28.dp)
                .clip(CircleShape)
                .background(scheme.onPrimary)
                .clickable(onClick = onEditClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit profile",
                tint = scheme.primary,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

@Composable
private fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(scheme.onPrimary.copy(alpha = 0.15f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = scheme.onPrimary
        )
    }
}

@Composable
private fun StatPills(
    friendsCount: Int,
    groupsCount: Int,
    eventsCount: Int,
    onFriendsClick: () -> Unit,
    onGroupsClick: () -> Unit,
    onEventsClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(scheme.onPrimary.copy(alpha = 0.12f))
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatPill(Icons.Default.Group, friendsCount, "Friends", onFriendsClick, Modifier.weight(1f))
        PillDivider()
        StatPill(Icons.Default.Groups, groupsCount, "Groups", onGroupsClick, Modifier.weight(1f))
        PillDivider()
        StatPill(Icons.Default.LocalBar, eventsCount, "Events", onEventsClick, Modifier.weight(1f))
    }
}

@Composable
private fun StatPill(
    icon: ImageVector,
    value: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = scheme.onPrimary.copy(alpha = 0.85f),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = scheme.onPrimary
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = scheme.onPrimary.copy(alpha = 0.75f)
        )
    }
}

@Composable
private fun PillDivider() {
    Box(
        modifier = Modifier
            .height(34.dp)
            .width(1.dp)
            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
    )
}
