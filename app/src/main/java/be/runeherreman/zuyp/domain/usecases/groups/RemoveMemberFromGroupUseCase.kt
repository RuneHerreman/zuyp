package be.runeherreman.zuyp.domain.usecases.groups

import be.runeherreman.zuyp.domain.repository.GroupRepository
import java.util.UUID
import javax.inject.Inject

class RemoveMemberFromGroupUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(groupId: UUID, memberId: UUID, requesterId: UUID) {
        groupRepository.removeMember(groupId, memberId, requesterId)
    }
}