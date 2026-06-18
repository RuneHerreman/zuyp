package be.runeherreman.zuyp.domain.model

import com.mapbox.geojson.Point
import java.util.UUID

data class Marker(
    val hangoutId: UUID,
    val title: String,
    val position: Point
)
