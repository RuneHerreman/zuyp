package be.runeherreman.zuyp.ui.hangout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.User
import coil.compose.AsyncImage
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun HangoutScreen(
    uiState: HangoutUiState,
    onBackClick: () -> Unit = {},
    onFriendClick: (UUID) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        BackButton(onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.hangout.private) {
            PrivateBadge()
        }

        HangoutHeader(
            hangout = uiState.hangout,
            uiState = uiState
        )

        Spacer(modifier = Modifier.height(24.dp))

        ActionButtons()

        Spacer(modifier = Modifier.height(32.dp))

        AttendeesSection(
            attendees = uiState.hangout.attendees, friendShips = uiState.friendShipMapping,
            toggleFriendClick = { onFriendClick(it) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        ExpensesSection()
    }
}

@Composable
fun HangoutHeader(hangout: Hangout, uiState: HangoutUiState) {
    Text(
        text = hangout.title,
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.primary,
        lineHeight = 1.1.em
    )

    Spacer(modifier = Modifier.height(8.dp))

    val dateFormatter = DateTimeFormatter.ofPattern("MMM d yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH'h'mm")

    InfoRow(
        icon = Icons.Default.CalendarToday,
        text = "${hangout.startDate.format(dateFormatter)} - from ${hangout.startDate.format(timeFormatter)} to ${hangout.endDate.format(timeFormatter)}"
    )
    Spacer(modifier = Modifier.height(4.dp))
    InfoRow(
        icon = Icons.Default.LocationOn,
        text = hangout.locationName
    )
    Spacer(modifier = Modifier.height(4.dp))
    InfoRow(
        icon = uiState.weatherIcon,
        text = uiState.weatherPrediction
    )
}

@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ActionButtons() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Button(
            onClick = { /* TODO */ },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("I'm going", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
        Button(
            onClick = { /* TODO */ },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Not interested", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
        Button(
            onClick = { /* TODO */ },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun AttendeesSection(
    attendees: List<User>,
    friendShips: Map<UUID, Boolean>,
    toggleFriendClick: (UUID) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Attendees", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
    }

    Spacer(modifier = Modifier.height(4.dp))

    attendees.forEach { user ->
        AttendeeItem(user = user, friendShips = friendShips, toggleFriendClick = {toggleFriendClick(user.id)})
    }
}

@Composable
fun AttendeeItem(
    user: User,
    friendShips: Map<UUID, Boolean>,
    toggleFriendClick: (UUID) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(32.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model= "https://cataas.com/cat",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = user.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)

        if (friendShips[user.id] == true)
            IsFriendButton(toggleFriendClick = { toggleFriendClick(user.id) })
        else
            AddFriendButton(toggleFriendClick = { toggleFriendClick(user.id) })
    }
}

@Composable
fun AddFriendButton(
    toggleFriendClick : () -> Unit
){
    Button(
        onClick = { toggleFriendClick() },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        modifier = Modifier.height(32.dp)
    ) {
        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            "Add friend",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun IsFriendButton(
    toggleFriendClick : () -> Unit
){
    Button(
        onClick = { toggleFriendClick() },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF99FFAF).copy(alpha = 0.40f),
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        modifier = Modifier.height(32.dp)
    ) {
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            "Add friend",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C6B24)
        )
    }
}

@Composable
fun ExpensesSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AttachMoney, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Expenses", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        }
        Icon(Icons.Default.Add, contentDescription = "Add Expense")
    }

    Spacer(modifier = Modifier.height(4.dp))

    ExpenseItem(title = "🍻 4 Stella's", payerName = "Koen Koreman", amount = "€ 14.10")
}

@Composable
fun ExpenseItem(title: String, payerName: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(text = "paid by: $payerName", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(text = amount, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(end = 8.dp))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun BackButton(onBackClick: () -> Unit) {
    IconButton(
        onClick = onBackClick,
        modifier = Modifier
            .size(40.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
    ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun PrivateBadge() {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "Private",
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
}