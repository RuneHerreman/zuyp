package be.runeherreman.zuyp.domain.useCases.groups

import be.runeherreman.zuyp.domain.repository.GroupRepository
import java.util.UUID
import javax.inject.Inject

class AddMemberToGroupUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(groupId: UUID, userId: UUID) {
        groupRepository.addMember(groupId, userId)
    }
}