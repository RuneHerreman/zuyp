package be.runeherreman.zuyp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.domain.useCases.GetHangoutsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHangoutsUseCase: GetHangoutsUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadHangouts()
    }

    fun loadHangouts(){
        viewModelScope.launch {
            getHangoutsUseCase().collect { items ->
                _uiState.update {
                    it.copy(
                        hangouts = items
                    )
                }
            }
        }
    }
}