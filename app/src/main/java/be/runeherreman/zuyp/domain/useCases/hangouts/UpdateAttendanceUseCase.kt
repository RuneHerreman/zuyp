package be.runeherreman.zuyp.domain.useCases.hangouts

import be.runeherreman.zuyp.data.local.room.entity.AttendanceStatus
import be.runeherreman.zuyp.domain.repository.HangoutRepository
import java.util.UUID
import javax.inject.Inject

class UpdateAttendanceUseCase @Inject constructor(
    private val hangoutRepository: HangoutRepository
) {
    suspend operator fun invoke(hangoutId: UUID, userId: UUID, attendaceStatus: AttendanceStatus?) {

        if (attendaceStatus == null) {
            hangoutRepository.removeAttendee(hangoutId, userId)
            return
        }
        hangoutRepository.updateAttendenceStatus(hangoutId, userId, attendaceStatus)
    }
}