package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = TeaLeafGreenLight,
    onPrimary = Color(0xFF052E16),
    primaryContainer = TeaLeafGreenDark,
    onPrimaryContainer = Color(0xFFDCFCE7),
    secondary = ChaiAmberLight,
    onSecondary = Color(0xFF451A03),
    secondaryContainer = ChaiKadakBrown,
    onSecondaryContainer = Color(0xFFFEF3C7),
    tertiary = TeaLeafGreenAccent,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = Color(0xFFA7F3D0),
    background = ChaiDarkBackground,
    onBackground = Color(0xFFE2EFE5),
    surface = ChaiDarkSurface,
    onSurface = Color(0xFFE2EFE5),
    surfaceVariant = ChaiDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFB8CEBE),
    outline = ChaiDarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = TeaLeafGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = TeaLeafGreenContainer,
    onPrimaryContainer = TeaLeafGreenDark,
    secondary = ChaiKadakBrown,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = ChaiAmber,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFEF9C3),
    onTertiaryContainer = Color(0xFF713F12),
    background = ChaiBackgroundLight,
    onBackground = Color(0xFF142418),
    surface = ChaiSurfaceLight,
    onSurface = Color(0xFF142418),
    surfaceVariant = ChaiSurfaceVariantLight,
    onSurfaceVariant = Color(0xFF38523F),
    outline = ChaiOutlineLight
)

@Composable
fun JanmanchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    JanmanchTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

