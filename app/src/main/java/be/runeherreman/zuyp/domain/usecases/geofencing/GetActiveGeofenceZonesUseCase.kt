package be.runeherreman.zuyp.domain.usecases.geofencing

import be.runeherreman.zuyp.data.fake.data.CurrentUser
import be.runeherreman.zuyp.domain.model.AttendanceStatus
import be.runeherreman.zuyp.domain.model.GeoFence
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.repository.HangoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class GetActiveGeofenceZonesUseCase @Inject constructor(
    private val hangoutRepository: HangoutRepository,
) {
    // emits when data changes
    fun getActiveGeofenceZones(): Flow<List<GeoFence>> =
        hangoutRepository.getAllHangouts().map { hangouts -> toZones(hangouts) }

    suspend fun getSnapshot(): List<GeoFence> =
        toZones(hangoutRepository.getAllHangouts().first())

    private fun toZones(hangouts: List<Hangout>): List<GeoFence> {
        val now = LocalDateTime.now()
        return hangouts
            .filter { it.endDate.isAfter(now) && it.isGoingFor(CurrentUser.id) }
            .map { GeoFence(it.id, it.latitude, it.longitude) }
    }

    private fun Hangout.isGoingFor(userId: UUID) =
        attendees.any { it.id == userId && it.attendanceStatus == AttendanceStatus.GOING }
}
