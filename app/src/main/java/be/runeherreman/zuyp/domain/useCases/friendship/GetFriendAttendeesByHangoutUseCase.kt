package be.runeherreman.zuyp.domain.useCases.friendship

import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.domain.repository.UserRepository
import be.runeherreman.zuyp.data.local.room.entity.hangouts.AttendanceStatus
import java.util.UUID
import javax.inject.Inject

class GetFriendAttendeesByHangoutUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(currentUserId: UUID, hangouts: List<Hangout>): Map<UUID, List<User>> {
        val friendIds = userRepository.getFriendsOfUser(currentUserId).map { it.id }.toHashSet()
        return hangouts.associate { hangout ->
            hangout.id to hangout.attendees.filter {
                it.id in friendIds && it.attendanceStatus == AttendanceStatus.GOING
            }
        }
    }
}
