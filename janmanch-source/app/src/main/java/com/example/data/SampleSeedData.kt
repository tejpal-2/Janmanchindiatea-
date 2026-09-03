package com.example.data

import com.example.model.CommentEntity
import com.example.model.NotificationEntity
import com.example.model.PostEntity
import com.example.model.UserEntity

object SampleSeedData {

    val currentUser = UserEntity(
        id = "user_me",
        username = "raju_meena",
        fullName = "Raju Meena",
        email = "raju@janmanch.in",
        passwordHash = "password123",
        avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
        bannerUrl = "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=800&auto=format&fit=crop&q=80",
        bio = "☕ चाय प्रेमी | स्वतंत्र विचारक | भारतीय संस्कृति और जन कल्याण के लिए समर्पित।",
        location = "जयपुर, राजस्थान",
        followersCount = 428,
        followingCount = 192,
        chaiPoints = 350,
        isVerified = true,
        isAdmin = true
    )

    val initialUsers = listOf(
        currentUser,
        UserEntity(
            id = "user_1",
            username = "anand_patel",
            fullName = "आनंद पटेल (Anand Patel)",
            email = "anand@janmanch.in",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
            bio = "कृषि विशेषज्ञ और पर्यावरण संरक्षक | गांव की मिट्टी से जुड़ाव 🌾",
            location = "अहमदाबाद, गुजरात",
            followersCount = 1250,
            followingCount = 310,
            chaiPoints = 820,
            isVerified = true
        ),
        UserEntity(
            id = "user_2",
            username = "priya_sharma",
            fullName = "प्रिया शर्मा (Priya Sharma)",
            email = "priya@janmanch.in",
            avatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=400&auto=format&fit=crop&q=80",
            bio = "डिजिटल पत्रकार | जनहित के मुद्दों पर सटीक रिपोर्टिंग 🎙️",
            location = "नई दिल्ली, भारत",
            followersCount = 3400,
            followingCount = 450,
            chaiPoints = 1450,
            isVerified = true
        ),
        UserEntity(
            id = "user_3",
            username = "vikram_rathore",
            fullName = "विक्रम राठौड़ (Vikram Rathore)",
            email = "vikram@janmanch.in",
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80",
            bio = "इतिहास और भारतीय स्थापत्य कला प्रेमी | धरोहर संरक्षण 🏛️",
            location = "उदयपुर, राजस्थान",
            followersCount = 980,
            followingCount = 210,
            chaiPoints = 640,
            isVerified = false
        ),
        UserEntity(
            id = "user_4",
            username = "sunita_devi",
            fullName = "सुनीता देवी (Sunita Devi)",
            email = "sunita@janmanch.in",
            avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&auto=format&fit=crop&q=80",
            bio = "स्वयं सहायता समूह प्रेरणा | महिला उद्यमिता 🌿",
            location = "पटना, बिहार",
            followersCount = 760,
            followingCount = 140,
            chaiPoints = 510,
            isVerified = true
        )
    )

    val initialPosts = listOf(
        PostEntity(
            id = "post_1",
            authorId = "user_me",
            authorName = "Raju Meena",
            authorUsername = "raju_meena",
            authorAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
            authorIsVerified = true,
            category = "चाय और चर्चा",
            content = "☕ 'जनमंच इंडिया टी' (Janmanch India Tea) के सभी साथियों को राम-राम! \n\nएक कप कड़क मसाला चाय के साथ दिन की शुरुआत और देश के विकास पर सार्थक विमर्श ही हमारी पहचान है। इस मंच पर अपनी बात बेबाक और मर्यादित तरीके से रखें। आपकी क्या राय है आज के मुख्य सामाजिक मुद्दों पर? #ChaiPeCharcha #JanmanchIndia",
            imageUrl = "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=800&auto=format&fit=crop&q=80",
            chaiMood = "☕ कड़क मसाला चाय",
            likesCount = 84,
            commentsCount = 19,
            sharesCount = 12,
            isLiked = true,
            isSaved = true,
            isPinned = true,
            createdAt = System.currentTimeMillis() - 1000 * 60 * 30
        ),
        PostEntity(
            id = "post_2",
            authorId = "user_1",
            authorName = "आनंद पटेल (Anand Patel)",
            authorUsername = "anand_patel",
            authorAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
            authorIsVerified = true,
            category = "किसान व ग्रामीण",
            content = "🌾 जैविक खेती और प्राकृतिक खाद से फसलों की पैदावार में 30% तक सुधार देखा जा रहा है। किसान भाइयों को सरकारी योजनाओं और सौर ऊर्जा पंप सेटों का अधिक से अधिक लाभ उठाना चाहिए।\n\nआइए मिलकर भारतीय कृषि को आत्मनिर्भर बनाएं!",
            imageUrl = "https://images.unsplash.com/photo-1586771107445-d3ca888129ff?w=800&auto=format&fit=crop&q=80",
            chaiMood = "🌿 अदरक तुलसी चाय",
            likesCount = 142,
            commentsCount = 28,
            sharesCount = 35,
            isLiked = false,
            isSaved = false,
            createdAt = System.currentTimeMillis() - 1000 * 60 * 120
        ),
        PostEntity(
            id = "post_3",
            authorId = "user_2",
            authorName = "प्रिया शर्मा (Priya Sharma)",
            authorUsername = "priya_sharma",
            authorAvatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=400&auto=format&fit=crop&q=80",
            authorIsVerified = true,
            category = "युवा व तकनीक",
            content = "📱 भारत में डिजिटल पब्लिक इंफ्रास्ट्रक्चर (DPI) और UPI क्रांति ने दुनिया भर में नए मानक स्थापित किए हैं। आज छोटे से छोटे चाय के ठेले से लेकर बड़े मॉल तक डिजिटल भुगतान सुलभ है।\n\nवीडियो रिपोर्ट देखें और अपने अनुभव साझा करें!",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            videoDuration = "03:40",
            imageUrl = "https://images.unsplash.com/photo-1556742049-0a67c5574f73?w=800&auto=format&fit=crop&q=80",
            chaiMood = "🫖 स्पेशल कुल्हड़ चाय",
            likesCount = 210,
            commentsCount = 45,
            sharesCount = 50,
            isLiked = true,
            isSaved = false,
            createdAt = System.currentTimeMillis() - 1000 * 60 * 240
        ),
        PostEntity(
            id = "post_4",
            authorId = "user_3",
            authorName = "विक्रम राठौड़ (Vikram Rathore)",
            authorUsername = "vikram_rathore",
            authorAvatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80",
            authorIsVerified = false,
            category = "संस्कृति व कला",
            content = "🏛️ राजस्थान के प्राचीन दुर्ग और बावड़ियों की वास्तुकला अद्भुत है। बारिश के मौसम में इन ऐतिहासिक स्थलों का सौंदर्य और भी निखर उठता है। धरोहर को संरक्षित रखना हम सभी की साझी जिम्मेदारी है।",
            imageUrl = "https://images.unsplash.com/photo-1599661046289-e31897846e41?w=800&auto=format&fit=crop&q=80",
            chaiMood = "🔥 शाम की चर्चा",
            likesCount = 95,
            commentsCount = 14,
            sharesCount = 8,
            isLiked = false,
            isSaved = false,
            createdAt = System.currentTimeMillis() - 1000 * 60 * 480
        ),
        PostEntity(
            id = "post_5",
            authorId = "user_4",
            authorName = "सुनीता देवी (Sunita Devi)",
            authorUsername = "sunita_devi",
            authorAvatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&auto=format&fit=crop&q=80",
            authorIsVerified = true,
            category = "राजनीति व समाज",
            content = "ग्रामीण क्षेत्रों में महिलाओं के कौशल विकास केंद्र से कई परिवारों को नई आर्थिक ताकत मिली है। जब समाज की हर बेटी शिक्षित और स्वावलंबी होगी, तभी भारत सशक्त बनेगा।",
            imageUrl = "https://images.unsplash.com/photo-1607344645866-009c320b5ab8?w=800&auto=format&fit=crop&q=80",
            chaiMood = "☕ कड़क मसाला चाय",
            likesCount = 188,
            commentsCount = 31,
            sharesCount = 22,
            isLiked = false,
            isSaved = false,
            createdAt = System.currentTimeMillis() - 1000 * 60 * 720
        )
    )

    val initialComments = listOf(
        CommentEntity(
            id = "comm_1",
            postId = "post_1",
            authorId = "user_1",
            authorName = "आनंद पटेल",
            authorUsername = "anand_patel",
            authorAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
            text = "बहुत ही शानदार शुरुआत! जनमंच पर सभी को खुलकर विचार रखने का अवसर मिलना चाहिए।",
            createdAt = System.currentTimeMillis() - 1000 * 60 * 20
        ),
        CommentEntity(
            id = "comm_2",
            postId = "post_1",
            authorId = "user_2",
            authorName = "प्रिया शर्मा",
            authorUsername = "priya_sharma",
            authorAvatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=400&auto=format&fit=crop&q=80",
            text = "चाय और चर्चा भारतीय संस्कृति का अभिन्न अंग है। शुभकामनाएं!",
            createdAt = System.currentTimeMillis() - 1000 * 60 * 15
        ),
        CommentEntity(
            id = "comm_3",
            postId = "post_3",
            authorId = "user_me",
            authorName = "Raju Meena",
            authorUsername = "raju_meena",
            authorAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
            text = "डिजिटल भारत की यह यात्रा हर नागरिक के लिए गर्व का विषय है।",
            createdAt = System.currentTimeMillis() - 1000 * 60 * 100
        )
    )

    val initialNotifications = listOf(
        NotificationEntity(
            id = "notif_1",
            userId = "user_me",
            type = "LIKE",
            senderName = "प्रिया शर्मा",
            senderAvatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=400&auto=format&fit=crop&q=80",
            title = "आपकी पोस्ट पसंद की गई",
            message = "प्रिया शर्मा ने आपकी चाय पे चर्चा पोस्ट को पसंद किया।",
            relatedPostId = "post_1",
            isRead = false,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 10
        ),
        NotificationEntity(
            id = "notif_2",
            userId = "user_me",
            type = "COMMENT",
            senderName = "आनंद पटेल",
            senderAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
            title = "नई टिप्पणी",
            message = "आनंद पटेल ने आपकी पोस्ट पर टिप्पणी की: 'बहुत ही शानदार शुरुआत!'",
            relatedPostId = "post_1",
            isRead = false,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 20
        ),
        NotificationEntity(
            id = "notif_3",
            userId = "user_me",
            type = "ANNOUNCEMENT",
            senderName = "जनमंच टीम",
            senderAvatarUrl = "",
            title = "जनमंच इंडिया टी में स्वागत है",
            message = "जनमंच समुदाय में आपका हार्दिक अभिनंदन। कृपया दिशानिर्देशों का पालन करें।",
            relatedPostId = null,
            isRead = true,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24
        )
    )
}
