package be.runeherreman.zuyp.ui.hangout.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
fun DeleteButton(onDeleteClick: () -> Unit) {
    IconButton(
        onClick = onDeleteClick,
        modifier = Modifier
            .size(40.dp)
            .background(MaterialTheme.colorScheme.error, CircleShape)
    ) {
        Icon(Icons.Outlined.Delete, contentDescription = "Delete Hangout", tint = MaterialTheme.colorScheme.onError)
    }
}
