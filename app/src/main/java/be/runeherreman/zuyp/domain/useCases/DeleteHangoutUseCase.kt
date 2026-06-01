package be.runeherreman.zuyp.domain.useCases

import be.runeherreman.zuyp.domain.repository.HangoutRepository
import java.util.UUID
import javax.inject.Inject

class DeleteHangoutUseCase @Inject constructor(
    private val hangoutRepository: HangoutRepository
) {
    suspend operator fun invoke(hangoutId: UUID, userId: UUID) {
        val hangout = hangoutRepository.getHangoutById(hangoutId)

        if (hangout!!.creator.id == userId)
        {
            hangoutRepository.removeHangout(hangoutId)

            hangout.attendees.forEach { attendee ->
                hangoutRepository.removeAttendee(hangoutId, attendee.id)
            }
        }
    }
}