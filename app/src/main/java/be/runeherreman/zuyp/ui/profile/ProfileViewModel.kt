package be.runeherreman.zuyp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.data.fake.data.CurrentUser
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.useCases.hangouts.GetAllHangoutsUseCase
import be.runeherreman.zuyp.domain.useCases.friendship.GetFriendsUseCase
import be.runeherreman.zuyp.domain.useCases.users.SetUserLocationPreferenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getFriendsUseCase: GetFriendsUseCase,
    private val getAllHangoutsUseCase: GetAllHangoutsUseCase,
    private val setNotificationPreferenceUseCase: SetUserLocationPreferenceUseCase,
    private val setUserLocationPreferenceUseCase: SetUserLocationPreferenceUseCase,
) : ViewModel() {
    private val currentUserId: UUID = CurrentUser.id

    private val _uiState = MutableStateFlow(
        ProfileUiState(user = CurrentUser.user, isLoading = true)
    )
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        viewModelScope.launch {
            val friendCount = getFriendsUseCase(userId = currentUserId).count()
            _uiState.update { it.copy(friendsCount = friendCount) }
        }

        viewModelScope.launch {
            getAllHangoutsUseCase().collect { hangouts ->
                val now = LocalDateTime.now()

                val owned = hangouts.filter { it.creator.id == currentUserId }
                val upcoming = hangouts.filter {
                    it.startDate.isAfter(now) && isAttendingOrCreator(it)
                }
                val eventsCount = hangouts.count(::isAttendingOrCreator)

                _uiState.update {
                    it.copy(
                        ownedHangouts = owned,
                        upcomingHangouts = upcoming,
                        eventsCount = eventsCount,
                        isLoading = false
                    )
                }
            }
        }
    }

    /** A hangout the current user created or is on the attendee list of. */
    private fun isAttendingOrCreator(hangout: Hangout): Boolean =
        hangout.creator.id == currentUserId ||
        hangout.attendees.any { it.id == currentUserId }

    fun openSettings() {
        _uiState.update { it.copy(isSettingsOpen = true) }
    }

    fun closeSettings() {
        _uiState.update { it.copy(isSettingsOpen = false) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            setNotificationsEnabled(enabled)
            _uiState.update { it.copy(notificationsEnabled = enabled) }
        }
    }

    fun setLocationSharingEnabled(enabled: Boolean) {
        viewModelScope.launch {
            setLocationSharingEnabled(enabled)
            _uiState.update { it.copy(locationSharingEnabled = enabled) }
        }
    }

    fun onEditProfile() {
        // TODO: navigate to an edit-profile flow once it exists
    }

    fun onHangoutClick(hangout: Hangout) {
        // TODO
    }
}
