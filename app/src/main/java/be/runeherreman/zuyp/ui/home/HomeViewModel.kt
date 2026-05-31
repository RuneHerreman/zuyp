package be.runeherreman.zuyp.ui.home

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.useCases.GetAllHangoutsUseCase
import be.runeherreman.zuyp.domain.useCases.GetFriendAttendeesByHangoutUseCase
import be.runeherreman.zuyp.domain.useCases.GetHangoutsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import be.runeherreman.zuyp.domain.useCases.SendZuypAlertUseCase
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHangoutsUseCase: GetHangoutsUseCase,
    private val getAllHangoutsUseCase: GetAllHangoutsUseCase,
    private val getFriendAttendeesByHangoutUseCase: GetFriendAttendeesByHangoutUseCase,
    private val sendZuypAlertUseCase: SendZuypAlertUseCase
): ViewModel() {
    private val currentUserId = UUID.fromString("01234566-8f09-4567-4af8-def000000014")
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private var allHangouts: List<Hangout> = emptyList()

    init {
        viewModelScope.launch {
            getHangoutsUseCase().collect { items ->
                val friendAttendees = getFriendAttendeesByHangoutUseCase(currentUserId, items)
                _uiState.update { it.copy(hangouts = items, friendAttendees = friendAttendees) }
            }
        }
        viewModelScope.launch {
            getAllHangoutsUseCase().collect { items ->
                allHangouts = items
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val friendAttendees = getFriendAttendeesByHangoutUseCase(currentUserId, _uiState.value.hangouts)
            _uiState.update { it.copy(friendAttendees = friendAttendees, isRefreshing = false) }
        }
    }

    fun openSearch() {
        _uiState.update { it.copy(isSearchOpen = true, searchQuery = "", searchResults = emptyList()) }
    }

    fun closeSearch() {
        _uiState.update { it.copy(isSearchOpen = false, searchQuery = "", searchResults = emptyList()) }
    }

    fun onSearchQueryChange(query: String) {
        val results = if (query.isBlank()) emptyList() else {
            allHangouts.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.locationName.contains(query, ignoreCase = true) ||
                it.creator.name.contains(query, ignoreCase = true)
            }
        }
        _uiState.update { it.copy(searchQuery = query, searchResults = results) }
    }

    fun openMapsForHangout(hangout: Hangout, context: android.content.Context) {
        val uri =
            "geo:${hangout.latitude},${hangout.longitude}?q=${hangout.latitude},${hangout.longitude}(${hangout.title})"
            .toUri()

        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        val chooser = Intent.createChooser(mapIntent, "Navigate to ${hangout.title}")

        context.startActivity(chooser)
    }

    fun sendZuypAlert() {
        viewModelScope.launch {
            sendZuypAlertUseCase(userId = currentUserId, hangoutId = UUID.fromString("10000000-0000-0000-0000-000000000002"))
        }
    }

}
