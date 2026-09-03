package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.CommentEntity
import com.example.model.FollowEntity
import com.example.model.NotificationEntity
import com.example.model.PostEntity
import com.example.model.ReportEntity
import com.example.model.StoryEntity
import com.example.model.ChatThreadEntity
import com.example.model.ChatMessageEntity
import com.example.model.CommunityItemEntity
import com.example.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserByIdDirect(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE isBlocked = 0 ORDER BY followersCount DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE isBlocked = 1")
    fun getBlockedUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isBlocked = :blocked WHERE id = :userId")
    suspend fun setBlocked(userId: String, blocked: Boolean)

    @Query("UPDATE users SET followersCount = MAX(0, followersCount + :delta) WHERE id = :userId")
    suspend fun changeFollowers(userId: String, delta: Int)

    @Query("UPDATE users SET followingCount = MAX(0, followingCount + :delta) WHERE id = :userId")
    suspend fun changeFollowing(userId: String, delta: Int)

    @Query("SELECT * FROM users WHERE (fullName LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%') AND isBlocked = 0")
    fun searchUsers(query: String): Flow<List<UserEntity>>
}

@Dao
interface PostDao {
    @Query("SELECT * FROM posts WHERE isHidden = 0 ORDER BY isPinned DESC, createdAt DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Query("""
        SELECT * FROM posts 
        WHERE isHidden = 0 
        AND (
            category = :category 
            OR (category LIKE '%' || :category || '%') 
            OR (:category LIKE '%खबर%' AND (category LIKE '%खबर%' OR category LIKE '%ख़बर%'))
            OR (:category LIKE '%ख़बर%' AND (category LIKE '%खबर%' OR category LIKE '%ख़बर%'))
            OR (:category LIKE '%युवा%' AND category LIKE '%युवा%')
        )
        ORDER BY isPinned DESC, createdAt DESC
    """)
    fun getPostsByCategory(category: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :id LIMIT 1")
    fun getPostById(id: String): Flow<PostEntity?>

    @Query("SELECT * FROM posts WHERE id = :id LIMIT 1")
    suspend fun getPostByIdDirect(id: String): PostEntity?

    @Query("SELECT * FROM posts WHERE authorId = :authorId AND isHidden = 0 ORDER BY createdAt DESC")
    fun getPostsByAuthor(authorId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE isLiked = 1 AND isHidden = 0 ORDER BY createdAt DESC")
    fun getLikedPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE isSaved = 1 AND isHidden = 0 ORDER BY createdAt DESC")
    fun getSavedPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE videoUrl IS NOT NULL AND videoUrl != '' AND isHidden = 0 ORDER BY createdAt DESC")
    fun getReels(): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("DELETE FROM posts WHERE id = :id")
    suspend fun deletePost(id: String)

    @Query("UPDATE posts SET isLiked = :isLiked, likesCount = :likesCount WHERE id = :id")
    suspend fun updateLike(id: String, isLiked: Boolean, likesCount: Int)

    @Query("UPDATE posts SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateSave(id: String, isSaved: Boolean)

    @Query("UPDATE posts SET sharesCount = sharesCount + 1 WHERE id = :id")
    suspend fun incrementShares(id: String)

    @Query("UPDATE posts SET isPinned = :isPinned WHERE id = :id")
    suspend fun setPinned(id: String, isPinned: Boolean)

    @Query("UPDATE posts SET isHidden = :isHidden WHERE id = :id")
    suspend fun setHidden(id: String, isHidden: Boolean)

    @Query("UPDATE posts SET isReported = :isReported WHERE id = :id")
    suspend fun setReported(id: String, isReported: Boolean)

    @Query("UPDATE posts SET commentsCount = commentsCount + 1 WHERE id = :id")
    suspend fun incrementComments(id: String)

    @Query("SELECT * FROM posts WHERE (content LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR authorName LIKE '%' || :query || '%') AND isHidden = 0 ORDER BY createdAt DESC")
    fun searchPosts(query: String): Flow<List<PostEntity>>
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAt ASC")
    fun getCommentsForPost(postId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Query("DELETE FROM comments WHERE id = :id")
    suspend fun deleteComment(id: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotifications(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: String)

    @Query("DELETE FROM notifications WHERE userId = :userId")
    suspend fun clearNotifications(userId: String)
}

@Dao
interface FollowDao {
    @Query("SELECT * FROM follows WHERE followerId = :followerId")
    fun getFollowing(followerId: String): Flow<List<FollowEntity>>

    @Query("SELECT * FROM follows WHERE followedId = :followedId")
    fun getFollowers(followedId: String): Flow<List<FollowEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM follows WHERE followerId = :followerId AND followedId = :followedId)")
    fun isFollowing(followerId: String, followedId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM follows WHERE followerId = :followerId AND followedId = :followedId)")
    suspend fun isFollowingDirect(followerId: String, followedId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollow(follow: FollowEntity)

    @Query("DELETE FROM follows WHERE followerId = :followerId AND followedId = :followedId")
    suspend fun deleteFollow(followerId: String, followedId: String)
}

@Dao
interface ReportDao {
    @Query("SELECT * FROM reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Query("UPDATE reports SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("DELETE FROM reports WHERE id = :id")
    suspend fun deleteReport(id: String)
}

@Dao
interface StoryDao {
    @Query("SELECT COUNT(*) FROM stories WHERE expiresAt > :now")
    suspend fun countActive(now: Long = System.currentTimeMillis()): Int

    @Query("SELECT * FROM stories WHERE expiresAt > :now ORDER BY createdAt DESC")
    fun getActiveStories(now: Long = System.currentTimeMillis()): Flow<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Query("UPDATE stories SET isViewed = 1 WHERE id = :id")
    suspend fun markViewed(id: String)
}

@Dao
interface ChatDao {
    @Query("SELECT COUNT(*) FROM chat_threads")
    suspend fun countThreads(): Int

    @Query("SELECT * FROM chat_threads ORDER BY updatedAt DESC")
    fun getThreads(): Flow<List<ChatThreadEntity>>

    @Query("SELECT * FROM chat_messages WHERE threadId = :threadId ORDER BY sentAt ASC")
    fun getMessages(threadId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThreads(threads: List<ChatThreadEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Update
    suspend fun updateThread(thread: ChatThreadEntity)
}

@Dao
interface CommunityDao {
    @Query("SELECT COUNT(*) FROM community_items")
    suspend fun count(): Int

    @Query("SELECT * FROM community_items ORDER BY createdAt DESC")
    fun getItems(): Flow<List<CommunityItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<CommunityItemEntity>)
}
