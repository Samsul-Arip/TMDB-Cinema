package com.samsul.moviedb.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samsul.moviedb.core.util.Resource
import com.samsul.moviedb.domain.usecase.GetMovieDetailUseCase
import com.samsul.moviedb.domain.usecase.GetMovieReviewsUseCase
import com.samsul.moviedb.domain.usecase.GetMovieTrailerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val movieId: Int,
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val getMovieReviewsUseCase: GetMovieReviewsUseCase,
    private val getMovieTrailerUseCase: GetMovieTrailerUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailUiState(isLoadingDetail = true))
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    init {
        loadAll()
    }

    fun refresh() {
        loadAll(isRefresh = true)
    }

    fun loadAll(isRefresh: Boolean = false) {
        if (isRefresh) {
            _uiState.update { it.copy(isRefreshing = true) }
        }
        loadDetail(isRefresh = isRefresh)
        loadTrailers()
        loadReviews(page = 1, reset = isRefresh, isRefresh = isRefresh)
    }

    fun loadDetail(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!isRefresh && _uiState.value.movieDetail == null) {
                _uiState.update { it.copy(isLoadingDetail = true) }
            }
            getMovieDetailUseCase(movieId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        if (!isRefresh) {
                            _uiState.update { it.copy(isLoadingDetail = result.isLoading) }
                        }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoadingDetail = false,
                                isRefreshing = false,
                                movieDetail = result.data,
                                detailError = null,
                                isFromCache = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            val detail = result.data ?: it.movieDetail
                            it.copy(
                                isLoadingDetail = false,
                                isRefreshing = false,
                                movieDetail = detail,
                                detailError = if (detail == null) result.message else null,
                                isFromCache = result.isFromCache && detail != null
                            )
                        }
                    }
                }
            }
        }
    }

    fun loadTrailers() {
        viewModelScope.launch {
            getMovieTrailerUseCase(movieId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoadingTrailers = result.isLoading) }
                    }
                    is Resource.Success -> {
                        val trailers = result.data ?: emptyList()
                        _uiState.update {
                            it.copy(
                                isLoadingTrailers = false,
                                trailers = trailers,
                                selectedTrailer = trailers.firstOrNull()
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(isLoadingTrailers = false)
                        }
                    }
                }
            }
        }
    }

    fun loadReviews(page: Int = 1, reset: Boolean = false, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (page == 1) {
                if (!isRefresh) {
                    _uiState.update { it.copy(isLoadingReviews = true, reviewsError = null) }
                }
            } else {
                _uiState.update { it.copy(isLoadingMoreReviews = true) }
            }

            getMovieReviewsUseCase(movieId = movieId, page = page, reset = reset).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        if (page == 1 && !isRefresh) {
                            _uiState.update { it.copy(isLoadingReviews = result.isLoading) }
                        }
                    }
                    is Resource.Success -> {
                        val freshReviews = result.data ?: emptyList()
                        _uiState.update { current ->
                            val combined = if (page == 1) {
                                freshReviews
                            } else {
                                (current.reviews + freshReviews).distinctBy { it.id }
                            }
                            val hasNewItems = if (page == 1) {
                                freshReviews.isNotEmpty()
                            } else {
                                combined.size > current.reviews.size
                            }
                            current.copy(
                                isLoadingReviews = false,
                                isLoadingMoreReviews = false,
                                reviews = combined,
                                currentReviewPage = page,
                                canPaginateReviews = freshReviews.isNotEmpty() && hasNewItems,
                                reviewsError = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { current ->
                            val fallback = result.data ?: current.reviews
                            current.copy(
                                isLoadingReviews = false,
                                isLoadingMoreReviews = false,
                                reviews = fallback,
                                canPaginateReviews = false,
                                reviewsError = if (fallback.isEmpty()) result.message else null
                            )
                        }
                    }
                }
            }
        }
    }

    fun loadNextReviewPage() {
        val state = _uiState.value
        if (!state.isLoadingDetail &&
            !state.isLoadingReviews &&
            !state.isLoadingMoreReviews &&
            state.canPaginateReviews &&
            state.reviews.isNotEmpty()
        ) {
            loadReviews(page = state.currentReviewPage + 1, reset = false)
        }
    }
}
