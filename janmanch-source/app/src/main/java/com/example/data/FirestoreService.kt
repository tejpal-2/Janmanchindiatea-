package com.example.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.model.PostEntity
import com.example.model.UserEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreService(private val context: Context) {

    private fun firebaseApp(): FirebaseApp? = try {
        FirebaseApp.getApps(context).firstOrNull() ?: FirebaseApp.initializeApp(context)
    } catch (e: Exception) {
        Log.d("FirestoreService", "Firebase is not configured: ${e.message}")
        null
    }

    private val firebaseAuth: FirebaseAuth? by lazy {
        try {
            firebaseApp()?.let { FirebaseAuth.getInstance(it) }
        } catch (e: Exception) {
            Log.d("FirestoreService", "Firebase Auth unavailable: ${e.message}")
            null
        }
    }

    private val firebaseStorage: FirebaseStorage? by lazy {
        try {
            firebaseApp()?.let { FirebaseStorage.getInstance(it) }
        } catch (e: Exception) {
            Log.d("FirestoreService", "Firebase Storage unavailable: ${e.message}")
            null
        }
    }

    private val firestore: FirebaseFirestore? by lazy {
        try {
            val app = if (FirebaseApp.getApps(context).isEmpty()) {
                val initializedApp = try {
                    FirebaseApp.initializeApp(context)
                } catch (e: Exception) {
                    null
                }

                initializedApp ?: null
            } else {
                FirebaseApp.getInstance()
            }

            if (app != null) {
                FirebaseFirestore.getInstance(app)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.d("FirestoreService", "Local database mode active: ${e.message}")
            null
        }
    }

    suspend fun signIn(email: String, password: String): Result<String> = runCatching {
        val auth = firebaseAuth ?: error("Firebase Authentication is not configured")
        auth.signInWithEmailAndPassword(email, password).await()
        auth.currentUser?.uid ?: error("Authentication returned no user")
    }

    suspend fun createAccount(email: String, password: String): Result<String> = runCatching {
        val auth = firebaseAuth ?: error("Firebase Authentication is not configured")
        auth.createUserWithEmailAndPassword(email, password).await()
        auth.currentUser?.uid ?: error("Authentication returned no user")
    }

    fun signOut() {
        firebaseAuth?.signOut()
    }

    fun signedInEmail(): String? = firebaseAuth?.currentUser?.email

    suspend fun uploadMedia(uri: Uri, userId: String, kind: String): Result<String> = runCatching {
        val storage = firebaseStorage ?: error("Firebase Storage is not configured")
        val fileName = "${System.currentTimeMillis()}_${uri.lastPathSegment ?: "media"}"
        val ref = storage.reference.child("users/$userId/$kind/$fileName")
        ref.putFile(uri).await()
        ref.downloadUrl.await().toString()
    }

    suspend fun saveUser(user: UserEntity): Result<Unit> {
        return writeDocument("users", user.id, hashMapOf(
                "id" to user.id,
                "username" to user.username,
                "fullName" to user.fullName,
                "email" to user.email,
                "avatarUrl" to user.avatarUrl,
                "bannerUrl" to user.bannerUrl,
                "bio" to user.bio,
                "location" to user.location,
                "followersCount" to user.followersCount,
                "followingCount" to user.followingCount,
                "chaiPoints" to user.chaiPoints,
                "isVerified" to user.isVerified,
                "joinedDate" to user.joinedDate
            ))
    }

    suspend fun saveComment(comment: com.example.model.CommentEntity): Result<Unit> =
        writeDocument("comments", comment.id, mapOf(
            "id" to comment.id, "postId" to comment.postId, "authorId" to comment.authorId,
            "authorName" to comment.authorName, "authorUsername" to comment.authorUsername,
            "authorAvatarUrl" to comment.authorAvatarUrl, "text" to comment.text,
            "createdAt" to comment.createdAt
        ))

    suspend fun saveFollow(follow: com.example.model.FollowEntity, following: Boolean): Result<Unit> {
        val id = "${follow.followerId}_${follow.followedId}"
        return if (following) writeDocument("follows", id, mapOf(
            "id" to id, "followerId" to follow.followerId, "followedId" to follow.followedId,
            "createdAt" to follow.createdAt
        )) else deleteDocument("follows", id)
    }

    suspend fun saveNotification(notification: com.example.model.NotificationEntity): Result<Unit> =
        writeDocument("notifications", notification.id, mapOf(
            "id" to notification.id, "userId" to notification.userId, "type" to notification.type,
            "senderName" to notification.senderName, "senderAvatarUrl" to notification.senderAvatarUrl,
            "title" to notification.title, "message" to notification.message,
            "relatedPostId" to notification.relatedPostId, "isRead" to notification.isRead,
            "timestamp" to notification.timestamp
        ))

    suspend fun saveReport(report: com.example.model.ReportEntity): Result<Unit> =
        writeDocument("reports", report.id, mapOf(
            "id" to report.id, "postId" to report.postId, "postSnippet" to report.postSnippet,
            "reportedByUserId" to report.reportedByUserId, "reportedByUserName" to report.reportedByUserName,
            "reason" to report.reason, "status" to report.status, "timestamp" to report.timestamp
        ))

    suspend fun saveStory(story: com.example.model.StoryEntity): Result<Unit> =
        writeDocument("stories", story.id, mapOf(
            "id" to story.id, "authorId" to story.authorId, "authorName" to story.authorName,
            "authorAvatarUrl" to story.authorAvatarUrl, "mediaUrl" to story.mediaUrl,
            "caption" to story.caption, "expiresAt" to story.expiresAt,
            "createdAt" to story.createdAt
        ))

    suspend fun saveStoryView(storyId: String, userId: String): Result<Unit> =
        writeDocument("stories/$storyId/views", userId, mapOf(
            "userId" to userId,
            "viewedAt" to System.currentTimeMillis()
        ))

    suspend fun saveChatMessage(message: com.example.model.ChatMessageEntity): Result<Unit> =
        writeDocument("chat_messages", message.id, mapOf(
            "id" to message.id, "threadId" to message.threadId, "senderId" to message.senderId,
            "senderName" to message.senderName, "text" to message.text, "sentAt" to message.sentAt
        ))

    private suspend fun writeDocument(
        collection: String,
        id: String,
        data: Map<String, Any?>
    ): Result<Unit> {
        val db = firestore ?: return Result.failure(Exception("Firestore not initialized"))
        return try {
            db.collection(collection).document(id).set(data, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreService", "Failed to write $collection/$id: ${e.message}")
            Result.failure(e)
        }
    }

    private suspend fun deleteDocument(collection: String, id: String): Result<Unit> {
        val db = firestore ?: return Result.failure(Exception("Firestore not initialized"))
        return try {
            db.collection(collection).document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreService", "Failed to delete $collection/$id: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Listen to the "posts" collection in Firestore in real-time.
     */
    fun listenToPosts(): Flow<List<PostEntity>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        var listenerRegistration: ListenerRegistration? = null
        try {
            listenerRegistration = db.collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(50)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("FirestoreService", "Listen failed: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val posts = snapshot.documents.mapNotNull { doc ->
                            try {
                                val id = doc.getString("id") ?: doc.id
                                val authorId = doc.getString("authorId") ?: "user_anon"
                                val authorName = doc.getString("authorName") ?: "अनाम सदस्य"
                                val authorUsername = doc.getString("authorUsername") ?: "user"
                                val authorAvatarUrl = doc.getString("authorAvatarUrl") ?: ""
                                val authorIsVerified = doc.getBoolean("authorIsVerified") ?: false
                                val category = doc.getString("category") ?: "चाय और चर्चा"
                                val content = doc.getString("content") ?: ""
                                val imageUrl = doc.getString("imageUrl")
                                val videoUrl = doc.getString("videoUrl")
                                val videoDuration = doc.getString("videoDuration")
                                val chaiMood = doc.getString("chaiMood") ?: "☕ कड़क मसाला चाय"
                                val likesCount = (doc.getLong("likesCount") ?: 0L).toInt()
                                val commentsCount = (doc.getLong("commentsCount") ?: 0L).toInt()
                                val sharesCount = (doc.getLong("sharesCount") ?: 0L).toInt()
                                val isPinned = doc.getBoolean("isPinned") ?: false
                                val isReported = doc.getBoolean("isReported") ?: false
                                val isHidden = doc.getBoolean("isHidden") ?: false
                                val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()

                                PostEntity(
                                    id = id,
                                    authorId = authorId,
                                    authorName = authorName,
                                    authorUsername = authorUsername,
                                    authorAvatarUrl = authorAvatarUrl,
                                    authorIsVerified = authorIsVerified,
                                    category = category,
                                    content = content,
                                    imageUrl = imageUrl,
                                    videoUrl = videoUrl,
                                    videoDuration = videoDuration,
                                    chaiMood = chaiMood,
                                    likesCount = likesCount,
                                    commentsCount = commentsCount,
                                    sharesCount = sharesCount,
                                    isLiked = false,
                                    isSaved = false,
                                    isPinned = isPinned,
                                    isReported = isReported,
                                    isHidden = isHidden,
                                    createdAt = createdAt
                                )
                            } catch (e: Exception) {
                                Log.w("FirestoreService", "Error parsing post doc: ${e.message}")
                                null
                            }
                        }
                        trySend(posts)
                    }
                }
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error setting up listener: ${e.message}")
        }

        awaitClose {
            listenerRegistration?.remove()
        }
    }

    /**
     * Publish a new post to Firestore database without altering existing collections or structure.
     */
    suspend fun savePost(post: PostEntity): Result<Unit> {
        val db = firestore ?: return Result.failure(Exception("Firestore not initialized"))
        return try {
            val postMap = hashMapOf<String, Any?>(
                "id" to post.id,
                "authorId" to post.authorId,
                "authorName" to post.authorName,
                "authorUsername" to post.authorUsername,
                "authorAvatarUrl" to post.authorAvatarUrl,
                "authorIsVerified" to post.authorIsVerified,
                "category" to post.category,
                "content" to post.content,
                "imageUrl" to post.imageUrl,
                "videoUrl" to post.videoUrl,
                "videoDuration" to post.videoDuration,
                "chaiMood" to post.chaiMood,
                "likesCount" to post.likesCount,
                "commentsCount" to post.commentsCount,
                "sharesCount" to post.sharesCount,
                "isPinned" to post.isPinned,
                "isReported" to post.isReported,
                "isHidden" to post.isHidden,
                "createdAt" to post.createdAt
            )

            db.collection("posts")
                .document(post.id)
                .set(postMap, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreService", "Failed to save post to Firestore: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun incrementShareCount(postId: String): Result<Unit> {
        return incrementPostMetric(postId, "sharesCount", 1)
    }

    suspend fun incrementPostMetric(postId: String, field: String, delta: Long): Result<Unit> {
        val db = firestore ?: return Result.failure(Exception("Firestore not initialized"))
        return try {
            db.collection("posts").document(postId)
                .update(field, FieldValue.increment(delta))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w("FirestoreService", "Failed to update post metric: ${e.message}")
            Result.failure(e)
        }
    }
}
