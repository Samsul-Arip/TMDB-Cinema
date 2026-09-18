package com.samsul.moviedb.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samsul.moviedb.R
import com.samsul.moviedb.ui.preview.PreviewConstants
import com.samsul.moviedb.ui.theme.CinemaAmberStart
import com.samsul.moviedb.ui.theme.MovieAndroidTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun MovieSplashScreen(
    onSplashFinished: () -> Unit
) {
    val scale = remember { Animatable(0.8f) }
    val alpha = remember { Animatable(0f) }
    val isPreview = LocalInspectionMode.current

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600)
            )
        }
        if (!isPreview) {
            delay(1500.milliseconds) // Waktu penyiapan awal / fetch data TMDB
            onSplashFinished()
        }
    }

    // Pulsing loading dots animation
    val infiniteTransition = rememberInfiniteTransition(label = "dots_loading")
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val centerOffset = Offset(size.width / 2f, size.height * 0.45f)
                val radius = size.maxDimension * 0.55f
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFF59E0B).copy(alpha = 0.18f),
                            Color(0xFF08090E).copy(alpha = 0.95f),
                            Color(0xFF050608)
                        ),
                        center = centerOffset,
                        radius = radius
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
        ) {
            // Logo Badge with outer depth & emblem card
            Box(
                modifier = Modifier.size(108.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer floating subtle ambient halo
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF59E0B).copy(alpha = 0.08f))
                )

                // Emblem Box Card
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(Color(0xFF12141F))
                        .border(1.5.dp, Color(0xFF2B3145), RoundedCornerShape(26.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Subtle amber tint overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFBBF24).copy(alpha = 0.15f),
                                        Color(0xFFD97706).copy(alpha = 0.08f)
                                    )
                                )
                            )
                    )

                    // Inner Golden Play Mark
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFDE68A),
                                        Color(0xFFFBBF24),
                                        Color(0xFFD97706)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = stringResource(R.string.content_desc_app_logo),
                            tint = Color(0xFF08090E),
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Brand Text: "Movie" (White) + "DB" (Amber #FBBF24)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.brand_part_one),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = stringResource(R.string.brand_part_two),
                    color = CinemaAmberStart,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )
            }

            // Tagline
            Text(
                text = stringResource(R.string.splash_tagline),
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        // Footer / Loading State & TMDB Attribution
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        ) {
            // Subtle Animated Dots Loader Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(CinemaAmberStart.copy(alpha = dot1Alpha))
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(CinemaAmberStart.copy(alpha = dot2Alpha))
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(CinemaAmberStart.copy(alpha = dot3Alpha))
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // TMDB API Attribution
            Text(
                text = stringResource(R.string.splash_tmdb_attribution),
                color = Color(0xFF64748B),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Preview(name = PreviewConstants.PREVIEW_SPLASH_SCREEN, showBackground = true)
@Composable
private fun MovieSplashScreenPreview() {
    MovieAndroidTheme {
        MovieSplashScreen(onSplashFinished = {})
    }
}
