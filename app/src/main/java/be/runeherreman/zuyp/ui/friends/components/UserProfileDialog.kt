package be.runeherreman.zuyp.ui.friends.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                IdentityRow(profile.user)

                StatsRow(
                    friendsCount = profile.friendsCount,
                    groupsCount = profile.groupsCount,
                    eventsCount = profile.eventsCount
                )

                if (profile.mutualFriends.isNotEmpty()) {
                    MutualFriendsRow(profile.mutualFriends)
                }

                ProfileActions(
                    profile = profile,
                    onAddFriend = onAddFriend,
                    onRemoveFriend = onRemoveFriend,
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
private fun IdentityRow(user: User) {
    val age = remember(user.birthdate) { ageFrom(user.birthdate) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        UserAvatar(user = user, size = 96.dp)
        Spacer(Modifier.width(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (age != null) {
                Text(
                    text = "$age years old",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatsRow(friendsCount: Int, groupsCount: Int, eventsCount: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard(value = friendsCount, label = "Friends", modifier = Modifier.weight(1f))
        StatCard(value = groupsCount, label = "Groups", modifier = Modifier.weight(1f))
        StatCard(value = eventsCount, label = "Events", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(value: Int, label: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.border(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant,
            RoundedCornerShape(16.dp)
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineSmall,
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
}

@Composable
private fun MutualFriendsRow(mutualFriends: List<User>) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        MemberAvatarCluster(members = mutualFriends, avatarSize = 28.dp, maxVisible = 3)
        Spacer(Modifier.width(12.dp))
        Text(
            text = mutualFriendsLabel(mutualFriends),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ProfileActions(
    profile: UserProfile,
    onAddFriend: (User) -> Unit,
    onRemoveFriend: (User) -> Unit,
    onDismiss: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (profile.isFriend) {
            Button(
                onClick = { onRemoveFriend(profile.user) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(Icons.Default.PersonRemove, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Remove friend")
            }
        } else {
            Button(
                onClick = { onAddFriend(profile.user) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Add friend")
            }
        }
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Close")
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
