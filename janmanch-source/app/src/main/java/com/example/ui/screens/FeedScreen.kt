package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.i18n.Localization
import com.example.model.AppLanguage
import com.example.model.PostEntity
import com.example.model.UserEntity
import com.example.ui.components.CategoryFilterBar
import com.example.ui.components.PostCard
import com.example.ui.components.ReportPostDialog
import com.example.ui.theme.ChaiAmber
import com.example.ui.theme.ChaiCardamomBrown
import com.example.ui.theme.ChaiTerracotta

@Composable
fun FeedScreen(
    posts: List<PostEntity>,
    currentUser: UserEntity?,
    language: AppLanguage,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    onSaveClick: (String) -> Unit,
    onShareClick: (String) -> Unit,
    onReportSubmit: (String, String) -> Unit,
    onBlockUser: (String) -> Unit,
    onDeletePost: (String) -> Unit,
    onTogglePin: (String) -> Unit,
    onOpenPostDetail: (String) -> Unit,
    onNavigateCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    var reportingPostId by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        Pair("सभी", Localization.getString("category_all", language)),
        Pair("चाय और चर्चा", "☕ " + Localization.getString("category_chai", language)),
        Pair("राजनीति व समाज", "🏛️ " + Localization.getString("category_politics", language)),
        Pair("ख़बरें", "📰 " + Localization.getString("category_news", language)),
        Pair("संस्कृति व कला", "🎨 " + Localization.getString("category_culture", language)),
        Pair("किसान व ग्रामीण", "🌾 " + Localization.getString("category_rural", language)),
        Pair("युवा व तकनीक", "💡 " + Localization.getString("category_tech", language))
    )

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("feed_list"),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Community Hero Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(135.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.tea_leaf_banner_1788068367867),
                            contentDescription = "Chai Patti Community Banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.8f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "☕ जनमंच इंडिया टी",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = ChaiAmber,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "सच्ची राय, खुली चर्चा, हर नागरिक की आवाज़।",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White,
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // Category Horizontal Tabs
            item {
                CategoryFilterBar(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onSelectCategory = onSelectCategory
                )
            }

            // Quick Create Bar
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .clickable { onNavigateCreate() }
                        .testTag("quick_create_box"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
                            contentDescription = "My Avatar",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = Localization.getString("create_post_hint", language),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Write",
                            tint = ChaiTerracotta,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Posts
            if (posts.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "☕", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "इस श्रेणी में अभी कोई पोस्ट नहीं है।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(posts, key = { it.id }) { post ->
                    Box(modifier = Modifier.padding(horizontal = 14.dp)) {
                        PostCard(
                            post = post,
                            language = language,
                            currentUserId = currentUser?.id,
                            isAdmin = currentUser?.isAdmin ?: false,
                            onLikeClick = { onLikeClick(post.id) },
                            onCommentClick = { onCommentClick(post.id) },
                            onSaveClick = { onSaveClick(post.id) },
                            onShareClick = { onShareClick(post.id) },
                            onReportClick = { reportingPostId = post.id },
                            onBlockAuthorClick = { onBlockUser(post.authorId) },
                            onDeleteClick = { onDeletePost(post.id) },
                            onTogglePinClick = { onTogglePin(post.id) },
                            onPostClick = { onOpenPostDetail(post.id) }
                        )
                    }
                }
            }
        }

        // Floating Action Button to create post
        FloatingActionButton(
            onClick = onNavigateCreate,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
                .testTag("fab_create_post"),
            containerColor = ChaiTerracotta,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create Post")
        }

        // Report Dialog
        reportingPostId?.let { postId ->
            ReportPostDialog(
                language = language,
                onDismiss = { reportingPostId = null },
                onSubmit = { reason ->
                    onReportSubmit(postId, reason)
                    reportingPostId = null
                }
            )
        }
    }
}
