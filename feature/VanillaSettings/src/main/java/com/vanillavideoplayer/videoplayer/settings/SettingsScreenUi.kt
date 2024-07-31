package com.vanillavideoplayer.videoplayer.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.harshil258.adplacer.utils.SharedPrefConfig.Companion.sharedPrefConfig
import com.vanillavideoplayer.videoplayer.core.ui.NativeAdCompose
import com.vanillavideoplayer.videoplayer.core.ui.R
import com.vanillavideoplayer.videoplayer.core.ui.composviews.VanillaClickablePrefItem
import com.vanillavideoplayer.videoplayer.core.ui.composviews.VanillaPlayerTopBar
import com.vanillavideoplayer.videoplayer.core.ui.designsystem.VanillaIcons
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class SettingEnum {
    APPEARANCE, MEDIA_LIB, PLAYER, DECODER_SETTING, UNLOCK_PREMIUM, AUDIO_PREFS, SUBTITLE_DETAILS, ABOUT_US, LANGUAGE
}

@Preview(showSystemUi = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenUi(
    onNavigateUp: () -> Unit = {},
    onItemClick: (SettingEnum) -> Unit = {},
    isPurchased: MutableState<Boolean> = mutableStateOf(false)
) {
    val scrollBehave = TopAppBarDefaults.pinnedScrollBehavior()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehave.nestedScrollConnection), topBar = {
        VanillaPlayerTopBar(s = stringResource(id = R.string.settings),
            behavior = scrollBehave,
            icon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = VanillaIcons.ArrowBack,
                        contentDescription = stringResource(id = R.string.navigate_up)
                    )
                }
            })
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(state = rememberScrollState())
        ) {

            val context = LocalContext.current
//            if (!isPurchased.value) {
            VanillaClickablePrefItem(title = stringResource(id = R.string.unlock_premium),
                description = stringResource(id = R.string.unlock_premium_description),
                icon = VanillaIcons.ShoppingCart,
                onClick = {
                    onItemClick(SettingEnum.UNLOCK_PREMIUM)
                })
//            }
            VanillaClickablePrefItem(title = stringResource(id = R.string.appearance_name),
                description = stringResource(id = R.string.appearance_description),
                icon = VanillaIcons.Appearance,
                onClick = {
                    onItemClick(SettingEnum.APPEARANCE)
                })
            VanillaClickablePrefItem(title = stringResource(id = R.string.language),
                description = stringResource(id = R.string.lanugage_description),
                icon = VanillaIcons.Language,
                onClick = {
                    onItemClick(SettingEnum.LANGUAGE)
                })
            VanillaClickablePrefItem(title = stringResource(id = R.string.player_name),
                description = stringResource(id = R.string.player_description),
                icon = VanillaIcons.Player,
                onClick = {
                    onItemClick(SettingEnum.PLAYER)
                })
            var adLoaded by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    delay(50) // 50ms delay
                    adLoaded = true
                }
            }

            if (adLoaded) {
                NativeAdCompose()
            }
            VanillaClickablePrefItem(title = stringResource(id = R.string.audio),
                description = stringResource(R.string.audio_desc),
                icon = VanillaIcons.Audio,
                onClick = {
                    onItemClick(SettingEnum.AUDIO_PREFS)
                })
            VanillaClickablePrefItem(title = stringResource(id = R.string.media_library),
                description = stringResource(id = R.string.media_library_description),
                icon = VanillaIcons.Movie,
                onClick = {
                    onItemClick(SettingEnum.MEDIA_LIB)
                })
            VanillaClickablePrefItem(title = stringResource(id = R.string.subtitle),
                description = stringResource(R.string.subtitle_desc),
                icon = VanillaIcons.Subtitle,
                onClick = {
                    onItemClick(SettingEnum.SUBTITLE_DETAILS)
                })
            VanillaClickablePrefItem(title = stringResource(id = R.string.decoder),
                description = stringResource(R.string.decoder_desc),
                icon = VanillaIcons.Decoder,
                onClick = {
                    onItemClick(SettingEnum.DECODER_SETTING)
                })
            VanillaClickablePrefItem(title = stringResource(id = R.string.about_name),
                description = stringResource(id = R.string.about_description),
                icon = VanillaIcons.Info,
                onClick = {
                    sharedPrefConfig.appDetails.privacyPolicyUrl.takeIf { it.isNotEmpty() }.let {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(it)
                        )
                        context.startActivity(intent)
                    }
                })
        }
    }
}

