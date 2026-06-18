package be.runeherreman.zuyp.ui.friends

import be.runeherreman.zuyp.domain.model.Group
import be.runeherreman.zuyp.domain.model.User

data class FriendsUiState(
    val user: User? = null,
    val friends: List<User> = emptyList(),
    val groups: List<Group> = emptyList(),
    val isLoading: Boolean = false,
    val dialog: FriendsDialog? = null   // replaces all 5 flags/payloads
)

// User info for pop up
data class UserProfile(
    val user: User,
    val friendsCount: Int,
    val groupsCount: Int,
    val eventsCount: Int,
    val mutualFriends: List<User>,
    val isFriend: Boolean
)

// Stands for all popup dialogs
sealed interface FriendsDialog {
    data object CreateGroup : FriendsDialog
    data class AddFriend(val candidates: List<User>) : FriendsDialog
    data class EditGroup(val group: Group) : FriendsDialog
    data class GroupMembers(val group: Group) : FriendsDialog
    data class UserProfileDialog(val profile: UserProfile) : FriendsDialog
}