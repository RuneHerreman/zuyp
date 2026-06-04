package be.runeherreman.zuyp.ui.home

import be.runeherreman.zuyp.domain.model.AddressSuggestion
import be.runeherreman.zuyp.domain.model.Group
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.ResolvedAddress
import be.runeherreman.zuyp.domain.model.User
import java.time.LocalDateTime
import java.util.UUID

data class HomeUiState(
    val hangouts: List<Hangout> = emptyList(),
    val friendAttendees: Map<UUID, List<User>> = emptyMap(),
    val isRefreshing: Boolean = false,
    val availableUsers: List<User> = emptyList(),
    val availableGroups: List<Group> = emptyList(),

    // Address autocomplete state for the create-hangout form
    val addressQuery: String = "",
    val addressSuggestions: List<AddressSuggestion> = emptyList(),
    val isAddressLoading: Boolean = false,
    val selectedAddress: ResolvedAddress? = null,

    // Forms
    val createHangoutForm: CreateHangoutForm? = null,
    val zuypHangoutForm: CreateHangoutForm? = null,
    val isCreateHangoutOpen: Boolean = false,
    val isZuypHangoutOpen: Boolean = false,
    val isZuypSending: Boolean = false,

    // if zero attendance, laugh at user
    val phrases: List<String> = listOf(
        "Voorlopig bitter hard alleen",
        "Tafel voor geen!",
        "Participantally challenged",
        "De deur is open, jij moet er gewoon door",
        "Breng jij meer sfeer?",
        "Geen volk is ook volk",
        "Ben jij van de partij?",
        "Zo, zo eenzaam",
        "Je hoort me zingen waar ik ga. Na na na na",
        "Wanneer stopt het? Deze eenzaamheid",
        "Kom af, asociale flappie!",
        "Kben fabelachtig eenzaam als de maan, kom erbij!"
    ),

    // Search hangouts
    val isSearchOpen: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<Hangout> = emptyList()
)

data class CreateHangoutForm(
    val title: String = "",
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val isAllDay: Boolean = false,
    val memberSearch: String = "",
    val selectedMembers: List<User> = emptyList(),
    val isPrivate: Boolean = true,
)

sealed interface HomeEvent {
    data class LocationClicked(val hangout: Hangout) : HomeEvent
    data class HangoutClicked(val hangoutId: String) : HomeEvent

    data object SearchOpen : HomeEvent
    data object SearchClose : HomeEvent
    data class SearchQueryChange(val query: String) : HomeEvent

    data object Refresh : HomeEvent

    data object ZuypAlertClick : HomeEvent
    data object ZuypHangoutClose : HomeEvent
    data object CreateZuypHangout : HomeEvent

    data object CreateHangoutOpen : HomeEvent
    data object CreateHangoutClose : HomeEvent
    data object CreateHangout : HomeEvent

    data class AddressQueryChange(val query: String) : HomeEvent
    data class AddressSelect(val suggestion: AddressSuggestion) : HomeEvent
    data object AddressClear : HomeEvent

    data class CreateHangoutFormUpdate(val event: CreateHangoutFormEvent) : HomeEvent
    data class ZuypHangoutFormUpdate(val event: CreateHangoutFormEvent) : HomeEvent
}

sealed interface CreateHangoutFormEvent {
    data class TitleChanged(val title: String) : CreateHangoutFormEvent
    data class StartDateChanged(val dt: LocalDateTime) : CreateHangoutFormEvent
    data class EndDateChanged(val dt: LocalDateTime) : CreateHangoutFormEvent
    data class AllDayChanged(val isAllDay: Boolean) : CreateHangoutFormEvent
    data class MemberSearchChanged(val query: String) : CreateHangoutFormEvent
    data class MemberToggled(val user: User) : CreateHangoutFormEvent
    data class GroupSelected(val group: Group) : CreateHangoutFormEvent
    data class PrivateChanged(val isPrivate: Boolean) : CreateHangoutFormEvent
    data object InviteAll : CreateHangoutFormEvent
}
