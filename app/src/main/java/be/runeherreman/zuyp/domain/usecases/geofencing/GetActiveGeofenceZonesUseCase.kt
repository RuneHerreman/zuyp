package be.runeherreman.zuyp.domain.usecases.geofencing

import be.runeherreman.zuyp.data.fake.data.CurrentUser
import be.runeherreman.zuyp.domain.model.AttendanceStatus
import be.runeherreman.zuyp.domain.model.GeoFence
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.repository.HangoutRepository
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class GetActiveGeofenceZonesUseCase @Inject constructor(
    private val hangoutRepository: HangoutRepository,
) {
    // Re-emits at each startDate/endDate boundary so zones activate/deactivate at the right moment.
    fun getActiveGeofenceZones(): Flow<List<GeoFence>> =
        hangoutRepository.getAllHangouts().flatMapLatest { hangouts ->
            flow {
                while (true) {
                    val now = LocalDateTime.now()
                    emit(toZones(hangouts, now))

                    val nextBoundary = hangouts
                        .flatMap { listOf(it.startDate, it.endDate) }
                        .filter { it.isAfter(now) }
                        .minOrNull() ?: break

                    delay(Duration.between(now, nextBoundary).toMillis())
                }
            }
        }

    suspend fun getSnapshot(): List<GeoFence> =
        toZones(hangoutRepository.getAllHangouts().first(), LocalDateTime.now())

    private fun toZones(hangouts: List<Hangout>, now: LocalDateTime): List<GeoFence> =
        hangouts
            .filter { !now.isBefore(it.startDate) && it.endDate.isAfter(now) && it.isGoingFor(CurrentUser.id) }
            .map { GeoFence(it.id, it.latitude, it.longitude) }

    private fun Hangout.isGoingFor(userId: UUID) =
        attendees.any { it.id == userId && it.attendanceStatus == AttendanceStatus.GOING }
}
