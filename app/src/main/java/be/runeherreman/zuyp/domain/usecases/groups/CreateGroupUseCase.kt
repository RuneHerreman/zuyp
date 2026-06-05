package be.runeherreman.zuyp.domain.usecases.groups

import be.runeherreman.zuyp.domain.model.Group
import be.runeherreman.zuyp.domain.repository.GroupRepository
import javax.inject.Inject

class CreateGroupUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(group: Group) {
        groupRepository.createGroup(group)
    }
}