package be.runeherreman.zuyp.domain.useCases.groups

import be.runeherreman.zuyp.domain.repository.GroupRepository
import java.util.UUID
import javax.inject.Inject

class RemoveGroupUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(groupId: UUID, requesterId: UUID) {
        groupRepository.removeGroup(groupId, requesterId)
    }
}