package com.vanillavideoplayer.hd.videoplayer.pro.settings.screens.subtitle

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.FontEnum
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.NativeAdCompose
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.R
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.RadioStringBtnView
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaCancelButton
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaClickablePrefItem
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaDoneButton
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaPlayerDialog
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaPlayerTopBar
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaPrefSwitchView
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaPrefSwitchWithDividerView
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.designsystem.VanillaIcons
import com.vanillavideoplayer.hd.videoplayer.pro.settings.composables.SubtitlePrefs
import com.vanillavideoplayer.hd.videoplayer.pro.settings.composables.chooserDialog
import com.vanillavideoplayer.hd.videoplayer.pro.settings.utils.DesiHelperObj
import com.vanillavideoplayer.hd.videoplayer.pro.settings.x.nameString
import java.nio.charset.Charset

@Composable
fun SuggestedSubLangSetting(
    defaultLang: String, onClickEvent: () -> Unit
) {
    VanillaClickablePrefItem(title = stringResource(id = R.string.preferred_subtitle_lang), description = defaultLang.takeIf { it.isNotBlank() } ?: stringResource(
        id = R.string.preferred_subtitle_lang_description
    ), icon = VanillaIcons.Language, onClick = onClickEvent)
}


@Composable
fun SubTextEncodingPref(
    defaultEncoding: String, onClickEvent: () -> Unit
) {
    VanillaClickablePrefItem(
        title = stringResource(R.string.subtitle_text_encoding), description = defaultEncoding, icon = VanillaIcons.Subtitle, onClick = onClickEvent
    )
}

@Composable
fun SubEmbeddedStylesPref(
    boolChecked: Boolean, onClickEvent: () -> Unit
) {
    VanillaPrefSwitchView(
        title = stringResource(R.string.embedded_styles), description = stringResource(R.string.embedded_styles_desc), icon = VanillaIcons.Style, isChecked = boolChecked, onClick = onClickEvent
    )
}

@Composable
fun SubTextSizePref(
    currentSize: Int, onClickEvent: () -> Unit, enabled: Boolean
) {
    VanillaClickablePrefItem(
        title = stringResource(id = R.string.subtitle_text_size), description = currentSize.toString(), icon = VanillaIcons.FontSize, onClick = onClickEvent, enabled = enabled
    )
}


@Composable
fun SubFontPref(
    defaultFontEnum: FontEnum, onClickEvent: () -> Unit, boolOn: Boolean
) {
    VanillaClickablePrefItem(
        title = stringResource(id = R.string.subtitle_font), description = defaultFontEnum.nameString(), icon = VanillaIcons.Font, onClick = onClickEvent, enabled = boolOn
    )
}


@Composable
fun UseSysCaptionStyle(
    boolChecked: Boolean, onCheckedEvent: () -> Unit, onClickEvent: () -> Unit
) {
    VanillaPrefSwitchWithDividerView(
        s = stringResource(R.string.system_caption_style), desc = stringResource(R.string.system_caption_style_desc), isChecked = boolChecked, checked = onCheckedEvent, imageVector = VanillaIcons.Caption, click = onClickEvent
    )
}


@Composable
fun SubBackgroundPref(
    boolChecked: Boolean, onClickEvent: () -> Unit, enabled: Boolean
) {
    VanillaPrefSwitchView(
        title = stringResource(id = R.string.subtitle_background), description = stringResource(id = R.string.subtitle_background_desc), icon = VanillaIcons.Background, isChecked = boolChecked, onClick = onClickEvent, enabled = enabled
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubtitlePrefScreen(
    onNavUp: () -> Unit, subVM: SubtitlePrefVM = hiltViewModel()
) {
    val pref by subVM.prefFlow.collectAsStateWithLifecycle()
    val uiLevel by subVM.uiLevel.collectAsStateWithLifecycle()
    val lang = remember { listOf(Pair("None", "")) + DesiHelperObj.getAvailableDesis() }
    val charsetRes = stringArrayResource(id = R.array.charsets_list)
    val context = LocalContext.current

    val scrollBehav = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehav.nestedScrollConnection), topBar = {
        VanillaPlayerTopBar(s = stringResource(id = R.string.subtitle), behavior = scrollBehav, icon = {
            IconButton(onClick = onNavUp) {
                Icon(
                    imageVector = VanillaIcons.ArrowBack, contentDescription = stringResource(id = R.string.navigate_up)
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
            SubtitlePrefs(string = stringResource(id = R.string.playback))
            SuggestedSubLangSetting(defaultLang = DesiHelperObj.getDesiDisplayLang(pref.suggestedSubtitleLang), onClickEvent = { subVM.displayDialog(SubtitlePrefDialog.SubLangDialog) })
            SubTextEncodingPref(defaultEncoding = charsetRes.first { it.contains(pref.subtitleTextEncodingVal) }, onClickEvent = { subVM.displayDialog(SubtitlePrefDialog.SubEncodingDialog) })
            NativeAdCompose()
            SubtitlePrefs(string = stringResource(id = R.string.appearance_name))
            SubFontPref(
                defaultFontEnum = pref.subtitleFontEnum, onClickEvent = { subVM.displayDialog(SubtitlePrefDialog.SubFontDialog) }, boolOn = pref.boolSystemCaptionStyle.not()
            )
            SubTextBoldPref(
                boolChecked = pref.boolSubtitleTextBold, onClickEvent = subVM::switchSubtitleToBold, boolEnabled = pref.boolSystemCaptionStyle.not()
            )
            SubTextSizePref(
                currentSize = pref.subtitleTextSizeVal, onClickEvent = { subVM.displayDialog(SubtitlePrefDialog.SubSizeDialog) }, enabled = pref.boolSystemCaptionStyle.not()
            )

            UseSysCaptionStyle(boolChecked = pref.boolSystemCaptionStyle, onCheckedEvent = subVM::switchUseSystemCaptionStyle, onClickEvent = { context.startActivity(Intent(Settings.ACTION_CAPTIONING_SETTINGS)) })

            SubEmbeddedStylesPref(
                boolChecked = pref.boolApplyEmbeddedStyles, onClickEvent = subVM::switchApplyEmbeddedStyles
            )
            SubBackgroundPref(
                boolChecked = pref.boolSubtitleBackground, onClickEvent = subVM::switchSubtitleBackground, enabled = pref.boolSystemCaptionStyle.not()
            )

        }

        uiLevel.subPrefDialog?.let { showDialog ->
            when (showDialog) {
                SubtitlePrefDialog.SubLangDialog -> {
                    chooserDialog(
                        header = stringResource(id = R.string.preferred_subtitle_lang), onDismissDialog = subVM::dismissDialog
                    ) {
                        items(lang) {
                            RadioStringBtnView(s = it.first, selected = it.second == pref.suggestedSubtitleLang, function = {
                                subVM.changeSubtitleLanguage(it.second)
                                subVM.dismissDialog()
                            })
                        }
                    }
                }

                SubtitlePrefDialog.SubFontDialog -> {
                    chooserDialog(
                        header = stringResource(id = R.string.subtitle_font), onDismissDialog = subVM::dismissDialog
                    ) {
                        items(FontEnum.entries.toTypedArray()) {
                            RadioStringBtnView(s = it.nameString(), selected = it == pref.subtitleFontEnum, function = {
                                subVM.changeSubtitleFont(it)
                                subVM.dismissDialog()
                            })
                        }
                    }
                }

                SubtitlePrefDialog.SubSizeDialog -> {
                    var size by remember { mutableIntStateOf(pref.subtitleTextSizeVal) }

                    VanillaPlayerDialog(onDismissRequest = subVM::dismissDialog, title = { Text(text = stringResource(id = R.string.subtitle_text_size)) }, content = {
                        Text(
                            text = size.toString(), modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp), textAlign = TextAlign.Center, fontSize = size.sp, style = MaterialTheme.typography.titleMedium
                        )
                        Slider(
                            value = size.toFloat(), onValueChange = { size = it.toInt() }, valueRange = 15f..60f
                        )
                    }, confirmButton = {
                        VanillaDoneButton(onClick = {
                            subVM.changeSubtitleFontSize(size)
                            subVM.dismissDialog()
                        })
                    }, dismissButton = { VanillaCancelButton(onClick = subVM::dismissDialog) })
                }

                SubtitlePrefDialog.SubEncodingDialog -> {
                    chooserDialog(
                        header = stringResource(id = R.string.subtitle_text_encoding), onDismissDialog = subVM::dismissDialog
                    ) {
                        items(charsetRes) {
                            val currentCharset = it.substringAfterLast("(", "").removeSuffix(")")
                            if (currentCharset.isEmpty() || Charset.isSupported(currentCharset)) {
                                RadioStringBtnView(s = it, selected = currentCharset == pref.subtitleTextEncodingVal, function = {
                                    subVM.changeSubtitleEncoding(currentCharset)
                                    subVM.dismissDialog()
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubTextBoldPref(
    boolChecked: Boolean, onClickEvent: () -> Unit, boolEnabled: Boolean
) {
    VanillaPrefSwitchView(
        title = stringResource(id = R.string.subtitle_text_bold), description = stringResource(id = R.string.subtitle_text_bold_desc), icon = VanillaIcons.Bold, isChecked = boolChecked, onClick = onClickEvent, enabled = boolEnabled
    )
}

