package be.runeherreman.zuyp.ui.home

import be.runeherreman.zuyp.domain.model.Hangout

data class HomeUiState(
    val hangouts: List<Hangout> = emptyList(),
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
    val isSearchOpen: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<Hangout> = emptyList()
)
