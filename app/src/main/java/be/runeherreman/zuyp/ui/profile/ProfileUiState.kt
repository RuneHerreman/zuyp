package be.runeherreman.zuyp.ui.profile

import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.User

data class ProfileUiState(
    val user: User? = null,
    val friendsCount: Int = 0,
    val groupsCount: Int = 0,
    val eventsCount: Int = 0,
    val ownedHangouts: List<Hangout> = emptyList(),
    val upcomingHangouts: List<Hangout> = emptyList(),
    val isLoading: Boolean = false,

    // Settings popup
    val isSettingsOpen: Boolean = false,
    val notificationsEnabled: Boolean = false,
    val locationSharingEnabled: Boolean = false
)
