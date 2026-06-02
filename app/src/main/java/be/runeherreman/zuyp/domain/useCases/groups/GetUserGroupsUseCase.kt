package be.runeherreman.zuyp.domain.useCases.groups

import be.runeherreman.zuyp.domain.model.Group
import be.runeherreman.zuyp.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class GetUserGroupsUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(userId: UUID): Flow<List<Group>> {
        return groupRepository.getUserGroups(userId)
    }
}