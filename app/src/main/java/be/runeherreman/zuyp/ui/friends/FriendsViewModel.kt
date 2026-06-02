package be.runeherreman.zuyp.ui.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.data.fake.data.CurrentUser
import be.runeherreman.zuyp.domain.useCases.friendship.GetFriendsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val getFriendsUseCase: GetFriendsUseCase,
): ViewModel() {
    private val _uiState = MutableStateFlow(FriendsUiState(user = CurrentUser.user, isLoading = true))
    val uiState: StateFlow<FriendsUiState> = _uiState

    val currentUserId = CurrentUser.id

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    friends = getFriendsUseCase(currentUserId),
                    isLoading = false
                )
            }
        }
    }
}