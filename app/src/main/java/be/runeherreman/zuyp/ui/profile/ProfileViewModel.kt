package be.runeherreman.zuyp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.data.fake.data.CurrentUser
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.useCases.hangouts.GetAllHangoutsUseCase
import be.runeherreman.zuyp.domain.useCases.friendship.GetFriendsUseCase
import be.runeherreman.zuyp.domain.useCases.groups.GetUserGroupsUseCase
import be.runeherreman.zuyp.domain.useCases.users.EditProfileUseCase
import be.runeherreman.zuyp.domain.useCases.users.GetStartupScreenUseCase
import be.runeherreman.zuyp.domain.useCases.users.GetUserByIdUseCase
import be.runeherreman.zuyp.domain.useCases.users.SetStartupScreenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getFriendsUseCase: GetFriendsUseCase,
    private val getUserGroupsUseCase: GetUserGroupsUseCase,
    private val getAllHangoutsUseCase: GetAllHangoutsUseCase,
    private val getStartupScreenUseCase: GetStartupScreenUseCase,
    private val setStartupScreenUseCase: SetStartupScreenUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val editProfileUseCase: EditProfileUseCase,
) : ViewModel() {
    private val currentUserId: UUID = CurrentUser.id

    private val _uiState = MutableStateFlow(ProfileUiState(user = CurrentUser.user, isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadUserAndFriends()

        viewModelScope.launch {
            getAllHangoutsUseCase().collect { hangouts ->
                val now = LocalDateTime.now()

                _uiState.update { it ->
                    it.copy(
                        ownedHangouts = hangouts.filter { it.creator.id == currentUserId },
                        upcomingHangouts = hangouts.filter { it.startDate.isAfter(now) && isAttendingOrCreator(it) },
                        previousHangouts = hangouts.filter { it.endDate.isBefore(now) && isAttendingOrCreator(it) },
                        eventsCount = hangouts.count(::isAttendingOrCreator),
                        isLoading = false
                    )
                }
            }
        }

        viewModelScope.launch {
            getStartupScreenUseCase().collect { route -> _uiState.update { it.copy(startupRoute = route) } }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            loadUserAndFriends().join()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private fun loadUserAndFriends() = viewModelScope.launch {
        val user = getUserByIdUseCase(currentUserId) ?: CurrentUser.user
        val friendCount = getFriendsUseCase(userId = currentUserId).count()
        val groupCount = getUserGroupsUseCase(currentUserId).first().size
        _uiState.update { it.copy(user = user, friendsCount = friendCount, groupsCount = groupCount) }
    }

    // What screen does the app open on?
    fun setStartupScreen(route: String) {
        viewModelScope.launch {
            setStartupScreenUseCase(route)
        }
    }

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
