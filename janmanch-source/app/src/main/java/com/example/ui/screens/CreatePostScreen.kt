package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.i18n.Localization
import com.example.model.AppLanguage
import com.example.model.UserEntity
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.ChaiAmber
import com.example.ui.theme.ChaiKadakBrown
import com.example.ui.theme.TeaLeafGreenContainer
import com.example.ui.theme.TeaLeafGreenDark
import com.example.ui.theme.TeaLeafGreenPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreatePostScreen(
    currentUser: UserEntity?,
    language: AppLanguage,
    onBack: () -> Unit,
    onSubmitPost: (String, String, String, String?, String?, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var content by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("चाय और चर्चा") }
    var selectedMood by remember { mutableStateOf("☕ कड़क मसाला चाय") }
    var imageUrl by remember { mutableStateOf("") }
    var videoUrl by remember { mutableStateOf("") }
    var videoDuration by remember { mutableStateOf("02:30") }
    var isPublishing by remember { mutableStateOf(false) }
    var showUrlInputs by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // ActivityResult Launchers for Image & Video picking
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // Some providers return a URI without persistable permission.
            }
            imageUrl = it.toString()
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // Some providers return a URI without persistable permission.
            }
            videoUrl = it.toString()
            if (videoDuration.isBlank()) videoDuration = "02:15"
        }
    }

    val categories = listOf(
        "चाय और चर्चा",
        "राजनीति व समाज",
        "ख़बरें",
        "संस्कृति व कला",
        "किसान व ग्रामीण",
        "युवा व तकनीक"
    )

    val chaiMoods = listOf(
        "☕ कड़क मसाला चाय",
        "🫖 स्पेशल कुल्हड़ चाय",
        "🌿 अदरक तुलसी",
        "🔥 शाम की चर्चा",
        "🌅 सुबह की चुस्की"
    )

    val trendingTags = listOf(
        "#ChaiPeCharcha",
        "#JanmanchIndia",
        "#KisanVikas",
        "#Swadeshi",
        "#DigitalBharat"
    )

    val samplePhotoPresets = listOf(
        Pair("चाय की दुकान ☕", "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=800"),
        Pair("ग्रामीण जीवन 🌾", "https://images.unsplash.com/photo-1586771107445-d3ca888129ff?w=800"),
        Pair("भारतीय धरोहर 🏛️", "https://images.unsplash.com/photo-1599661046289-e31897846e41?w=800"),
        Pair("डिजिटल भारत 📱", "https://images.unsplash.com/photo-1556742049-0a67c5574f73?w=800")
    )

    val scrollState = rememberScrollState()
    val isUserLoggedIn = currentUser != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = Localization.getString("create_post_title", language),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            if (content.isNotBlank() && isUserLoggedIn && !isPublishing) {
                                isPublishing = true
                                scope.launch {
                                    delay(300)
                                    onSubmitPost(
                                        content,
                                        selectedCategory,
                                        selectedMood,
                                        imageUrl.takeIf { it.isNotBlank() },
                                        videoUrl.takeIf { it.isNotBlank() },
                                        videoDuration.takeIf { videoUrl.isNotBlank() }
                                    )
                                    isPublishing = false
                                }
                            }
                        },
                        enabled = content.isNotBlank() && isUserLoggedIn && !isPublishing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TeaLeafGreenPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("publish_post_button")
                    ) {
                        if (isPublishing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("प्रकाशन जारी...", fontSize = 12.sp)
                        } else {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = Localization.getString("publish_button", language),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Require Login Banner if not logged in
            if (!isUserLoggedIn) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "लॉग इन आवश्यक है",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = "जनमंच पर नई चर्चा पोस्ट करने के लिए कृपया पहले अपने खाते में लॉग इन करें।",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            // Author Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = currentUser?.avatarUrl?.ifBlank { null }
                        ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
                    contentDescription = "Author Avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(2.dp, TeaLeafGreenPrimary.copy(alpha = 0.3f), CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentUser?.fullName ?: "जनमंच सदस्य",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (currentUser?.isVerified == true) {
                            Spacer(modifier = Modifier.width(4.dp))
                            VerifiedBadge()
                        }
                    }
                    Text(
                        text = if (currentUser != null) "@${currentUser.username} • ${currentUser.location}" else "@अतिथि (Guest)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Post content Input
            OutlinedTextField(
                value = content,
                onValueChange = { if (it.length <= 1000) content = it },
                placeholder = {
                    Text(
                        text = Localization.getString("create_post_hint", language),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .testTag("post_content_input"),
                shape = RoundedCornerShape(16.dp),
                enabled = isUserLoggedIn
            )

            // Character counter and Quick Tags
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, start = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    trendingTags.take(3).forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = TeaLeafGreenContainer,
                            modifier = Modifier.clickable {
                                if (!content.contains(tag)) {
                                    content = if (content.isBlank()) tag else "$content $tag"
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Tag, contentDescription = null, modifier = Modifier.size(10.dp), tint = TeaLeafGreenDark)
                                Text(tag.removePrefix("#"), fontSize = 11.sp, color = TeaLeafGreenDark, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
                Text(
                    text = "${content.length}/1000",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Selection
            Text(
                text = Localization.getString("select_category", language),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TeaLeafGreenPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chai Mood Selection
            Text(
                text = Localization.getString("select_mood", language),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chaiMoods.forEach { mood ->
                    FilterChip(
                        selected = selectedMood == mood,
                        onClick = { selectedMood = mood },
                        label = { Text(mood, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ChaiAmber,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Photo / Video Upload & Attachments Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📸 फोटो व वीडियो संलग्नक (Upload Media)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        IconButton(onClick = { showUrlInputs = !showUrlInputs }) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = "Toggle URL Input",
                                tint = TeaLeafGreenPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Media Upload Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Pick Photo from Gallery
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(arrayOf("image/*"))
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TeaLeafGreenPrimary)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("फोटो चुनें", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        // Pick Video from Gallery
                        OutlinedButton(
                            onClick = {
                                videoPickerLauncher.launch(arrayOf("video/*"))
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ChaiKadakBrown)
                        ) {
                            Icon(Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("वीडियो चुनें", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset Quick Photos
                    Text(
                        text = "त्वरित फोटो प्रीसेट (Quick Preset):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        samplePhotoPresets.forEach { (label, url) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (imageUrl == url) TeaLeafGreenPrimary else MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .clickable {
                                        imageUrl = if (imageUrl == url) "" else url
                                    }
                                    .padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = if (imageUrl == url) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    // Optional Manual URL inputs
                    if (showUrlInputs) {
                        Spacer(modifier = Modifier.height(12.dp))

                        // Image URL Input
                        OutlinedTextField(
                            value = imageUrl,
                            onValueChange = { imageUrl = it },
                            label = { Text(Localization.getString("add_image_url", language), fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Image, contentDescription = null, tint = TeaLeafGreenPrimary) },
                            trailingIcon = {
                                if (imageUrl.isNotBlank()) {
                                    IconButton(onClick = { imageUrl = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Video URL Input
                        OutlinedTextField(
                            value = videoUrl,
                            onValueChange = { videoUrl = it },
                            label = { Text(Localization.getString("add_video_url", language), fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Videocam, contentDescription = null, tint = ChaiAmber) },
                            trailingIcon = {
                                if (videoUrl.isNotBlank()) {
                                    IconButton(onClick = { videoUrl = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Image Preview
            if (imageUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(14.dp))
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Image Preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clickable { imageUrl = "" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove Photo",
                            tint = Color.White,
                            modifier = Modifier.padding(6.dp).size(16.dp)
                        )
                    }
                }
            }

            // Video Preview Badge
            if (videoUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TeaLeafGreenContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = TeaLeafGreenDark, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("वीडियो संलग्न है ($videoDuration)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TeaLeafGreenDark)
                                Text(videoUrl.take(35) + "...", fontSize = 10.sp, color = TeaLeafGreenDark.copy(alpha = 0.8f))
                            }
                        }
                        IconButton(onClick = { videoUrl = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Remove Video", tint = TeaLeafGreenDark)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
