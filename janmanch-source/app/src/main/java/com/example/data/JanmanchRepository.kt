package com.example.data

import com.example.model.CommentEntity
import com.example.model.FollowEntity
import com.example.model.NotificationEntity
import com.example.model.PostEntity
import com.example.model.ReportEntity
import com.example.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class JanmanchRepository(
    private val context: android.content.Context,
    private val database: AppDatabase,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val userDao = database.userDao()
    private val postDao = database.postDao()
    private val commentDao = database.commentDao()
    private val notificationDao = database.notificationDao()
    private val followDao = database.followDao()
    private val reportDao = database.reportDao()

    private val firestoreService = FirestoreService(context)

    private val _currentUser = MutableStateFlow<UserEntity?>(SampleSeedData.currentUser)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    init {
        coroutineScope.launch {
            seedInitialDataIfNeeded()
            syncRemoteFirestorePosts()
        }
    }

    private suspend fun syncRemoteFirestorePosts() {
        try {
            firestoreService.listenToPosts().collect { remotePosts ->
                if (remotePosts.isNotEmpty()) {
                    postDao.insertPosts(remotePosts)
                }
            }
        } catch (e: Exception) {
            // Graceful fallback to local Room data
        }
    }

    private suspend fun seedInitialDataIfNeeded() = withContext(Dispatchers.IO) {
        val existing = userDao.getUserByEmail(SampleSeedData.currentUser.email)
        if (existing == null) {
            userDao.insertUsers(SampleSeedData.initialUsers)
            postDao.insertPosts(SampleSeedData.initialPosts)
            commentDao.insertComments(SampleSeedData.initialComments)
            notificationDao.insertNotifications(SampleSeedData.initialNotifications)
            
            // Seed sample follows
            followDao.insertFollow(FollowEntity(followerId = "user_me", followedId = "user_1"))
            followDao.insertFollow(FollowEntity(followerId = "user_me", followedId = "user_2"))
            followDao.insertFollow(FollowEntity(followerId = "user_3", followedId = "user_me"))
        }
    }

    // Auth
    suspend fun login(email: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByEmail(email.trim())
        if (user != null) {
            _currentUser.value = user
            Result.success(user)
        } else {
            // If doesn't exist, create a new demo user with this email
            val newUser = UserEntity(
                id = "user_" + UUID.randomUUID().toString().take(8),
                username = email.substringBefore("@").replace(".", "_"),
                fullName = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                email = email.trim(),
                passwordHash = password,
                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80",
                bio = "जनमंच इंडिया टी परिवार का सदस्य ☕",
                location = "भारत (India)",
                followersCount = 0,
                followingCount = 0,
                chaiPoints = 100,
                isVerified = false,
                isAdmin = false
            )
            userDao.insertUser(newUser)
            _currentUser.value = newUser
            Result.success(newUser)
        }
    }

    suspend fun register(
        fullName: String,
        username: String,
        email: String,
        password: String,
        location: String
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim()
        val cleanUsername = username.trim().removePrefix("@")
        val newUser = UserEntity(
            id = "user_" + UUID.randomUUID().toString().take(8),
            username = cleanUsername,
            fullName = fullName.trim(),
            email = cleanEmail,
            passwordHash = password,
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80",
            bio = "जनमंच इंडिया टी सदस्य ☕",
            location = if (location.isNotBlank()) location.trim() else "भारत",
            followersCount = 0,
            followingCount = 0,
            chaiPoints = 150,
            isVerified = false,
            isAdmin = false
        )
        userDao.insertUser(newUser)
        _currentUser.value = newUser
        Result.success(newUser)
    }

    fun continueAsGuest() {
        _currentUser.value = SampleSeedData.currentUser
    }

    fun logout() {
        _currentUser.value = null
    }

    suspend fun updateProfile(
        fullName: String,
        bio: String,
        location: String,
        avatarUrl: String
    ) = withContext(Dispatchers.IO) {
        val current = _currentUser.value ?: return@withContext
        val updated = current.copy(
            fullName = fullName.trim(),
            bio = bio.trim(),
            location = location.trim(),
            avatarUrl = avatarUrl.ifBlank { current.avatarUrl }
        )
        userDao.updateUser(updated)
        _currentUser.value = updated
    }

    // Posts & Feed
    fun getAllPosts(): Flow<List<PostEntity>> = postDao.getAllPosts()

    fun getPostsByCategory(category: String): Flow<List<PostEntity>> {
        return if (category == "सभी" || category == "All") {
            postDao.getAllPosts()
        } else {
            postDao.getPostsByCategory(category)
        }
    }

    fun getPostById(id: String): Flow<PostEntity?> = postDao.getPostById(id)

    fun getPostsByAuthor(authorId: String): Flow<List<PostEntity>> = postDao.getPostsByAuthor(authorId)

    fun getLikedPosts(): Flow<List<PostEntity>> = postDao.getLikedPosts()

    fun getSavedPosts(): Flow<List<PostEntity>> = postDao.getSavedPosts()

    suspend fun createPost(
        content: String,
        category: String,
        chaiMood: String,
        imageUrl: String?,
        videoUrl: String?,
        videoDuration: String?
    ): Result<PostEntity> = withContext(Dispatchers.IO) {
        val author = _currentUser.value ?: return@withContext Result.failure(Exception("Not logged in"))
        val newPost = PostEntity(
            id = "post_" + UUID.randomUUID().toString().take(8),
            authorId = author.id,
            authorName = author.fullName,
            authorUsername = author.username,
            authorAvatarUrl = author.avatarUrl,
            authorIsVerified = author.isVerified,
            category = category,
            content = content.trim(),
            imageUrl = imageUrl?.takeIf { it.isNotBlank() },
            videoUrl = videoUrl?.takeIf { it.isNotBlank() },
            videoDuration = videoDuration?.takeIf { it.isNotBlank() },
            chaiMood = chaiMood,
            likesCount = 1,
            commentsCount = 0,
            sharesCount = 0,
            isLiked = true,
            isSaved = false,
            isPinned = false,
            createdAt = System.currentTimeMillis()
        )
        postDao.insertPost(newPost)
        try {
            firestoreService.savePost(newPost)
        } catch (e: Exception) {
            // Log or ignore network error, post is safely in local Room DB
        }
        Result.success(newPost)
    }

    suspend fun toggleLike(postId: String) = withContext(Dispatchers.IO) {
        val post = postDao.getPostByIdDirect(postId) ?: return@withContext
        val newLiked = !post.isLiked
        val newCount = if (newLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
        postDao.updateLike(postId, newLiked, newCount)

        // Send notification to author if someone likes
        val current = _currentUser.value
        if (newLiked && current != null && post.authorId != current.id) {
            notificationDao.insertNotification(
                NotificationEntity(
                    id = "notif_" + UUID.randomUUID().toString().take(8),
                    userId = post.authorId,
                    type = "LIKE",
                    senderName = current.fullName,
                    senderAvatarUrl = current.avatarUrl,
                    title = "आपकी पोस्ट पसंद की गई",
                    message = "${current.fullName} ने आपकी पोस्ट को पसंद किया।",
                    relatedPostId = postId,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun toggleSave(postId: String) = withContext(Dispatchers.IO) {
        val post = postDao.getPostByIdDirect(postId) ?: return@withContext
        postDao.updateSave(postId, !post.isSaved)
    }

    suspend fun deletePost(postId: String) = withContext(Dispatchers.IO) {
        postDao.deletePost(postId)
    }

    suspend fun togglePinPost(postId: String) = withContext(Dispatchers.IO) {
        val post = postDao.getPostByIdDirect(postId) ?: return@withContext
        postDao.setPinned(postId, !post.isPinned)
    }

    suspend fun setPostHidden(postId: String, isHidden: Boolean) = withContext(Dispatchers.IO) {
        postDao.setHidden(postId, isHidden)
    }

    // Comments
    fun getCommentsForPost(postId: String): Flow<List<CommentEntity>> = commentDao.getCommentsForPost(postId)

    suspend fun addComment(postId: String, text: String): Result<CommentEntity> = withContext(Dispatchers.IO) {
        val author = _currentUser.value ?: return@withContext Result.failure(Exception("Not logged in"))
        val comment = CommentEntity(
            id = "comm_" + UUID.randomUUID().toString().take(8),
            postId = postId,
            authorId = author.id,
            authorName = author.fullName,
            authorUsername = author.username,
            authorAvatarUrl = author.avatarUrl,
            text = text.trim(),
            createdAt = System.currentTimeMillis()
        )
        commentDao.insertComment(comment)
        postDao.incrementComments(postId)

        val post = postDao.getPostByIdDirect(postId)
        if (post != null && post.authorId != author.id) {
            notificationDao.insertNotification(
                NotificationEntity(
                    id = "notif_" + UUID.randomUUID().toString().take(8),
                    userId = post.authorId,
                    type = "COMMENT",
                    senderName = author.fullName,
                    senderAvatarUrl = author.avatarUrl,
                    title = "नई टिप्पणी",
                    message = "${author.fullName} ने आपकी पोस्ट पर टिप्पणी की: \"${text.take(30)}...\"",
                    relatedPostId = postId,
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        Result.success(comment)
    }

    // Users & Network
    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    fun getBlockedUsers(): Flow<List<UserEntity>> = userDao.getBlockedUsers()

    fun isFollowing(followerId: String, followedId: String): Flow<Boolean> =
        followDao.isFollowing(followerId, followedId)

    suspend fun toggleFollow(followedId: String) = withContext(Dispatchers.IO) {
        val current = _currentUser.value ?: return@withContext
        val isFollowed = followDao.isFollowingDirect(current.id, followedId)
        if (isFollowed) {
            followDao.deleteFollow(current.id, followedId)
        } else {
            followDao.insertFollow(FollowEntity(current.id, followedId))
            // Send notif
            notificationDao.insertNotification(
                NotificationEntity(
                    id = "notif_" + UUID.randomUUID().toString().take(8),
                    userId = followedId,
                    type = "FOLLOW",
                    senderName = current.fullName,
                    senderAvatarUrl = current.avatarUrl,
                    title = "नया फॉलोअर",
                    message = "${current.fullName} ने आपको फॉलो करना शुरू किया है।",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun blockUser(userId: String) = withContext(Dispatchers.IO) {
        userDao.setBlocked(userId, true)
    }

    suspend fun unblockUser(userId: String) = withContext(Dispatchers.IO) {
        userDao.setBlocked(userId, false)
    }

    // Search
    fun searchPosts(query: String): Flow<List<PostEntity>> = postDao.searchPosts(query)

    fun searchUsers(query: String): Flow<List<UserEntity>> = userDao.searchUsers(query)

    // Notifications
    fun getNotifications(): Flow<List<NotificationEntity>> {
        val currentId = _currentUser.value?.id ?: "user_me"
        return notificationDao.getNotifications(currentId)
    }

    suspend fun markNotificationsRead() = withContext(Dispatchers.IO) {
        val currentId = _currentUser.value?.id ?: "user_me"
        notificationDao.markAllAsRead(currentId)
    }

    suspend fun clearNotifications() = withContext(Dispatchers.IO) {
        val currentId = _currentUser.value?.id ?: "user_me"
        notificationDao.clearNotifications(currentId)
    }

    // Reports & Admin
    fun getAllReports(): Flow<List<ReportEntity>> = reportDao.getAllReports()

    suspend fun reportPost(postId: String, reason: String) = withContext(Dispatchers.IO) {
        val current = _currentUser.value ?: return@withContext
        val post = postDao.getPostByIdDirect(postId)
        val report = ReportEntity(
            id = "rep_" + UUID.randomUUID().toString().take(8),
            postId = postId,
            postSnippet = post?.content?.take(80) ?: "Post #$postId",
            reportedByUserId = current.id,
            reportedByUserName = current.fullName,
            reason = reason,
            status = "PENDING",
            timestamp = System.currentTimeMillis()
        )
        reportDao.insertReport(report)
        postDao.setReported(postId, true)
    }

    suspend fun resolveReport(reportId: String, hidePost: Boolean) = withContext(Dispatchers.IO) {
        reportDao.updateStatus(reportId, if (hidePost) "ACTIONED_HIDDEN" else "DISMISSED")
    }
}
