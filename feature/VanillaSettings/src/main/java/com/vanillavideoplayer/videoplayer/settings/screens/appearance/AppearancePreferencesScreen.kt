package com.vanillavideoplayer.videoplayer.settings.screens.appearance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vanillavideoplayer.videoplayer.core.model.Size
import com.vanillavideoplayer.videoplayer.core.model.ThemeConfigEnum
import com.vanillavideoplayer.videoplayer.core.ui.NativeAdCompose
import com.vanillavideoplayer.videoplayer.core.ui.R
import com.vanillavideoplayer.videoplayer.core.ui.composviews.RadioStringBtnView
import com.vanillavideoplayer.videoplayer.core.ui.composviews.VanillaPlayerTopBar
import com.vanillavideoplayer.videoplayer.core.ui.composviews.VanillaPrefSwitchView
import com.vanillavideoplayer.videoplayer.core.ui.composviews.VanillaPrefSwitchWithDividerView
import com.vanillavideoplayer.videoplayer.core.ui.designsystem.VanillaIcons
import com.vanillavideoplayer.videoplayer.core.ui.theme.supportsDynamicTheming
import com.vanillavideoplayer.videoplayer.settings.composables.SubtitlePrefs
import com.vanillavideoplayer.videoplayer.settings.composables.chooserDialog
import com.vanillavideoplayer.videoplayer.settings.x.nameString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearancePreferencesScreen(
    onNavigateUp: () -> Unit,
    viewModel: AppearancePreferencesViewModel = hiltViewModel()
) {
    val preferences by viewModel.preferencesFlow.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            // TODO: Check why the appbar flickers when changing the theme with small appbar and not with large appbar
            VanillaPlayerTopBar(
                s = stringResource(id = R.string.appearance_name),
                behavior = scrollBehaviour,
                icon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = VanillaIcons.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_up)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(state = rememberScrollState())
        ) {
            SubtitlePrefs(string = stringResource(id = R.string.appearance_name))
            DarkThemeSetting(
                currentPreference = preferences.themeConfigEnum,
                onChecked = viewModel::toggleDarkTheme,
                onClick = { viewModel.showDialog(AppearancePreferenceDialog.Theme) }
            )
            NativeAdCompose()
            DynamicThemingSetting(
                isChecked = preferences.useDynamicColors,
                onClick = viewModel::toggleUseDynamicColors
            )
            HighContrastDarkThemeSetting(
                isChecked = preferences.useHighContrastDarkTheme,
                onClick = viewModel::toggleUseHighContrastDarkTheme
            )


        }

        uiState.showDialog?.let { showDialog ->
            when (showDialog) {
                AppearancePreferenceDialog.Theme -> {
                    chooserDialog(
                        header = stringResource(id = R.string.dark_theme),
                        onDismissDialog = viewModel::hideDialog
                    ) {
                        items(ThemeConfigEnum.entries.toTypedArray()) {
                            RadioStringBtnView(
                                s = it.nameString(),
                                selected = (it == preferences.themeConfigEnum),
                                function = {
                                    viewModel.updateThemeConfig(it)
                                    viewModel.hideDialog()
                                }
                            )
                        }
                    }
                }

                AppearancePreferenceDialog.ThumbnailSize -> {
                    chooserDialog(
                        header = stringResource(id = R.string.thumbnail_size),
                        onDismissDialog = viewModel::hideDialog
                    ) {
                        items(Size.entries.toTypedArray()) {
                            RadioStringBtnView(
                                s = it.nameString(),
                                selected = (it == preferences.thumbnailSize),
                                function = {
                                    viewModel.updateThumbnailSize(it)
                                    viewModel.hideDialog()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DarkThemeSetting(
    currentPreference: ThemeConfigEnum,
    onChecked: () -> Unit,
    onClick: () -> Unit
) {
    VanillaPrefSwitchWithDividerView(
        s = stringResource(id = R.string.dark_theme),
        desc = currentPreference.nameString(),
        isChecked = currentPreference == ThemeConfigEnum.THEME_ON,
        checked = onChecked,
        imageVector = VanillaIcons.DarkMode,
        click = onClick
    )
}

@Composable
fun HighContrastDarkThemeSetting(
    isChecked: Boolean,
    onClick: () -> Unit
) {
    VanillaPrefSwitchView(
        title = stringResource(R.string.high_contrast_dark_theme),
        description = stringResource(R.string.high_contrast_dark_theme_desc),
        isChecked = isChecked,
        onClick = onClick,
        icon = VanillaIcons.Contrast
    )
}

@Composable
fun DynamicThemingSetting(
    isChecked: Boolean,
    onClick: () -> Unit
) {
    if (supportsDynamicTheming()) {
        VanillaPrefSwitchView(
            title = stringResource(id = R.string.dynamic_theme),
            description = stringResource(id = R.string.dynamic_theme_description),
            isChecked = isChecked,
            onClick = onClick,
            icon = VanillaIcons.Appearance
        )
    }
}

