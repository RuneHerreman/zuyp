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
import be.runeherreman.zuyp.domain.useCases.users.GetAllUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val getFriendsUseCase: GetFriendsUseCase,
    private val getUserGroupsUseCase: GetUserGroupsUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val createGroupUseCase: CreateGroupUseCase,
    private val renameGroupUseCase: RenameGroupUseCase,
    private val removeGroupUseCase: RemoveGroupUseCase,
    private val addMemberToGroupUseCase: AddMemberToGroupUseCase,
    private val removeMemberFromGroupUseCase: RemoveMemberFromGroupUseCase,
    private val addFriendshipUseCase: AddFriendshipUseCase,
    private val removeFriendshipUseCase: RemoveFriendshipUseCase
) : ViewModel() {
    private val currentUserId: UUID = CurrentUser.id

    private val _uiState = MutableStateFlow(
        FriendsUiState(user = CurrentUser.user, isLoading = true)
    )
    val uiState: StateFlow<FriendsUiState> = _uiState

    init {
        // Groups stream live, so a newly created group shows up automatically.
        viewModelScope.launch {
            getUserGroupsUseCase(currentUserId).collect { groups ->
                _uiState.update { it.copy(groups = groups, isLoading = false) }
            }
        }
        // Friends are a one-shot fetch; runs in its own coroutine so the groups
        // collect above (which never completes) doesn't block it.
        loadFriends()
    }

    private fun loadFriends() {
        viewModelScope.launch {
            val friends = getFriendsUseCase(currentUserId)
            _uiState.update { it.copy(friends = friends, isLoading = false) }
        }
    }

    fun openCreateGroup() {
        // Group members are picked from the user's friends only (see FriendsScreen).
        _uiState.update { it.copy(isCreateGroupOpen = true) }
    }

    fun closeCreateGroup() {
        _uiState.update { it.copy(isCreateGroupOpen = false) }
    }

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
            _uiState.update { it.copy(isCreateGroupOpen = false) }
        }
    }

    fun openEditGroup(group: Group) {
        _uiState.update { it.copy(editingGroup = group) }
    }

    fun closeEditGroup() {
        _uiState.update { it.copy(editingGroup = null) }
    }

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

            _uiState.update { it.copy(editingGroup = null) }
        }
    }

    fun leaveGroup(group: Group) {
        viewModelScope.launch {
            removeMemberFromGroupUseCase(group.id, currentUserId, currentUserId)
        }
    }

    fun deleteGroup(group: Group) {
        viewModelScope.launch {
            removeGroupUseCase(group.id, currentUserId)
        }
    }

    fun openAddFriend() {
        viewModelScope.launch {
            val friendIds = _uiState.value.friends.mapTo(mutableSetOf()) { it.id }
            val candidates = getAllUsersUseCase()
                .filter { it.id != currentUserId && it.id !in friendIds }
            _uiState.update { it.copy(isAddFriendOpen = true, addFriendCandidates = candidates) }
        }
    }

    fun closeAddFriend() {
        _uiState.update { it.copy(isAddFriendOpen = false) }
    }

    fun addFriend(user: User) {
        viewModelScope.launch {
            addFriendshipUseCase(currentUserId, user.id)
            loadFriends()
            _uiState.update { it.copy(isAddFriendOpen = false) }
        }
    }

    fun removeFriend(friend: User) {
        viewModelScope.launch {
            removeFriendshipUseCase(currentUserId, friend.id)
            loadFriends()
        }
    }
}
