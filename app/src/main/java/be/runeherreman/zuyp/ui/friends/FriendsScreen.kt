package be.runeherreman.zuyp.ui.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import be.runeherreman.zuyp.domain.model.Group
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.ui.friends.components.AddFriendDialog
import be.runeherreman.zuyp.ui.friends.components.CreateGroupDialog
import be.runeherreman.zuyp.ui.friends.components.FriendRow
import be.runeherreman.zuyp.ui.friends.components.GroupCard
import be.runeherreman.zuyp.ui.friends.components.SectionHeader

@Composable
fun FriendsScreen(
    uiState: FriendsUiState,
    modifier: Modifier = Modifier,
    onCreateGroupOpen: () -> Unit = {},
    onCreateGroupClose: () -> Unit = {},
    onCreateGroup: (String, List<User>) -> Unit = { _, _ -> },
    onDeleteGroup: (Group) -> Unit = {},
    onAddFriendOpen: () -> Unit = {},
    onAddFriendClose: () -> Unit = {},
    onSendFriendRequest: (String) -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Friends &\ngroups",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp)
            )

            if (uiState.isLoading) {
                LoadingState()
            } else {
                GroupsSection(
                    groups = uiState.groups,
                    onCreateGroup = onCreateGroupOpen,
                    onDeleteGroup = onDeleteGroup
                )

                FriendsSection(
                    friends = uiState.friends,
                    onAddFriend = onAddFriendOpen
                )

                // Breathing room above the bottom navigation bar.
                androidx.compose.foundation.layout.Spacer(Modifier.padding(bottom = 8.dp))
            }
        }
    }

    if (uiState.isCreateGroupOpen) {
        CreateGroupDialog(
            availableUsers = uiState.availableUsers,
            onDismiss = onCreateGroupClose,
            onCreate = onCreateGroup
        )
    }

    if (uiState.isAddFriendOpen) {
        AddFriendDialog(
            onDismiss = onAddFriendClose,
            onSendRequest = onSendFriendRequest
        )
    }
}

@Composable
private fun GroupsSection(
    groups: List<Group>,
    onCreateGroup: () -> Unit,
    onDeleteGroup: (Group) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(
            title = "Groups",
            leadingIcon = Icons.Default.Group,
            actionLabel = "Create group",
            actionIcon = Icons.Default.Add,
            onActionClick = onCreateGroup
        )
        if (groups.isEmpty()) {
            EmptyHint("No groups yet. Create one to plan together.")
        } else {
            groups.forEach { group ->
                GroupCard(group = group, onDelete = onDeleteGroup)
            }
        }
    }
}

@Composable
private fun FriendsSection(
    friends: List<User>,
    onAddFriend: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(
            title = "All friends",
            leadingIcon = Icons.Default.Person,
            actionLabel = "Add friend",
            actionIcon = Icons.Default.PersonAdd,
            onActionClick = onAddFriend
        )
        if (friends.isEmpty()) {
            EmptyHint("No friends yet. Add someone to get started.")
        } else {
            friends.forEach { friend ->
                FriendRow(friend = friend)
            }
        }
    }
}

@Composable
private fun EmptyHint(message: String) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize().padding(top = 48.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}
