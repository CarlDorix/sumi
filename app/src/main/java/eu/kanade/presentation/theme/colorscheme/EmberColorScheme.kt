package eu.kanade.presentation.theme.colorscheme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Colors for Ember theme
 *
 * A warm, high-contrast scheme. Neutrals carry a brown tint rather than sitting on pure grey, and
 * the on-colours are pushed further from their surfaces than M3 defaults, so text reads harder
 * against the background.
 *
 * Key colors:
 * Primary #FFB067 (amber)
 * Tertiary #D8C77E (warm gold)
 * Neutral #1A1410 (warm charcoal)
 */
internal object EmberColorScheme : BaseColorScheme() {

    override val darkScheme = darkColorScheme(
        primary = Color(0xFFFFB067),
        onPrimary = Color(0xFF4A2400),
        primaryContainer = Color(0xFF6B3A00),
        onPrimaryContainer = Color(0xFFFFDCC2),
        inversePrimary = Color(0xFF9A4E00),
        secondary = Color(0xFFFFB067), // Unread badge
        onSecondary = Color(0xFF4A2400), // Unread badge text
        secondaryContainer = Color(0xFF5C4534), // Navigation bar selector pill & progress indicator (remaining)
        onSecondaryContainer = Color(0xFFFFD6B0), // Navigation bar selector icon
        tertiary = Color(0xFFD8C77E), // Downloaded badge
        onTertiary = Color(0xFF3A3300), // Downloaded badge text
        tertiaryContainer = Color(0xFF574C1B),
        onTertiaryContainer = Color(0xFFF5E39A),
        background = Color(0xFF1A1410),
        onBackground = Color(0xFFF7ECE2),
        surface = Color(0xFF1A1410),
        onSurface = Color(0xFFF7ECE2),
        surfaceVariant = Color(0xFF2A211A), // Navigation bar background (ThemePrefWidget)
        onSurfaceVariant = Color(0xFFE0CFC0),
        surfaceTint = Color(0xFFFFB067),
        inverseSurface = Color(0xFFF7ECE2),
        inverseOnSurface = Color(0xFF1A1410),
        outline = Color(0xFFA6907C),
        surfaceContainerLowest = Color(0xFF150F0B),
        surfaceContainerLow = Color(0xFF201913),
        surfaceContainer = Color(0xFF2A211A), // Navigation bar background
        surfaceContainerHigh = Color(0xFF362B22),
        surfaceContainerHighest = Color(0xFF43362B),
    )

    override val lightScheme = lightColorScheme(
        primary = Color(0xFF9A4E00),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFFFDCC2),
        onPrimaryContainer = Color(0xFF321400),
        inversePrimary = Color(0xFFFFB067),
        secondary = Color(0xFF9A4E00), // Unread badge
        onSecondary = Color(0xFFFFFFFF), // Unread badge text
        secondaryContainer = Color(0xFFFFDCC2), // Navigation bar selector pill & progress indicator (remaining)
        onSecondaryContainer = Color(0xFF321400), // Navigation bar selector icon
        tertiary = Color(0xFF6B5D0F), // Downloaded badge
        onTertiary = Color(0xFFFFFFFF), // Downloaded badge text
        tertiaryContainer = Color(0xFFF5E39A),
        onTertiaryContainer = Color(0xFF211B00),
        background = Color(0xFFFFF8F2),
        onBackground = Color(0xFF22190F),
        surface = Color(0xFFFFF8F2),
        onSurface = Color(0xFF22190F),
        surfaceVariant = Color(0xFFF2E3D5), // Navigation bar background (ThemePrefWidget)
        onSurfaceVariant = Color(0xFF52443A),
        surfaceTint = Color(0xFF9A4E00),
        inverseSurface = Color(0xFF372F27),
        inverseOnSurface = Color(0xFFFDEEE3),
        outline = Color(0xFF6E5D4F),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFFFF1E6),
        surfaceContainer = Color(0xFFF8EADC), // Navigation bar background
        surfaceContainerHigh = Color(0xFFF2E3D5),
        surfaceContainerHighest = Color(0xFFECDCCC),
    )
}
