package be.runeherreman.zuyp.domain.useCases.hangouts

import be.runeherreman.zuyp.data.local.room.entity.hangouts.AttendanceStatus
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
