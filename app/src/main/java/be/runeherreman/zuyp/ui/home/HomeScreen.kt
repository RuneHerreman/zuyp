package be.runeherreman.zuyp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.ui.home.components.HangoutCard


@Composable
fun HomeScreen(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    onLocationClick: (Hangout) -> Unit = { _ -> },
    onHangoutClick: (Hangout) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column (
            modifier = Modifier.fillMaxWidth().padding(top=16.dp),

        ) {
            Text(
                text = "Upcoming\nEvents",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            ZuypEmergencyButton(
                modifier = Modifier.align(Alignment.End)
            )
        }


        Spacer(modifier = Modifier.height(30.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            uiState.hangouts.forEach {
                HangoutCard(
                    hangout = it,
                    onLocationClick = onLocationClick,
                    phrases = uiState.phrases,
                    onClick = onHangoutClick
                )
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

@Composable
fun ZuypEmergencyButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
){
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
        ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Alarm,
                contentDescription = "Emergency"
            )

            Text(
                text = "I want to Zuyp",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}