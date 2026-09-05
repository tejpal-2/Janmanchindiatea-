package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.CommentEntity
import com.example.model.FollowEntity
import com.example.model.NotificationEntity
import com.example.model.PostEntity
import com.example.model.ReportEntity
import com.example.model.StoryEntity
import com.example.model.ChatThreadEntity
import com.example.model.ChatMessageEntity
import com.example.model.CommunityItemEntity
import com.example.model.CreatorEarningsEntity
import com.example.model.UserEntity

@Database(
    entities = [
        UserEntity::class,
        PostEntity::class,
        CommentEntity::class,
        NotificationEntity::class,
        FollowEntity::class,
        ReportEntity::class,
        StoryEntity::class,
        ChatThreadEntity::class,
        ChatMessageEntity::class,
        CommunityItemEntity::class,
        CreatorEarningsEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun notificationDao(): NotificationDao
    abstract fun followDao(): FollowDao
    abstract fun reportDao(): ReportDao
    abstract fun storyDao(): StoryDao
    abstract fun chatDao(): ChatDao
    abstract fun communityDao(): CommunityDao
    abstract fun earningsDao(): EarningsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "janmanch_india_tea.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
