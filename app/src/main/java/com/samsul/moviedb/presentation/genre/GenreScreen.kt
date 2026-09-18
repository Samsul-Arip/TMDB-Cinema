package com.samsul.moviedb.presentation.genre

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.samsul.moviedb.R
import com.samsul.moviedb.core.ui.components.CinemaPullToRefreshBox
import com.samsul.moviedb.core.ui.components.CategoryRowShimmer
import com.samsul.moviedb.core.ui.components.CinemaMovieCard
import com.samsul.moviedb.core.ui.components.EmptyStateView
import com.samsul.moviedb.core.ui.components.ErrorStateView
import com.samsul.moviedb.core.ui.components.MovieGridShimmer
import com.samsul.moviedb.core.ui.components.OfflineBadge
import androidx.compose.ui.tooling.preview.Preview
import com.samsul.moviedb.domain.model.Genre
import com.samsul.moviedb.domain.model.Movie
import com.samsul.moviedb.ui.theme.CinemaAmberEnd
import com.samsul.moviedb.ui.theme.CinemaAmberStart
import com.samsul.moviedb.ui.theme.CinemaMutedSubtitle
import com.samsul.moviedb.ui.theme.CinemaTopAmbientGlow
import com.samsul.moviedb.ui.theme.TechnicalTestAndroidTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun GenreScreen(
    onMovieClick: (movieId: Int) -> Unit,
    onViewAllGenresClick: () -> Unit,
    viewModel: GenreViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    GenreContent(
        uiState = uiState,
        onMovieClick = onMovieClick,
        onViewAllGenresClick = onViewAllGenresClick,
        onGenreSelect = { viewModel.selectGenre(it) },
        onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
        onToggleSearch = { viewModel.toggleSearch() },
        onRefresh = { viewModel.refresh() },
        onLoadMore = { viewModel.loadNextMoviePage() },
        onRetry = { viewModel.retry() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreContent(
    uiState: GenreUiState,
    onMovieClick: (movieId: Int) -> Unit,
    onViewAllGenresClick: () -> Unit,
    onGenreSelect: (genreId: Int) -> Unit,
    onSearchQueryChange: (query: String) -> Unit,
    onToggleSearch: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gridState = rememberLazyGridState()

    // Map genreId to genreName for fast lookup
    val genreMap = remember(uiState.genres) {
        uiState.genres.associateBy({ it.id }, { it.name })
    }

    val distinctGenres = remember(uiState.genres) {
        uiState.genres.distinctBy { it.id }
    }

    val distinctMovies = remember(uiState.displayedMovies) {
        uiState.displayedMovies.distinctBy { it.id }
    }

    val selectedGenreName = remember(uiState.selectedGenreId, uiState.genres) {
        if (uiState.selectedGenreId == 0) null else genreMap[uiState.selectedGenreId]
    }

    // Scroll to top when category changes
    LaunchedEffect(uiState.selectedGenreId) {
        gridState.scrollToItem(0)
    }

    // Pagination detection
    val shouldPaginate by remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisibleItem >= totalItems - 4
        }
    }

    LaunchedEffect(shouldPaginate) {
        if (shouldPaginate && !uiState.isLoadingMovies && !uiState.isLoadingMore && uiState.canPaginate && uiState.searchQuery.isBlank()) {
            onLoadMore()
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
                            CinemaTopAmbientGlow.copy(alpha = 0.12f),
                            Color(0xFF08090E).copy(alpha = 0.95f),
                            Color(0xFF050608)
                        ),
                        center = Offset(x = 540f, y = 120f),
                        radius = 800f
                    )
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // ================= TOP APP BAR =================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left Brand Badge: ▶ MovieDB
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(7.dp),
                            color = CinemaAmberStart,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 20.sp
                                    )
                                ) {
                                    append("Movie")
                                }
                                withStyle(
                                    SpanStyle(
                                        color = CinemaAmberStart,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 20.sp
                                    )
                                ) {
                                    append("DB")
                                }
                            }
                        )
                    }

                    // Right Action Icon: Search
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF141724))
                            .border(BorderStroke(1.dp, Color(0xFF252A3C)), CircleShape)
                            .clickable { onToggleSearch() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search",
                            tint = if (uiState.isSearchActive) CinemaAmberStart else Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Expandable Search Bar
                AnimatedVisibility(
                    visible = uiState.isSearchActive,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { onSearchQueryChange(it) },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.search_hint),
                                color = CinemaMutedSubtitle,
                                fontSize = 14.sp
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
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }

                // Offline badge
                OfflineBadge(isFromCache = uiState.isFromCache)

                // ================= GENRES SECTION =================
                SectionHeader(
                    title = stringResource(R.string.genres_title),
                    viewAllText = stringResource(R.string.view_all),
                    onViewAllClick = onViewAllGenresClick,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )

                if (uiState.isLoadingGenres && uiState.genres.isEmpty()) {
                    CategoryRowShimmer()
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // "All" filter chip (id = 0, default selected)
                        item(key = 0) {
                            SvgCategoryChip(
                                title = stringResource(R.string.all_genres),
                                isSelected = uiState.selectedGenreId == 0,
                                onClick = { onGenreSelect(0) }
                            )
                        }

                        // TMDB categories
                        items(
                            items = distinctGenres,
                            key = { it.id }
                        ) { genre ->
                            SvgCategoryChip(
                                title = genre.name,
                                isSelected = uiState.selectedGenreId == genre.id,
                                onClick = { onGenreSelect(genre.id) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ================= 2-COLUMN DISCOVER MOVIE GRID =================
                when {
                    (uiState.isLoadingMovies && uiState.movies.isEmpty()) || (uiState.isSearching && uiState.searchResults.isEmpty()) -> {
                        MovieGridShimmer(modifier = Modifier.fillMaxSize())
                    }
                    uiState.errorMessage != null && uiState.movies.isEmpty() && uiState.searchQuery.isBlank() -> {
                        ErrorStateView(
                            message = uiState.errorMessage ?: stringResource(R.string.error_network),
                            onRetry = onRetry,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    !uiState.isLoadingMovies && !uiState.isSearching && distinctMovies.isEmpty() -> {
                        val emptyMsg = if (uiState.searchQuery.isNotBlank()) {
                            stringResource(R.string.empty_search_movies, uiState.searchQuery)
                        } else {
                            stringResource(R.string.empty_movies)
                        }
                        EmptyStateView(
                            message = emptyMsg,
                            onRetry = if (uiState.searchQuery.isNotBlank()) null else onRetry,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        LazyVerticalGrid(
                            state = gridState,
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = distinctMovies,
                                key = { it.id }
                            ) { movie ->
                                val resolvedGenre = movie.genreIds.firstOrNull()?.let { genreMap[it] } ?: selectedGenreName
                                CinemaMovieCard(
                                    movie = movie,
                                    genreName = resolvedGenre,
                                    onClick = { onMovieClick(movie.id) },
                                    onPlayTrailerClick = { onMovieClick(movie.id) }
                                )
                            }

                            // Loading more footer
                            if (uiState.isLoadingMore) {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 20.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp,
                                            color = CinemaAmberStart
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = stringResource(R.string.loading_more),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = CinemaMutedSubtitle
                                        )
                                    }
                                }
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

@Composable
fun SectionHeader(
    title: String,
    viewAllText: String,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            ),
            color = Color.White
        )

        Text(
            text = viewAllText,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            ),
            color = CinemaAmberStart,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable(onClick = onViewAllClick)
                .padding(vertical = 4.dp, horizontal = 2.dp)
        )
    }
}

@Composable
fun SvgCategoryChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color.Transparent else Color(0xFF141624),
        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFF262B3F))
    ) {
        Box(
            modifier = if (isSelected) {
                Modifier.background(
                    Brush.linearGradient(
                        colors = listOf(
                            CinemaAmberStart,
                            CinemaAmberEnd
                        )
                    )
                )
            } else {
                Modifier
            }
                .padding(horizontal = 20.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                    fontSize = 13.5.sp
                ),
                color = if (isSelected) Color.Black else Color(0xFF94A3B8)
            )
        }
    }
}

// ================= PREVIEWS =================

@Preview(name = "Genre Screen - Success", showBackground = true)
@Composable
private fun GenreScreenSuccessPreview() {
    TechnicalTestAndroidTheme {
        GenreContent(
            uiState = GenreUiState(
                isLoadingGenres = false,
                isLoadingMovies = false,
                genres = listOf(
                    Genre(28, "Action"),
                    Genre(12, "Adventure"),
                    Genre(16, "Animation"),
                    Genre(35, "Comedy")
                ),
                selectedGenreId = 0,
                movies = listOf(
                    Movie(1, "Toy Story 5", "Woody and Buzz return", "/poster1.jpg", "/backdrop1.jpg", "2026", 8.4, 500, listOf(16)),
                    Movie(2, "Resident Evil", "Survival action", "/poster2.jpg", "/backdrop2.jpg", "2026", 8.1, 400, listOf(28)),
                    Movie(3, "Colony", "Sci-fi thriller", "/poster3.jpg", "/backdrop3.jpg", "2026", 8.1, 300, listOf(878)),
                    Movie(4, "The Odyssey", "Epic ancient Greece", "/poster4.jpg", "/backdrop4.jpg", "2026", 8.0, 200, listOf(12))
                )
            ),
            onMovieClick = {},
            onViewAllGenresClick = {},
            onGenreSelect = {},
            onSearchQueryChange = {},
            onToggleSearch = {},
            onRefresh = {},
            onLoadMore = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Genre Screen - Loading", showBackground = true)
@Composable
private fun GenreScreenLoadingPreview() {
    TechnicalTestAndroidTheme {
        GenreContent(
            uiState = GenreUiState(
                isLoadingGenres = true,
                isLoadingMovies = true
            ),
            onMovieClick = {},
            onViewAllGenresClick = {},
            onGenreSelect = {},
            onSearchQueryChange = {},
            onToggleSearch = {},
            onRefresh = {},
            onLoadMore = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Genre Screen - Search Active", showBackground = true)
@Composable
private fun GenreScreenSearchPreview() {
    TechnicalTestAndroidTheme {
        GenreContent(
            uiState = GenreUiState(
                isSearchActive = true,
                searchQuery = "Toy",
                searchResults = listOf(
                    Movie(1, "Toy Story 5", "Woody and Buzz return", "/poster1.jpg", "/backdrop1.jpg", "2026", 8.4, 500, listOf(16))
                )
            ),
            onMovieClick = {},
            onViewAllGenresClick = {},
            onGenreSelect = {},
            onSearchQueryChange = {},
            onToggleSearch = {},
            onRefresh = {},
            onLoadMore = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Genre Screen - Empty", showBackground = true)
@Composable
private fun GenreScreenEmptyPreview() {
    TechnicalTestAndroidTheme {
        GenreContent(
            uiState = GenreUiState(
                isLoadingGenres = false,
                isLoadingMovies = false,
                movies = emptyList()
            ),
            onMovieClick = {},
            onViewAllGenresClick = {},
            onGenreSelect = {},
            onSearchQueryChange = {},
            onToggleSearch = {},
            onRefresh = {},
            onLoadMore = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Category Chip Preview", showBackground = true)
@Composable
private fun SvgCategoryChipPreview() {
    TechnicalTestAndroidTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SvgCategoryChip(title = "All", isSelected = true, onClick = {})
            SvgCategoryChip(title = "Action", isSelected = false, onClick = {})
        }
    }
}

