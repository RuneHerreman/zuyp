package be.runeherreman.zuyp.ui.discover

import be.runeherreman.zuyp.domain.model.Marker

data class DiscoverUiState (
    val markers: List<Marker> = emptyList(),
)