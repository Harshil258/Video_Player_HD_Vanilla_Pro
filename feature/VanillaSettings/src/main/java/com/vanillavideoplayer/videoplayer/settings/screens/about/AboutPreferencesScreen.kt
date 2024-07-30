package com.vanillavideoplayer.videoplayer.settings.screens.about

import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.harshil258.adplacer.utils.SharedPrefConfig.Companion.sharedPrefConfig
import com.vanillavideoplayer.videoplayer.core.ui.R
import com.vanillavideoplayer.videoplayer.core.ui.composviews.VanillaPlayerTopBar
import com.vanillavideoplayer.videoplayer.core.ui.designsystem.VanillaIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPreferencesScreen(
    onNavigateUp: () -> Unit,
) {
    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    Scaffold(modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection), topBar = {
        VanillaPlayerTopBar(s = stringResource(id = R.string.about_name),
            behavior = scrollBehaviour,
            icon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = VanillaIcons.ArrowBack,
                        contentDescription = stringResource(id = R.string.navigate_up)
                    )
                }
            })
    }) { innerPadding ->
        val webView = remember {
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                settings.loadsImagesAutomatically = true

                // Set up force dark mode
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    settings.forceDark = WebSettings.FORCE_DARK_ON
                }

                webViewClient = WebViewClient()
            }
        }

        var isLoading by remember { mutableStateOf(true) }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                isLoading = false

            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return true
            }
        }

        webView.loadUrl(sharedPrefConfig.appDetails.privacyPolicyUrl)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                AndroidView(
                    factory = {
                        webView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

    }
}

