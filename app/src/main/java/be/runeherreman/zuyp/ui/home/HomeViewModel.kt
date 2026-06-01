package be.runeherreman.zuyp.ui.home

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.domain.model.AddressSuggestion
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.domain.useCases.CreateHangoutUseCase
import be.runeherreman.zuyp.domain.useCases.GetAllHangoutsUseCase
import be.runeherreman.zuyp.domain.useCases.GetFriendAttendeesByHangoutUseCase
import be.runeherreman.zuyp.domain.useCases.GetFriendsUseCase
import be.runeherreman.zuyp.domain.useCases.GetHangoutsUseCase
import be.runeherreman.zuyp.domain.useCases.ResolveAddressUseCase
import be.runeherreman.zuyp.domain.useCases.SearchAddressesUseCase
import be.runeherreman.zuyp.domain.useCases.UpdateAttendanceUseCase
import be.runeherreman.zuyp.data.local.room.entity.AttendanceStatus
import be.runeherreman.zuyp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import be.runeherreman.zuyp.domain.useCases.SendZuypAlertUseCase
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHangoutsUseCase: GetHangoutsUseCase,
    private val getAllHangoutsUseCase: GetAllHangoutsUseCase,
    private val getFriendAttendeesByHangoutUseCase: GetFriendAttendeesByHangoutUseCase,
    private val sendZuypAlertUseCase: SendZuypAlertUseCase,
    private val getFriendsUseCase: GetFriendsUseCase,
    private val createHangoutUseCase: CreateHangoutUseCase,
    private val updateAttendanceUseCase: UpdateAttendanceUseCase,
    private val searchAddressesUseCase: SearchAddressesUseCase,
    private val resolveAddressUseCase: ResolveAddressUseCase,
    private val userRepository: UserRepository
): ViewModel() {
    private val currentUserId = UUID.fromString("01234566-8f09-4567-4af8-def000000014")
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private var allHangouts: List<Hangout> = emptyList()
    private var currentUser: User? = null

    /** Drives debounced address lookups so we don't hit the API on every keystroke. */
    private val addressQueryFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            currentUser = userRepository.getUserById(currentUserId)
        }
        viewModelScope.launch {
            addressQueryFlow
                .debounce(300)
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
            _uiState.update {
                it.copy(isCreateHangoutOpen = true, availableUsers = allUsers)
            }
        }
    }

    fun closeCreateHangout() {
        _uiState.update { it.copy(isCreateHangoutOpen = false) }
        clearAddress()
    }

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

    fun createHangout(
        title: String,
        start: LocalDateTime,
        end: LocalDateTime,
        members: List<User>,
        isPublic: Boolean
    ) {
        val creator = currentUser ?: return
        // Guard: only allow creation with a resolved, existing address.
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
            private = !isPublic
        )
        viewModelScope.launch {
            createHangoutUseCase(hangout, members)
            _uiState.update { it.copy(isCreateHangoutOpen = false) }
            clearAddress()
        }
    }

}
