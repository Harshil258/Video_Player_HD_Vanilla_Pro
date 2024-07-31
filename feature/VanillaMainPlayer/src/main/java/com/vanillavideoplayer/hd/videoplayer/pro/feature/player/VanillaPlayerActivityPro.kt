package com.vanillavideoplayer.hd.videoplayer.pro.feature.player

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.media.audiofx.LoudnessEnhancer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Rational
import android.util.TypedValue
import android.view.KeyEvent
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.WindowManager
import android.view.accessibility.CaptioningManager
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.CaptionStyleCompat
import androidx.media3.ui.PlayerView
import androidx.media3.ui.TimeBar
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.harshil258.adplacer.interfaces.InterAdCallBack
import com.harshil258.adplacer.utils.Constants.adPlacerApplication
import com.harshil258.adplacer.utils.Constants.runningActivity
import com.harshil258.adplacer.utils.Logger
import com.harshil258.adplacer.utils.SharedPrefConfig.Companion.sharedPrefConfig
import com.vanillavideoplayer.hd.videoplayer.pro.core.common.CommonAi
import com.vanillavideoplayer.hd.videoplayer.pro.core.common.extra.clearCacheFolder
import com.vanillavideoplayer.hd.videoplayer.pro.core.common.extra.convertGivenCharsetToUTF8
import com.vanillavideoplayer.hd.videoplayer.pro.core.common.extra.getFNameFromUri
import com.vanillavideoplayer.hd.videoplayer.pro.core.common.extra.getMediaFromUri
import com.vanillavideoplayer.hd.videoplayer.pro.core.common.extra.getPathFromUri
import com.vanillavideoplayer.hd.videoplayer.pro.core.common.extra.isTvBox
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.DecoderPriority
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.ScreenOrientationEnum
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.ThemeConfigEnum
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.VidZoomEnum
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.databinding.ActivityPlayerBinding
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.dialogs.SpeedControlsDialogFrag
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.dialogs.TrackChooserDialogFrag
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.dialogs.ZoomOptionsDialogFrag
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.dialogs.nameRes
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.model.SubDataModel
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.x.attachImgDrawable
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.x.checkIfFastSeekEnabled
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.x.checkIfPortrait
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.x.convertInSub
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.x.convertToTf
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.x.getAudioSessionId
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.x.next
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.x.prettyPrintIntent
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.x.returnActOrient
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.x.returnDesiSubs
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.x.returnSubMime
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.x.returnTrackIndex
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.x.seekBackward
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.x.seekTorward
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.x.switchPlayPause
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.x.toggleSystemBars
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.x.toggleTrack
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.z.LightManager
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.z.PlayerGestureHandler
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.z.PlaylistHandler
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.z.VanillaAudioManager
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.z.VanillaPlayerApiClass
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.z.convetToMillis
import dagger.hilt.android.AndroidEntryPoint
import io.github.anilbeesetti.nextlib.media3ext.ffdecoder.NextRenderersFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.charset.Charset
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.R as coreUiR


@SuppressLint("UnsafeOptInUsageError")
@AndroidEntryPoint
class VanillaPlayerActivityPro : AppCompatActivity() {

    lateinit var vanillaBinding: ActivityPlayerBinding

    private val playerViewModel: PlayerViewModel by viewModels()
    private val currentContext = this
    private val applicationPreferences get() = playerViewModel.appPrefsData.value
    private val playerPreferences get() = playerViewModel.playerPrefs.value

    private var playWhenReady = true
    private var isPlaybackFinished = false

    var isFileLoaded = false
    var boolControlsLocked = false
    private var shouldFetchPlaylist = true
    private var boolSubtitleLauncherHasUri = false
    private var boolFirstFrameRendered = false
    private var boolFrameRendered = false
    private var boolPlayingOnScrubStart: Boolean = false
    private var previousScrubPosition = 0L
    private var scrubStartPosition: Long = -1L
    private var currentOrientation: Int? = null
    private var currentVideoOrientation: Int? = null
    var currentVideoSize: VideoSize? = null
    private var hideVolumeIndicatorJob: Job? = null
    private var hideBrightnessIndicatorJob: Job? = null
    private var hideInfoLayoutJob: Job? = null

    private val shouldFastSeek: Boolean
        get() = playerPreferences.checkIfFastSeekEnabled(player.duration)

    private lateinit var player: Player
    private lateinit var playerGestureHandler: PlayerGestureHandler
    private lateinit var playlistHandler: PlaylistHandler
    private lateinit var trackSelector: DefaultTrackSelector
    private var surfaceView: SurfaceView? = null
    private var mediaSession: MediaSession? = null
    private lateinit var vanillaPlayerApiClass: VanillaPlayerApiClass
    private lateinit var vanillaAudioManager: VanillaAudioManager
    private lateinit var lightManager: LightManager
    var loudnessEnhancer: LoudnessEnhancer? = null

    private val playbackStateListener: Player.Listener = playbackStateListener()
    private val subtitleFileLauncher = registerForActivityResult(OpenDocument()) { uri ->
        if (uri != null) {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            boolSubtitleLauncherHasUri = true
            playerViewModel.externalSubtitles.add(uri)
        }
        playVideo(playlistHandler.getPointedUri() ?: intent.data!!)
    }

    private lateinit var audioTrackButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var exoContentFrameLayout: AspectRatioFrameLayout
    private lateinit var lockControlsButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var playbackSpeedButton: ImageButton
    private lateinit var playerLockControls: FrameLayout
    private lateinit var playerUnlockControls: FrameLayout
    private lateinit var playerCenterControls: LinearLayout
    private lateinit var prevButton: ImageButton
    private lateinit var screenRotationButton: ImageButton
    private lateinit var pipButton: ImageButton
    private lateinit var seekBar: TimeBar
    private lateinit var subtitleTrackButton: ImageButton
    private lateinit var unlockControlsButton: ImageButton
    private lateinit var videoTitleTextView: TextView
    private lateinit var videoZoomButton: ImageButton
    private lateinit var btn_ss: ImageView
    private var clickedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prettyPrintIntent()

        AppCompatDelegate.setDefaultNightMode(
            when (applicationPreferences.themeConfigEnum) {
                ThemeConfigEnum.THEME_SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                ThemeConfigEnum.THEME_OFF -> AppCompatDelegate.MODE_NIGHT_NO
                ThemeConfigEnum.THEME_ON -> AppCompatDelegate.MODE_NIGHT_YES
            }
        )

        if (applicationPreferences.useDynamicColors) {
            DynamicColors.applyToActivityIfAvailable(this)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        vanillaBinding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(vanillaBinding.root)

        audioTrackButton = vanillaBinding.playerView.findViewById(R.id.btn_audio_track)
        backButton = vanillaBinding.playerView.findViewById(R.id.back_button)
        exoContentFrameLayout = vanillaBinding.playerView.findViewById(R.id.exo_content_frame)
        lockControlsButton = vanillaBinding.playerView.findViewById(R.id.btn_lock_controls)
        nextButton = vanillaBinding.playerView.findViewById(R.id.btn_play_next)
        playbackSpeedButton = vanillaBinding.playerView.findViewById(R.id.btn_playback_speed)
        playerLockControls = vanillaBinding.playerView.findViewById(R.id.player_lock_controls)
        playerUnlockControls = vanillaBinding.playerView.findViewById(R.id.player_unlock_controls)
        playerCenterControls = vanillaBinding.playerView.findViewById(R.id.player_center_controls)
        prevButton = vanillaBinding.playerView.findViewById(R.id.btn_play_prev)
        screenRotationButton = vanillaBinding.playerView.findViewById(R.id.screen_rotate)
        pipButton = vanillaBinding.playerView.findViewById(R.id.btn_pip)
        seekBar = vanillaBinding.playerView.findViewById(R.id.exo_progress)
        subtitleTrackButton = vanillaBinding.playerView.findViewById(R.id.btn_subtitle_track)
        unlockControlsButton = vanillaBinding.playerView.findViewById(R.id.btn_unlock_controls)
        videoTitleTextView = vanillaBinding.playerView.findViewById(R.id.video_name)
        videoZoomButton = vanillaBinding.playerView.findViewById(R.id.btn_video_zoom)
        btn_ss = vanillaBinding.playerView.findViewById(R.id.btn_ss)

        updatePictureInPictureParams()

        seekBar.addListener(object : TimeBar.OnScrubListener {
            override fun onScrubStart(timeBar: TimeBar, position: Long) {
                if (player.isPlaying) {
                    boolPlayingOnScrubStart = true
                    player.pause()
                }
                boolFrameRendered = true
                scrubStartPosition = player.currentPosition
                previousScrubPosition = player.currentPosition
                scrub(position)
                showPlayerInfo(
                    info = CommonAi.formatDurationInHours(position),
                    subInfo = "[${CommonAi.formatDurationSign(position - scrubStartPosition)}]"
                )
            }

            override fun onScrubMove(timeBar: TimeBar, position: Long) {
                scrub(position)
                showPlayerInfo(
                    info = CommonAi.formatDurationInHours(position),
                    subInfo = "[${CommonAi.formatDurationSign(position - scrubStartPosition)}]"
                )
            }

            override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                hidePlayerInfo(0L)
                scrubStartPosition = -1L
                if (boolPlayingOnScrubStart) {
                    player.play()
                }
            }
        })

        vanillaAudioManager =
            VanillaAudioManager(am = getSystemService(Context.AUDIO_SERVICE) as AudioManager)
        lightManager = LightManager(activity = this)
        playerGestureHandler = PlayerGestureHandler(
            playerViewModel = playerViewModel,
            vanillaAct = this,
            vanillaAm = vanillaAudioManager,
            lightManager = lightManager
        )

        playlistHandler = PlaylistHandler()
        vanillaPlayerApiClass = VanillaPlayerApiClass(this)
    }

    private fun isPiPSupported(): Boolean {
        val pm = packageManager
        return pm.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
    }

    override fun onStart() {
        if (playerPreferences.savePlayerBright) {
            lightManager.updateLight(playerPreferences.playerBright)
        }
        createPlayer()
        setOrientation()
        initPlaylist()
        initializePlayerView()
        playVideo()
        super.onStart()
    }

    override fun onStop() {
        vanillaBinding.volumeGestureLayout.visibility = View.GONE
        vanillaBinding.brightnessGestureLayout.visibility = View.GONE
        currentOrientation = requestedOrientation
        releasePlayer()
        super.onStop()
    }

    private fun createPlayer() {
        val renderersFactory =
            NextRenderersFactory(applicationContext).setEnableDecoderFallback(true)
                .setExtensionRendererMode(
                    when (playerPreferences.decoderPriority) {
                        DecoderPriority.DEVICE_ONLY -> DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
                        DecoderPriority.PREFER_DEVICE -> DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON
                        DecoderPriority.PREFER_APP -> DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                    }
                )

        trackSelector = DefaultTrackSelector(applicationContext).apply {
            this.setParameters(
                this.buildUponParameters()
                    .setPreferredAudioLanguage(playerPreferences.suggestedAudioLang)
                    .setPreferredTextLanguage(playerPreferences.suggestedSubtitleLang)
            )
        }

        player = ExoPlayer.Builder(applicationContext).setRenderersFactory(renderersFactory)
            .setTrackSelector(trackSelector)
            .setAudioAttributes(getAudioAttributes(), playerPreferences.audioFocusNiJarurChheKeNai)
            .setHandleAudioBecomingNoisy(playerPreferences.boolPauseOnHeadsetDisconnect).build()

        try {
            if (player.canAdvertiseSession()) {
                mediaSession = MediaSession.Builder(this, player).build()
            }
            loudnessEnhancer =
                if (playerPreferences.audioFocusNiJarurChheKeNai) LoudnessEnhancer(player.getAudioSessionId) else null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        player.addListener(playbackStateListener)
        vanillaAudioManager.loudnessEnhancer = loudnessEnhancer
    }

    private fun setOrientation() {
        requestedOrientation =
            currentOrientation ?: playerPreferences.playerScreenOrientationEnum.returnActOrient()
    }

    private fun initializePlayerView() {
        vanillaBinding.playerView.apply {
            setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
            player = this@VanillaPlayerActivityPro.player
            controllerShowTimeoutMs = playerPreferences.optionsAutoHideTimeout.convetToMillis
            setControllerVisibilityListener(PlayerView.ControllerVisibilityListener { visibility ->
                toggleSystemBars(showBars = visibility == View.VISIBLE && !boolControlsLocked)
            })

            subtitleView?.apply {
                val captioningManager =
                    getSystemService(Context.CAPTIONING_SERVICE) as CaptioningManager
                val systemCaptionStyle =
                    CaptionStyleCompat.createFromCaptionStyle(captioningManager.userStyle)
                val userStyle = CaptionStyleCompat(Color.WHITE,
                    Color.BLACK.takeIf { playerPreferences.boolSubtitleBackground }
                        ?: Color.TRANSPARENT,
                    Color.TRANSPARENT,
                    CaptionStyleCompat.EDGE_TYPE_DROP_SHADOW,
                    Color.BLACK,
                    Typeface.create(playerPreferences.subtitleFontEnum.convertToTf(),
                        Typeface.BOLD.takeIf { playerPreferences.boolSubtitleTextBold }
                            ?: Typeface.NORMAL))
                setStyle(systemCaptionStyle.takeIf { playerPreferences.boolSystemCaptionStyle }
                    ?: userStyle)
                setApplyEmbeddedStyles(playerPreferences.boolApplyEmbeddedStyles)
                setFixedTextSize(
                    TypedValue.COMPLEX_UNIT_SP, playerPreferences.subtitleTextSizeVal.toFloat()
                )
            }
        }

        audioTrackButton.setOnClickListener {
            trackSelector.currentMappedTrackInfo ?: return@setOnClickListener
            TrackChooserDialogFrag(type = C.TRACK_TYPE_AUDIO,
                tracks = player.currentTracks,
                onTrackSelected = { player.toggleTrack(C.TRACK_TYPE_AUDIO, it) }).show(
                supportFragmentManager, "TrackSelectionDialog"
            )
        }

        subtitleTrackButton.setOnClickListener {
            trackSelector.currentMappedTrackInfo ?: return@setOnClickListener
            TrackChooserDialogFrag(
                type = C.TRACK_TYPE_TEXT,
                tracks = player.currentTracks,
                onTrackSelected = { player.toggleTrack(C.TRACK_TYPE_TEXT, it) },
            ).show(supportFragmentManager, "TrackSelectionDialog")
        }

        subtitleTrackButton.setOnLongClickListener {
            subtitleFileLauncher.launch(
                arrayOf(
                    MimeTypes.APPLICATION_SUBRIP,
                    MimeTypes.APPLICATION_TTML,
                    MimeTypes.TEXT_VTT,
                    MimeTypes.TEXT_SSA,
                    MimeTypes.BASE_TYPE_APPLICATION + "/octet-stream",
                    MimeTypes.BASE_TYPE_TEXT + "/*"
                )
            )
            true
        }

        playbackSpeedButton.setOnClickListener {
            SpeedControlsDialogFrag(currentSpeed = player.playbackParameters.speed, onChange = {
                playerViewModel.isPlaybackSpeedChanged = true
                player.setPlaybackSpeed(it)
            }).show(supportFragmentManager, "PlaybackSpeedSelectionDialog")
        }

        nextButton.setOnClickListener {
            if (playlistHandler.isNextPossible()) {
                if (sharedPrefConfig.appDetails.showInterstitialOnNextButton == "ON") {
                    runningActivity?.let { it1 ->
                        adPlacerApplication.interstitialManager.loadAndShowInter(it1, object : InterAdCallBack {
                            override fun onContinueFlow() {
                                playlistHandler.getPointedUri()?.let { savePlayerState(it) }
                                playerViewModel.resetAllToDefaults()
                                playVideo(playlistHandler.returnNextUri()!!)
                            }
                        })
                    }
                } else {
                    playlistHandler.getPointedUri()?.let { savePlayerState(it) }
                    playerViewModel.resetAllToDefaults()
                    playVideo(playlistHandler.returnNextUri()!!)
                }
            }
        }
        prevButton.setOnClickListener {
            if (playlistHandler.isPrevPossible()) {
                if (sharedPrefConfig.appDetails.showInterstitialOnNextButton == "ON") {
                    runningActivity?.let { it1 ->
                        adPlacerApplication.interstitialManager.loadAndShowInter(it1, object : InterAdCallBack {
                            override fun onContinueFlow() {
                                playlistHandler.getPointedUri()?.let { savePlayerState(it) }
                                playerViewModel.resetAllToDefaults()
                                playVideo(playlistHandler.returnPrevUri()!!)
                            }
                        })
                    }
                } else {
                    playlistHandler.getPointedUri()?.let { savePlayerState(it) }
                    playerViewModel.resetAllToDefaults()
                    playVideo(playlistHandler.returnPrevUri()!!)
                }
            }
        }
        btn_ss.setOnClickListener {

            if (clickedTime - System.currentTimeMillis() > 1000) {
                return@setOnClickListener
            }
            clickedTime = System.currentTimeMillis()

            playlistHandler.getPointedUri()?.let { uri ->
                val bitmap = getVideoFrame(uri, player.currentPosition)
                Logger.d("TAG_NEO", "initializePlayerView: ${bitmap?.width} , ${bitmap?.height}")
                bitmap?.let {
                    saveBitmapToFileAndNotifyGallery(this@VanillaPlayerActivityPro, it, "${System.currentTimeMillis()}.jpg")
                } ?: kotlin.run {
                    Toast.makeText(this@VanillaPlayerActivityPro, "ScreenShot Failed!", Toast.LENGTH_SHORT).show()
                }
            } ?: kotlin.run {
                Toast.makeText(this@VanillaPlayerActivityPro, "ScreenShot Failed!", Toast.LENGTH_SHORT).show()
            }
        }
        lockControlsButton.setOnClickListener {
            playerUnlockControls.visibility = View.INVISIBLE
            playerLockControls.visibility = View.VISIBLE
            boolControlsLocked = true
            toggleSystemBars(showBars = false)
        }
        unlockControlsButton.setOnClickListener {
            playerLockControls.visibility = View.INVISIBLE
            playerUnlockControls.visibility = View.VISIBLE
            boolControlsLocked = false
            vanillaBinding.playerView.showController()
            toggleSystemBars(showBars = true)
        }
        videoZoomButton.setOnClickListener {
            val videoZoom = playerPreferences.playerVidZoomEnum.next()
            applyVideoZoom(vidZoomEnum = videoZoom, showInfo = true)
        }

        videoZoomButton.setOnLongClickListener {
            ZoomOptionsDialogFrag(defaultVidZoomEnum = playerPreferences.playerVidZoomEnum,
                onVideoZoomOptionSelected = {
                    applyVideoZoom(
                        vidZoomEnum = it, showInfo = true
                    )
                }).show(supportFragmentManager, "VideoZoomOptionsDialog")
            true
        }
        screenRotationButton.setOnClickListener {
            requestedOrientation = when (resources.configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                else -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
        }
//        screenRotationButton.setOnLongClickListener {
//            playerPreferences.playerScreenOrientationEnum.also {
//                requestedOrientation = it.returnActOrient(currentVideoOrientation)
//            }
//            true
//        }
        pipButton.setOnClickListener {
            updatePictureInPictureParams()?.let {
                setPictureInPictureParams(it)
            }
        }
        backButton.setOnClickListener { finish() }
    }

    private fun getVideoFrame(videoPath: Uri, frameTime: Long): Bitmap? {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(this@VanillaPlayerActivityPro, videoPath)
            return retriever.getFrameAtTime(frameTime*1000, MediaMetadataRetriever.OPTION_NEXT_SYNC)
        } catch (ex: IllegalArgumentException) {
            Logger.e("TAG_NEO", "error getting video frame ${ex.message}")
        } catch (ex: RuntimeException) {
            Logger.e("TAG_NEO", "error getting video frame ${ex.message}")
        } finally {
            try {
                retriever.release()
            } catch (ex: RuntimeException) {
            }
        }
        return null
    }

    private fun updatePictureInPictureParams(): PictureInPictureParams? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isPiPSupported()) {
                val params = PictureInPictureParams.Builder()
                    .setAspectRatio(Rational(16, 9))
                    .build()
                setPictureInPictureParams(params)
                params
            } else {
                pipButton.visibility = View.GONE
                null
            }
        } catch (e: IllegalStateException) {
            pipButton.visibility = View.GONE
            null
        }
    }

    private fun initPlaylist() = lifecycleScope.launch(Dispatchers.IO) {
        try {
            intent?.data?.let {
                val mediaUri = getMediaFromUri(it)

                if (mediaUri != null) {
                    val playlist = playerViewModel.getPlaylistFromUri(mediaUri)
                    playlistHandler.changeQueueList(playlist)
                }
            }
        } catch (_: Exception) {
        }
    }

    private fun playVideo(uri: Uri? = null) {
        lifecycleScope.launch(Dispatchers.IO) {
            val currentUri = uri ?: intent.data ?: return@launch

            if (shouldFetchPlaylist) {
                val mediaUri = getMediaFromUri(currentUri)

                if (mediaUri != null) {
                    launch(Dispatchers.IO) {
                        val playlist = playerViewModel.getPlaylistFromUri(mediaUri)
                        playlistHandler.changeQueueList(playlist ?: emptyList())
                    }
                }
                shouldFetchPlaylist = false
            }

            playlistHandler.updatePointedItemUri(uri = currentUri)

            val currentPath = getPathFromUri(currentUri)
            playerViewModel.updateState(currentPath)
            if (intent.data == currentUri && vanillaPlayerApiClass.boolPositionAllocated) {
                playerViewModel.currentPlaybackPosition = vanillaPlayerApiClass.pos?.toLong()
            }

            val apiSubs =
                if (intent.data == currentUri) vanillaPlayerApiClass.returnSubs() else emptyList()
            val localSubs =
                currentUri.returnDesiSubs(currentContext, playerViewModel.externalSubtitles.toList())
            val externalSubs = playerViewModel.externalSubtitles.map { it.convertInSub(currentContext) }

            val subtitleStreams = createExternalSubtitleStreams(apiSubs + localSubs + externalSubs)
            val mediaStream =
                createMediaStream(currentUri).buildUpon().setSubtitleConfigurations(subtitleStreams)
                    .build()

            withContext(Dispatchers.Main) {
                surfaceView = SurfaceView(this@VanillaPlayerActivityPro).apply {
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                }
                player.setVideoSurfaceView(surfaceView)
                exoContentFrameLayout.addView(surfaceView, 0)

                val title = vanillaPlayerApiClass.header ?: getFNameFromUri(currentUri)
                videoTitleTextView.text = title

                player.setMediaItem(mediaStream, playerViewModel.currentPlaybackPosition ?: C.TIME_UNSET)
                player.playWhenReady = playWhenReady
                player.prepare()
            }
        }
    }

    private fun releasePlayer() {
        Timber.d("Releasing player")
        playWhenReady = player.playWhenReady
        playlistHandler.getPointedUri()?.let { savePlayerState(it) }
        player.removeListener(playbackStateListener)
        player.release()
        mediaSession?.release()
        mediaSession = null
    }

    private fun playbackStateListener() = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            vanillaBinding.playerView.keepScreenOn = isPlaying
            super.onIsPlayingChanged(isPlaying)
        }

        override fun onAudioSessionIdChanged(audioSessionId: Int) {
            super.onAudioSessionIdChanged(audioSessionId)
            loudnessEnhancer?.release()

            try {
                loudnessEnhancer = LoudnessEnhancer(audioSessionId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        @SuppressLint("SourceLockedOrientationActivity")
        override fun onVideoSizeChanged(videoSize: VideoSize) {
            currentVideoSize = videoSize
            applyVideoZoom(vidZoomEnum = playerPreferences.playerVidZoomEnum, showInfo = false)

            if (currentOrientation != null) return

            if (playerPreferences.playerScreenOrientationEnum == ScreenOrientationEnum.VIDEO_ORIENTATION_SO) {
                currentVideoOrientation = if (videoSize.checkIfPortrait) {
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                }
                requestedOrientation = currentVideoOrientation!!
            }
            super.onVideoSizeChanged(videoSize)
        }

        override fun onPlayerError(error: PlaybackException) {
            Timber.e(error)
            val alertDialog =
                MaterialAlertDialogBuilder(this@VanillaPlayerActivityPro).setTitle(getString(coreUiR.string.error_playing_video))
                    .setMessage(error.message ?: getString(coreUiR.string.unknown_error))
                    .setPositiveButton(currentContext.resources.getString(com.vanillavideoplayer.hd.videoplayer.pro.core.ui.R.string.ok)) { dialog, _ ->
                        dialog.dismiss()
                    }.setOnDismissListener {
                        if (playlistHandler.isNextPossible()) playVideo(playlistHandler.returnNextUri()!!) else finish()
                    }.create()

            alertDialog.show()
            super.onPlayerError(error)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_ENDED -> {
                    Timber.d("Player state: ENDED")
                    isPlaybackFinished = true
                    if (playlistHandler.isNextPossible() && playerPreferences.boolAutoplay) {
                        playlistHandler.getPointedUri()?.let { savePlayerState(it) }
                        playVideo(playlistHandler.returnNextUri()!!)
                    } else {
                        finish()
                    }
                }

                Player.STATE_READY -> {
                    Timber.d("Player state: READY")
                    Timber.d(playlistHandler.toString())
                    boolFrameRendered = true
                    isFileLoaded = true
                }

                Player.STATE_BUFFERING -> {
                    Timber.d("Player state: BUFFERING")
                }

                Player.STATE_IDLE -> {
                    Timber.d("Player state: IDLE")
                }
            }
            super.onPlaybackStateChanged(playbackState)
        }

        override fun onRenderedFirstFrame() {
            boolFirstFrameRendered = true
            vanillaBinding.playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
            super.onRenderedFirstFrame()
        }

        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)
            if (boolFirstFrameRendered) return

            if (boolSubtitleLauncherHasUri) {
                val textTracks =
                    player.currentTracks.groups.filter { it.type == C.TRACK_TYPE_TEXT && it.isSupported }
                playerViewModel.currentSubtitleTrackIndex = textTracks.size - 1
            }
            boolSubtitleLauncherHasUri = false
            player.toggleTrack(C.TRACK_TYPE_AUDIO, playerViewModel.currentAudioTrackIndex)
            player.toggleTrack(C.TRACK_TYPE_TEXT, playerViewModel.currentSubtitleTrackIndex)
            player.setPlaybackSpeed(playerViewModel.currentPlaybackSpeed)
        }
    }

    override fun finish() {
        clearCacheFolder()
        if (vanillaPlayerApiClass.checkIfCanReturnResult) {
            val result = vanillaPlayerApiClass.returnResult(
                boolPlaybackComplete = isPlaybackFinished,
                duration = player.duration,
                position = player.currentPosition
            )
            setResult(Activity.RESULT_OK, result)
        }
        super.finish()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            playlistHandler.clearQueue()
            playerViewModel.resetAllToDefaults()
            setIntent(intent)
            prettyPrintIntent()
            shouldFetchPlaylist = true
            playVideo()
        }
    }

    override fun setRequestedOrientation(requestedOrientation: Int) {
        super.setRequestedOrientation(requestedOrientation)
        screenRotationButton.attachImgDrawable(this, getRotationDrawable())
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_DPAD_UP -> {
                if (!vanillaBinding.playerView.isControllerFullyVisible || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                    vanillaAudioManager.audioVolumeUP(playerPreferences.systemVolumePanelDekhadu)
                    showVolumeGestureLayout()
                    return true
                }
            }

            KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (!vanillaBinding.playerView.isControllerFullyVisible || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    vanillaAudioManager.audioVolumeDown(playerPreferences.systemVolumePanelDekhadu)
                    showVolumeGestureLayout()
                    return true
                }
            }

            KeyEvent.KEYCODE_MEDIA_PLAY, KeyEvent.KEYCODE_MEDIA_PAUSE, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, KeyEvent.KEYCODE_BUTTON_SELECT -> {
                when {
                    keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE -> player.pause()
                    keyCode == KeyEvent.KEYCODE_MEDIA_PLAY -> player.play()
                    player.isPlaying -> player.pause()
                    else -> player.play()
                }
                return true
            }

            KeyEvent.KEYCODE_BUTTON_START, KeyEvent.KEYCODE_BUTTON_A, KeyEvent.KEYCODE_SPACE -> {
                if (!vanillaBinding.playerView.isControllerFullyVisible) {
                    vanillaBinding.playerView.switchPlayPause()
                    return true
                }
            }

            KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_BUTTON_L2, KeyEvent.KEYCODE_MEDIA_REWIND -> {
                if (!vanillaBinding.playerView.isControllerFullyVisible || keyCode == KeyEvent.KEYCODE_MEDIA_REWIND) {
                    val pos = player.currentPosition
                    if (scrubStartPosition == -1L) {
                        scrubStartPosition = pos
                    }
                    val position = (pos - 10_000).coerceAtLeast(0L)
                    player.seekBackward(position, shouldFastSeek)
                    showPlayerInfo(
                        info = CommonAi.formatDurationInHours(position),
                        subInfo = "[${CommonAi.formatDurationSign(position - scrubStartPosition)}]"
                    )
                    return true
                }
            }

            KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_BUTTON_R2, KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                if (!vanillaBinding.playerView.isControllerFullyVisible || keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
                    val pos = player.currentPosition
                    if (scrubStartPosition == -1L) {
                        scrubStartPosition = pos
                    }

                    val position = (pos + 10_000).coerceAtMost(player.duration)
                    player.seekTorward(position, shouldFastSeek)
                    showPlayerInfo(
                        info = CommonAi.formatDurationInHours(position),
                        subInfo = "[${CommonAi.formatDurationSign(position - scrubStartPosition)}]"
                    )
                    return true
                }
            }

            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                if (!vanillaBinding.playerView.isControllerFullyVisible) {
                    vanillaBinding.playerView.showController()
                    return true
                }
            }

            KeyEvent.KEYCODE_BACK -> {
                if (vanillaBinding.playerView.isControllerFullyVisible && player.isPlaying && isTvBox()) {
                    vanillaBinding.playerView.hideController()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN -> {
                hideVolumeGestureLayout()
                return true
            }

            KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_BUTTON_L2, KeyEvent.KEYCODE_MEDIA_REWIND, KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_BUTTON_R2, KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                hidePlayerInfo()
                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun getAudioAttributes(): AudioAttributes {
        return AudioAttributes.Builder().setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE).build()
    }

    private fun scrub(position: Long) {
        if (boolFrameRendered) {
            boolFrameRendered = false
            if (position > previousScrubPosition) {
                player.seekTorward(position, shouldFastSeek)
            } else {
                player.seekBackward(position, shouldFastSeek)
            }
            previousScrubPosition = position
        }
    }

    fun showVolumeGestureLayout() {
        hideVolumeIndicatorJob?.cancel()
        with(vanillaBinding) {
            volumeGestureLayout.visibility = View.VISIBLE
            volumeProgressBar.max = vanillaAudioManager.maxVolumeLevel.times(100)
            volumeProgressBar.progress = vanillaAudioManager.defaultVolume.times(100).toInt()
            volumeProgressText.text = vanillaAudioManager.audioLevelPercentage.toString()
        }
    }

    fun showBrightnessGestureLayout() {
        hideBrightnessIndicatorJob?.cancel()
        with(vanillaBinding) {
            brightnessGestureLayout.visibility = View.VISIBLE
            brightnessProgressBar.max = lightManager.maxLightLevel.times(100).toInt()
            brightnessProgressBar.progress = lightManager.presentLightLevel.times(100).toInt()
            brightnessProgressText.text = lightManager.lightPercentage.toString()
        }
    }

    fun showPlayerInfo(info: String, subInfo: String? = null) {
        hideInfoLayoutJob?.cancel()
        with(vanillaBinding) {
            infoLayout.visibility = View.VISIBLE
            infoText.text = info
            infoSubtext.visibility = View.GONE.takeIf { subInfo == null } ?: View.VISIBLE
            infoSubtext.text = subInfo
        }
    }

    fun showTopInfo(info: String) {
        with(vanillaBinding) {
            topInfoLayout.visibility = View.VISIBLE
            topInfoText.text = info
        }
    }

    fun hideVolumeGestureLayout(delayTimeMillis: Long = HIDE_DELAY_MILLIS) {
        if (vanillaBinding.volumeGestureLayout.visibility != View.VISIBLE) return
        hideVolumeIndicatorJob = lifecycleScope.launch {
            delay(delayTimeMillis)
            vanillaBinding.volumeGestureLayout.visibility = View.GONE
        }
    }

    fun hideBrightnessGestureLayout(delayTimeMillis: Long = HIDE_DELAY_MILLIS) {
        if (vanillaBinding.brightnessGestureLayout.visibility != View.VISIBLE) return
        hideBrightnessIndicatorJob = lifecycleScope.launch {
            delay(delayTimeMillis)
            vanillaBinding.brightnessGestureLayout.visibility = View.GONE
        }
        if (playerPreferences.savePlayerBright) {
            playerViewModel.setPlayerBrightness(window.attributes.screenBrightness)
        }
    }

    fun hidePlayerInfo(delayTimeMillis: Long = HIDE_DELAY_MILLIS) {
        if (vanillaBinding.infoLayout.visibility != View.VISIBLE) return
        hideInfoLayoutJob = lifecycleScope.launch {
            delay(delayTimeMillis)
            vanillaBinding.infoLayout.visibility = View.GONE
        }
    }

    fun hideTopInfo() {
        vanillaBinding.topInfoLayout.visibility = View.GONE
    }

    private fun savePlayerState(uri: Uri) {
        if (boolFirstFrameRendered) {
            playerViewModel.saveState(
                path = getPathFromUri(uri),
                position = player.currentPosition,
                duration = player.duration,
                audioTrackIndex = player.returnTrackIndex(C.TRACK_TYPE_AUDIO),
                subtitleTrackIndex = player.returnTrackIndex(C.TRACK_TYPE_TEXT),
                playbackSpeed = player.playbackParameters.speed,
                lastPlayed = System.currentTimeMillis()
            )
        }
        boolFirstFrameRendered = false
    }

    private fun createMediaStream(uri: Uri) =
        MediaItem.Builder().setMediaId(uri.toString()).setUri(uri).build()

    private fun createExternalSubtitleStreams(subDataModels: List<SubDataModel>): List<MediaItem.SubtitleConfiguration> {
        return subDataModels.map {
            val charset = if (with(playerPreferences.subtitleTextEncodingVal) {
                    isNotEmpty() && Charset.isSupported(this)
                }) {
                Charset.forName(playerPreferences.subtitleTextEncodingVal)
            } else {
                null
            }
            MediaItem.SubtitleConfiguration.Builder(
                convertGivenCharsetToUTF8(
                    uri = it.subUri, charset = charset
                )
            ).apply {
                setId(it.subUri.toString())
                setMimeType(it.subUri.returnSubMime())
                setLabel(it.subName)
                if (it.boolSelected) setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
            }.build()
        }
    }

    private fun resetExoContentFrameWidthAndHeight() {
        exoContentFrameLayout.layoutParams.width = LayoutParams.MATCH_PARENT
        exoContentFrameLayout.layoutParams.height = LayoutParams.MATCH_PARENT
        exoContentFrameLayout.scaleX = 1.0f
        exoContentFrameLayout.scaleY = 1.0f
        exoContentFrameLayout.requestLayout()
    }

    private fun applyVideoZoom(vidZoomEnum: VidZoomEnum, showInfo: Boolean) {
        playerViewModel.setVideoZoom(vidZoomEnum)
        resetExoContentFrameWidthAndHeight()
        when (vidZoomEnum) {
            VidZoomEnum.VANILLA_FIT -> {
                vanillaBinding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                videoZoomButton.attachImgDrawable(this, coreUiR.drawable.ic_fit_screen)
            }

            VidZoomEnum.VANILLA_STRETCH -> {
                vanillaBinding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                videoZoomButton.attachImgDrawable(this, coreUiR.drawable.ic_aspect_ratio)
            }

            VidZoomEnum.VANILLA_CROP -> {
                vanillaBinding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                videoZoomButton.attachImgDrawable(this, coreUiR.drawable.ic_crop_landscape)
            }

            VidZoomEnum.VANILLA_SOO_TAKA -> {
                currentVideoSize?.let {
                    exoContentFrameLayout.layoutParams.width = it.width
                    exoContentFrameLayout.layoutParams.height = it.height
                    exoContentFrameLayout.requestLayout()
                }
                vanillaBinding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                videoZoomButton.attachImgDrawable(this, coreUiR.drawable.ic_width_wide)
            }
        }
        if (showInfo) {
            lifecycleScope.launch {
                vanillaBinding.infoLayout.visibility = View.VISIBLE
                vanillaBinding.infoText.text = getString(vidZoomEnum.nameRes())
                delay(HIDE_DELAY_MILLIS)
                vanillaBinding.infoLayout.visibility = View.GONE
            }
        }
    }

    companion object {
        const val HIDE_DELAY_MILLIS = 1000L
    }

    private fun saveBitmapToFileAndNotifyGallery(context: Context, bitmap: Bitmap, fileName: String) {
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val file = File(picturesDir, fileName)
        var outputStream: OutputStream? = null

        try {
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()

            MediaScannerConnection.scanFile(context, arrayOf(file.toString()), null) { path, uri ->
                Toast.makeText(context, "ScreenShot Saved!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            outputStream?.close()
        }
    }
}

private fun Activity.getRotationDrawable(): Int {
    return when (requestedOrientation) {
        ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT, ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT -> coreUiR.drawable.ic_portrait

        ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE, ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE -> coreUiR.drawable.ic_landscape

        else -> coreUiR.drawable.ic_screen_rotation
    }
}
