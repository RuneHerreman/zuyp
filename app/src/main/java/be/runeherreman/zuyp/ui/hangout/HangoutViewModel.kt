package be.runeherreman.zuyp.ui.hangout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.domain.useCases.GetHangoutByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HangoutViewModel @Inject constructor(
    private val getHangoutByIdUseCase: GetHangoutByIdUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(HangoutUiState())
    val uiState: StateFlow<HangoutUiState> = _uiState

    fun loadHangout(hangoutId: String) {
        viewModelScope.launch {
            getHangoutByIdUseCase(hangoutId).let { item ->
                _uiState.update {
                    it.copy(
                        hangout = item ?: HangoutUiState().hangout
                    )
                }
            }
        }
    }
}