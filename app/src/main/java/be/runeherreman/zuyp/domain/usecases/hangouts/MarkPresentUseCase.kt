package be.runeherreman.zuyp.domain.usecases.hangouts

import be.runeherreman.zuyp.domain.model.AttendanceStatus
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.repository.HangoutRepository
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class MarkPresentUseCase @Inject constructor(
    private val hangoutRepository: HangoutRepository,
) {
    /** Returns true if the user was marked PRESENT. */
    suspend operator fun invoke(hangoutId: UUID, userId: UUID): Boolean {
        val hangout = hangoutRepository.getHangoutById(hangoutId) ?: return false
        if (!hangout.isLive() || !hangout.isGoingFor(userId)) return false

        hangoutRepository.updateAttendenceStatus(hangoutId, userId, AttendanceStatus.PRESENT)
        return true
    }

    private fun Hangout.isLive(): Boolean {
        val now = LocalDateTime.now()
        return !now.isBefore(startDate) && now.isBefore(endDate)
    }

    private fun Hangout.isGoingFor(userId: UUID) =
        attendees.any { it.id == userId && it.attendanceStatus == AttendanceStatus.GOING }
}
