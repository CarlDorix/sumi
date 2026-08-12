package tachiyomi.presentation.core.components.material

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Vertical space occupied by a floating navigation bar that content is allowed to scroll beneath.
 *
 * The bar is drawn as an overlay rather than a [Scaffold] `bottomBar`, so nothing reserves room for
 * it automatically. [Scaffold] adds this to the bottom of the padding it hands to content, which
 * keeps the last item in any list scrollable clear of the bar while still letting content pass
 * behind the translucent surface.
 *
 * Zero everywhere except beneath the home navigation, so pushed screens are unaffected.
 */
val LocalFloatingNavBarPadding = compositionLocalOf<Dp> { 0.dp }
