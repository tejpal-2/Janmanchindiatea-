package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.AppLanguage
import com.example.model.ChatMessageEntity
import com.example.model.ChatThreadEntity
import com.example.model.CommunityItemEntity
import com.example.model.PostEntity
import com.example.model.ReportEntity
import com.example.model.StoryEntity
import com.example.model.UserEntity
import com.example.ui.theme.ChaiAmber
import com.example.ui.theme.ChaiTerracotta

private data class HubFilter(val key: String, val hindi: String, val english: String)

private val hubFilters = listOf(
    HubFilter("STORIES", "स्टोरी", "Stories"),
    HubFilter("REELS", "रील्स", "Reels"),
    HubFilter("CHAT", "चैट", "Chat"),
    HubFilter("NEWS", "खबरें", "News"),
    HubFilter("JOBS", "नौकरी", "Jobs"),
    HubFilter("MEDIA", "मीडिया", "Media"),
    HubFilter("LIVE", "लाइव", "Live"),
    HubFilter("WRITING", "लेखन", "Writing"),
    HubFilter("CREATOR", "क्रिएटर", "Creator"),
    HubFilter("ADMIN", "एडमिन", "Admin"),
    HubFilter("REPORT", "रिपोर्ट", "Report"),
    HubFilter("BLOCK", "ब्लॉक", "Block")
)

@Composable
fun CommunityScreen(
    language: AppLanguage,
    currentUser: UserEntity?,
    stories: List<StoryEntity>,
    reels: List<PostEntity>,
    chats: List<ChatThreadEntity>,
    chatMessages: List<ChatMessageEntity>,
    selectedChatId: String?,
    communityItems: List<CommunityItemEntity>,
    reports: List<ReportEntity>,
    blockedUsers: List<UserEntity>,
    onStoryViewed: (String) -> Unit,
    onSelectChat: (String?) -> Unit,
    onSendMessage: (String) -> Unit,
    onOpenPost: (String) -> Unit,
    onOpenCreate: () -> Unit,
    onOpenModeration: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hindi = language == AppLanguage.HINDI
    var selectedFilter by remember { mutableStateOf("STORIES") }
    var messageText by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Explore, contentDescription = null, tint = ChaiTerracotta)
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (hindi) "जनमंच एक्सप्लोर" else "Janmanch Explore",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = ChaiTerracotta
                    )
                )
                Text(
                    text = if (hindi) "स्टोरी, रील्स, चैट और समुदाय सेवाएं" else "Stories, reels, chat and community services",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(hubFilters) { filter ->
                FilterChip(
                    selected = selectedFilter == filter.key,
                    onClick = {
                        selectedFilter = filter.key
                        if (filter.key != "CHAT") onSelectChat(null)
                    },
                    label = { Text(if (hindi) filter.hindi else filter.english) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ChaiTerracotta,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        when (selectedFilter) {
            "STORIES" -> StoriesContent(stories, hindi, onStoryViewed)
            "REELS" -> ReelsContent(reels, hindi, onOpenPost)
            "CHAT" -> ChatContent(
                chats = chats,
                messages = chatMessages,
                selectedChatId = selectedChatId,
                currentUserId = currentUser?.id,
                hindi = hindi,
                messageText = messageText,
                onMessageTextChange = { messageText = it },
                onSelectChat = onSelectChat,
                onSendMessage = {
                    onSendMessage(messageText)
                    messageText = ""
                }
            )
            else -> HubSectionContent(
                section = selectedFilter,
                items = communityItems.filter { it.section == selectedFilter },
                reportCount = reports.count { it.status == "PENDING" },
                blockedCount = blockedUsers.size,
                hindi = hindi,
                onOpenCreate = onOpenCreate,
                onOpenModeration = onOpenModeration,
                onOpenSettings = onOpenSettings
            )
        }
    }
}

@Composable
private fun StoriesContent(
    stories: List<StoryEntity>,
    hindi: Boolean,
    onStoryViewed: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = if (hindi) "24 घंटे की स्टोरीज़" else "24-hour Stories",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(stories, key = { it.id }) { story ->
                    Column(
                        modifier = Modifier.width(112.dp).clickable { onStoryViewed(story.id) },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = story.mediaUrl,
                            contentDescription = story.caption,
                            modifier = Modifier.size(94.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = story.authorName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (story.isViewed) {
                                if (hindi) "देखी गई" else "Viewed"
                            } else {
                                if (hindi) "नई स्टोरी" else "New story"
                            },
                            color = if (story.isViewed) MaterialTheme.colorScheme.onSurfaceVariant else ChaiTerracotta,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
        items(stories, key = { "${it.id}_card" }) { story ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column {
                    AsyncImage(
                        model = story.mediaUrl,
                        contentDescription = story.caption,
                        modifier = Modifier.fillMaxWidth().height(190.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = "${story.authorName}: ${story.caption}",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun ReelsContent(
    reels: List<PostEntity>,
    hindi: Boolean,
    onOpenPost: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = if (hindi) "रील्स और शॉर्ट वीडियो" else "Reels and short videos",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
        items(reels, key = { it.id }) { reel ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onOpenPost(reel.id) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = reel.imageUrl ?: reel.authorAvatarUrl,
                        contentDescription = reel.content,
                        modifier = Modifier.size(96.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("▶ ${reel.authorName}", fontWeight = FontWeight.Bold)
                        Text(
                            text = reel.content,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = if (hindi) "वीडियो खोलें" else "Open video",
                            color = ChaiTerracotta,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatContent(
    chats: List<ChatThreadEntity>,
    messages: List<ChatMessageEntity>,
    selectedChatId: String?,
    currentUserId: String?,
    hindi: Boolean,
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSelectChat: (String?) -> Unit,
    onSendMessage: () -> Unit
) {
    if (selectedChatId == null) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = if (hindi) "संदेश और बातचीत" else "Messages and conversations",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            items(chats, key = { it.id }) { chat ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onSelectChat(chat.id) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = chat.participantAvatarUrl,
                            contentDescription = chat.participantName,
                            modifier = Modifier.size(50.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(chat.participantName, fontWeight = FontWeight.Bold)
                            Text(chat.lastMessage, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        if (chat.unreadCount > 0) {
                            Surface(shape = CircleShape, color = ChaiTerracotta) {
                                Text("${chat.unreadCount}", color = Color.White, modifier = Modifier.padding(7.dp))
                            }
                        }
                    }
                }
            }
        }
    } else {
        val participant = chats.firstOrNull { it.id == selectedChatId }
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onSelectChat(null) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = if (hindi) "वापस" else "Back")
                }
                Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = ChaiTerracotta)
                Spacer(Modifier.width(8.dp))
                Text(participant?.participantName ?: (if (hindi) "चैट" else "Chat"), fontWeight = FontWeight.Bold)
            }
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (message.senderId == currentUserId) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (message.senderId == currentUserId) ChaiTerracotta else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = message.text,
                                color = if (message.senderId == currentUserId) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = onMessageTextChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text(if (hindi) "संदेश लिखें..." else "Write a message...") }
                )
                IconButton(
                    onClick = onSendMessage,
                    enabled = messageText.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, contentDescription = if (hindi) "भेजें" else "Send", tint = ChaiTerracotta)
                }
            }
        }
    }
}

@Composable
private fun HubSectionContent(
    section: String,
    items: List<CommunityItemEntity>,
    reportCount: Int,
    blockedCount: Int,
    hindi: Boolean,
    onOpenCreate: () -> Unit,
    onOpenModeration: () -> Unit,
    onOpenSettings: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            val title = items.firstOrNull()?.title ?: sectionTitle(section, hindi)
            Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }
        if (items.isEmpty()) {
            item {
                Text(
                    text = if (hindi) "यह सेक्शन जल्द ही अपडेट होगा।" else "This section will be updated soon.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        items(items, key = { it.id }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SectionIcon(section)
                        Spacer(Modifier.width(8.dp))
                        Text(item.title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(item.summary)
                    if (section == "ADMIN" || section == "REPORT" || section == "BLOCK") {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = when (section) {
                                "ADMIN" -> if (hindi) "$reportCount लंबित रिपोर्ट" else "$reportCount pending reports"
                                "REPORT" -> if (hindi) "रिपोर्ट कतार: $reportCount" else "Report queue: $reportCount"
                                else -> if (hindi) "$blockedCount खाते ब्लॉक हैं" else "$blockedCount blocked accounts"
                            },
                            color = ChaiTerracotta,
                            fontSize = 12.sp
                        )
                    }
                    if (item.actionLabel.isNotBlank()) {
                        Button(
                            onClick = {
                                when (section) {
                                    "WRITING", "CREATOR" -> onOpenCreate()
                                    "ADMIN", "REPORT" -> onOpenModeration()
                                    "BLOCK" -> onOpenSettings()
                                }
                            },
                            enabled = section in setOf("WRITING", "CREATOR", "ADMIN", "REPORT", "BLOCK"),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(item.actionLabel)
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionIcon(section: String) {
    Icon(
        imageVector = when (section) {
            "JOBS" -> Icons.Default.Work
            "LIVE" -> Icons.Default.LiveTv
            else -> Icons.Default.Explore
        },
        contentDescription = null,
        tint = ChaiAmber
    )
}

private fun sectionTitle(section: String, hindi: Boolean): String = when (section) {
    "NEWS" -> if (hindi) "समाचार" else "News"
    "JOBS" -> if (hindi) "नौकरी और अवसर" else "Jobs and opportunities"
    "MEDIA" -> if (hindi) "मीडिया लाइब्रेरी" else "Media library"
    "LIVE" -> if (hindi) "लाइव कार्यक्रम" else "Live events"
    "WRITING" -> if (hindi) "लेखन मंच" else "Writing space"
    "CREATOR" -> if (hindi) "क्रिएटर स्टूडियो" else "Creator studio"
    "ADMIN" -> if (hindi) "एडमिन और मॉडरेशन" else "Admin and moderation"
    "REPORT" -> if (hindi) "रिपोर्ट केंद्र" else "Report center"
    else -> if (hindi) "सुरक्षा और ब्लॉक" else "Safety and blocks"
}
