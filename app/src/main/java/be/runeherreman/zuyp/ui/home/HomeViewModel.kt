package be.runeherreman.zuyp.ui.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.domain.useCases.GetHangoutsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri
import be.runeherreman.zuyp.domain.model.Hangout

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

    fun openMapsForHangout(hangout: Hangout, context: Context) {
        val uri =
            "geo:${hangout.latitude},${hangout.longitude}?q=${hangout.latitude},${hangout.longitude}(${hangout.title})"
            .toUri()

        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        val chooser = Intent.createChooser(mapIntent, "Navigate to ${hangout.title}")

        context.startActivity(chooser)
    }
}