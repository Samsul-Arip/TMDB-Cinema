package com.samsul.moviedb.presentation.movielist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.samsul.moviedb.R
import com.samsul.moviedb.ui.components.CinemaPullToRefreshBox
import com.samsul.moviedb.ui.components.EmptyStateView
import com.samsul.moviedb.ui.components.ErrorStateView
import com.samsul.moviedb.ui.components.MovieGridShimmer
import com.samsul.moviedb.ui.components.MoviePosterCard
import com.samsul.moviedb.ui.components.OfflineBadge
import com.samsul.moviedb.ui.preview.PreviewConstants
import com.samsul.moviedb.ui.preview.PreviewData
import com.samsul.moviedb.ui.theme.CinemaBackground
import com.samsul.moviedb.ui.theme.CinemaGold
import com.samsul.moviedb.ui.theme.CinemaTextPrimary
import com.samsul.moviedb.ui.theme.CinemaTextSecondary
import com.samsul.moviedb.ui.theme.MovieAndroidTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MovieListScreen(
    genreId: Int,
    genreName: String,
    onMovieClick: (movieId: Int) -> Unit,
    onBackClick: () -> Unit,
    viewModel: MovieListViewModel = koinViewModel(parameters = { parametersOf(genreId) })
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MovieListContent(
        uiState = uiState,
        genreName = genreName,
        onMovieClick = onMovieClick,
        onBackClick = onBackClick,
        onRefresh = { viewModel.refresh() },
        onLoadMore = { viewModel.loadNextPage() },
        onRetry = { viewModel.loadMovies(page = 1, reset = true) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListContent(
    uiState: MovieListUiState,
    genreName: String,
    onMovieClick: (movieId: Int) -> Unit,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gridState = rememberLazyGridState()

    val distinctMovies = remember(uiState.movies) {
        uiState.movies.distinctBy { it.id }
    }

    // Endless scrolling detection
    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = gridState.layoutInfo.totalItemsCount
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()
            if (lastVisibleItem == null || totalItems == 0) {
                false
            } else {
                lastVisibleItem.index >= totalItems - 4
            }
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.movies_in_genre, genreName),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = CinemaTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.content_desc_back),
                            tint = CinemaGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CinemaBackground)
            )
        },
        containerColor = CinemaBackground
    ) { innerPadding ->
        CinemaPullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                OfflineBadge(isFromCache = uiState.isFromCache)

            when {
                uiState.isLoading && uiState.movies.isEmpty() -> {
                    MovieGridShimmer()
                }

                uiState.errorMessage != null && uiState.movies.isEmpty() -> {
                    ErrorStateView(
                        message = uiState.errorMessage,
                        onRetry = onRetry
                    )
                }

                uiState.movies.isEmpty() -> {
                    EmptyStateView(
                        message = stringResource(R.string.empty_movies),
                        onRetry = onRetry
                    )
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = gridState,
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = distinctMovies,
                            key = { it.id }
                        ) { movie ->
                            MoviePosterCard(
                                movie = movie,
                                onClick = { onMovieClick(movie.id) }
                            )
                        }

                        // Endless scroll loading footer
                        if (uiState.isLoadingMore) {
                            item(span = { GridItemSpan(2) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = CinemaGold,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = stringResource(R.string.loading_more),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = CinemaTextSecondary
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
}

// ================= PREVIEWS =================

@Preview(name = PreviewConstants.PREVIEW_MOVIE_LIST_SUCCESS, showBackground = true)
@Composable
private fun MovieListScreenSuccessPreview() {
    MovieAndroidTheme {
        MovieListContent(
            uiState = MovieListUiState(
                isLoading = false,
                movies = PreviewData.movies
            ),
            genreName = PreviewConstants.MOCK_GENRE_ACTION,
            onMovieClick = {},
            onBackClick = {},
            onRefresh = {},
            onLoadMore = {},
            onRetry = {}
        )
    }
}

@Preview(name = PreviewConstants.PREVIEW_MOVIE_LIST_LOADING, showBackground = true)
@Composable
private fun MovieListScreenLoadingPreview() {
    MovieAndroidTheme {
        MovieListContent(
            uiState = MovieListUiState(
                isLoading = true,
                movies = emptyList()
            ),
            genreName = PreviewConstants.MOCK_GENRE_ACTION,
            onMovieClick = {},
            onBackClick = {},
            onRefresh = {},
            onLoadMore = {},
            onRetry = {}
        )
    }
}

@Preview(name = PreviewConstants.PREVIEW_MOVIE_LIST_EMPTY, showBackground = true)
@Composable
private fun MovieListScreenEmptyPreview() {
    MovieAndroidTheme {
        MovieListContent(
            uiState = MovieListUiState(
                isLoading = false,
                movies = emptyList()
            ),
            genreName = PreviewConstants.MOCK_GENRE_ACTION,
            onMovieClick = {},
            onBackClick = {},
            onRefresh = {},
            onLoadMore = {},
            onRetry = {}
        )
    }
}

