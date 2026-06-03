package be.runeherreman.zuyp.ui.discover

import be.runeherreman.zuyp.data.fake.data.CurrentUser
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.Marker
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import java.util.UUID

data class DiscoverUiState (
    val markers: List<Marker> = emptyList(),
    val selectedHangout: Hangout? = null,
    val hangoutPopupOpen: Boolean = false,
    val currentUserId: UUID = CurrentUser.id,
    val viewportState: MapViewportState = MapViewportState()
)