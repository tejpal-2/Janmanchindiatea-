package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val fullName: String,
    val email: String,
    val passwordHash: String = "",
    val avatarUrl: String = "",
    val bannerUrl: String = "",
    val bio: String = "",
    val location: String = "नई दिल्ली, भारत",
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val chaiPoints: Int = 100,
    val isVerified: Boolean = false,
    val isAdmin: Boolean = false,
    val isBlocked: Boolean = false,
    val joinedDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val authorId: String,
    val authorName: String,
    val authorUsername: String,
    val authorAvatarUrl: String = "",
    val authorIsVerified: Boolean = false,
    val category: String = "चाय और चर्चा",
    val content: String,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val videoDuration: String? = null,
    val chaiMood: String = "☕ कड़क मसाला चाय",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val isPinned: Boolean = false,
    val isReported: Boolean = false,
    val isHidden: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val authorId: String,
    val authorName: String,
    val authorUsername: String,
    val authorAvatarUrl: String = "",
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val type: String, // LIKE, COMMENT, FOLLOW, ANNOUNCEMENT, REPORT
    val senderName: String,
    val senderAvatarUrl: String = "",
    val title: String,
    val message: String,
    val relatedPostId: String? = null,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "follows", primaryKeys = ["followerId", "followedId"])
data class FollowEntity(
    val followerId: String,
    val followedId: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val postSnippet: String,
    val reportedByUserId: String,
    val reportedByUserName: String,
    val reason: String,
    val status: String = "PENDING", // PENDING, RESOLVED, DISMISSED
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey val id: String,
    val authorId: String,
    val authorName: String,
    val authorAvatarUrl: String = "",
    val mediaUrl: String,
    val caption: String = "",
    val expiresAt: Long = System.currentTimeMillis() + 24 * 60 * 60 * 1000,
    val isViewed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_threads")
data class ChatThreadEntity(
    @PrimaryKey val id: String,
    val participantId: String,
    val participantName: String,
    val participantAvatarUrl: String = "",
    val lastMessage: String = "",
    val updatedAt: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val threadId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val sentAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "community_items")
data class CommunityItemEntity(
    @PrimaryKey val id: String,
    val section: String,
    val title: String,
    val summary: String,
    val authorName: String = "जनमंच टीम",
    val imageUrl: String? = null,
    val actionLabel: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class AppLanguage {
    HINDI,
    ENGLISH
}

enum class NavTab {
    FEED,
    SEARCH,
    CREATE,
    NETWORK,
    NOTIFICATIONS,
    PROFILE,
    SETTINGS,
    ADMIN_MODERATION,
    COMMUNITY
}
