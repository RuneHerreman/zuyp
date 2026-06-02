package be.runeherreman.zuyp.ui.friends.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.ui.friends.UserProfile
import java.time.LocalDate
import java.time.Period

/**
 * Popup showing a user's profile: avatar, name, age, their friend/group/event
 * counts, the friends you share, and an add/remove friend action.
 */
@Composable
fun UserProfileDialog(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onAddFriend: (User) -> Unit,
    onRemoveFriend: (User) -> Unit
) {
    BackHandler(onBack = onDismiss)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Box {
                DismissButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 28.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Identity(profile.user)

                    StatsCard(
                        friendsCount = profile.friendsCount,
                        groupsCount = profile.groupsCount,
                        eventsCount = profile.eventsCount
                    )

                    if (profile.mutualFriends.isNotEmpty()) {
                        MutualFriendsRow(profile.mutualFriends)
                    }

                    FriendActionButton(
                        profile = profile,
                        onAddFriend = onAddFriend,
                        onRemoveFriend = onRemoveFriend
                    )
                }
            }
        }
    }
}

@Composable
private fun DismissButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = modifier.size(36.dp),
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun Identity(user: User) {
    val age = remember(user.birthdate) { ageFrom(user.birthdate) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Avatar with a gradient ring for a bit of pop.
        Box(
            modifier = Modifier
                .size(112.dp)
                .padding(5.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            UserAvatar(user = user, size = 102.dp)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            if (age != null) {
                Text(
                    text = "$age years old",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatsCard(friendsCount: Int, groupsCount: Int, eventsCount: Int) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(value = friendsCount, label = "Friends", modifier = Modifier.weight(1f))
            StatDivider()
            StatItem(value = groupsCount, label = "Groups", modifier = Modifier.weight(1f))
            StatDivider()
            StatItem(value = eventsCount, label = "Events", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatItem(value: Int, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .height(32.dp)
            .width(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    )
}

@Composable
private fun MutualFriendsRow(mutualFriends: List<User>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        MemberAvatarCluster(members = mutualFriends, avatarSize = 26.dp, maxVisible = 3)
        Spacer(Modifier.width(10.dp))
        Text(
            text = mutualFriendsLabel(mutualFriends),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FriendActionButton(
    profile: UserProfile,
    onAddFriend: (User) -> Unit,
    onRemoveFriend: (User) -> Unit
) {
    if (profile.isFriend) {
        Button(
            onClick = { onRemoveFriend(profile.user) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Icon(Icons.Default.PersonRemove, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Remove friend")
        }
    } else {
        Button(
            onClick = { onAddFriend(profile.user) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Add friend")
        }
    }
}

/** "also friends with A and B" — caps the list and rolls the rest into "N others". */
private fun mutualFriendsLabel(mutualFriends: List<User>): String {
    val names = mutualFriends.map { it.name }
    val joined = when {
        names.size == 1 -> names[0]
        names.size == 2 -> "${names[0]} and ${names[1]}"
        names.size == 3 -> "${names[0]}, ${names[1]} and ${names[2]}"
        else -> "${names[0]}, ${names[1]} and ${names.size - 2} others"
    }
    return "also friends with $joined"
}

private fun ageFrom(birthdate: LocalDate): Int? {
    val years = Period.between(birthdate, LocalDate.now()).years
    return years.takeIf { it >= 0 }
}
