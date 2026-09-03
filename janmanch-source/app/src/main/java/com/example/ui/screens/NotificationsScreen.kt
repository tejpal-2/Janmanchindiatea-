package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.i18n.Localization
import com.example.model.AppLanguage
import com.example.model.NotificationEntity
import com.example.ui.theme.ChaiAmber
import com.example.ui.theme.ChaiTerracotta
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationsScreen(
    notifications: List<NotificationEntity>,
    language: AppLanguage,
    onMarkAllRead: () -> Unit,
    onClearAll: () -> Unit,
    onNotificationClick: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showOnlyUnread by remember { mutableStateOf(false) }

    val filtered = if (showOnlyUnread) notifications.filter { !it.isRead } else notifications

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = Localization.getString("notifications_title", language),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = ChaiTerracotta
                )
            )

            Row {
                IconButton(onClick = onMarkAllRead, modifier = Modifier.testTag("mark_read_button")) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Mark all read",
                        tint = ChaiTerracotta
                    )
                }
                IconButton(onClick = onClearAll, modifier = Modifier.testTag("clear_notifs_button")) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear all",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Filter chips
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = !showOnlyUnread,
                onClick = { showOnlyUnread = false },
                label = { Text(Localization.getString("all_notifications", language), fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ChaiTerracotta, selectedLabelColor = Color.White)
            )
            FilterChip(
                selected = showOnlyUnread,
                onClick = { showOnlyUnread = true },
                label = { Text(Localization.getString("unread_notifications", language), fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ChaiTerracotta, selectedLabelColor = Color.White)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (filtered.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "🔔", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = Localization.getString("no_notifications", language),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("notifications_list"),
                contentPadding = PaddingValues(14.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered, key = { it.id }) { notif ->
                    val iconVector = when (notif.type) {
                        "LIKE" -> Icons.Default.Favorite
                        "COMMENT" -> Icons.Default.ChatBubble
                        "FOLLOW" -> Icons.Default.PersonAdd
                        else -> Icons.Default.Campaign
                    }
                    val iconColor = when (notif.type) {
                        "LIKE" -> Color(0xFFDC2626)
                        "COMMENT" -> Color(0xFF2563EB)
                        "FOLLOW" -> Color(0xFF16A34A)
                        else -> ChaiAmber
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNotificationClick(notif.relatedPostId) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (!notif.isRead) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box {
                                AsyncImage(
                                    model = notif.senderAvatarUrl.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200" },
                                    contentDescription = notif.senderName,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = iconColor,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .align(Alignment.BottomEnd)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = iconVector,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(11.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = notif.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = notif.message,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                Text(
                                    text = formatTimestamp(notif.timestamp),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = diff / (1000 * 60)
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "अभी"
        minutes < 60 -> "${minutes} मिनट पहले"
        hours < 24 -> "${hours} घंटे पहले"
        else -> SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(timestamp))
    }
}
