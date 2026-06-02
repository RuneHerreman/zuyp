package be.runeherreman.zuyp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.data.fake.data.CurrentUser
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.useCases.hangouts.GetAllHangoutsUseCase
import be.runeherreman.zuyp.domain.useCases.friendship.GetFriendsUseCase
import be.runeherreman.zuyp.domain.useCases.users.EditProfileUseCase
import be.runeherreman.zuyp.domain.useCases.users.GetStartupScreenUseCase
import be.runeherreman.zuyp.domain.useCases.users.GetUserByIdUserCase
import be.runeherreman.zuyp.domain.useCases.users.SetStartupScreenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
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
    private val getStartupScreenUseCase: GetStartupScreenUseCase,
    private val setStartupScreenUseCase: SetStartupScreenUseCase,
    private val getUserByIdUseCase: GetUserByIdUserCase,
    private val editProfileUseCase: EditProfileUseCase,
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

        viewModelScope.launch {
            getStartupScreenUseCase().collect { route ->
                _uiState.update { it.copy(startupRoute = route) }
            }
        }

        viewModelScope.launch {
            val user = getUserByIdUseCase(currentUserId) ?: CurrentUser.user
            _uiState.update { it.copy(user = user) }
        }
    }

    /** Persists which screen the app should open on launch. */
    fun setStartupScreen(route: String) {
        viewModelScope.launch {
            setStartupScreenUseCase(route)
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

    fun onEditProfile() {
        _uiState.update { it.copy(isSettingsOpen = false, isEditProfileOpen = true) }
    }

    fun closeEditProfile() {
        _uiState.update { it.copy(isEditProfileOpen = false) }
    }

    fun saveProfile(name: String, email: String, birthdate: LocalDate) {
        val updated = (_uiState.value.user ?: CurrentUser.user).copy(
            name = name,
            email = email,
            birthdate = birthdate
        )
        viewModelScope.launch {
            editProfileUseCase(updated)
            _uiState.update { it.copy(user = updated, isEditProfileOpen = false) }
        }
    }
}
