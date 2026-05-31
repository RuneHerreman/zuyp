package be.runeherreman.zuyp.ui.hangout

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.ui.graphics.vector.ImageVector
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.User
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class HangoutUiState(
    val hangout: Hangout = Hangout(
        id = UUID.randomUUID(),
        title = "Example hangout",
        description = "This is an example hangout description",
        locationName = "Frankelstraat 28",
        latitude = 50.7,
        longitude = 3.785,
        startDate = LocalDateTime.of(2026, 4, 18, 14, 0),
        endDate = LocalDateTime.of(2026, 4, 18, 23, 0),
        attendees = emptyList(),
        creator = User(UUID.randomUUID(), "KoenK", LocalDate.of(2002, 7, 20), "koen@gmail.com"),
        private = false,
    ),
    val currentUser: User = User(
        UUID.fromString("01234566-8f09-4567-4af8-def000000014"),
        "Koen Koreman",
        LocalDate.of(2002, 7, 20),
        "koen.koreman@gmail.com"
    ),
    val friendShipMapping: Map<UUID, Boolean> = emptyMap(),
    val weatherPrediction: String = "",
    val isLoadingWeather: Boolean = true,
    val weatherIcon: ImageVector = Icons.Filled.Thermostat,
    val isError: Boolean = false
)