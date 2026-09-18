package com.samsul.moviedb.presentation.detail.components

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.samsul.moviedb.R
import com.samsul.moviedb.ui.theme.CinemaAmberStart

fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

/**
 * Utility to launch a YouTube video in the native app or fallback to browser.
 */
fun openYouTubeVideo(context: Context, videoId: String) {
    val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId")).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId")).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(appIntent)
    } catch (e: ActivityNotFoundException) {
        try {
            context.startActivity(webIntent)
        } catch (ignored: Exception) {
        }
    } catch (e: Exception) {
        try {
            context.startActivity(webIntent)
        } catch (ignored: Exception) {
        }
    }
}

@Composable
fun AppYouTubePlayer(
    videoId: String,
    modifier: Modifier = Modifier,
    onError: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = remember(context) { context.findActivity() }

    if (LocalInspectionMode.current) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = null,
                tint = CinemaAmberStart,
                modifier = Modifier.size(48.dp)
            )
        }
        return
    }

    var isReady by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }
    var currentFullscreenView by remember { mutableStateOf<View?>(null) }
    var exitFullscreenCallback by remember { mutableStateOf<(() -> Unit)?>(null) }

    // Intercept back button when in fullscreen mode to exit fullscreen
    BackHandler(enabled = isFullscreen) {
        exitFullscreenCallback?.invoke()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                YouTubePlayerView(ctx).apply {
                    enableAutomaticInitialization = false
                    lifecycleOwner.lifecycle.addObserver(this)

                    val options = IFramePlayerOptions.Builder(ctx)
                        .controls(1)
                        .fullscreen(1)
                        .autoplay(1)
                        .build()

                    addFullscreenListener(object : FullscreenListener {
                        override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                            isFullscreen = true
                            currentFullscreenView = fullscreenView
                            exitFullscreenCallback = exitFullscreen

                            val act = activity ?: return
                            val decorView = act.window?.decorView as? ViewGroup ?: return

                            (fullscreenView.parent as? ViewGroup)?.removeView(fullscreenView)
                            val params = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            decorView.addView(fullscreenView, params)

                            // Immersive mode: hide status and navigation bars
                            val window = act.window
                            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                            insetsController.hide(WindowInsetsCompat.Type.systemBars())
                            act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                        }

                        override fun onExitFullscreen() {
                            isFullscreen = false
                            val act = activity ?: return
                            val decorView = act.window?.decorView as? ViewGroup ?: return

                            currentFullscreenView?.let { v ->
                                decorView.removeView(v)
                            }
                            currentFullscreenView = null
                            exitFullscreenCallback = null

                            // Restore system bars and orientation
                            val window = act.window
                            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                            insetsController.show(WindowInsetsCompat.Type.systemBars())
                            act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        }
                    })

                    initialize(
                        object : AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                isReady = true
                                youTubePlayer.loadVideo(videoId, 0f)
                            }

                            override fun onStateChange(
                                youTubePlayer: YouTubePlayer,
                                state: PlayerConstants.PlayerState
                            ) {
                                if (state == PlayerConstants.PlayerState.PLAYING ||
                                    state == PlayerConstants.PlayerState.PAUSED ||
                                    state == PlayerConstants.PlayerState.VIDEO_CUED
                                ) {
                                    isReady = true
                                }
                            }

                            override fun onError(
                                youTubePlayer: YouTubePlayer,
                                error: PlayerConstants.PlayerError
                            ) {
                                hasError = true
                                onError?.invoke()
                            }
                        },
                        false,
                        options,
                        videoId
                    )
                }
            },
            modifier = Modifier.fillMaxSize(),
            onRelease = { playerView ->
                // Clean up fullscreen view if active during release
                currentFullscreenView?.let { v ->
                    (v.parent as? ViewGroup)?.removeView(v)
                }
                activity?.let { act ->
                    val window = act.window
                    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                    insetsController.show(WindowInsetsCompat.Type.systemBars())
                    act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
                lifecycleOwner.lifecycle.removeObserver(playerView)
                playerView.release()
            }
        )

        // Loading spinner while player is initializing or buffering
        if (!isReady && !hasError) {
            CircularProgressIndicator(
                modifier = Modifier.size(36.dp),
                color = CinemaAmberStart,
                strokeWidth = 3.dp
            )
        }

        // Fallback UI if embedding is restricted or fails
        if (hasError) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F1118))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.trailer_load_failed),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.trailer_load_failed_hint),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.5.sp
                    ),
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { openYouTubeVideo(context, videoId) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCC0000)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.open_in_youtube),
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "YouTube Player Placeholder Preview", showBackground = true)
@Composable
private fun AppYouTubePlayerPreview() {
    AppYouTubePlayer(videoId = "dQw4w9WgXcQ")
}


