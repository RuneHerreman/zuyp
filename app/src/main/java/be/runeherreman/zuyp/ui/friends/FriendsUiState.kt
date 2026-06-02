package be.runeherreman.zuyp.ui.friends

import be.runeherreman.zuyp.domain.model.Group
import be.runeherreman.zuyp.domain.model.User

data class FriendsUiState(
    val user: User? = null,
    val friends: List<User> = emptyList(),
    val groups: List<Group> = emptyList(),
    val isLoading: Boolean = false,
    /** Users who can still be added as a friend (everyone minus self and current friends). */
    val addFriendCandidates: List<User> = emptyList(),
    val isCreateGroupOpen: Boolean = false,
    val isAddFriendOpen: Boolean = false,
    /** The group currently being edited, or null when the edit dialog is closed. */
    val editingGroup: Group? = null,
    /** The group whose members are being shown, or null when that popup is closed. */
    val viewingGroup: Group? = null,
    /** The profile being shown in the user info popup, or null when it's closed. */
    val viewingProfile: UserProfile? = null
)

/** Aggregated info about a user, shown in the profile popup. */
data class UserProfile(
    val user: User,
    val friendsCount: Int,
    val groupsCount: Int,
    val eventsCount: Int,
    val mutualFriends: List<User>,
    val isFriend: Boolean
)
