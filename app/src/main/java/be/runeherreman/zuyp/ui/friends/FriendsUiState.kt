package be.runeherreman.zuyp.ui.friends

import be.runeherreman.zuyp.domain.model.User

data class FriendsUiState (
    val user: User? = null,
    val friends: List<User> = emptyList(),
    val isLoading: Boolean = false
)