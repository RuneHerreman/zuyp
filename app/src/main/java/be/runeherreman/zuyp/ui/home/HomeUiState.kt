package be.runeherreman.zuyp.ui.home

import be.runeherreman.zuyp.domain.model.Hangout

data class HomeUiState (
    val hangouts: List<Hangout> = emptyList()
)