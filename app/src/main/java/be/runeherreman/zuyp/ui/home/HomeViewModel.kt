package be.runeherreman.zuyp.ui.home

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.domain.model.AddressSuggestion
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.domain.useCases.hangouts.CreateHangoutUseCase
import be.runeherreman.zuyp.domain.useCases.hangouts.GetAllHangoutsUseCase
import be.runeherreman.zuyp.domain.useCases.friendship.GetFriendsUseCase
import be.runeherreman.zuyp.domain.useCases.users.GetAllUsersUseCase
import be.runeherreman.zuyp.domain.useCases.friendship.GetFriendAttendeesByHangoutUseCase
import be.runeherreman.zuyp.domain.useCases.groups.GetUserGroupsUseCase
import be.runeherreman.zuyp.domain.useCases.hangouts.GetHangoutsUseCase
import be.runeherreman.zuyp.domain.useCases.utils.ResolveAddressUseCase
import be.runeherreman.zuyp.data.fake.data.CurrentUser
import be.runeherreman.zuyp.domain.useCases.utils.SearchAddressesUseCase
import be.runeherreman.zuyp.domain.useCases.notification.SendZuypAlertUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHangoutsUseCase: GetHangoutsUseCase,
    private val getAllHangoutsUseCase: GetAllHangoutsUseCase,
    private val getFriendAttendeesByHangoutUseCase: GetFriendAttendeesByHangoutUseCase,
    private val getFriendsUseCase: GetFriendsUseCase,
    private val sendZuypAlertUseCase: SendZuypAlertUseCase,
    private val createHangoutUseCase: CreateHangoutUseCase,
    private val searchAddressesUseCase: SearchAddressesUseCase,
    private val resolveAddressUseCase: ResolveAddressUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val getUserGroupsUseCase: GetUserGroupsUseCase,
): ViewModel() {
    private val currentUserId: UUID = CurrentUser.id
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private var allHangouts: List<Hangout> = emptyList()

    private val addressQueryFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            addressQueryFlow
                .debounce(300) // don't hit api at every keystroke
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isBlank()) {
                        _uiState.update {
                            it.copy(addressSuggestions = emptyList(), isAddressLoading = false)
                        }
                        return@collectLatest
                    }
                    _uiState.update { it.copy(isAddressLoading = true) }
                    val results = searchAddressesUseCase(query)
                    _uiState.update {
                        it.copy(addressSuggestions = results, isAddressLoading = false)
                    }
                }
        }
        viewModelScope.launch {
            getHangoutsUseCase().collect { items ->
                val visible = items.filter(::isVisibleToCurrentUser)
                val friendAttendees = getFriendAttendeesByHangoutUseCase(currentUserId, visible)
                _uiState.update { it.copy(hangouts = visible, friendAttendees = friendAttendees) }
            }
        }
        viewModelScope.launch {
            getAllHangoutsUseCase().collect { items ->
                allHangouts = items.filter(::isVisibleToCurrentUser)
            }
        }
    }

    private fun isVisibleToCurrentUser(hangout: Hangout): Boolean =
        !hangout.private ||
        hangout.creator.id == currentUserId ||
        hangout.attendees.any { it.id == currentUserId }

    fun onEvent(event: HomeEvent, context: Context) {
        when (event) {
            HomeEvent.Refresh               -> refresh()
            HomeEvent.SearchOpen            -> openSearch()
            HomeEvent.SearchClose           -> closeSearch()
            HomeEvent.ZuypAlertClick        -> openZuypHangout()
            HomeEvent.ZuypHangoutClose      -> closeZuypHangout()
            HomeEvent.CreateHangoutOpen     -> openCreateHangout()
            HomeEvent.CreateHangoutClose    -> closeCreateHangout()
            HomeEvent.AddressClear          -> clearAddress()
            is HomeEvent.LocationClicked    -> openMapsForHangout(event.hangout, context)
            is HomeEvent.SearchQueryChange  -> onSearchQueryChange(event.query)

            is HomeEvent.AddressQueryChange -> onAddressQueryChange(event.query)
            is HomeEvent.AddressSelect      -> selectAddress(event.suggestion)
            is HomeEvent.CreateZuypHangout  -> createZuypHangout(
                title = event.title,
                start = event.startDate,
                members = event.users,
                private = event.private
            )
            is HomeEvent.CreateHangout      -> createHangout(
                title = event.title,
                start = event.startDate,
                end = event.endDate,
                members = event.users,
                private = event.private
            )
            is HomeEvent.HangoutClicked     -> Unit
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val friendAttendees = getFriendAttendeesByHangoutUseCase(currentUserId, _uiState.value.hangouts)
            _uiState.update { it.copy(friendAttendees = friendAttendees, isRefreshing = false) }
        }
    }

    // ======================================
    //            SEARCH HANGOUTS
    // ======================================
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

    // ======================================
    //            open-and-closers
    // ======================================
    fun openCreateHangout() {
        viewModelScope.launch {
            val allUsers = getAllUsersUseCase().filter { it.id != currentUserId }
            val groups = getUserGroupsUseCase(currentUserId).first()
            _uiState.update {
                it.copy(
                    isCreateHangoutOpen = true,
                    isZuypHangoutOpen = false,
                    availableUsers = allUsers,
                    availableGroups = groups
                )
            }
        }
    }

    fun openZuypHangout() {
        viewModelScope.launch {
            val friends = getFriendsUseCase(currentUserId)
            val groups = getUserGroupsUseCase(currentUserId).first()
            _uiState.update {
                it.copy(
                    isZuypHangoutOpen = true,
                    isCreateHangoutOpen = false,
                    availableUsers = friends,
                    availableGroups = groups
                )
            }
        }
    }

    fun closeCreateHangout() {
        _uiState.update { it.copy(isCreateHangoutOpen = false) }
        clearAddress()
    }

    fun closeZuypHangout() {
        _uiState.update { it.copy(isZuypHangoutOpen = false, isZuypSending = false) }
        clearAddress()
    }

    // ======================================
    //            ADDRESSES
    // ======================================
    fun onAddressQueryChange(query: String) {
        // Any edit invalidates a previously confirmed address — the user must
        // pick a real suggestion again, which is what enforces "it has to exist".
        _uiState.update { it.copy(addressQuery = query, selectedAddress = null) }
        addressQueryFlow.value = query
    }

    fun selectAddress(suggestion: AddressSuggestion) {
        viewModelScope.launch {
            val resolved = resolveAddressUseCase(suggestion.id) ?: return@launch
            _uiState.update {
                it.copy(
                    selectedAddress = resolved,
                    addressQuery = resolved.fullAddress,
                    addressSuggestions = emptyList(),
                    isAddressLoading = false
                )
            }
            addressQueryFlow.value = resolved.fullAddress
        }
    }

    fun clearAddress() {
        _uiState.update {
            it.copy(
                addressQuery = "",
                addressSuggestions = emptyList(),
                selectedAddress = null,
                isAddressLoading = false
            )
        }
        addressQueryFlow.value = ""
    }

    // ======================================
    //            CREATION CALLS
    // ======================================
    fun createHangout(
        title: String,
        start: LocalDateTime,
        end: LocalDateTime,
        members: List<User>,
        private: Boolean
    ) {
        val creator = CurrentUser.user
        val address = _uiState.value.selectedAddress ?: return
        val hangoutId = UUID.randomUUID()
        val hangout = Hangout(
            id = hangoutId,
            title = title,
            description = "",
            locationName = address.fullAddress,
            latitude = address.latitude,
            longitude = address.longitude,
            startDate = start,
            endDate = end,
            attendees = emptyList(),
            creator = creator,
            private = private
        )
        viewModelScope.launch {
            createHangoutUseCase(hangout, members)
            _uiState.update { it.copy(isCreateHangoutOpen = false) }
            clearAddress()
        }
    }

    fun createZuypHangout(
        title: String,
        start: LocalDateTime,
        members: List<User>,
        private: Boolean
    ) {
        val now = LocalDateTime.now()
        val latest = now.plusHours(24)
        if (!start.isAfter(now) || start.isAfter(latest)) return
        val creator = CurrentUser.user
        val address = _uiState.value.selectedAddress ?: return
        val hangoutId = UUID.randomUUID()
        val end = if (start.toLocalTime() == java.time.LocalTime.MIDNIGHT)
            start.toLocalDate().atTime(23, 59)
        else
            start.plusHours(2)
        val hangout = Hangout(
            id = hangoutId,
            title = title,
            description = "",
            locationName = address.fullAddress,
            latitude = address.latitude,
            longitude = address.longitude,
            startDate = start,
            endDate = end,
            attendees = emptyList(),
            creator = creator,
            private = private
        )
        viewModelScope.launch {
            _uiState.update { it.copy(isZuypSending = true) }
            createHangoutUseCase(hangout, members)
            sendZuypAlertUseCase(userId = currentUserId, hangoutId = hangoutId)
            _uiState.update { it.copy(isZuypHangoutOpen = false, isZuypSending = false) }
            clearAddress()
        }
    }

}
