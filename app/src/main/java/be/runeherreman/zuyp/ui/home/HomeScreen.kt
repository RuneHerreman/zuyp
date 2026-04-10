package be.runeherreman.zuyp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import be.runeherreman.zuyp.ui.home.components.HangoutCard


@Composable
fun HomeScreen(
    uiState: HomeUiState,
    modifier: Modifier = Modifier
) {
    // Column ensures elements are placed vertically
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Upcoming",
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier.height(30.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            uiState.hangouts.forEach {
                HangoutCard(hangout = it)
            }
        }

//        // This is where you'd use your uiState
//        if (uiState.isLoading) {
//            CircularProgressIndicator()
//        } else {
//            // Display your content here
//        }
    }
}