package com.samsul.moviedb.presentation.detail

import com.samsul.moviedb.domain.model.MovieDetail
import com.samsul.moviedb.domain.model.Review
import com.samsul.moviedb.domain.model.Trailer

data class MovieDetailUiState(
    val isLoadingDetail: Boolean = false,
    val movieDetail: MovieDetail? = null,
    val detailError: String? = null,

    val trailers: List<Trailer> = emptyList(),
    val isLoadingTrailers: Boolean = false,
    val selectedTrailer: Trailer? = null,

    val reviews: List<Review> = emptyList(),
    val isLoadingReviews: Boolean = false,
    val isLoadingMoreReviews: Boolean = false,
    val currentReviewPage: Int = 1,
    val canPaginateReviews: Boolean = true,
    val reviewsError: String? = null,

    val isFromCache: Boolean = false,
    val isRefreshing: Boolean = false
)
