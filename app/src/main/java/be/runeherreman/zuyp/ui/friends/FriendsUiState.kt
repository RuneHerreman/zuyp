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
    val editingGroup: Group? = null
)
