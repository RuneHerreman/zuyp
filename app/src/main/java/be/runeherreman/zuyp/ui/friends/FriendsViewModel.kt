package be.runeherreman.zuyp.ui.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.data.fake.data.CurrentUser
import be.runeherreman.zuyp.domain.model.Group
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.domain.useCases.friendship.AddFriendshipUseCase
import be.runeherreman.zuyp.domain.useCases.friendship.GetFriendsUseCase
import be.runeherreman.zuyp.domain.useCases.groups.CreateGroupUseCase
import be.runeherreman.zuyp.domain.useCases.groups.GetUserGroupsUseCase
import be.runeherreman.zuyp.domain.useCases.groups.RemoveGroupUseCase
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
    private val removeGroupUseCase: RemoveGroupUseCase,
    private val addFriendshipUseCase: AddFriendshipUseCase
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
        viewModelScope.launch {
            val available = getAllUsersUseCase().filter { it.id != currentUserId }
            _uiState.update { it.copy(isCreateGroupOpen = true, availableUsers = available) }
        }
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

    fun deleteGroup(group: Group) {
        viewModelScope.launch {
            removeGroupUseCase(group.id, currentUserId)
        }
    }

    fun openAddFriend() {
        _uiState.update { it.copy(isAddFriendOpen = true) }
    }

    fun closeAddFriend() {
        _uiState.update { it.copy(isAddFriendOpen = false) }
    }

    fun addFriend(username: String) {
        val query = username.trim()
        if (query.isBlank()) return
        viewModelScope.launch {
            val match = getAllUsersUseCase()
                .firstOrNull { it.id != currentUserId && it.name.equals(query, ignoreCase = true) }
            if (match != null) {
                addFriendshipUseCase(currentUserId, match.id)
                loadFriends()
            }
            _uiState.update { it.copy(isAddFriendOpen = false) }
        }
    }
}
