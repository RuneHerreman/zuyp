package be.runeherreman.zuyp.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import be.runeherreman.zuyp.domain.model.AddressSuggestion
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.ui.home.components.CreateHangoutPopup
import be.runeherreman.zuyp.ui.home.components.HangoutCard
import be.runeherreman.zuyp.ui.home.components.SearchOverlay
import be.runeherreman.zuyp.ui.home.components.ZuypEmergencyButton
import be.runeherreman.zuyp.ui.home.components.ZuypHangoutOverlay
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    onLocationClick: (Hangout) -> Unit = {},
    onHangoutClick: (Hangout) -> Unit = {},
    onSearchOpen: () -> Unit = {},
    onSearchClose: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onZuypAlertClick: () -> Unit = {},
    onZuypHangoutClose: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onCreateHangoutOpen: () -> Unit = {},
    onCreateHangoutClose: () -> Unit = {},
    onAddressQueryChange: (String) -> Unit = {},
    onAddressSelect: (AddressSuggestion) -> Unit = {},
    onAddressClear: () -> Unit = {},
    onCreateHangout: (String, LocalDateTime, LocalDateTime, List<User>, Boolean) -> Unit = { _, _, _, _, _ -> },
    onCreateZuypHangout: (String, LocalDateTime, List<User>, Boolean) -> Unit = { _, _, _, _ -> }
) {
    Box(modifier = modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Upcoming\nEvents",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onSearchOpen) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search events",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ZuypEmergencyButton(modifier = Modifier.align(Alignment.End), onClick = onZuypAlertClick)

            Spacer(modifier = Modifier.height(30.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                uiState.hangouts.forEach {
                    HangoutCard(
                        hangout = it,
                        onLocationClick = onLocationClick,
                        phrases = uiState.phrases,
                        friendAttendees = uiState.friendAttendees[it.id] ?: emptyList(),
                        onClick = onHangoutClick
                    )
                }
            }
        }
        } // PullToRefreshBox

        AnimatedVisibility(
            visible = uiState.isSearchOpen,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            SearchOverlay(
                query = uiState.searchQuery,
                results = uiState.searchResults,
                phrases = uiState.phrases,
                friendAttendees = uiState.friendAttendees,
                onQueryChange = onSearchQueryChange,
                onClose = onSearchClose,
                onLocationClick = onLocationClick,
                onHangoutClick = onHangoutClick
            )
        }

        FloatingActionButton(
            onClick = onCreateHangoutOpen,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(6.dp),
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Create hangout")
        }

        if (uiState.isCreateHangoutOpen) {
            CreateHangoutPopup(
                availableUsers = uiState.availableUsers,
                addressQuery = uiState.addressQuery,
                addressSuggestions = uiState.addressSuggestions,
                isAddressLoading = uiState.isAddressLoading,
                isAddressSelected = uiState.selectedAddress != null,
                onAddressQueryChange = onAddressQueryChange,
                onAddressSelect = onAddressSelect,
                onAddressClear = onAddressClear,
                onDismiss = onCreateHangoutClose,
                onCreate = onCreateHangout
            )
        }

        if (uiState.isZuypHangoutOpen) {
            ZuypHangoutOverlay(
                availableUsers = uiState.availableUsers,
                addressQuery = uiState.addressQuery,
                addressSuggestions = uiState.addressSuggestions,
                isAddressLoading = uiState.isAddressLoading,
                isAddressSelected = uiState.selectedAddress != null,
                isSending = uiState.isZuypSending,
                onAddressQueryChange = onAddressQueryChange,
                onAddressSelect = onAddressSelect,
                onAddressClear = onAddressClear,
                onDismiss = onZuypHangoutClose,
                onCreateZuyp = onCreateZuypHangout
            )
        }
    }
}
