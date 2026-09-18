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
    primary = Color(0xFF63DBB3),
    onPrimary = Color(0xFF003828),
    primaryContainer = ZedEmeraldPrimaryDark,
    onPrimaryContainer = Color(0xFF81F8CD),
    secondary = Color(0xFFAEC6FF),
    onSecondary = Color(0xFF002E69),
    secondaryContainer = Color(0xFF00448F),
    onSecondaryContainer = Color(0xFFD8E2FF),
    tertiary = Color(0xFFFFB59D),
    onTertiary = Color(0xFF5E1B00),
    tertiaryContainer = Color(0xFF832900),
    onTertiaryContainer = Color(0xFFFFDBCF),
    background = ZedBackgroundDark,
    onBackground = Color(0xFFE0E3E1),
    surface = ZedSurfaceDark,
    onSurface = Color(0xFFE0E3E1),
    surfaceVariant = ZedSurfaceVariantDark,
    onSurfaceVariant = Color(0xFFBFC9C3),
    outline = ZedOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = ZedEmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = ZedEmeraldContainer,
    onPrimaryContainer = ZedOnEmeraldContainer,
    secondary = ZedNavySecondary,
    onSecondary = Color.White,
    secondaryContainer = ZedNavySecondaryContainer,
    onSecondaryContainer = ZedOnNavyContainer,
    tertiary = ZedCopperAccent,
    onTertiary = Color.White,
    tertiaryContainer = ZedCopperContainer,
    onTertiaryContainer = Color(0xFF3B0B00),
    background = ZedBackgroundLight,
    onBackground = Color(0xFF171D1B),
    surface = ZedSurfaceLight,
    onSurface = Color(0xFF171D1B),
    surfaceVariant = ZedSurfaceVariantLight,
    onSurfaceVariant = Color(0xFF3F4945),
    outline = ZedOutlineLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our brand palette by default for strong identity
    content: @Composable () -> Unit,
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
