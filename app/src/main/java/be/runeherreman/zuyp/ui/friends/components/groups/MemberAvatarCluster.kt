package be.runeherreman.zuyp.ui.friends.components.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.ui.friends.components.UserAvatar

/**
 * Overlapping stack of member avatars (a "face pile") with a "+N" bubble when
 * the group has more members than [maxVisible].
 */
@Composable
fun MemberAvatarCluster(
    members: List<User>,
    modifier: Modifier = Modifier,
    avatarSize: Dp = 34.dp,
    maxVisible: Int = 3
) {
    if (members.isEmpty()) return

    val visible = members.take(maxVisible)
    val overflow = members.size - visible.size
    // Each avatar slides left under the previous one to create the stacked look.
    val step = avatarSize * 0.62f
    val slots = visible.size + if (overflow > 0) 1 else 0
    // offset() doesn't reserve layout width, so size the row explicitly or the
    // trailing faces would draw over neighbouring content.
    val totalWidth = avatarSize + step * (slots - 1).coerceAtLeast(0)

    Box(modifier = modifier.size(width = totalWidth, height = avatarSize)) {
        visible.forEachIndexed { index, member ->
            RingedAvatar(
                modifier = Modifier.offset(x = step * index)
            ) {
                UserAvatar(user = member, size = avatarSize)
            }
        }
        if (overflow > 0) {
            RingedAvatar(
                modifier = Modifier.offset(x = step * visible.size)
            ) {
                OverflowBubble(count = overflow, size = avatarSize)
            }
        }
    }
}

/** White ring around an avatar so overlapping faces stay visually separated. */
@Composable
private fun RingedAvatar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
    ) {
        content()
    }
}

@Composable
private fun OverflowBubble(count: Int, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+$count",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}
