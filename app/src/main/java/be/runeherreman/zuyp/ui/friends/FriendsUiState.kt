package be.runeherreman.zuyp.ui.friends

import be.runeherreman.zuyp.domain.model.Group
import be.runeherreman.zuyp.domain.model.User

data class FriendsUiState(
    val user: User? = null,
    val friends: List<User> = emptyList(),
    val groups: List<Group> = emptyList(),
    val isLoading: Boolean = false,
    /** Everyone (minus the current user) available to add to a new group. */
    val availableUsers: List<User> = emptyList(),
    val isCreateGroupOpen: Boolean = false,
    val isAddFriendOpen: Boolean = false
)
