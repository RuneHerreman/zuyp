package be.runeherreman.zuyp.ui.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.data.fake.data.CurrentUser
import be.runeherreman.zuyp.domain.model.Group
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.domain.useCases.friendship.AddFriendshipUseCase
import be.runeherreman.zuyp.domain.useCases.friendship.GetFriendsUseCase
import be.runeherreman.zuyp.domain.useCases.friendship.RemoveFriendshipUseCase
import be.runeherreman.zuyp.domain.useCases.groups.AddMemberToGroupUseCase
import be.runeherreman.zuyp.domain.useCases.groups.CreateGroupUseCase
import be.runeherreman.zuyp.domain.useCases.groups.GetUserGroupsUseCase
import be.runeherreman.zuyp.domain.useCases.groups.RemoveGroupUseCase
import be.runeherreman.zuyp.domain.useCases.groups.RemoveMemberFromGroupUseCase
import be.runeherreman.zuyp.domain.useCases.groups.RenameGroupUseCase
import be.runeherreman.zuyp.domain.useCases.hangouts.GetAllHangoutsUseCase
import be.runeherreman.zuyp.domain.useCases.users.GetAllUsersUseCase
import be.runeherreman.zuyp.domain.model.Hangout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val getFriendsUseCase: GetFriendsUseCase,
    private val getUserGroupsUseCase: GetUserGroupsUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val getAllHangoutsUseCase: GetAllHangoutsUseCase,
    private val createGroupUseCase: CreateGroupUseCase,
    private val renameGroupUseCase: RenameGroupUseCase,
    private val removeGroupUseCase: RemoveGroupUseCase,
    private val addMemberToGroupUseCase: AddMemberToGroupUseCase,
    private val removeMemberFromGroupUseCase: RemoveMemberFromGroupUseCase,
    private val addFriendshipUseCase: AddFriendshipUseCase,
    private val removeFriendshipUseCase: RemoveFriendshipUseCase
) : ViewModel() {
    private val currentUserId: UUID = CurrentUser.id

    private val _uiState = MutableStateFlow(FriendsUiState(user = CurrentUser.user, isLoading = true))
    val uiState: StateFlow<FriendsUiState> = _uiState

    init {
        // Groups stream live, so a newly created group shows up automatically.
        viewModelScope.launch {
            getUserGroupsUseCase(currentUserId).collect { groups ->
                _uiState.update { it.copy(groups = groups, isLoading = false) }
            }
        }
        loadFriends()
    }

    fun dismissDialog() = _uiState.update { it.copy(dialog = null) }

    private fun loadFriends() {
        viewModelScope.launch {
            val friends = getFriendsUseCase(currentUserId)
            _uiState.update { it.copy(friends = friends, isLoading = false) }
        }
    }

    fun openCreateGroup() = _uiState.update { it.copy(dialog = FriendsDialog.CreateGroup) }

    fun createGroup(name: String, members: List<User>) {
        if (name.isBlank()) return
        val group = Group(
            id = UUID.randomUUID(),
            creatorId = currentUserId,
            name = name.trim(),
            description = "",
            members = members
        )
        viewModelScope.launch {
            createGroupUseCase(group)
            dismissDialog()
        }
    }

    fun openEditGroup(group: Group) = _uiState.update { it.copy(dialog = FriendsDialog.EditGroup(group)) }

    fun saveGroupEdits(group: Group, name: String, members: List<User>) {
        viewModelScope.launch {
            val trimmed = name.trim()
            if (trimmed.isNotBlank() && trimmed != group.name) {
                renameGroupUseCase(group.id, trimmed, currentUserId)
            }

            val originalIds = group.members.map { it.id }.toSet()
            val newIds = members.map { it.id }.toSet()

            (newIds - originalIds).forEach { addMemberToGroupUseCase(group.id, it) }
            (originalIds - newIds)
                .filter { it != group.creatorId }
                .forEach { removeMemberFromGroupUseCase(group.id, it, currentUserId) }

            dismissDialog()
        }
    }

    fun leaveGroup(group: Group) {
        viewModelScope.launch {
            removeMemberFromGroupUseCase(group.id, currentUserId, currentUserId)
        }
    }

    fun deleteGroup(group: Group) = viewModelScope.launch { removeGroupUseCase(group.id, currentUserId) }

    fun openGroupMembers(group: Group) = _uiState.update { it.copy(dialog = FriendsDialog.GroupMembers(group)) }

    fun openAddFriend() {
        viewModelScope.launch {
            val friendIds = _uiState.value.friends.mapTo(mutableSetOf()) { it.id }
            val candidates = getAllUsersUseCase()
                .filter { it.id != currentUserId && it.id !in friendIds }
            _uiState.update { it.copy(dialog = FriendsDialog.AddFriend(candidates)) }
        }
    }

    fun addFriend(user: User) {
        viewModelScope.launch {
            addFriendshipUseCase(currentUserId, user.id)
            loadFriends()
            dismissDialog()
        }
    }

    fun removeFriend(friend: User) {
        viewModelScope.launch {
            removeFriendshipUseCase(currentUserId, friend.id)
            loadFriends()
        }
    }

    fun openUserProfile(user: User) {
        viewModelScope.launch {
            val theirFriends = getFriendsUseCase(user.id)
            val groupCount = getUserGroupsUseCase(user.id).first().size
            val eventCount = getAllHangoutsUseCase().first().count { it.involves(user.id) }

            val myFriendIds = getFriendsUseCase(currentUserId).mapTo(mutableSetOf()) { it.id }
            val mutualFriends = theirFriends.filter { it.id in myFriendIds && it.id != currentUserId }
            val isFriend = user.id in myFriendIds

            _uiState.update {
                it.copy(
                    dialog = FriendsDialog.UserProfileDialog(
                        UserProfile(
                            user = user,
                            friendsCount = theirFriends.size,
                            groupsCount = groupCount,
                            eventsCount = eventCount,
                            mutualFriends = mutualFriends,
                            isFriend = isFriend
                        )
                    )
                )
            }
        }
    }

    private fun Hangout.involves(userId: UUID): Boolean = creator.id == userId || attendees.any { it.id == userId }
}
