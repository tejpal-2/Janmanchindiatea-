package com.example.i18n

import com.example.model.AppLanguage

object Localization {

    fun getString(key: String, lang: AppLanguage): String {
        val map = if (lang == AppLanguage.HINDI) hindiStrings else englishStrings
        return map[key] ?: englishStrings[key] ?: key
    }

    val hindiStrings = mapOf(
        "app_title" to "जनमंच इंडिया टी",
        "app_tagline" to "चाय की चुस्की, देश की चर्चा",
        "tab_home" to "होम",
        "tab_search" to "खोजें",
        "tab_create" to "पोस्ट लिखें",
        "tab_network" to "नेटवर्क",
        "tab_explore" to "एक्सप्लोर",
        "tab_notifications" to "सूचनाएं",
        "tab_profile" to "प्रोफ़ाइल",
        "tab_settings" to "सेटिंग्स",
        "tab_admin" to "मॉडरेशन",

        // Auth
        "login_title" to "जनमंच में स्वागत है",
        "login_subtitle" to "भारतीय समुदाय और चाय पे चर्चा से जुड़ें",
        "login_tab" to "लॉग इन",
        "register_tab" to "नया खाता बनाएं",
        "guest_login" to "अतिथि (Guest) के रूप में देखें",
        "email_label" to "ईमेल पता",
        "password_label" to "पासवर्ड",
        "name_label" to "पूरा नाम",
        "username_label" to "उपयोगकर्ता नाम (@username)",
        "location_label" to "शहर / राज्य",
        "login_button" to "लॉग इन करें",
        "register_button" to "खाता बनाएं",
        "logout_button" to "लॉग आउट करें",
        "logout_confirm_title" to "लॉग आउट करें?",
        "logout_confirm_msg" to "क्या आप सच में अपने खाते से बाहर निकलना चाहते हैं?",

        // Feed & Categories
        "category_all" to "सभी",
        "category_chai" to "चाय और चर्चा",
        "category_politics" to "राजनीति व समाज",
        "category_news" to "ख़बरें",
        "category_culture" to "संस्कृति व कला",
        "category_rural" to "किसान व ग्रामीण",
        "category_tech" to "युवा व तकनीक",

        // Post Card
        "like" to "पसंद",
        "comment" to "टिप्पणी",
        "share" to "शेयर",
        "save" to "सहेजें",
        "saved" to "सहेजा गया",
        "report" to "रिपोर्ट करें",
        "block_user" to "यूज़र को ब्लॉक करें",
        "delete_post" to "पोस्ट हटाएं",
        "pin_post" to "पिन करें",
        "unpin_post" to "अनपिन करें",
        "follow" to "फॉलो करें",
        "following" to "फॉलो कर रहे हैं",
        "unfollow" to "अनफॉलो",
        "read_more" to "और पढ़ें...",
        "read_less" to "कम देखें",
        "post_pinned" to "📌 मुख्य चर्चा (Pinned)",
        "verified_user" to "सत्यापित सदस्य",
        "share_success" to "लिंक कॉपी हो गया!",

        // Create Post
        "create_post_title" to "नई चर्चा शुरू करें",
        "create_post_hint" to "चाय की चुस्की के साथ अपनी राय या खबर साझा करें...",
        "select_category" to "विषय / श्रेणी चुनें",
        "select_mood" to "चाय का मूड (Chai Mood)",
        "add_image_url" to "तस्वीर जोड़ें (Image URL)",
        "add_video_url" to "वीडियो जोड़ें (Video URL)",
        "video_duration_hint" to "अवधि (उदा. 02:45)",
        "publish_button" to "चर्चा प्रकाशित करें",
        "publishing" to "प्रकाशित हो रहा है...",
        "post_success" to "आपकी पोस्ट सफलतापूर्वक प्रकाशित हो गई!",

        // Comments
        "comments_title" to "चर्चा व टिप्पणियां",
        "write_comment_hint" to "अपनी राय लिखें...",
        "post_comment" to "भेजें",
        "no_comments" to "अभी कोई टिप्पणी नहीं है। पहली टिप्पणी करें!",

        // Search & Explore
        "search_hint" to "पोस्ट, विषय या सदस्य खोजें...",
        "trending_topics" to "🔥 ट्रेंडिंग चर्चाएं",
        "popular_creators" to "👥 लोकप्रिय सदस्य",
        "search_results" to "खोज परिणाम",
        "no_search_results" to "कोई परिणाम नहीं मिला।",

        // Network
        "network_title" to "जनमंच नेटवर्क",
        "network_subtitle" to "देशभर के चाय प्रेमियों और विचारकों से जुड़ें",
        "suggested_members" to "सुझाए गए सदस्य",
        "my_followers" to "मेरे फॉलोअर्स",
        "my_following" to "जिन्हें फॉलो कर रहे हैं",

        // Notifications
        "notifications_title" to "सूचनाएं",
        "all_notifications" to "सभी",
        "unread_notifications" to "अपठित",
        "clear_all" to "सभी साफ करें",
        "no_notifications" to "कोई नई सूचना नहीं है।",

        // Profile
        "profile_title" to "मेरी प्रोफ़ाइल",
        "edit_profile" to "प्रोफ़ाइल बदलें",
        "save_profile" to "सहेजें",
        "bio_label" to "बायो / परिचय",
        "tab_my_posts" to "मेरी पोस्ट",
        "tab_liked_posts" to "पसंद की गई",
        "tab_saved_posts" to "सहेजी गई",
        "stats_posts" to "पोस्ट",
        "stats_followers" to "फॉलोअर्स",
        "stats_following" to "फॉलोइंग",
        "stats_chai" to "☕ चाय कप",

        // Settings
        "settings_title" to "सेटिंग्स व नियम",
        "lang_setting" to "भाषा बदलें (Language)",
        "dark_mode_setting" to "डार्क थीम (Dark Mode)",
        "admin_mode" to "एडमिन मॉडरेशन पैनल",
        "blocked_users" to "ब्लॉक किए गए खाते",
        "guidelines_title" to "📜 जनमंच आचार संहिता",
        "about_title" to "ℹ️ जनमंच इंडिया टी के बारे में",
        "app_version" to "संस्करण 1.0 (रिलीज़ तैयार)",

        // Reports & Moderation
        "report_title" to "पोस्ट की शिकायत करें",
        "report_reason_spam" to "स्पैम या भ्रामक प्रचार",
        "report_reason_hate" to "अनुचित या नफरत फैलाने वाली भाषा",
        "report_reason_fake" to "झूठी खबर या गलत जानकारी",
        "report_reason_harass" to "परेशान करना या दुर्व्यवहार",
        "report_submit" to "शिकायत दर्ज करें",
        "report_submitted" to "आपकी शिकायत दर्ज कर ली गई है।",
        "cancel" to "रद्द करें",
        "confirm" to "पुष्टि करें"
    )

    val englishStrings = mapOf(
        "app_title" to "Janmanch India Tea",
        "app_tagline" to "Sip of Chai, Voice of the Nation",
        "tab_home" to "Home",
        "tab_search" to "Search",
        "tab_create" to "Create Post",
        "tab_network" to "Network",
        "tab_explore" to "Explore",
        "tab_notifications" to "Notifications",
        "tab_profile" to "Profile",
        "tab_settings" to "Settings",
        "tab_admin" to "Moderation",

        // Auth
        "login_title" to "Welcome to Janmanch",
        "login_subtitle" to "Join the Indian community and Chai Pe Charcha",
        "login_tab" to "Login",
        "register_tab" to "Create Account",
        "guest_login" to "Continue as Guest",
        "email_label" to "Email Address",
        "password_label" to "Password",
        "name_label" to "Full Name",
        "username_label" to "Username (@username)",
        "location_label" to "City / State",
        "login_button" to "Sign In",
        "register_button" to "Register",
        "logout_button" to "Log Out",
        "logout_confirm_title" to "Log Out?",
        "logout_confirm_msg" to "Are you sure you want to log out from your account?",

        // Feed & Categories
        "category_all" to "All",
        "category_chai" to "Chai & Charcha",
        "category_politics" to "Politics & Society",
        "category_news" to "News & Updates",
        "category_culture" to "Culture & Art",
        "category_rural" to "Farming & Rural",
        "category_tech" to "Youth & Tech",

        // Post Card
        "like" to "Like",
        "comment" to "Comment",
        "share" to "Share",
        "save" to "Save",
        "saved" to "Saved",
        "report" to "Report",
        "block_user" to "Block User",
        "delete_post" to "Delete Post",
        "pin_post" to "Pin Post",
        "unpin_post" to "Unpin Post",
        "follow" to "Follow",
        "following" to "Following",
        "unfollow" to "Unfollow",
        "read_more" to "Read more...",
        "read_less" to "Show less",
        "post_pinned" to "📌 Featured Charcha (Pinned)",
        "verified_user" to "Verified Member",
        "share_success" to "Link copied to clipboard!",

        // Create Post
        "create_post_title" to "Start a New Charcha",
        "create_post_hint" to "Share your thoughts, local news, or tea stories...",
        "select_category" to "Select Category",
        "select_mood" to "Chai Mood Tag",
        "add_image_url" to "Add Photo (Image URL)",
        "add_video_url" to "Add Video (Video URL)",
        "video_duration_hint" to "Duration (e.g. 02:45)",
        "publish_button" to "Publish Charcha",
        "publishing" to "Publishing...",
        "post_success" to "Your post has been published successfully!",

        // Comments
        "comments_title" to "Discussion & Comments",
        "write_comment_hint" to "Write your perspective...",
        "post_comment" to "Send",
        "no_comments" to "No comments yet. Be the first to share your thoughts!",

        // Search & Explore
        "search_hint" to "Search posts, topics, or members...",
        "trending_topics" to "🔥 Trending Discussions",
        "popular_creators" to "👥 Popular Members",
        "search_results" to "Search Results",
        "no_search_results" to "No results found.",

        // Network
        "network_title" to "Janmanch Network",
        "network_subtitle" to "Connect with chai lovers and thinkers across India",
        "suggested_members" to "Suggested Members",
        "my_followers" to "My Followers",
        "my_following" to "Following",

        // Notifications
        "notifications_title" to "Notifications",
        "all_notifications" to "All",
        "unread_notifications" to "Unread",
        "clear_all" to "Clear All",
        "no_notifications" to "No new notifications.",

        // Profile
        "profile_title" to "My Profile",
        "edit_profile" to "Edit Profile",
        "save_profile" to "Save",
        "bio_label" to "Bio / Description",
        "tab_my_posts" to "My Posts",
        "tab_liked_posts" to "Liked",
        "tab_saved_posts" to "Saved",
        "stats_posts" to "Posts",
        "stats_followers" to "Followers",
        "stats_following" to "Following",
        "stats_chai" to "☕ Chai Cups",

        // Settings
        "settings_title" to "Settings & Guidelines",
        "lang_setting" to "Language (भाषा)",
        "dark_mode_setting" to "Dark Mode",
        "admin_mode" to "Admin Moderation Panel",
        "blocked_users" to "Blocked Accounts",
        "guidelines_title" to "📜 Community Guidelines",
        "about_title" to "ℹ️ About Janmanch India Tea",
        "app_version" to "Version 1.0 (Release Ready)",

        // Reports & Moderation
        "report_title" to "Report Post",
        "report_reason_spam" to "Spam or commercial promotion",
        "report_reason_hate" to "Inappropriate or hateful content",
        "report_reason_fake" to "Misinformation or fake news",
        "report_reason_harass" to "Harassment or abuse",
        "report_submit" to "Submit Report",
        "report_submitted" to "Report submitted for moderation.",
        "cancel" to "Cancel",
        "confirm" to "Confirm"
    )
}
