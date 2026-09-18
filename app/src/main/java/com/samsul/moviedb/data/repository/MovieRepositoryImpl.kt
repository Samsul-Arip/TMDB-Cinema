package com.samsul.moviedb.data.repository

import com.samsul.moviedb.core.network.NetworkMonitor
import com.samsul.moviedb.core.util.Constants
import com.samsul.moviedb.core.util.Resource
import com.samsul.moviedb.data.local.dao.GenreDao
import com.samsul.moviedb.data.local.dao.MovieDao
import com.samsul.moviedb.data.local.dao.MovieDetailDao
import com.samsul.moviedb.data.local.dao.ReviewDao
import com.samsul.moviedb.data.mapper.toDomain
import com.samsul.moviedb.data.mapper.toEntity
import com.samsul.moviedb.data.remote.TmdbApiService
import com.samsul.moviedb.domain.model.Genre
import com.samsul.moviedb.domain.model.Movie
import com.samsul.moviedb.domain.model.MovieDetail
import com.samsul.moviedb.domain.model.Review
import com.samsul.moviedb.domain.model.Trailer
import com.samsul.moviedb.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import java.io.IOException

class MovieRepositoryImpl(
    private val apiService: TmdbApiService,
    private val genreDao: GenreDao,
    private val movieDao: MovieDao,
    private val movieDetailDao: MovieDetailDao,
    private val reviewDao: ReviewDao,
    private val networkMonitor: NetworkMonitor? = null
) : MovieRepository {

    private fun isOnline(): Boolean = networkMonitor?.isOnline() ?: true

    private fun getErrorMessage(e: Exception, fallback: String): String {
        return if (e is IOException) {
            Constants.ERROR_NETWORK_CONNECTION
        } else {
            e.localizedMessage ?: fallback
        }
    }

    override fun getMovieGenres(): Flow<Resource<List<Genre>>> = flow {
        val cachedEntities = genreDao.getAllGenres().firstOrNull() ?: emptyList()
        val cachedGenres = cachedEntities.map { it.toDomain() }

        if (!isOnline()) {
            if (cachedGenres.isNotEmpty()) {
                emit(Resource.Error(
                    message = Constants.ERROR_NETWORK_CONNECTION,
                    data = cachedGenres,
                    isFromCache = true
                ))
            } else {
                emit(Resource.Error(message = Constants.ERROR_NETWORK_CONNECTION))
            }
            return@flow
        }

        // Online: preload cached data for instant display without waiting for network
        if (cachedGenres.isNotEmpty()) {
            emit(Resource.Success(data = cachedGenres, isFromCache = false))
        } else {
            emit(Resource.Loading(isLoading = true))
        }

        try {
            val response = apiService.getMovieGenres()
            val dtoList = response.genres ?: emptyList()
            val entities = dtoList.map { it.toEntity() }
            genreDao.insertGenres(entities)

            val freshGenres = dtoList.map { it.toDomain() }
            emit(Resource.Success(data = freshGenres, isFromCache = false))
        } catch (e: Exception) {
            val message = getErrorMessage(e, Constants.ERROR_FAILED_LOAD_GENRES)

            if (cachedGenres.isNotEmpty()) {
                emit(Resource.Error(message = message, data = cachedGenres, isFromCache = true))
            } else {
                emit(Resource.Error(message = message))
            }
        }
    }

    override fun getDiscoverMovies(
        genreId: Int?,
        page: Int,
        reset: Boolean
    ): Flow<Resource<List<Movie>>> = flow {
        val activeGenreId = genreId ?: 0
        if (reset) {
            movieDao.deleteMoviesByGenre(activeGenreId)
        }

        val cachedEntities = (movieDao.getMoviesByGenre(activeGenreId).firstOrNull() ?: emptyList()).distinctBy { it.id }
        val cachedMovies = cachedEntities.map { it.toDomain() }.distinctBy { it.id }

        if (!isOnline()) {
            if (cachedMovies.isNotEmpty() && page == 1) {
                emit(Resource.Error(
                    message = Constants.ERROR_NETWORK_CONNECTION,
                    data = cachedMovies,
                    isFromCache = true
                ))
            } else {
                emit(Resource.Error(message = Constants.ERROR_NETWORK_CONNECTION))
            }
            return@flow
        }

        // Online: preload cached page 1 for instant display without waiting for network
        if (cachedMovies.isNotEmpty() && page == 1 && !reset) {
            emit(Resource.Success(data = cachedMovies, isFromCache = false))
        } else if (cachedMovies.isEmpty()) {
            emit(Resource.Loading(isLoading = true))
        }

        try {
            val apiGenreId = if (activeGenreId == 0) null else activeGenreId
            val response = apiService.discoverMovies(genreId = apiGenreId, page = page)
            val dtoList = response.results ?: emptyList()
            val entities = dtoList.map { it.toEntity(genreId = activeGenreId, page = page) }
            movieDao.insertMovies(entities)

            // Emit updated list from local database or fresh response
            val updatedEntities = (movieDao.getMoviesByGenre(activeGenreId).firstOrNull() ?: entities).distinctBy { it.id }
            val movies = updatedEntities.map { it.toDomain() }.distinctBy { it.id }
            emit(Resource.Success(data = movies, isFromCache = false))
        } catch (e: Exception) {
            val message = getErrorMessage(e, Constants.ERROR_FAILED_LOAD_MOVIES)

            if (cachedMovies.isNotEmpty()) {
                emit(Resource.Error(message = message, data = cachedMovies, isFromCache = true))
            } else {
                emit(Resource.Error(message = message))
            }
        }
    }

    override fun getLatestMovies(genreId: Int?): Flow<Resource<List<Movie>>> = flow {
        val activeGenreId = genreId ?: 0
        val cachedEntities = (movieDao.getMoviesByGenreAndCategory(activeGenreId, Constants.CATEGORY_LATEST).firstOrNull() ?: emptyList()).distinctBy { it.id }
        val cachedMovies = cachedEntities.map { it.toDomain() }.distinctBy { it.id }

        if (!isOnline()) {
            if (cachedMovies.isNotEmpty()) {
                emit(Resource.Error(
                    message = Constants.ERROR_NETWORK_CONNECTION,
                    data = cachedMovies,
                    isFromCache = true
                ))
            } else {
                emit(Resource.Error(message = Constants.ERROR_NETWORK_CONNECTION))
            }
            return@flow
        }

        if (cachedMovies.isNotEmpty()) {
            emit(Resource.Success(data = cachedMovies, isFromCache = false))
        } else {
            emit(Resource.Loading(isLoading = true))
        }

        try {
            val response = if (activeGenreId == 0) {
                apiService.getNowPlayingMovies(page = 1)
            } else {
                apiService.discoverMovies(
                    genreId = activeGenreId,
                    page = 1,
                    sortBy = Constants.SORT_BY_RELEASE_DATE_DESC
                )
            }
            val dtoList = response.results ?: emptyList()
            val entities = dtoList.map { it.toEntity(genreId = activeGenreId, page = 1, categoryType = Constants.CATEGORY_LATEST) }
            movieDao.insertMovies(entities)

            val updatedEntities = (movieDao.getMoviesByGenreAndCategory(activeGenreId, Constants.CATEGORY_LATEST).firstOrNull() ?: entities).distinctBy { it.id }
            emit(Resource.Success(data = updatedEntities.map { it.toDomain() }.distinctBy { it.id }, isFromCache = false))
        } catch (e: Exception) {
            val message = getErrorMessage(e, Constants.ERROR_FAILED_LOAD_LATEST)

            if (cachedMovies.isNotEmpty()) {
                emit(Resource.Error(message = message, data = cachedMovies, isFromCache = true))
            } else {
                emit(Resource.Error(message = message))
            }
        }
    }

    override fun getPopularMovies(genreId: Int?): Flow<Resource<List<Movie>>> = flow {
        val activeGenreId = genreId ?: 0
        val cachedEntities = (movieDao.getMoviesByGenreAndCategory(activeGenreId, Constants.CATEGORY_POPULAR).firstOrNull() ?: emptyList()).distinctBy { it.id }
        val cachedMovies = cachedEntities.map { it.toDomain() }.distinctBy { it.id }

        if (!isOnline()) {
            if (cachedMovies.isNotEmpty()) {
                emit(Resource.Error(
                    message = Constants.ERROR_NETWORK_CONNECTION,
                    data = cachedMovies,
                    isFromCache = true
                ))
            } else {
                emit(Resource.Error(message = Constants.ERROR_NETWORK_CONNECTION))
            }
            return@flow
        }

        if (cachedMovies.isNotEmpty()) {
            emit(Resource.Success(data = cachedMovies, isFromCache = false))
        } else {
            emit(Resource.Loading(isLoading = true))
        }

        try {
            val apiGenreId = if (activeGenreId == 0) null else activeGenreId
            val response = apiService.discoverMovies(
                genreId = apiGenreId,
                page = 1,
                sortBy = Constants.SORT_BY_POPULARITY_DESC
            )
            val dtoList = response.results ?: emptyList()
            val entities = dtoList.map { it.toEntity(genreId = activeGenreId, page = 1, categoryType = Constants.CATEGORY_POPULAR) }
            movieDao.insertMovies(entities)

            val updatedEntities = (movieDao.getMoviesByGenreAndCategory(activeGenreId, Constants.CATEGORY_POPULAR).firstOrNull() ?: entities).distinctBy { it.id }
            emit(Resource.Success(data = updatedEntities.map { it.toDomain() }.distinctBy { it.id }, isFromCache = false))
        } catch (e: Exception) {
            val message = getErrorMessage(e, Constants.ERROR_FAILED_LOAD_POPULAR)

            if (cachedMovies.isNotEmpty()) {
                emit(Resource.Error(message = message, data = cachedMovies, isFromCache = true))
            } else {
                emit(Resource.Error(message = message))
            }
        }
    }

    override fun getMovieDetail(movieId: Int): Flow<Resource<MovieDetail>> = flow {
        val cachedEntity = movieDetailDao.getMovieDetail(movieId).firstOrNull()
        val cachedDetail = cachedEntity?.toDomain()

        if (!isOnline()) {
            if (cachedDetail != null) {
                emit(Resource.Error(
                    message = Constants.ERROR_NETWORK_CONNECTION,
                    data = cachedDetail,
                    isFromCache = true
                ))
            } else {
                emit(Resource.Error(message = Constants.ERROR_NETWORK_CONNECTION))
            }
            return@flow
        }

        // Online: preload cached detail for instant display without waiting for network
        if (cachedDetail != null) {
            emit(Resource.Success(data = cachedDetail, isFromCache = false))
        } else {
            emit(Resource.Loading(isLoading = true))
        }

        try {
            val response = apiService.getMovieDetail(movieId)
            movieDetailDao.insertMovieDetail(response.toEntity())
            emit(Resource.Success(data = response.toDomain(), isFromCache = false))
        } catch (e: Exception) {
            val message = getErrorMessage(e, Constants.ERROR_FAILED_LOAD_DETAIL)

            if (cachedDetail != null) {
                emit(Resource.Error(message = message, data = cachedDetail, isFromCache = true))
            } else {
                emit(Resource.Error(message = message))
            }
        }
    }

    override fun getMovieReviews(
        movieId: Int,
        page: Int,
        reset: Boolean
    ): Flow<Resource<List<Review>>> = flow {
        if (reset) {
            reviewDao.deleteReviewsByMovie(movieId)
        }

        val cachedEntities = reviewDao.getReviewsByMovie(movieId).firstOrNull() ?: emptyList()
        val cachedReviews = cachedEntities.map { it.toDomain() }

        if (!isOnline()) {
            if (cachedReviews.isNotEmpty() && page == 1) {
                emit(Resource.Error(
                    message = Constants.ERROR_NETWORK_CONNECTION,
                    data = cachedReviews,
                    isFromCache = true
                ))
            } else {
                emit(Resource.Error(message = Constants.ERROR_NETWORK_CONNECTION))
            }
            return@flow
        }

        // Online: preload cached reviews for page 1 for instant display without waiting for network
        if (cachedReviews.isNotEmpty() && page == 1 && !reset) {
            emit(Resource.Success(data = cachedReviews, isFromCache = false))
        } else if (cachedReviews.isEmpty()) {
            emit(Resource.Loading(isLoading = true))
        }

        try {
            val response = apiService.getMovieReviews(movieId = movieId, page = page)
            val dtoList = response.results ?: emptyList()
            val freshReviews = dtoList.map { it.toDomain() }

            if (dtoList.isNotEmpty()) {
                val entities = dtoList.map { it.toEntity(movieId = movieId, page = page) }
                reviewDao.insertReviews(entities)
            }

            if (page == 1) {
                val updatedEntities = reviewDao.getReviewsByMovie(movieId).firstOrNull() ?: emptyList()
                val reviews = if (updatedEntities.isNotEmpty()) {
                    updatedEntities.map { it.toDomain() }
                } else {
                    freshReviews
                }
                emit(Resource.Success(data = reviews, isFromCache = false))
            } else {
                emit(Resource.Success(data = freshReviews, isFromCache = false))
            }
        } catch (e: Exception) {
            val message = getErrorMessage(e, Constants.ERROR_FAILED_LOAD_REVIEWS)

            if (cachedReviews.isNotEmpty() && page == 1) {
                emit(Resource.Error(message = message, data = cachedReviews, isFromCache = true))
            } else {
                emit(Resource.Error(message = message))
            }
        }
    }

    override fun getMovieTrailers(movieId: Int): Flow<Resource<List<Trailer>>> = flow {
        if (!isOnline()) {
            emit(Resource.Error(message = Constants.ERROR_NETWORK_CONNECTION))
            return@flow
        }

        emit(Resource.Loading(isLoading = true))
        try {
            val response = apiService.getMovieVideos(movieId)
            val allVideos = response.results?.map { it.toDomain() } ?: emptyList()
            val youtubeVideos = allVideos.filter { it.isYouTube }

            // Prefer Trailers, fallback to Teaser or Clips if no Trailer
            val trailers = youtubeVideos.filter { it.type.equals(Constants.VIDEO_TYPE_TRAILER, ignoreCase = true) }
            val finalVideos = if (trailers.isNotEmpty()) trailers else youtubeVideos

            emit(Resource.Success(data = finalVideos, isFromCache = false))
        } catch (e: Exception) {
            val message = getErrorMessage(e, Constants.ERROR_FAILED_LOAD_TRAILERS)
            emit(Resource.Error(message = message))
        }
    }

    override fun searchMovies(query: String, page: Int): Flow<Resource<List<Movie>>> = flow {
        if (query.isBlank()) {
            emit(Resource.Success(data = emptyList(), isFromCache = false))
            return@flow
        }

        if (!isOnline()) {
            emit(Resource.Error(message = Constants.ERROR_NETWORK_CONNECTION))
            return@flow
        }

        emit(Resource.Loading(isLoading = true))
        try {
            val response = apiService.searchMovies(query = query, page = page)
            val dtoList = response.results ?: emptyList()
            val movies = dtoList.map { it.toDomain() }.distinctBy { it.id }
            emit(Resource.Success(data = movies, isFromCache = false))
        } catch (e: Exception) {
            val message = getErrorMessage(e, Constants.ERROR_FAILED_SEARCH_MOVIES)
            emit(Resource.Error(message = message))
        }
    }
}
