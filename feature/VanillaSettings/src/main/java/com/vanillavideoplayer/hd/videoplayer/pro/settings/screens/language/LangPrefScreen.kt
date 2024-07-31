package com.vanillavideoplayer.hd.videoplayer.pro.settings.screens.language

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.LangData
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.NativeAdCompose
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.R
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaPlayerTopBar
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.designsystem.VanillaIcons
import com.vanillavideoplayer.hd.videoplayer.pro.settings.GlobalPrefs
import com.vanillavideoplayer.hd.videoplayer.pro.settings.x.updateLocale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val languages = mutableStateListOf(
    LangData("English", ""), LangData("Arabic", "ar"), LangData("German", "de"), LangData("Greek", "el"), LangData("Spanish", "es"), LangData("Persian", "fa"), LangData("French", "fr"), LangData("Hindi", "hi"), LangData("Italian", "it"), LangData("Hebrew", "iw"), LangData("Japanese", "ja"), LangData("Korean", "ko"), LangData("Dutch", "nl"), LangData("Punjabi", "pa"), LangData("Polish", "pl"), LangData("Portuguese", "pt"), LangData("Russian", "ru"), LangData("Turkish", "tr"), LangData("Ukrainian", "uk")
)

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LangPrefScreen(
    onNavigateUp: () -> Unit = {},
    shouldShowBackNavigation: Boolean = true,
) {
    val context = LocalContext.current

    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior()
    var selectedLanguageCode by remember { mutableStateOf(GlobalPrefs().getLangCode(context)) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection), topBar = {
        VanillaPlayerTopBar(s = stringResource(id = R.string.language), behavior = scrollBehaviour, icon = {
            if (shouldShowBackNavigation) {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = VanillaIcons.ArrowBack, contentDescription = stringResource(id = R.string.navigate_up)
                    )
                }
            }
        }, function = {
            IconButton(
                onClick = {
                    updateLocale(selectedLanguageCode, context)
                    GlobalPrefs().setLangCode(context, selectedLanguageCode)
                    onNavigateUp()
                }, modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = VanillaIcons.Check, contentDescription = stringResource(id = R.string.done)
                )
            }
        })
    }) { paddingVal ->

        Column(modifier = Modifier.padding(paddingVal)) {
            var adLoaded by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    delay(50) // 50ms delay
                    adLoaded = true
                }
            }

            if (adLoaded) {
                NativeAdCompose(fromWhichScreen = "Language")
            }
            LanguageList(languages = languages, selectedLanguageCode = selectedLanguageCode) { clickedLanguage ->
                selectedLanguageCode = clickedLanguage.langCode
            }
        }

    }
}

@Composable
fun LanguageList(languages: List<LangData>, selectedLanguageCode: String?, onLanguageClick: (LangData) -> Unit) {
    LazyColumn {
        items(languages) { language ->
            LanguageItem(language = language, selectedLanguageCode = selectedLanguageCode, onLanguageClick = onLanguageClick)
        }
    }
}

@Composable
fun LanguageItem(language: LangData, selectedLanguageCode: String?, onLanguageClick: (LangData) -> Unit) {

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onLanguageClick(language) }, elevation = CardDefaults.cardElevation(if (language.langCode == selectedLanguageCode) 8.dp else 2.dp), colors = CardDefaults.cardColors(if (language.langCode == selectedLanguageCode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(vertical = 16.dp, horizontal = 16.dp * 1.5F), contentAlignment = Alignment.CenterStart
        ) {

            Text(
                text = language.langName,
                maxLines = 1,
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 15.sp, fontWeight = FontWeight.Medium),
            )
            if (language.langCode == selectedLanguageCode) {
                Icon(
                    imageVector = Icons.Default.Check, contentDescription = stringResource(id = R.string.language), tint = MaterialTheme.colorScheme.primary, modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}
