package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.i18n.Localization
import com.example.model.AppLanguage
import com.example.model.UserEntity
import com.example.ui.theme.ChaiAmber
import com.example.ui.theme.ChaiTerracotta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    language: AppLanguage,
    isDarkTheme: Boolean,
    adminModeEnabled: Boolean,
    blockedUsers: List<UserEntity>,
    currentUser: UserEntity?,
    onBack: () -> Unit,
    onToggleLanguage: () -> Unit,
    onToggleTheme: () -> Unit,
    onToggleAdminMode: () -> Unit,
    onOpenModeration: () -> Unit,
    onUnblockUser: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showBlockedListDialog by remember { mutableStateOf(false) }
    var showGuidelinesDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Localization.getString("settings_title", language), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Preferences Section
            item {
                Text(
                    text = Localization.getString("app_preferences", language),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = ChaiTerracotta,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        // Language Switcher
                        SettingsRow(
                            icon = Icons.Default.Language,
                            title = Localization.getString("language_label", language),
                            subtitle = if (language == AppLanguage.HINDI) "हिंदी (Hindi)" else "English",
                            onClick = onToggleLanguage
                        )

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                        // Theme Toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DarkMode, contentDescription = null, tint = ChaiTerracotta)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = Localization.getString("dark_theme", language),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                                )
                            }
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = { onToggleTheme() },
                                colors = SwitchDefaults.colors(checkedThumbColor = ChaiTerracotta)
                            )
                        }
                    }
                }
            }

            // Safety & Community Section
            item {
                Text(
                    text = Localization.getString("safety_and_rules", language),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = ChaiTerracotta,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        // Blocked Users
                        SettingsRow(
                            icon = Icons.Default.Block,
                            title = Localization.getString("blocked_users", language),
                            subtitle = "${blockedUsers.size} सदस्य ब्लॉक किए गए",
                            onClick = { showBlockedListDialog = true }
                        )

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                        // Community Guidelines
                        SettingsRow(
                            icon = Icons.Default.Gavel,
                            title = Localization.getString("community_guidelines", language),
                            subtitle = "सद्भाव, मर्यादा और संवाद के नियम",
                            onClick = { showGuidelinesDialog = true }
                        )
                    }
                }
            }

            // Admin & Moderation Section
            item {
                Text(
                    text = Localization.getString("admin_section", language),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = ChaiTerracotta,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = ChaiTerracotta)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = Localization.getString("admin_mode", language),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                                    )
                                    Text(
                                        text = "प्रशासनिक नियंत्रण सक्षम करें",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }
                            }
                            Switch(
                                checked = adminModeEnabled || (currentUser?.isAdmin == true),
                                onCheckedChange = { onToggleAdminMode() },
                                colors = SwitchDefaults.colors(checkedThumbColor = ChaiTerracotta)
                            )
                        }

                        if (adminModeEnabled || currentUser?.isAdmin == true) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            SettingsRow(
                                icon = Icons.Default.Security,
                                title = Localization.getString("moderation_queue", language),
                                subtitle = "शिकायतें देखें और कार्रवाई करें",
                                onClick = onOpenModeration
                            )
                        }
                    }
                }
            }

            // App Info Section
            item {
                Text(
                    text = Localization.getString("about_app", language),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = ChaiTerracotta,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        SettingsRow(
                            icon = Icons.Default.Info,
                            title = "Janmanch India Tea v1.0.0",
                            subtitle = "स्वदेशी संवाद मंच • आत्मनिर्भर भारत",
                            onClick = { showAboutDialog = true }
                        )
                    }
                }
            }

            // Logout Button
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("logout_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Localization.getString("logout_button", language),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(Localization.getString("logout_confirm_title", language), fontWeight = FontWeight.Bold) },
            text = { Text(Localization.getString("logout_confirm_msg", language)) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(Localization.getString("logout_button", language))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(Localization.getString("cancel", language))
                }
            }
        )
    }

    // Blocked Users Dialog
    if (showBlockedListDialog) {
        AlertDialog(
            onDismissRequest = { showBlockedListDialog = false },
            title = { Text(Localization.getString("blocked_users", language), fontWeight = FontWeight.Bold) },
            text = {
                if (blockedUsers.isEmpty()) {
                    Text("कोई ब्लॉक किया गया सदस्य नहीं है।")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        blockedUsers.forEach { user ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = user.fullName, fontWeight = FontWeight.Medium)
                                OutlinedButton(
                                    onClick = { onUnblockUser(user.id) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(Localization.getString("unblock", language), fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBlockedListDialog = false }) {
                    Text(Localization.getString("cancel", language))
                }
            }
        )
    }

    // Community Guidelines Dialog
    if (showGuidelinesDialog) {
        AlertDialog(
            onDismissRequest = { showGuidelinesDialog = false },
            title = { Text("☕ समुदाय दिशानिर्देश", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("1. सभी सदस्यों के प्रति सम्मानजनक भाषा का प्रयोग करें।")
                    Text("2. भ्रामक समाचार अथवा अभद्र सामग्री पोस्ट न करें।")
                    Text("3. चाय और चर्चा के मंच पर स्वस्थ और रचनात्मक संवाद को बढ़ावा दें।")
                    Text("4. किसी भी नियम उल्लंघन की शिकायत रिपोर्ट बटन से करें।")
                }
            },
            confirmButton = {
                Button(
                    onClick = { showGuidelinesDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ChaiTerracotta)
                ) {
                    Text("समझ गया")
                }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("☕ जनमंच इंडिया टी", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("जनमंच इंडिया टी भारत का अपना सोशल मीडिया मंच है जहाँ हर नागरिक अपनी बात बेबाक रूप से रख सकता है।")
                    Text("संस्करण: 1.0.0 (Release APK Ready)")
                    Text("आर्किटेक्चर: Kotlin, Jetpack Compose, Room Cache & Firebase Ready")
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ChaiTerracotta)
                ) {
                    Text("ठीक है")
                }
            }
        )
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = ChaiTerracotta)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
