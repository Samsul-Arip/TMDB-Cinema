package com.samsul.moviedb.presentation.detail

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.VideocamOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.samsul.moviedb.R
import com.samsul.moviedb.ui.components.AppYouTubePlayer
import com.samsul.moviedb.ui.components.CinemaPullToRefreshBox
import com.samsul.moviedb.ui.components.ErrorStateView
import com.samsul.moviedb.ui.components.OfflineBadge
import com.samsul.moviedb.ui.components.ReviewItemShimmer
import com.samsul.moviedb.ui.components.ShimmerBox
import com.samsul.moviedb.ui.components.openYouTubeVideo
import com.samsul.moviedb.domain.model.Genre
import com.samsul.moviedb.domain.model.MovieDetail
import com.samsul.moviedb.domain.model.Review
import com.samsul.moviedb.domain.model.Trailer
import androidx.compose.ui.tooling.preview.Preview
import com.samsul.moviedb.ui.theme.CinemaAmberEnd
import com.samsul.moviedb.ui.theme.CinemaAmberStart
import com.samsul.moviedb.ui.theme.CinemaRatingStar
import com.samsul.moviedb.ui.theme.CinemaTopAmbientGlow
import com.samsul.moviedb.ui.theme.TechnicalTestAndroidTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MovieDetailScreen(
    movieId: Int,
    onBackClick: () -> Unit,
    viewModel: MovieDetailViewModel = koinViewModel(parameters = { parametersOf(movieId) })
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MovieDetailContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onRefresh = { viewModel.refresh() },
        onRetry = { viewModel.loadAll() },
        onLoadMoreReviews = { viewModel.loadNextReviewPage() }
    )
}

@Composable
fun MovieDetailContent(
    uiState: MovieDetailUiState,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onLoadMoreReviews: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    val distinctReviews = remember(uiState.reviews) {
        uiState.reviews.distinctBy { it.id }
    }

    // Endless scroll detection for reviews (User Story 6)
    val shouldLoadMoreReviews by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val isNearBottom = if (lastVisible == null || totalItems == 0) {
                false
            } else {
                lastVisible.index >= totalItems - 2
            }
            isNearBottom &&
                listState.firstVisibleItemIndex > 1 &&
                uiState.canPaginateReviews &&
                !uiState.isLoadingReviews &&
                !uiState.isLoadingMoreReviews &&
                distinctReviews.isNotEmpty()
        }
    }

    LaunchedEffect(shouldLoadMoreReviews) {
        if (shouldLoadMoreReviews) {
            onLoadMoreReviews()
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color(0xFF08090E)
    ) { innerPadding ->
        CinemaPullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            CinemaTopAmbientGlow.copy(alpha = 0.10f),
                            Color(0xFF08090E).copy(alpha = 0.95f),
                            Color(0xFF050608)
                        ),
                        center = Offset(x = 540f, y = 140f),
                        radius = 850f
                    )
                )
        ) {
            when {
                uiState.isLoadingDetail && uiState.movieDetail == null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp),
                            shape = RoundedCornerShape(20.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            ShimmerBox(
                                modifier = Modifier
                                    .width(96.dp)
                                    .height(140.dp),
                                shape = RoundedCornerShape(14.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                ShimmerBox(
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .height(22.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                ShimmerBox(
                                    modifier = Modifier
                                        .fillMaxWidth(0.5f)
                                        .height(16.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                ShimmerBox(
                                    modifier = Modifier
                                        .width(70.dp)
                                        .height(18.dp),
                                    shape = RoundedCornerShape(9.dp)
                                )
                            }
                        }
                    }
                }

                uiState.detailError != null && uiState.movieDetail == null -> {
                    ErrorStateView(
                        message = uiState.detailError ?: stringResource(R.string.error_network),
                        onRetry = onRetry,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                uiState.movieDetail != null -> {
                    val detail = uiState.movieDetail!!

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 40.dp)
                    ) {
                        // ================= 1 & 2. HERO BACKDROP & OVERLAPPING POSTER =================
                        item {
                            MovieHeroSection(
                                detail = detail,
                                onBackClick = onBackClick
                            )
                        }

                        // Offline Badge
                        item {
                            OfflineBadge(isFromCache = uiState.isFromCache)
                        }

                        // ================= 3. GENRE CHIPS =================
                        if (detail.genres.isNotEmpty()) {
                            item {
                                LazyRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(detail.genres, key = { it.id }) { genre ->
                                        Surface(
                                            shape = RoundedCornerShape(13.dp),
                                            color = Color(0xFF141724),
                                            border = BorderStroke(1.dp, Color(0xFF262B3F))
                                        ) {
                                            Text(
                                                text = genre.name,
                                                color = Color(0xFFCBD5E1),
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 10.5.sp
                                                ),
                                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // ================= 4. OVERVIEW SECTION =================
                        if (detail.overview.isNotBlank()) {
                            item {
                                MovieOverviewSection(overview = detail.overview)
                            }
                        }

                        // ================= 5. YOUTUBE TRAILER PLAYER =================
                        item {
                            MovieTrailerSection(
                                trailer = uiState.selectedTrailer,
                                backdropUrl = detail.fullBackdropUrl ?: detail.fullPosterUrl,
                                isLoading = uiState.isLoadingTrailers
                            )
                        }

                        // ================= 6. USER REVIEWS =================
                        item {
                            Text(
                                text = stringResource(R.string.reviews_count_format, distinctReviews.size),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.5.sp
                                ),
                                color = CinemaAmberStart,
                                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 12.dp)
                            )
                        }

                        if (uiState.isLoadingReviews && distinctReviews.isEmpty()) {
                            items(3) {
                                ReviewItemShimmer(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp))
                            }
                        } else if (distinctReviews.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFF121522))
                                        .padding(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.no_reviews_available),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        } else {
                            items(
                                items = distinctReviews,
                                key = { it.id }
                            ) { review ->
                                CinemaReviewCard(
                                    review = review,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                                )
                            }

                            // Endless scrolling indicator
                            if (uiState.isLoadingMoreReviews) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(22.dp),
                                            color = CinemaAmberStart,
                                            strokeWidth = 2.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Backdrop Hero with Overlapping Poster & Metadata matching the SVG mockup
 */
@Composable
fun MovieHeroSection(
    detail: MovieDetail,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(295.dp)
    ) {
        // 1. Backdrop Hero Area (Height 225.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(225.dp)
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(detail.fullBackdropUrl ?: detail.fullPosterUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.content_desc_backdrop),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    ShimmerBox(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(0.dp)
                    )
                }
            )

            // Backdrop Fade Mask
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.0f to Color.Black.copy(alpha = 0.35f),
                                0.3f to Color.Transparent,
                                0.65f to Color(0xFF08090E).copy(alpha = 0.6f),
                                1.0f to Color(0xFF08090E)
                            )
                        )
                    )
            )

            // Top Floating Back Button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.65f))
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)), CircleShape)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.content_desc_back),
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // 2. Overlapping Poster Card & Primary Info Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 135.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Overlapping Poster Card
            Card(
                modifier = Modifier
                    .width(96.dp)
                    .height(142.dp)
                    .shadow(12.dp, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2435)),
                border = BorderStroke(1.2.dp, Color(0xFF333D56))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(detail.fullPosterUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.content_desc_movie_poster, detail.title),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Title & Metadata to the Right of Poster
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 4.dp)
            ) {
                // Title
                Text(
                    text = detail.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.5.sp,
                        lineHeight = 22.sp
                    ),
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Compact Metadata Row: ★ 9.1 • 99 mnt • 2026
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Rating Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, Color(0xFF2B3045))
                    ) {
                        Text(
                            text = stringResource(R.string.rating_star_format, detail.voteAverage),
                            color = CinemaRatingStar,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    // Runtime & Year
                    val runtimeText = if (detail.runtime != null && detail.runtime > 0) "${detail.runtime} mnt" else ""
                    val yearText = detail.releaseDate?.take(4) ?: ""
                    val metadataCombined = if (runtimeText.isNotEmpty() && yearText.isNotEmpty()) {
                        stringResource(R.string.runtime_year_format, runtimeText, yearText)
                    } else if (runtimeText.isNotEmpty()) {
                        "• $runtimeText"
                    } else if (yearText.isNotEmpty()) {
                        "• $yearText"
                    } else ""

                    if (metadataCombined.isNotEmpty()) {
                        Text(
                            text = metadataCombined,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color(0xFF94A3B8),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Release Status Badge: ✓ Dirilis
                val statusText = detail.status?.ifBlank { null } ?: "Dirilis"
                Surface(
                    shape = RoundedCornerShape(9.dp),
                    color = Color(0xFF15241D),
                    border = BorderStroke(1.dp, Color(0xFF1F3D2F))
                ) {
                    Text(
                        text = stringResource(R.string.status_released_prefix, statusText),
                        color = Color(0xFF34D399),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

/**
 * Overview / Synopsis Section matching SVG Mockup
 */
@Composable
fun MovieOverviewSection(overview: String) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = stringResource(R.string.synopsis_title),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.5.sp
            ),
            color = CinemaAmberStart
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = overview,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.5.sp,
                lineHeight = 18.sp
            ),
            color = Color(0xFF94A3B8),
            maxLines = if (isExpanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis
        )

        if (overview.length > 120) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isExpanded) {
                    stringResource(R.string.read_less)
                } else {
                    stringResource(R.string.read_more)
                },
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = CinemaAmberStart,
                modifier = Modifier
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 2.dp)
            )
        }
    }
}

/**
 * YouTube Trailer Player Section matching SVG Mockup
 */
@Composable
fun MovieTrailerSection(
    trailer: Trailer?,
    backdropUrl: String?,
    isLoading: Boolean
) {
    val context = LocalContext.current
    var isPlayerActive by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.trailer_section_title),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.5.sp
                ),
                color = CinemaAmberStart
            )

            if (trailer != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF161A26),
                    border = BorderStroke(1.dp, Color(0xFF2A3249)),
                    modifier = Modifier.clickable {
                        openYouTubeVideo(context, trailer.key)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = Color(0xFFCC0000),
                            modifier = Modifier.size(13.dp, 10.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(7.dp)
                                )
                            }
                        }
                        Text(
                            text = stringResource(R.string.open_in_youtube),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when {
            isLoading -> {
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                    shape = RoundedCornerShape(18.dp)
                )
            }

            trailer != null -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .shadow(10.dp, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121520)),
                    border = BorderStroke(1.dp, Color(0xFF262B3D))
                ) {
                    if (isPlayerActive) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AppYouTubePlayer(
                                videoId = trailer.key,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Close Player Button to return to thumbnail preview
                            IconButton(
                                onClick = { isPlayerActive = false },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.7f))
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = stringResource(R.string.close_player),
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    } else {
                        // Trailer Artwork Preview with glowing Play button matching SVG
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { isPlayerActive = true },
                            contentAlignment = Alignment.Center
                        ) {
                            // Video Thumbnail Artwork
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(backdropUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Scrim overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.45f))
                            )

                            // YouTube Branding Badge (Top Left) - clicking directly opens YouTube
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFCC0000),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(14.dp)
                                    .clickable { openYouTubeVideo(context, trailer.key) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.content_desc_youtube),
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }

                            // Center Pulsing Play Button
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .shadow(8.dp, CircleShape)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(
                                                    CinemaAmberStart,
                                                    CinemaAmberEnd
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.PlayArrow,
                                        contentDescription = stringResource(R.string.content_desc_play_trailer),
                                        tint = Color.Black,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = stringResource(R.string.watch_trailer_action),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp
                                    ),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            else -> {
                // Negative case: No trailer available
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF121520))
                        .border(BorderStroke(1.dp, Color(0xFF262B3D)), RoundedCornerShape(18.dp))
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.VideocamOff,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.no_trailer_available),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

/**
 * User Review Card matching SVG Mockup
 */
@Composable
fun CinemaReviewCard(
    review: Review,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121522)),
        border = BorderStroke(1.dp, Color(0xFF23273A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Avatar: initial circle or picture
                val initial = review.author.trim().take(1).uppercase().ifEmpty { "U" }
                if (!review.fullAvatarUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(review.fullAvatarUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF59E0B).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initial,
                            color = CinemaAmberStart,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.author.ifBlank { stringResource(R.string.anonymous_user) },
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (!review.createdAt.isNullOrBlank()) {
                        Text(
                            text = review.createdAt.take(10),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 9.sp
                            ),
                            color = Color(0xFF64748B)
                        )
                    }
                }

                if (review.rating != null && review.rating > 0.0) {
                    Text(
                        text = stringResource(R.string.rating_star_format, review.rating),
                        color = CinemaRatingStar,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quoted Review Content
            Text(
                text = "\"${review.content.trim()}\"",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.5.sp,
                    lineHeight = 16.sp
                ),
                color = Color(0xFF94A3B8),
                maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis
            )

            if (review.content.length > 120) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isExpanded) {
                        stringResource(R.string.read_less)
                    } else {
                        stringResource(R.string.read_more)
                    },
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = CinemaAmberStart
                )
            }
        }
    }
}

// ================= PREVIEWS =================

@Preview(name = "Movie Detail Screen - Success", showBackground = true)
@Composable
private fun MovieDetailScreenSuccessPreview() {
    TechnicalTestAndroidTheme {
        MovieDetailContent(
            uiState = MovieDetailUiState(
                isLoadingDetail = false,
                movieDetail = MovieDetail(
                    id = 1,
                    title = "Colony",
                    overview = "Professor Se-jeong is thrust into a bloody nightmare when a rapidly mutating virus is released during a biotech conference causing authorities to seal the facility. Trapped inside with no escape, Se-jeong must fight for survival.",
                    posterPath = "/poster.jpg",
                    backdropPath = "/backdrop.jpg",
                    releaseDate = "2026-04-12",
                    voteAverage = 8.1,
                    voteCount = 420,
                    runtime = 123,
                    status = "Released",
                    genres = listOf(
                        Genre(28, "Action"),
                        Genre(27, "Horror"),
                        Genre(878, "Science Fiction")
                    )
                ),
                trailers = listOf(
                    Trailer("t1", "dQw4w9WgXcQ", "Official Trailer", "YouTube", "Trailer", true)
                ),
                reviews = listOf(
                    Review("r1", "Leno", "Great visual effects and intense atmosphere throughout the whole runtime. Must watch!", "2026-09-02", null, 9.0),
                    Review("r2", "Sarah", "A gripping survival thriller with stellar performances.", "2026-09-05", null, 8.5)
                )
            ),
            onBackClick = {},
            onRefresh = {},
            onRetry = {},
            onLoadMoreReviews = {}
        )
    }
}

@Preview(name = "Movie Detail Screen - Loading", showBackground = true)
@Composable
private fun MovieDetailScreenLoadingPreview() {
    TechnicalTestAndroidTheme {
        MovieDetailContent(
            uiState = MovieDetailUiState(
                isLoadingDetail = true,
                movieDetail = null
            ),
            onBackClick = {},
            onRefresh = {},
            onRetry = {},
            onLoadMoreReviews = {}
        )
    }
}

@Preview(name = "Movie Detail Screen - Error", showBackground = true)
@Composable
private fun MovieDetailScreenErrorPreview() {
    TechnicalTestAndroidTheme {
        MovieDetailContent(
            uiState = MovieDetailUiState(
                isLoadingDetail = false,
                movieDetail = null,
                detailError = "Network connection issue. Please check your internet connection."
            ),
            onBackClick = {},
            onRefresh = {},
            onRetry = {},
            onLoadMoreReviews = {}
        )
    }
}

@Preview(name = "Review Card Preview", showBackground = true)
@Composable
private fun ReviewCardPreview() {
    TechnicalTestAndroidTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            CinemaReviewCard(
                review = Review(
                    id = "r1",
                    author = "Leno",
                    content = "Great visual effects and intense atmosphere throughout the whole runtime. Highly recommended for fans of the genre!",
                    createdAt = "2026-09-02",
                    avatarPath = null,
                    rating = 9.0
                )
            )
        }
    }
}

