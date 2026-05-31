package be.runeherreman.zuyp.ui.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.data.local.room.entity.AttendanceStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import be.runeherreman.zuyp.domain.useCases.UpdateAttendanceUseCase
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
) : ViewModel() {

    private val _uiState = MutableStateFlow(ZuypAlertUiState())
    val uiState: StateFlow<ZuypAlertUiState> = _uiState

    fun loadFromIntent(hangoutId: String, title: String, locationName: String, startDate: String, weather: String?) {
        _uiState.update {
            it.copy(
                hangoutId = hangoutId,
                title = title,
                locationName = locationName,
                startDate = startDate,
                weather = weather,
            )
        }
    }

    fun join(userId: UUID, onJoined: () -> Unit) {
        viewModelScope.launch {
            val hangoutId = _uiState.value.hangoutId.ifBlank { return@launch }
            updateAttendanceUseCase(
                hangoutId = UUID.fromString(hangoutId),
                userId = userId,
                attendaceStatus = AttendanceStatus.GOING,
            )
            withContext(Dispatchers.Main) { onJoined() }
        }
    }
}
