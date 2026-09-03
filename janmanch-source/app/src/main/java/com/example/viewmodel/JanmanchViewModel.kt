package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.JanmanchRepository
import com.example.model.AppLanguage
import com.example.model.CommentEntity
import com.example.model.NavTab
import com.example.model.NotificationEntity
import com.example.model.PostEntity
import com.example.model.ReportEntity
import com.example.model.UserEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class JanmanchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JanmanchRepository = JanmanchRepository(
        context = application,
        database = AppDatabase.getDatabase(application),
        coroutineScope = viewModelScope
    )

    // Global App Preferences
    private val _language = MutableStateFlow(AppLanguage.HINDI)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _currentTab = MutableStateFlow(NavTab.FEED)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    private val _selectedCategory = MutableStateFlow("सभी")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _adminModeEnabled = MutableStateFlow(false)
    val adminModeEnabled: StateFlow<Boolean> = _adminModeEnabled.asStateFlow()

    // Current User
    val currentUser: StateFlow<UserEntity?> = repository.currentUser

    // Posts & Feed reactively filtered by category
    val posts: StateFlow<List<PostEntity>> = _selectedCategory
        .flatMapLatest { cat -> repository.getPostsByCategory(cat) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All registered creators
    val users: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val blockedUsers: StateFlow<List<UserEntity>> = repository.getBlockedUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val followingIds: StateFlow<Set<String>> = currentUser
        .flatMapLatest { user -> if (user == null) flowOf(emptySet()) else repository.getFollowingIds(user.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val followerIds: StateFlow<Set<String>> = currentUser
        .flatMapLatest { user -> if (user == null) flowOf(emptySet()) else repository.getFollowerIds(user.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // Notifications
    val notifications: StateFlow<List<NotificationEntity>> = currentUser
        .flatMapLatest { user ->
            repository.getNotifications(user?.id ?: "user_me")
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reports for Admin
    val reports: StateFlow<List<ReportEntity>> = repository.getAllReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Post Detail View & Comments
    private val _selectedPostId = MutableStateFlow<String?>(null)
    val selectedPostId: StateFlow<String?> = _selectedPostId.asStateFlow()

    val selectedPost: StateFlow<PostEntity?> = _selectedPostId
        .flatMapLatest { id ->
            if (id != null) repository.getPostById(id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedPostComments: StateFlow<List<CommentEntity>> = _selectedPostId
        .flatMapLatest { id ->
            if (id != null) repository.getCommentsForPost(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResultsPosts: StateFlow<List<PostEntity>> = _searchQuery
        .flatMapLatest { q ->
            if (q.isBlank()) flowOf(emptyList()) else repository.searchPosts(q)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchResultsUsers: StateFlow<List<UserEntity>> = _searchQuery
        .flatMapLatest { q ->
            if (q.isBlank()) flowOf(emptyList()) else repository.searchUsers(q)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Profile Subtabs
    private val _profileTab = MutableStateFlow(0) // 0=My Posts, 1=Liked, 2=Saved
    val profileTab: StateFlow<Int> = _profileTab.asStateFlow()

    val myPosts: StateFlow<List<PostEntity>> = currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getPostsByAuthor(user.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val likedPosts: StateFlow<List<PostEntity>> = repository.getLikedPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedPosts: StateFlow<List<PostEntity>> = repository.getSavedPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.HINDI) AppLanguage.ENGLISH else AppLanguage.HINDI
    }

    fun setDarkTheme(dark: Boolean) {
        _isDarkTheme.value = dark
    }

    fun setTab(tab: NavTab) {
        _currentTab.value = tab
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setProfileTab(tabIndex: Int) {
        _profileTab.value = tabIndex
    }

    fun showSnackbar(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    // Auth actions
    fun login(email: String, pass: String) {
        viewModelScope.launch {
            val result = repository.login(email, pass)
            if (result.isSuccess) {
                showSnackbar(if (_language.value == AppLanguage.HINDI) "लॉग इन सफल हुआ!" else "Login successful!")
                _currentTab.value = NavTab.FEED
            } else {
                showSnackbar("लॉग इन विफल रहा")
            }
        }
    }

    fun register(fullName: String, username: String, email: String, pass: String, location: String) {
        viewModelScope.launch {
            val result = repository.register(fullName, username, email, pass, location)
            if (result.isSuccess) {
                showSnackbar(if (_language.value == AppLanguage.HINDI) "खाता सफलतापूर्वक बन गया!" else "Account created successfully!")
                _currentTab.value = NavTab.FEED
            } else {
                showSnackbar("पंजीकरण विफल रहा")
            }
        }
    }

    fun continueAsGuest() {
        repository.continueAsGuest()
        _currentTab.value = NavTab.FEED
        showSnackbar(if (_language.value == AppLanguage.HINDI) "अतिथि मोड में स्वागत है" else "Welcome in Guest Mode")
    }

    fun logout() {
        repository.logout()
        _currentTab.value = NavTab.FEED
        showSnackbar(if (_language.value == AppLanguage.HINDI) "लॉग आउट कर दिया गया है" else "Logged out successfully")
    }

    fun updateProfile(fullName: String, bio: String, location: String, avatarUrl: String) {
        viewModelScope.launch {
            repository.updateProfile(fullName, bio, location, avatarUrl)
            showSnackbar(if (_language.value == AppLanguage.HINDI) "प्रोफ़ाइल अपडेट हो गई!" else "Profile updated!")
        }
    }

    // Post actions
    fun createPost(
        content: String,
        category: String,
        chaiMood: String,
        imageUrl: String?,
        videoUrl: String?,
        videoDuration: String?
    ) {
        viewModelScope.launch {
            val res = repository.createPost(content, category, chaiMood, imageUrl, videoUrl, videoDuration)
            if (res.isSuccess) {
                showSnackbar(if (_language.value == AppLanguage.HINDI) "चर्चा प्रकाशित हो गई! ☕" else "Charcha published! ☕")
                _currentTab.value = NavTab.FEED
            } else {
                showSnackbar("कृपया पहले लॉग इन करें")
            }
        }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            repository.toggleLike(postId)
        }
    }

    fun toggleSave(postId: String) {
        viewModelScope.launch {
            repository.toggleSave(postId)
            showSnackbar(if (_language.value == AppLanguage.HINDI) "सहेजने की स्थिति बदली गई" else "Bookmark updated")
        }
    }

    fun sharePost(postId: String) {
        viewModelScope.launch {
            repository.incrementShare(postId)
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            repository.deletePost(postId)
            if (_selectedPostId.value == postId) {
                _selectedPostId.value = null
            }
            showSnackbar(if (_language.value == AppLanguage.HINDI) "पोस्ट हटा दी गई" else "Post deleted")
        }
    }

    fun togglePinPost(postId: String) {
        viewModelScope.launch {
            repository.togglePinPost(postId)
            showSnackbar(if (_language.value == AppLanguage.HINDI) "पिन स्थिति बदली गई" else "Pin status updated")
        }
    }

    fun openPostDetail(postId: String) {
        _selectedPostId.value = postId
    }

    fun closePostDetail() {
        _selectedPostId.value = null
    }

    fun addComment(postId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val res = repository.addComment(postId, text)
            if (res.isSuccess) {
                showSnackbar(if (_language.value == AppLanguage.HINDI) "टिप्पणी पोस्ट हो गई!" else "Comment posted!")
            } else {
                showSnackbar("टिप्पणी जोड़ने के लिए लॉग इन करें")
            }
        }
    }

    // Follow & Network
    fun toggleFollow(userId: String) {
        viewModelScope.launch {
            repository.toggleFollow(userId)
        }
    }

    fun blockUser(userId: String) {
        viewModelScope.launch {
            repository.blockUser(userId)
            showSnackbar(if (_language.value == AppLanguage.HINDI) "यूज़र को ब्लॉक कर दिया गया" else "User blocked")
        }
    }

    fun unblockUser(userId: String) {
        viewModelScope.launch {
            repository.unblockUser(userId)
            showSnackbar(if (_language.value == AppLanguage.HINDI) "यूज़र को अनब्लॉक किया गया" else "User unblocked")
        }
    }

    // Search
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Notifications
    fun markNotificationsRead() {
        viewModelScope.launch {
            repository.markNotificationsRead()
        }
    }

    fun clearNotifications() {
        viewModelScope.launch {
            repository.clearNotifications()
            showSnackbar(if (_language.value == AppLanguage.HINDI) "सभी सूचनाएं साफ की गईं" else "Notifications cleared")
        }
    }

    // Reports & Admin
    fun reportPost(postId: String, reason: String) {
        viewModelScope.launch {
            repository.reportPost(postId, reason)
            showSnackbar(if (_language.value == AppLanguage.HINDI) "शिकायत दर्ज कर ली गई है" else "Report submitted for review")
        }
    }

    fun toggleAdminMode() {
        _adminModeEnabled.value = !_adminModeEnabled.value
    }

    fun resolveReport(reportId: String, hidePost: Boolean, postId: String) {
        viewModelScope.launch {
            repository.resolveReport(reportId, hidePost)
            if (hidePost) {
                repository.setPostHidden(postId, true)
                showSnackbar(if (_language.value == AppLanguage.HINDI) "पोस्ट को हटा दिया गया है" else "Post has been hidden/moderated")
            } else {
                showSnackbar(if (_language.value == AppLanguage.HINDI) "रिपोर्ट खारिज की गई" else "Report dismissed")
            }
        }
    }
}
