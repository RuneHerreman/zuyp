package be.runeherreman.zuyp.ui.home

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.domain.useCases.CreateHangoutUseCase
import be.runeherreman.zuyp.domain.useCases.GetAllHangoutsUseCase
import be.runeherreman.zuyp.domain.useCases.GetFriendAttendeesByHangoutUseCase
import be.runeherreman.zuyp.domain.useCases.GetFriendsUseCase
import be.runeherreman.zuyp.domain.useCases.GetHangoutsUseCase
import be.runeherreman.zuyp.domain.useCases.UpdateAttendanceUseCase
import be.runeherreman.zuyp.data.local.room.entity.AttendanceStatus
import be.runeherreman.zuyp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import be.runeherreman.zuyp.domain.useCases.SendZuypAlertUseCase
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHangoutsUseCase: GetHangoutsUseCase,
    private val getAllHangoutsUseCase: GetAllHangoutsUseCase,
    private val getFriendAttendeesByHangoutUseCase: GetFriendAttendeesByHangoutUseCase,
    private val sendZuypAlertUseCase: SendZuypAlertUseCase,
    private val getFriendsUseCase: GetFriendsUseCase,
    private val createHangoutUseCase: CreateHangoutUseCase,
    private val updateAttendanceUseCase: UpdateAttendanceUseCase,
    private val userRepository: UserRepository
): ViewModel() {
    private val currentUserId = UUID.fromString("01234566-8f09-4567-4af8-def000000014")
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private var allHangouts: List<Hangout> = emptyList()
    private var currentUser: User? = null

    init {
        viewModelScope.launch {
            currentUser = userRepository.getUserById(currentUserId)
        }
        viewModelScope.launch {
            getHangoutsUseCase().collect { items ->
                val friendAttendees = getFriendAttendeesByHangoutUseCase(currentUserId, items)
                _uiState.update { it.copy(hangouts = items, friendAttendees = friendAttendees) }
            }
        }
        viewModelScope.launch {
            getAllHangoutsUseCase().collect { items ->
                allHangouts = items
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val friendAttendees = getFriendAttendeesByHangoutUseCase(currentUserId, _uiState.value.hangouts)
            _uiState.update { it.copy(friendAttendees = friendAttendees, isRefreshing = false) }
        }
    }

    fun openSearch() {
        _uiState.update { it.copy(isSearchOpen = true, searchQuery = "", searchResults = emptyList()) }
    }

    fun closeSearch() {
        _uiState.update { it.copy(isSearchOpen = false, searchQuery = "", searchResults = emptyList()) }
    }

    fun onSearchQueryChange(query: String) {
        val results = if (query.isBlank()) emptyList() else {
            allHangouts.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.locationName.contains(query, ignoreCase = true) ||
                it.creator.name.contains(query, ignoreCase = true)
            }
        }
        _uiState.update { it.copy(searchQuery = query, searchResults = results) }
    }

    fun openMapsForHangout(hangout: Hangout, context: android.content.Context) {
        val uri =
            "geo:${hangout.latitude},${hangout.longitude}?q=${hangout.latitude},${hangout.longitude}(${hangout.title})"
            .toUri()

        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        val chooser = Intent.createChooser(mapIntent, "Navigate to ${hangout.title}")

        context.startActivity(chooser)
    }

    fun sendZuypAlert() {
        viewModelScope.launch {
            sendZuypAlertUseCase(userId = currentUserId, hangoutId = UUID.fromString("10000000-0000-0000-0000-000000000002"))
        }
    }

    fun openCreateHangout() {
        viewModelScope.launch {
            val allUsers = userRepository.getAllUsers().filter { it.id != currentUserId }
            _uiState.update { it.copy(isCreateHangoutOpen = true, availableFriends = allUsers) }
        }
    }

    fun closeCreateHangout() {
        _uiState.update { it.copy(isCreateHangoutOpen = false) }
    }

    fun createHangout(
        title: String,
        date: LocalDate,
        location: String,
        members: List<User>,
        isPublic: Boolean
    ) {
        val creator = currentUser ?: return
        val hangoutId = UUID.randomUUID()
        val startDate = date.atTime(12, 0)
        val endDate = startDate.plusHours(2)
        val hangout = Hangout(
            id = hangoutId,
            title = title,
            description = "",
            locationName = location,
            latitude = 0.0,
            longitude = 0.0,
            startDate = startDate,
            endDate = endDate,
            attendees = emptyList(),
            creator = creator,
            private = !isPublic
        )
        viewModelScope.launch {
            createHangoutUseCase(hangout, members)
            _uiState.update { it.copy(isCreateHangoutOpen = false) }
        }
    }

}
