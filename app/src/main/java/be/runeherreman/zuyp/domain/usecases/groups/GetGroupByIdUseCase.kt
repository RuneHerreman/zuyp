package be.runeherreman.zuyp.domain.usecases.groups

import be.runeherreman.zuyp.domain.model.Group
import be.runeherreman.zuyp.domain.repository.GroupRepository
import java.util.UUID
import javax.inject.Inject

class GetGroupByIdUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(groupId: UUID): Group? {
        return groupRepository.getGroupById(groupId)
    }
}