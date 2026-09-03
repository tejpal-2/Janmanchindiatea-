package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.i18n.Localization
import com.example.model.AppLanguage
import com.example.model.PostEntity
import com.example.model.UserEntity
import com.example.ui.components.PostCard
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.ChaiAmber
import com.example.ui.theme.ChaiCardamomBrown
import com.example.ui.theme.ChaiCream
import com.example.ui.theme.ChaiTerracotta
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    currentUser: UserEntity?,
    myPosts: List<PostEntity>,
    likedPosts: List<PostEntity>,
    savedPosts: List<PostEntity>,
    currentSubtab: Int,
    onSubtabSelect: (Int) -> Unit,
    language: AppLanguage,
    onUpdateProfile: (String, String, String, String) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenPostDetail: (String) -> Unit,
    onLikePost: (String) -> Unit,
    onCommentPost: (String) -> Unit,
    onSavePost: (String) -> Unit,
    onSharePost: (String) -> Unit,
    onDeletePost: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditingProfile by remember { mutableStateOf(false) }

    var editName by remember(currentUser) { mutableStateOf(currentUser?.fullName ?: "") }
    var editBio by remember(currentUser) { mutableStateOf(currentUser?.bio ?: "") }
    var editLocation by remember(currentUser) { mutableStateOf(currentUser?.location ?: "") }
    var editAvatar by remember(currentUser) { mutableStateOf(currentUser?.avatarUrl ?: "") }

    val activeList = when (currentSubtab) {
        0 -> myPosts
        1 -> likedPosts
        else -> savedPosts
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen_list"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Banner & Header
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Banner
                AsyncImage(
                    model = currentUser?.bannerUrl?.ifBlank { "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=800" },
                    contentDescription = "Profile Banner",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(ChaiTerracotta.copy(alpha = 0.3f)),
                    contentScale = ContentScale.Crop
                )

                // Settings icon button top right
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        .testTag("profile_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }

                // Avatar overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 80.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AsyncImage(
                        model = currentUser?.avatarUrl?.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300" },
                        contentDescription = "User Avatar",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    OutlinedButton(
                        onClick = { isEditingProfile = true },
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("edit_profile_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = Localization.getString("edit_profile", language),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Profile Info Details
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = currentUser?.fullName ?: "जनमंच सदस्य",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    if (currentUser?.isVerified == true) {
                        Spacer(modifier = Modifier.width(6.dp))
                        VerifiedBadge()
                    }
                    if (currentUser?.isAdmin == true) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ChaiTerracotta
                        ) {
                            Text(
                                text = "ADMIN",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "@${currentUser?.username ?: "user"}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                if (!currentUser?.bio.isNullOrBlank()) {
                    Text(
                        text = currentUser!!.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentUser?.location ?: "भारत",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "शामिल: ${SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date(currentUser?.joinedDate ?: System.currentTimeMillis()))}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Cards
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${myPosts.size}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = Localization.getString("stats_posts", language),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${currentUser?.followersCount ?: 0}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = Localization.getString("stats_followers", language),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${currentUser?.followingCount ?: 0}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = Localization.getString("stats_following", language),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${currentUser?.chaiPoints ?: 100}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = ChaiTerracotta
                            )
                            Text(
                                text = Localization.getString("stats_chai", language),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Subtabs: My Posts / Liked / Saved
        item {
            Spacer(modifier = Modifier.height(8.dp))
            TabRow(
                selectedTabIndex = currentSubtab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = currentSubtab == 0,
                    onClick = { onSubtabSelect(0) },
                    text = {
                        Text(
                            Localization.getString("tab_my_posts", language),
                            fontWeight = if (currentSubtab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.testTag("tab_profile_myposts")
                )
                Tab(
                    selected = currentSubtab == 1,
                    onClick = { onSubtabSelect(1) },
                    text = {
                        Text(
                            Localization.getString("tab_liked_posts", language),
                            fontWeight = if (currentSubtab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.testTag("tab_profile_liked")
                )
                Tab(
                    selected = currentSubtab == 2,
                    onClick = { onSubtabSelect(2) },
                    text = {
                        Text(
                            Localization.getString("tab_saved_posts", language),
                            fontWeight = if (currentSubtab == 2) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.testTag("tab_profile_saved")
                )
            }
        }

        // Tab Posts List
        if (activeList.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "📭", fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "यहाँ कोई पोस्ट नहीं है।",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(activeList, key = { it.id }) { post ->
                Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                    PostCard(
                        post = post,
                        language = language,
                        currentUserId = currentUser?.id,
                        isAdmin = currentUser?.isAdmin ?: false,
                        onLikeClick = { onLikePost(post.id) },
                        onCommentClick = { onCommentPost(post.id) },
                        onSaveClick = { onSavePost(post.id) },
                        onShareClick = { onSharePost(post.id) },
                        onReportClick = {},
                        onBlockAuthorClick = {},
                        onDeleteClick = { onDeletePost(post.id) },
                        onTogglePinClick = {},
                        onPostClick = { onOpenPostDetail(post.id) }
                    )
                }
            }
        }
    }

    // Edit Profile Modal Dialog
    if (isEditingProfile) {
        AlertDialog(
            onDismissRequest = { isEditingProfile = false },
            title = { Text(Localization.getString("edit_profile", language), fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text(Localization.getString("name_label", language)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text(Localization.getString("bio_label", language)) },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editLocation,
                        onValueChange = { editLocation = it },
                        label = { Text(Localization.getString("location_label", language)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editAvatar,
                        onValueChange = { editAvatar = it },
                        label = { Text("अवतार फोटो URL (Avatar URL)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateProfile(editName, editBio, editLocation, editAvatar)
                        isEditingProfile = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChaiTerracotta)
                ) {
                    Text(Localization.getString("save_profile", language))
                }
            },
            dismissButton = {
                TextButton(onClick = { isEditingProfile = false }) {
                    Text(Localization.getString("cancel", language))
                }
            }
        )
    }
}
