package com.samsul.moviedb.presentation.genre.all

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Theaters
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.samsul.moviedb.R
import com.samsul.moviedb.core.ui.components.CinemaPullToRefreshBox
import com.samsul.moviedb.core.ui.components.EmptyStateView
import com.samsul.moviedb.core.ui.components.ErrorStateView
import com.samsul.moviedb.core.ui.components.GenreListShimmer
import com.samsul.moviedb.core.ui.components.OfflineBadge
import com.samsul.moviedb.domain.model.Genre
import androidx.compose.ui.tooling.preview.Preview
import com.samsul.moviedb.ui.theme.CinemaAmberEnd
import com.samsul.moviedb.ui.theme.CinemaAmberStart
import com.samsul.moviedb.ui.theme.CinemaMutedSubtitle
import com.samsul.moviedb.ui.theme.CinemaTopAmbientGlow
import com.samsul.moviedb.ui.theme.TechnicalTestAndroidTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun AllGenresScreen(
    onGenreClick: (genreId: Int, genreName: String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: AllGenresViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AllGenresContent(
        uiState = uiState,
        onGenreClick = onGenreClick,
        onBackClick = onBackClick,
        onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
        onRefresh = { viewModel.refresh() },
        onRetry = { viewModel.loadGenres() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllGenresContent(
    uiState: AllGenresUiState,
    onGenreClick: (genreId: Int, genreName: String) -> Unit,
    onBackClick: () -> Unit,
    onSearchQueryChange: (query: String) -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val distinctGenres = remember(uiState.filteredGenres) {
        uiState.filteredGenres.distinctBy { it.id }
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color(0xFF08090E),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.all_genres_title),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = stringResource(R.string.all_genres_subtitle),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp
                            ),
                            color = CinemaMutedSubtitle
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF141724))
                            .border(BorderStroke(1.dp, Color(0xFF252A3C)), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.content_desc_back),
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF08090E)
                )
            )
        }
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
                            CinemaTopAmbientGlow.copy(alpha = 0.12f),
                            Color(0xFF08090E).copy(alpha = 0.95f),
                            Color(0xFF050608)
                        ),
                        center = Offset(x = 540f, y = 80f),
                        radius = 800f
                    )
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Search Bar for Genres
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { onSearchQueryChange(it) },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_genres_hint),
                            color = CinemaMutedSubtitle,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = CinemaAmberStart,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Clear",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF12141F),
                        unfocusedContainerColor = Color(0xFF12141F),
                        focusedBorderColor = CinemaAmberStart,
                        unfocusedBorderColor = Color(0xFF24293D),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                OfflineBadge(isFromCache = uiState.isFromCache)

                when {
                    uiState.isLoading && uiState.genres.isEmpty() -> {
                        GenreListShimmer(modifier = Modifier.fillMaxSize())
                    }

                    uiState.errorMessage != null && uiState.genres.isEmpty() -> {
                        ErrorStateView(
                            message = uiState.errorMessage ?: stringResource(R.string.error_network),
                            onRetry = onRetry,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    distinctGenres.isEmpty() && !uiState.isLoading -> {
                        EmptyStateView(
                            message = stringResource(R.string.empty_genres),
                            onRetry = onRetry,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // "All Movies" First Card (only when not searching)
                            if (uiState.searchQuery.isBlank()) {
                                item(key = -1) {
                                    AllMoviesFeatureCard(
                                        onClick = {
                                            onGenreClick(0, "All")
                                        }
                                    )
                                }
                            }

                            // TMDB Genre Cards
                            items(
                                items = distinctGenres,
                                key = { it.id }
                            ) { genre ->
                                CinemaGenreCard(
                                    genre = genre,
                                    onClick = {
                                        onGenreClick(genre.id, genre.name)
                                    }
                                )
                            }

                            // Bottom spacer
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Featured "All Movies" Card
 */
@Composable
fun AllMoviesFeatureCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(115.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .shadow(6.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161A28)),
        border = BorderStroke(1.dp, CinemaAmberStart.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Transparent,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(CinemaAmberStart, CinemaAmberEnd)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = "›",
                    color = CinemaAmberStart,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column {
                Text(
                    text = stringResource(R.string.all_movies_label),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    ),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = stringResource(R.string.all_movies_subtitle),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = CinemaAmberStart
                )
            }
        }
    }
}

/**
 * Standard Cinema Genre Card
 */
@Composable
fun CinemaGenreCard(
    genre: Genre,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(115.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .shadow(4.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF12141F)),
        border = BorderStroke(1.dp, Color(0xFF24293D))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Genre initial or cinema icon badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1C2030),
                    border = BorderStroke(1.dp, Color(0xFF2E344A)),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = genre.name.take(1).uppercase(),
                            color = CinemaAmberStart,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                        )
                    }
                }

                Text(
                    text = "›",
                    color = Color(0xFF64748B),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column {
                Text(
                    text = genre.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp
                    ),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = stringResource(R.string.explore_movies_prompt),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = CinemaMutedSubtitle
                )
            }
        }
    }
}

// ================= PREVIEWS =================

@Preview(name = "All Genres Screen - Success", showBackground = true)
@Composable
private fun AllGenresScreenSuccessPreview() {
    TechnicalTestAndroidTheme {
        AllGenresContent(
            uiState = AllGenresUiState(
                isLoading = false,
                genres = listOf(
                    Genre(28, "Action"),
                    Genre(12, "Adventure"),
                    Genre(16, "Animation"),
                    Genre(35, "Comedy"),
                    Genre(80, "Crime"),
                    Genre(99, "Documentary")
                )
            ),
            onGenreClick = { _, _ -> },
            onBackClick = {},
            onSearchQueryChange = {},
            onRefresh = {},
            onRetry = {}
        )
    }
}

@Preview(name = "All Genres Screen - Loading", showBackground = true)
@Composable
private fun AllGenresScreenLoadingPreview() {
    TechnicalTestAndroidTheme {
        AllGenresContent(
            uiState = AllGenresUiState(
                isLoading = true,
                genres = emptyList()
            ),
            onGenreClick = { _, _ -> },
            onBackClick = {},
            onSearchQueryChange = {},
            onRefresh = {},
            onRetry = {}
        )
    }
}

@Preview(name = "All Genres Screen - Empty", showBackground = true)
@Composable
private fun AllGenresScreenEmptyPreview() {
    TechnicalTestAndroidTheme {
        AllGenresContent(
            uiState = AllGenresUiState(
                isLoading = false,
                genres = emptyList()
            ),
            onGenreClick = { _, _ -> },
            onBackClick = {},
            onSearchQueryChange = {},
            onRefresh = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Genre Category Card Preview", showBackground = true)
@Composable
private fun CinemaGenreCardPreview() {
    TechnicalTestAndroidTheme {
        Box(modifier = Modifier.padding(16.dp).width(160.dp)) {
            CinemaGenreCard(
                genre = Genre(28, "Action"),
                onClick = {}
            )
        }
    }
}

