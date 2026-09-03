package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.i18n.Localization
import com.example.model.AppLanguage
import com.example.model.PostEntity
import com.example.ui.theme.ChaiAmber
import com.example.ui.theme.ChaiCardamomBrown
import com.example.ui.theme.ChaiTerracotta
import com.example.ui.theme.TeaLeafGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PostCard(
    post: PostEntity,
    language: AppLanguage,
    currentUserId: String?,
    isAdmin: Boolean,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    onReportClick: () -> Unit,
    onBlockAuthorClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onTogglePinClick: () -> Unit,
    onPostClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var isExpandedText by remember { mutableStateOf(false) }
    var isVideoPlaying by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPostClick() }
            .testTag("post_card_${post.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Pinned indicator
            if (post.isPinned) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        tint = ChaiTerracotta,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = Localization.getString("post_pinned", language),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ChaiTerracotta
                        )
                    )
                }
            }

            // Header: Avatar, Name, Category Pill, Chai Mood, Menu
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar
                AsyncImage(
                    model = post.authorAvatarUrl.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200" },
                    contentDescription = post.authorName,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.authorName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (post.authorIsVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            VerifiedBadge()
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "@${post.authorUsername}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Text(
                            text = formatTimestamp(post.createdAt),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Category Tag
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text(
                        text = post.category,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                // Options Menu
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(Localization.getString("report", language)) },
                            onClick = {
                                menuExpanded = false
                                onReportClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(Localization.getString("block_user", language)) },
                            onClick = {
                                menuExpanded = false
                                onBlockAuthorClick()
                            }
                        )
                        if (isAdmin) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        Localization.getString(
                                            if (post.isPinned) "unpin_post" else "pin_post",
                                            language
                                        )
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onTogglePinClick()
                                }
                            )
                        }
                        if (currentUserId == post.authorId || isAdmin) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        Localization.getString("delete_post", language),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onDeleteClick()
                                }
                            )
                        }
                    }
                }
            }

            // Chai Mood Tag
            if (post.chaiMood.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = post.chaiMood,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post Text Content
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.5.sp,
                    lineHeight = 21.sp
                ),
                maxLines = if (isExpandedText) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis
            )

            if (post.content.length > 160) {
                Text(
                    text = Localization.getString(if (isExpandedText) "read_less" else "read_more", language),
                    color = ChaiTerracotta,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .clickable { isExpandedText = !isExpandedText }
                        .padding(top = 4.dp)
                )
            }

            // Image Content
            if (!post.imageUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "Post Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
            }

            // Video Content Card
            if (!post.videoUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF2E1C12), Color(0xFF140D09))
                            )
                        )
                        .clickable { isVideoPlaying = !isVideoPlaying },
                    contentAlignment = Alignment.Center
                ) {
                    if (!isVideoPlaying) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = ChaiTerracotta,
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play Video",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "वीडियो देखें (${post.videoDuration ?: "02:30"})",
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    } else {
                        AndroidView(
                            factory = { viewContext ->
                                VideoView(viewContext).apply {
                                    setVideoURI(Uri.parse(post.videoUrl))
                                    setOnPreparedListener { player ->
                                        player.isLooping = true
                                        start()
                                    }
                                }
                            },
                            update = { videoView ->
                                if (isVideoPlaying && !videoView.isPlaying) videoView.start()
                                if (!isVideoPlaying && videoView.isPlaying) videoView.pause()
                            },
                            modifier = Modifier.fillMaxWidth().height(190.dp)
                        )
                    }

                    // Duration Badge top right
                    post.videoDuration?.let { dur ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.Black.copy(alpha = 0.7f),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = dur,
                                color = Color.White,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row (Like, Comment, Share, Save)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Like Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onLikeClick() }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .testTag("like_button_${post.id}")
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLiked) Color(0xFFDC2626) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.likesCount}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (post.isLiked) FontWeight.Bold else FontWeight.Normal,
                            color = if (post.isLiked) Color(0xFFDC2626) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                // Comment Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onCommentClick() }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .testTag("comment_button_${post.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comments",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.commentsCount}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                // Share Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            onShareClick()
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "☕ Janmanch India Tea: ${post.content.take(100)}...")
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "चर्चा साझा करें")
                            try {
                                context.startActivity(shareIntent)
                            } catch (e: Exception) {
                                // The share counter is already recorded; unsupported targets simply keep the feed usable.
                            }
                        }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .testTag("share_button_${post.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.sharesCount}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                // Save Bookmark Button
                IconButton(
                    onClick = onSaveClick,
                    modifier = Modifier.size(32.dp).testTag("save_button_${post.id}")
                ) {
                    Icon(
                        imageVector = if (post.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save",
                        tint = if (post.isSaved) ChaiAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
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
        minutes < 1 -> "अभी (Just now)"
        minutes < 60 -> "${minutes}m पहले"
        hours < 24 -> "${hours}h पहले"
        days < 7 -> "${days}d पहले"
        else -> SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(timestamp))
    }
}
