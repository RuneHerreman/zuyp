package be.runeherreman.zuyp.ui.home

import android.content.Context
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
import be.runeherreman.zuyp.ui.utils.openMapsForHangout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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

    init {
        viewModelScope.launch {
            _uiState
                .map { it.addressQuery }
                .distinctUntilChanged()
                .debounce(300)
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
            HomeEvent.Refresh                       -> refresh()
            HomeEvent.SearchOpen                    -> openSearch()
            HomeEvent.SearchClose                   -> closeSearch()
            HomeEvent.ZuypAlertClick                -> openZuypHangout()
            HomeEvent.ZuypHangoutClose              -> closeZuypHangout()
            HomeEvent.CreateHangoutOpen             -> openCreateHangout()
            HomeEvent.CreateHangoutClose            -> closeCreateHangout()
            HomeEvent.AddressClear                  -> clearAddress()
            HomeEvent.CreateHangout                 -> createHangout()
            HomeEvent.CreateZuypHangout             -> createZuypHangout()
            is HomeEvent.LocationClicked            -> openMapsForHangout(event.hangout, context)
            is HomeEvent.SearchQueryChange          -> onSearchQueryChange(event.query)
            is HomeEvent.AddressQueryChange         -> onAddressQueryChange(event.query)
            is HomeEvent.AddressSelect              -> selectAddress(event.suggestion)
            is HomeEvent.CreateHangoutFormUpdate    -> applyFormEvent(event.event, ::updateCreateHangoutForm)
            is HomeEvent.ZuypHangoutFormUpdate      -> applyFormEvent(event.event, ::updateZuypHangoutForm)
            is HomeEvent.HangoutClicked             -> Unit
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

    fun openMapsForHangout(hangout: Hangout, context: Context) {
        be.runeherreman.zuyp.ui.utils.openMapsForHangout(hangout, context)
    }

    // ======================================
    //            OPEN AND CLOSERS
    // ======================================
    fun openCreateHangout() {
        val nowRounded = LocalDateTime.now().withSecond(0).withNano(0).withMinute(0).plusHours(1)
        viewModelScope.launch {
            val allUsers = getAllUsersUseCase().filter { it.id != currentUserId }
            val groups = getUserGroupsUseCase(currentUserId).first()
            _uiState.update {
                it.copy(
                    isCreateHangoutOpen = true,
                    isZuypHangoutOpen = false,
                    availableUsers = allUsers,
                    availableGroups = groups,
                    createHangoutForm = CreateHangoutForm(
                        startDateTime = nowRounded,
                        endDateTime = nowRounded.plusHours(2)
                    )
                )
            }
        }
    }

    fun openZuypHangout() {
        val nowRounded = LocalDateTime.now().withSecond(0).withNano(0).withMinute(0).plusHours(1)
        viewModelScope.launch {
            val friends = getFriendsUseCase(currentUserId)
            val groups = getUserGroupsUseCase(currentUserId).first()
            _uiState.update {
                it.copy(
                    isZuypHangoutOpen = true,
                    isCreateHangoutOpen = false,
                    availableUsers = friends,
                    availableGroups = groups,
                    zuypHangoutForm = CreateHangoutForm(
                        startDateTime = nowRounded,
                        endDateTime = nowRounded.plusHours(2)
                    )
                )
            }
        }
    }

    fun closeCreateHangout() {
        _uiState.update { it.copy(isCreateHangoutOpen = false, createHangoutForm = null) }
        clearAddress()
    }

    fun closeZuypHangout() {
        _uiState.update { it.copy(isZuypHangoutOpen = false, isZuypSending = false, zuypHangoutForm = null) }
        clearAddress()
    }

    // ======================================
    //            ADDRESSES
    // ======================================
    fun onAddressQueryChange(query: String) {
        _uiState.update { it.copy(addressQuery = query, selectedAddress = null) }
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
    }

    // ======================================
    //            FORM EVENTS
    // ======================================
    private fun updateCreateHangoutForm(update: (CreateHangoutForm) -> CreateHangoutForm) {
        _uiState.update { state ->
            val form = state.createHangoutForm ?: return@update state
            state.copy(createHangoutForm = update(form))
        }
    }

    private fun updateZuypHangoutForm(update: (CreateHangoutForm) -> CreateHangoutForm) {
        _uiState.update { state ->
            val form = state.zuypHangoutForm ?: return@update state
            state.copy(zuypHangoutForm = update(form))
        }
    }

    private fun applyFormEvent(
        event: CreateHangoutFormEvent,
        applyUpdate: ((CreateHangoutForm) -> CreateHangoutForm) -> Unit
    ) {
        when (event) {
            is CreateHangoutFormEvent.TitleChanged -> applyUpdate { it.copy(title = event.title) }
            is CreateHangoutFormEvent.StartDateChanged -> applyUpdate { it.copy(startDateTime = event.dt) }
            is CreateHangoutFormEvent.EndDateChanged -> applyUpdate { it.copy(endDateTime = event.dt) }
            is CreateHangoutFormEvent.AllDayChanged -> applyUpdate { it.copy(isAllDay = event.isAllDay) }
            is CreateHangoutFormEvent.MemberSearchChanged -> applyUpdate { it.copy(memberSearch = event.query) }
            is CreateHangoutFormEvent.MemberToggled -> applyUpdate { form ->
                    val exists = form.selectedMembers.any { it.id == event.user.id }
                    form.copy(
                        selectedMembers = if (exists)
                            form.selectedMembers.filter { it.id != event.user.id }
                        else
                            form.selectedMembers + event.user
                    )
                }
            is CreateHangoutFormEvent.GroupSelected -> applyUpdate { form ->
                    val members = event.group.members.filter { it.id != currentUserId }
                    form.copy(
                        selectedMembers = (form.selectedMembers + members).distinctBy { it.id },
                        memberSearch = ""
                    )
                }
            is CreateHangoutFormEvent.PrivateChanged -> applyUpdate { it.copy(isPrivate = event.isPrivate) }
            CreateHangoutFormEvent.InviteAll -> applyUpdate { form ->
                    form.copy(
                        selectedMembers = _uiState.value.availableUsers.distinctBy { it.id },
                        memberSearch = ""
                    )
                }
        }
    }

    // ======================================
    //            CREATION CALLS
    // ======================================
    fun createHangout() {
        val form = _uiState.value.createHangoutForm ?: return
        val address = _uiState.value.selectedAddress ?: return
        val creator = CurrentUser.user
        val finalStart = if (form.isAllDay) form.startDateTime.toLocalDate().atStartOfDay() else form.startDateTime
        val finalEnd = if (form.isAllDay) form.endDateTime.toLocalDate().atTime(23, 59) else form.endDateTime
        val hangout = Hangout(
            id = UUID.randomUUID(),
            title = form.title,
            description = "",
            locationName = address.fullAddress,
            latitude = address.latitude,
            longitude = address.longitude,
            startDate = finalStart,
            endDate = finalEnd,
            attendees = emptyList(),
            creator = creator,
            private = form.isPrivate
        )
        viewModelScope.launch {
            createHangoutUseCase(hangout, form.selectedMembers)
            _uiState.update { it.copy(isCreateHangoutOpen = false, createHangoutForm = null) }
            clearAddress()
        }
    }

    fun createZuypHangout() {
        val form = _uiState.value.zuypHangoutForm ?: return
        val address = _uiState.value.selectedAddress ?: return
        val now = LocalDateTime.now()
        val finalStart = if (form.isAllDay) form.startDateTime.toLocalDate().atStartOfDay() else form.startDateTime
        if (!finalStart.isAfter(now) || finalStart.isAfter(now.plusHours(24))) return
        val creator = CurrentUser.user
        val hangoutId = UUID.randomUUID()
        val end = if (finalStart.toLocalTime() == java.time.LocalTime.MIDNIGHT)
            finalStart.toLocalDate().atTime(23, 59)
        else
            finalStart.plusHours(2)
        val hangout = Hangout(
            id = hangoutId,
            title = form.title,
            description = "",
            locationName = address.fullAddress,
            latitude = address.latitude,
            longitude = address.longitude,
            startDate = finalStart,
            endDate = end,
            attendees = emptyList(),
            creator = creator,
            private = form.isPrivate
        )
        viewModelScope.launch {
            _uiState.update { it.copy(isZuypSending = true) }
            createHangoutUseCase(hangout, form.selectedMembers)
            sendZuypAlertUseCase(userId = currentUserId, hangoutId = hangoutId)
            _uiState.update { it.copy(isZuypHangoutOpen = false, isZuypSending = false, zuypHangoutForm = null) }
            clearAddress()
        }
    }
}
