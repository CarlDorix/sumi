package eu.kanade.presentation.more

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.GetApp
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import eu.kanade.presentation.components.PreferenceCard
import eu.kanade.presentation.components.PreferenceSectionHeader
import eu.kanade.presentation.more.settings.screen.SettingsMainScreen
import eu.kanade.presentation.more.settings.widget.SwitchPreferenceWidget
import eu.kanade.presentation.more.settings.widget.TextPreferenceWidget
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.more.DownloadQueueState
import tachiyomi.core.common.Constants
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.ScrollbarLazyColumn
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.i18n.pluralStringResource
import tachiyomi.presentation.core.i18n.stringResource
import cafe.adriel.voyager.core.screen.Screen as VoyagerScreen

/**
 * Everything that isn't reading: library-wide toggles and actions at the top, then every settings
 * category, grouped into scannable cards.
 *
 * Tools used to be its own navigation destination; folding it in here keeps the bottom bar to the
 * three things you actually move between, and the split still reads because each group is labelled.
 */
@Composable
fun SettingsListScreen(
    downloadQueueStateProvider: () -> DownloadQueueState,
    downloadedOnly: Boolean,
    onDownloadedOnlyChange: (Boolean) -> Unit,
    incognitoMode: Boolean,
    onIncognitoModeChange: (Boolean) -> Unit,
    onClickDownloadQueue: () -> Unit,
    onClickCategories: () -> Unit,
    onClickStats: () -> Unit,
    onClickSettingsScreen: (VoyagerScreen) -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val allItems = SettingsMainScreen.items

    // Grouped positionally against the upstream ordering: appearance/library/reader, then the
    // content-facing settings, then system-level ones, with About pulled out to sit with support.
    val appearance = allItems.take(3)
    val content = allItems.drop(3).take(3)
    val system = allItems.drop(6).dropLast(1)
    val about = allItems.takeLast(1)

    Scaffold { contentPadding ->
        ScrollbarLazyColumn(contentPadding = contentPadding) {
            item { PreferenceSectionHeader(stringResource(MR.strings.tools_group_toggles)) }
            item {
                PreferenceCard {
                    SwitchPreferenceWidget(
                        title = stringResource(MR.strings.label_downloaded_only),
                        subtitle = stringResource(MR.strings.downloaded_only_summary),
                        icon = Icons.Outlined.CloudOff,
                        checked = downloadedOnly,
                        onCheckedChanged = onDownloadedOnlyChange,
                    )
                    SwitchPreferenceWidget(
                        title = stringResource(MR.strings.pref_incognito_mode),
                        subtitle = stringResource(MR.strings.pref_incognito_mode_summary),
                        icon = ImageVector.vectorResource(R.drawable.ic_glasses_24dp),
                        checked = incognitoMode,
                        onCheckedChanged = onIncognitoModeChange,
                    )
                }
            }

            item { PreferenceSectionHeader(stringResource(MR.strings.tools_group_library)) }
            item {
                PreferenceCard {
                    val downloadQueueState = downloadQueueStateProvider()
                    TextPreferenceWidget(
                        title = stringResource(MR.strings.label_download_queue),
                        subtitle = when (downloadQueueState) {
                            DownloadQueueState.Stopped -> null
                            is DownloadQueueState.Paused -> {
                                val pending = downloadQueueState.pending
                                if (pending == 0) {
                                    stringResource(MR.strings.paused)
                                } else {
                                    "${stringResource(MR.strings.paused)} • ${
                                        pluralStringResource(
                                            MR.plurals.download_queue_summary,
                                            count = pending,
                                            pending,
                                        )
                                    }"
                                }
                            }
                            is DownloadQueueState.Downloading -> {
                                val pending = downloadQueueState.pending
                                pluralStringResource(MR.plurals.download_queue_summary, count = pending, pending)
                            }
                        },
                        icon = Icons.Outlined.GetApp,
                        onPreferenceClick = onClickDownloadQueue,
                    )
                    TextPreferenceWidget(
                        title = stringResource(MR.strings.categories),
                        icon = Icons.AutoMirrored.Outlined.Label,
                        onPreferenceClick = onClickCategories,
                    )
                    TextPreferenceWidget(
                        title = stringResource(MR.strings.label_stats),
                        icon = Icons.Outlined.QueryStats,
                        onPreferenceClick = onClickStats,
                    )
                }
            }

            settingsGroup(MR.strings.settings_group_appearance, appearance, onClickSettingsScreen)
            settingsGroup(MR.strings.settings_group_content, content, onClickSettingsScreen)
            settingsGroup(MR.strings.settings_group_system, system, onClickSettingsScreen)

            item { PreferenceSectionHeader(stringResource(MR.strings.settings_group_about)) }
            item {
                PreferenceCard {
                    about.forEach { item ->
                        TextPreferenceWidget(
                            title = stringResource(item.titleRes),
                            subtitle = item.formatSubtitle(),
                            icon = item.icon,
                            onPreferenceClick = { onClickSettingsScreen(item.screen) },
                        )
                    }
                    // No "Support us": it fundraises for Mihon, which is misleading from inside
                    // a fork they neither maintain nor are paid for.
                    TextPreferenceWidget(
                        title = stringResource(MR.strings.label_help),
                        icon = Icons.AutoMirrored.Outlined.HelpOutline,
                        onPreferenceClick = { uriHandler.openUri(Constants.URL_HELP) },
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

private fun LazyListScope.settingsGroup(
    titleRes: StringResource,
    items: List<SettingsMainScreen.Item>,
    onClickSettingsScreen: (VoyagerScreen) -> Unit,
) {
    if (items.isEmpty()) return

    item { PreferenceSectionHeader(stringResource(titleRes)) }
    item {
        PreferenceCard {
            items.forEach { item ->
                TextPreferenceWidget(
                    title = stringResource(item.titleRes),
                    subtitle = item.formatSubtitle(),
                    icon = item.icon,
                    onPreferenceClick = { onClickSettingsScreen(item.screen) },
                )
            }
        }
    }
}
