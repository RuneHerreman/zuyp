package be.runeherreman.zuyp.domain.usecases.hangouts

import be.runeherreman.zuyp.domain.model.AttendanceStatus
import be.runeherreman.zuyp.domain.repository.HangoutRepository
import java.util.UUID
import javax.inject.Inject

class MarkLeftUseCase @Inject constructor(
    private val hangoutRepository: HangoutRepository,
) {
    suspend operator fun invoke(hangoutId: UUID, userId: UUID) {
        val hangout = hangoutRepository.getHangoutById(hangoutId) ?: return
        val isPresent = hangout.attendees.any {
            it.id == userId && it.attendanceStatus == AttendanceStatus.PRESENT
        }
        if (isPresent) {
            hangoutRepository.updateAttendenceStatus(hangoutId, userId, AttendanceStatus.GOING)
        }
    }
}
