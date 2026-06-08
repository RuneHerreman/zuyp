package be.runeherreman.zuyp.ui.alert

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.data.fake.data.CurrentUser
import be.runeherreman.zuyp.domain.model.AttendanceStatus
import be.runeherreman.zuyp.domain.usecases.hangouts.UpdateAttendanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ZuypAlertViewModel @Inject constructor(
    private val updateAttendanceUseCase: UpdateAttendanceUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Intent extras are automatically available in SavedStateHandle for @HiltViewModel.
    private val _uiState = MutableStateFlow(
        ZuypAlertUiState(
            hangoutId    = savedStateHandle.get<String>("hangoutId")    ?: "",
            title        = savedStateHandle.get<String>("title")        ?: "",
            locationName = savedStateHandle.get<String>("locationName") ?: "",
            startDate    = savedStateHandle.get<String>("startDate")    ?: "",
            weather      = savedStateHandle.get<String>("weather"),
        )
    )
    val uiState: StateFlow<ZuypAlertUiState> = _uiState

    fun join() {
        viewModelScope.launch {
            val hangoutId = _uiState.value.hangoutId.ifBlank { return@launch }
            val hangoutUUID = runCatching { UUID.fromString(hangoutId) }.getOrNull() ?: return@launch
            updateAttendanceUseCase(
                hangoutId     = hangoutUUID,
                userId        = CurrentUser.id,
                attendaceStatus = AttendanceStatus.GOING,
            )
            dismiss()
        }
    }

    fun dismiss() {
        _uiState.update { it.copy(isDismissed = true) }
    }
}
