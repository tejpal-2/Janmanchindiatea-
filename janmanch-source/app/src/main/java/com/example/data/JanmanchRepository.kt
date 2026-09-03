package com.example.data

import com.example.model.CommentEntity
import com.example.model.FollowEntity
import com.example.model.NotificationEntity
import com.example.model.PostEntity
import com.example.model.ReportEntity
import com.example.model.UserEntity
import com.example.model.StoryEntity
import com.example.model.ChatThreadEntity
import com.example.model.ChatMessageEntity
import com.example.model.CommunityItemEntity
import android.net.Uri
import android.util.Patterns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
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
    private val storyDao = database.storyDao()
    private val chatDao = database.chatDao()
    private val communityDao = database.communityDao()

    private val firestoreService = FirestoreService(context)

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    init {
        coroutineScope.launch {
            seedInitialDataIfNeeded()
            firestoreService.signedInEmail()?.let { email ->
                userDao.getUserByEmail(email)?.let { _currentUser.value = it }
            }
            syncRemoteFirestorePosts()
        }
    }

    private suspend fun syncRemoteFirestorePosts() {
        try {
            firestoreService.listenToPosts().collect { remotePosts ->
                if (remotePosts.isNotEmpty()) {
                    // Keep user-specific Room state when a remote snapshot arrives.
                    // Firestore stores the canonical post, while likes/bookmarks are local
                    // until a signed-in account is available for server-side sync.
                    val mergedPosts = remotePosts.map { remote ->
                        val local = postDao.getPostByIdDirect(remote.id)
                        if (local == null) remote
                        else remote.copy(isLiked = local.isLiked, isSaved = local.isSaved)
                    }
                    postDao.insertPosts(mergedPosts)
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
        if (storyDao.countActive() == 0) storyDao.insertStories(SampleSeedData.initialStories)
        if (chatDao.countThreads() == 0) {
            chatDao.insertThreads(SampleSeedData.initialChatThreads)
            SampleSeedData.initialChatMessages.forEach { chatDao.insertMessage(it) }
        }
        if (communityDao.count() == 0) communityDao.insertItems(SampleSeedData.initialCommunityItems)
    }

    // Auth
    suspend fun login(email: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches() || password.isBlank()) {
            return@withContext Result.failure(Exception("Enter a valid email and password"))
        }
        val user = userDao.getUserByEmail(cleanEmail)
        if (user != null && passwordsMatch(user.passwordHash, password)) {
            _currentUser.value = user
            Result.success(user)
        } else {
            val firebaseResult = firestoreService.signIn(cleanEmail, password)
            if (firebaseResult.isFailure) return@withContext Result.failure(
                firebaseResult.exceptionOrNull() ?: Exception("Invalid credentials")
            )
            val uid = firebaseResult.getOrThrow()
            val remoteUser = user ?: UserEntity(
                id = uid,
                username = cleanEmail.substringBefore("@").replace(".", "_"),
                fullName = cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() },
                email = cleanEmail,
                passwordHash = "",
                bio = "जनमंच इंडिया टी परिवार का सदस्य ☕",
                location = "भारत (India)"
            )
            userDao.insertUser(remoteUser)
            firestoreService.saveUser(remoteUser)
            _currentUser.value = remoteUser
            Result.success(remoteUser)
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
        if (fullName.trim().isBlank() || !Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches() ||
            password.length < 6 || cleanUsername.isBlank()
        ) {
            return@withContext Result.failure(Exception("Valid email, username and 6+ character password are required"))
        }
        if (userDao.getUserByEmail(cleanEmail) != null || userDao.getUserByUsername(cleanUsername) != null) {
            return@withContext Result.failure(Exception("An account with these details already exists"))
        }
        val firebaseId = firestoreService.createAccount(cleanEmail, password).getOrNull()
        val newUser = UserEntity(
            id = firebaseId ?: "user_" + UUID.randomUUID().toString().take(8),
            username = cleanUsername,
            fullName = fullName.trim(),
            email = cleanEmail,
            passwordHash = hashPassword(password),
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
        firestoreService.saveUser(newUser)
        _currentUser.value = newUser
        Result.success(newUser)
    }

    private fun passwordsMatch(stored: String, password: String): Boolean =
        stored == password || stored == hashPassword(password)

    private fun hashPassword(password: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray(Charsets.UTF_8))
            .joinToString("") { byte -> "%02x".format(byte.toInt() and 0xff) }

    fun continueAsGuest() {
        _currentUser.value = SampleSeedData.currentUser
    }

    fun logout() {
        firestoreService.signOut()
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
        firestoreService.saveUser(updated)
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
        val cleanContent = content.trim()
        if (cleanContent.isBlank()) {
            return@withContext Result.failure(Exception("Post content cannot be empty"))
        }
        val uploadedImage = imageUrl?.takeIf { it.startsWith("content://") }?.let {
            firestoreService.uploadMedia(Uri.parse(it), author.id, "images").getOrNull()
        }
        val uploadedVideo = videoUrl?.takeIf { it.startsWith("content://") }?.let {
            firestoreService.uploadMedia(Uri.parse(it), author.id, "videos").getOrNull()
        }
        val newPost = PostEntity(
            id = "post_" + UUID.randomUUID().toString().take(8),
            authorId = author.id,
            authorName = author.fullName,
            authorUsername = author.username,
            authorAvatarUrl = author.avatarUrl,
            authorIsVerified = author.isVerified,
            category = category,
            content = cleanContent,
            imageUrl = uploadedImage ?: imageUrl?.takeIf { it.isNotBlank() },
            videoUrl = uploadedVideo ?: videoUrl?.takeIf { it.isNotBlank() },
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
        firestoreService.incrementPostMetric(postId, "likesCount", if (newLiked) 1 else -1)

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

    suspend fun incrementShare(postId: String) = withContext(Dispatchers.IO) {
        val post = postDao.getPostByIdDirect(postId) ?: return@withContext
        postDao.incrementShares(postId)
        firestoreService.incrementShareCount(postId)
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
        val cleanText = text.trim()
        if (cleanText.isBlank()) {
            return@withContext Result.failure(Exception("Comment cannot be empty"))
        }
        val post = postDao.getPostByIdDirect(postId)
            ?: return@withContext Result.failure(Exception("Post no longer exists"))
        val comment = CommentEntity(
            id = "comm_" + UUID.randomUUID().toString().take(8),
            postId = postId,
            authorId = author.id,
            authorName = author.fullName,
            authorUsername = author.username,
            authorAvatarUrl = author.avatarUrl,
            text = cleanText,
            createdAt = System.currentTimeMillis()
        )
        commentDao.insertComment(comment)
        postDao.incrementComments(postId)
        firestoreService.incrementPostMetric(postId, "commentsCount", 1)

        if (post.authorId != author.id) {
            notificationDao.insertNotification(
                NotificationEntity(
                    id = "notif_" + UUID.randomUUID().toString().take(8),
                    userId = post.authorId,
                    type = "COMMENT",
                    senderName = author.fullName,
                    senderAvatarUrl = author.avatarUrl,
                    title = "नई टिप्पणी",
                    message = "${author.fullName} ने आपकी पोस्ट पर टिप्पणी की: \"${cleanText.take(30)}…\"",
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

    fun getFollowingIds(userId: String): Flow<Set<String>> =
        followDao.getFollowing(userId).map { it.map(FollowEntity::followedId).toSet() }

    fun getFollowerIds(userId: String): Flow<Set<String>> =
        followDao.getFollowers(userId).map { it.map(FollowEntity::followerId).toSet() }

    fun getStories(): Flow<List<StoryEntity>> = storyDao.getActiveStories()

    fun getReels(): Flow<List<PostEntity>> = postDao.getReels()

    suspend fun markStoryViewed(storyId: String) = withContext(Dispatchers.IO) {
        storyDao.markViewed(storyId)
    }

    fun getChatThreads(): Flow<List<ChatThreadEntity>> = chatDao.getThreads()

    fun getChatMessages(threadId: String): Flow<List<ChatMessageEntity>> =
        chatDao.getMessages(threadId)

    suspend fun sendMessage(threadId: String, text: String): Result<ChatMessageEntity> =
        withContext(Dispatchers.IO) {
            val sender = _currentUser.value ?: return@withContext Result.failure(Exception("Not logged in"))
            val cleanText = text.trim()
            if (cleanText.isBlank()) return@withContext Result.failure(Exception("Message cannot be empty"))
            val message = ChatMessageEntity(
                id = "message_" + UUID.randomUUID().toString().take(8),
                threadId = threadId,
                senderId = sender.id,
                senderName = sender.fullName,
                text = cleanText
            )
            chatDao.insertMessage(message)
            chatDao.getThreads().first().firstOrNull { it.id == threadId }?.let {
                chatDao.updateThread(it.copy(lastMessage = cleanText, updatedAt = message.sentAt, unreadCount = 0))
            }
            Result.success(message)
        }

    fun getCommunityItems(): Flow<List<CommunityItemEntity>> = communityDao.getItems()

    suspend fun toggleFollow(followedId: String) = withContext(Dispatchers.IO) {
        val current = _currentUser.value ?: return@withContext
        val currentId = current.id
        if (current.id == followedId) return@withContext
        val isFollowed = followDao.isFollowingDirect(currentId, followedId)
        if (isFollowed) {
            followDao.deleteFollow(currentId, followedId)
            userDao.changeFollowing(currentId, -1)
            userDao.changeFollowers(followedId, -1)
        } else {
            followDao.insertFollow(FollowEntity(currentId, followedId))
            userDao.changeFollowing(currentId, 1)
            userDao.changeFollowers(followedId, 1)
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
        userDao.getUserByIdDirect(currentId)?.let {
            _currentUser.value = it
            firestoreService.saveUser(it)
        }
        userDao.getUserByIdDirect(followedId)?.let { firestoreService.saveUser(it) }
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
    fun getNotifications(userId: String): Flow<List<NotificationEntity>> =
        notificationDao.getNotifications(userId)

    fun getNotifications(): Flow<List<NotificationEntity>> =
        getNotifications(_currentUser.value?.id ?: "user_me")

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
