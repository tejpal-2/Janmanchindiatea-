package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import com.example.model.NavTab
import com.example.ui.components.JanmanchBottomNavigation
import com.example.ui.components.JanmanchTopAppBar
import com.example.ui.screens.AdminModerationScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CreatePostScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.NetworkScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.PostDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.JanmanchTheme
import com.example.viewmodel.JanmanchViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: JanmanchViewModel = viewModel()
            JanmanchApp(viewModel)
        }
    }
}

@Composable
fun JanmanchApp(viewModel: JanmanchViewModel) {
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
    val currentUser by viewModel.currentUser.collectAsState()
    LaunchedEffect(currentUser != null) {
        if (currentUser != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val language by viewModel.language.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val users by viewModel.users.collectAsState()
    val followingIds by viewModel.followingIds.collectAsState()
    val followerIds by viewModel.followerIds.collectAsState()
    val blockedUsers by viewModel.blockedUsers.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val reports by viewModel.reports.collectAsState()
    val selectedPostId by viewModel.selectedPostId.collectAsState()
    val selectedPost by viewModel.selectedPost.collectAsState()
    val selectedPostComments by viewModel.selectedPostComments.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResultsPosts by viewModel.searchResultsPosts.collectAsState()
    val searchResultsUsers by viewModel.searchResultsUsers.collectAsState()
    val profileTab by viewModel.profileTab.collectAsState()
    val myPosts by viewModel.myPosts.collectAsState()
    val likedPosts by viewModel.likedPosts.collectAsState()
    val savedPosts by viewModel.savedPosts.collectAsState()
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()
    val adminModeEnabled by viewModel.adminModeEnabled.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    JanmanchTheme(darkTheme = isDarkTheme) {
        // Back navigation handling for Android system back button / gesture
        BackHandler(enabled = selectedPostId != null || currentTab != NavTab.FEED) {
            if (selectedPostId != null) {
                viewModel.closePostDetail()
            } else if (currentTab != NavTab.FEED) {
                viewModel.setTab(NavTab.FEED)
            }
        }

        // If user is not logged in and not explicitly in guest mode
        if (currentUser == null) {
            AuthScreen(
                language = language,
                onLogin = { email, pass -> viewModel.login(email, pass) },
                onRegister = { name, username, email, pass, loc -> viewModel.register(name, username, email, pass, loc) },
                onGuestLogin = { viewModel.continueAsGuest() },
                onToggleLanguage = { viewModel.toggleLanguage() }
            )
            return@JanmanchTheme
        }

        // Post Detail View Overlay
        if (selectedPostId != null && selectedPost != null) {
            PostDetailScreen(
                post = selectedPost!!,
                comments = selectedPostComments,
                currentUser = currentUser,
                language = language,
                onBack = { viewModel.closePostDetail() },
                onLikePost = { viewModel.toggleLike(it) },
                onSavePost = { viewModel.toggleSave(it) },
                onSharePost = { viewModel.sharePost(it); viewModel.showSnackbar("लिंक कॉपी हो गया!") },
                onReportPost = { viewModel.reportPost(it, "अनुचित सामग्री") },
                onDeletePost = { viewModel.deletePost(it) },
                onTogglePin = { viewModel.togglePinPost(it) },
                onAddComment = { id, text -> viewModel.addComment(id, text) }
            )
            return@JanmanchTheme
        }

        // Dedicated sub-screens (Create Post, Settings, Admin)
        when (currentTab) {
            NavTab.CREATE -> {
                CreatePostScreen(
                    currentUser = currentUser,
                    language = language,
                    onBack = { viewModel.setTab(NavTab.FEED) },
                    onSubmitPost = { content, cat, mood, img, vid, dur ->
                        viewModel.createPost(content, cat, mood, img, vid, dur)
                    }
                )
                return@JanmanchTheme
            }
            NavTab.SETTINGS -> {
                SettingsScreen(
                    language = language,
                    isDarkTheme = isDarkTheme,
                    adminModeEnabled = adminModeEnabled,
                    blockedUsers = blockedUsers,
                    currentUser = currentUser,
                    onBack = { viewModel.setTab(NavTab.FEED) },
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onToggleTheme = { viewModel.setDarkTheme(!isDarkTheme) },
                    onToggleAdminMode = { viewModel.toggleAdminMode() },
                    onOpenModeration = { viewModel.setTab(NavTab.ADMIN_MODERATION) },
                    onUnblockUser = { viewModel.unblockUser(it) },
                    onLogout = { viewModel.logout() }
                )
                return@JanmanchTheme
            }
            NavTab.ADMIN_MODERATION -> {
                AdminModerationScreen(
                    reports = reports,
                    language = language,
                    onBack = { viewModel.setTab(NavTab.SETTINGS) },
                    onResolveReport = { repId, hide, postId -> viewModel.resolveReport(repId, hide, postId) }
                )
                return@JanmanchTheme
            }
            else -> {
                // Main Tabbed Navigation Scaffold
                Scaffold(
                    topBar = {
                        JanmanchTopAppBar(
                            language = language,
                            isDarkTheme = isDarkTheme,
                            unreadNotificationCount = notifications.count { !it.isRead },
                            chaiPoints = currentUser?.chaiPoints ?: 100,
                            onToggleLanguage = { viewModel.toggleLanguage() },
                            onToggleTheme = { viewModel.setDarkTheme(!isDarkTheme) },
                            onNotificationsClick = { viewModel.setTab(NavTab.NOTIFICATIONS) },
                            onSettingsClick = { viewModel.setTab(NavTab.SETTINGS) }
                        )
                    },
                    bottomBar = {
                        JanmanchBottomNavigation(
                            currentTab = currentTab,
                            language = language,
                            onTabSelected = { tab -> viewModel.setTab(tab) }
                        )
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "tab_transition"
                        ) { targetTab ->
                            when (targetTab) {
                                NavTab.FEED -> FeedScreen(
                                    posts = posts,
                                    currentUser = currentUser,
                                    language = language,
                                    selectedCategory = selectedCategory,
                                    onSelectCategory = { viewModel.setSelectedCategory(it) },
                                    onLikeClick = { viewModel.toggleLike(it) },
                                    onCommentClick = { viewModel.openPostDetail(it) },
                                    onSaveClick = { viewModel.toggleSave(it) },
                                    onShareClick = { viewModel.sharePost(it); viewModel.showSnackbar("लिंक कॉपी हो गया!") },
                                    onReportSubmit = { postId, reason -> viewModel.reportPost(postId, reason) },
                                    onBlockUser = { viewModel.blockUser(it) },
                                    onDeletePost = { viewModel.deletePost(it) },
                                    onTogglePin = { viewModel.togglePinPost(it) },
                                    onOpenPostDetail = { viewModel.openPostDetail(it) },
                                    onNavigateCreate = { viewModel.setTab(NavTab.CREATE) }
                                )
                                NavTab.SEARCH -> SearchScreen(
                                    searchQuery = searchQuery,
                                    onQueryChange = { viewModel.setSearchQuery(it) },
                                    postsResults = searchResultsPosts,
                                    usersResults = searchResultsUsers,
                                    popularUsers = users,
                                    currentUser = currentUser,
                                    language = language,
                                    onFollowToggle = { viewModel.toggleFollow(it) },
                                    onOpenPostDetail = { viewModel.openPostDetail(it) },
                                    onLikePost = { viewModel.toggleLike(it) },
                                    onCommentPost = { viewModel.openPostDetail(it) },
                                    onSavePost = { viewModel.toggleSave(it) },
                                    onSharePost = { viewModel.sharePost(it); viewModel.showSnackbar("लिंक साझा किया गया") },
                                    onReportPost = { viewModel.reportPost(it, "अनुचित पोस्ट") }
                                )
                                NavTab.NETWORK -> NetworkScreen(
                                    users = users,
                                    currentUser = currentUser,
                                    followingIds = followingIds,
                                    followerIds = followerIds,
                                    language = language,
                                    onFollowToggle = { viewModel.toggleFollow(it) }
                                )
                                NavTab.NOTIFICATIONS -> NotificationsScreen(
                                    notifications = notifications,
                                    language = language,
                                    onMarkAllRead = { viewModel.markNotificationsRead() },
                                    onClearAll = { viewModel.clearNotifications() },
                                    onNotificationClick = { postId ->
                                        if (postId != null) viewModel.openPostDetail(postId)
                                    }
                                )
                                NavTab.PROFILE -> ProfileScreen(
                                    currentUser = currentUser,
                                    myPosts = myPosts,
                                    likedPosts = likedPosts,
                                    savedPosts = savedPosts,
                                    currentSubtab = profileTab,
                                    onSubtabSelect = { viewModel.setProfileTab(it) },
                                    language = language,
                                    onUpdateProfile = { name, bio, loc, avatar ->
                                        viewModel.updateProfile(name, bio, loc, avatar)
                                    },
                                    onOpenSettings = { viewModel.setTab(NavTab.SETTINGS) },
                                    onOpenPostDetail = { viewModel.openPostDetail(it) },
                                    onLikePost = { viewModel.toggleLike(it) },
                                    onCommentPost = { viewModel.openPostDetail(it) },
                                    onSavePost = { viewModel.toggleSave(it) },
                                    onSharePost = { viewModel.sharePost(it); viewModel.showSnackbar("लिंक साझा किया गया") },
                                    onDeletePost = { viewModel.deletePost(it) }
                                )
                                else -> FeedScreen(
                                    posts = posts,
                                    currentUser = currentUser,
                                    language = language,
                                    selectedCategory = selectedCategory,
                                    onSelectCategory = { viewModel.setSelectedCategory(it) },
                                    onLikeClick = { viewModel.toggleLike(it) },
                                    onCommentClick = { viewModel.openPostDetail(it) },
                                    onSaveClick = { viewModel.toggleSave(it) },
                                    onShareClick = { viewModel.sharePost(it); viewModel.showSnackbar("लिंक कॉपी हो गया!") },
                                    onReportSubmit = { postId, reason -> viewModel.reportPost(postId, reason) },
                                    onBlockUser = { viewModel.blockUser(it) },
                                    onDeletePost = { viewModel.deletePost(it) },
                                    onTogglePin = { viewModel.togglePinPost(it) },
                                    onOpenPostDetail = { viewModel.openPostDetail(it) },
                                    onNavigateCreate = { viewModel.setTab(NavTab.CREATE) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
