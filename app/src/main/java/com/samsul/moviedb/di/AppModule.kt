package com.samsul.moviedb.di

import androidx.room.Room
import com.samsul.moviedb.BuildConfig
import com.samsul.moviedb.core.network.AuthInterceptor
import com.samsul.moviedb.data.local.AppDatabase
import com.samsul.moviedb.data.remote.TmdbApiService
import com.samsul.moviedb.data.repository.MovieRepositoryImpl
import com.samsul.moviedb.domain.repository.MovieRepository
import com.samsul.moviedb.domain.usecase.GetDiscoverMoviesUseCase
import com.samsul.moviedb.domain.usecase.GetLatestMoviesUseCase
import com.samsul.moviedb.domain.usecase.GetMovieDetailUseCase
import com.samsul.moviedb.domain.usecase.GetMovieGenresUseCase
import com.samsul.moviedb.domain.usecase.GetMovieReviewsUseCase
import com.samsul.moviedb.domain.usecase.GetMovieTrailerUseCase
import com.samsul.moviedb.domain.usecase.GetPopularMoviesUseCase
import com.samsul.moviedb.domain.usecase.SearchMoviesUseCase
import com.samsul.moviedb.presentation.detail.MovieDetailViewModel
import com.samsul.moviedb.presentation.genre.GenreViewModel
import com.samsul.moviedb.presentation.genre.all.AllGenresViewModel
import com.samsul.moviedb.core.network.NetworkMonitor
import com.samsul.moviedb.presentation.movielist.MovieListViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { NetworkMonitor(androidContext()) }

    single {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    single {
        AuthInterceptor(apiKey = BuildConfig.TMDB_API_KEY)
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.TMDB_BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<TmdbApiService> {
        get<Retrofit>().create(TmdbApiService::class.java)
    }
}

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "tmdb_cinema.db"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    single { get<AppDatabase>().genreDao() }
    single { get<AppDatabase>().movieDao() }
    single { get<AppDatabase>().movieDetailDao() }
    single { get<AppDatabase>().reviewDao() }
}

val repositoryModule = module {
    single<MovieRepository> {
        MovieRepositoryImpl(
            apiService = get(),
            genreDao = get(),
            movieDao = get(),
            movieDetailDao = get(),
            reviewDao = get(),
            networkMonitor = get()
        )
    }
}

val useCaseModule = module {
    factory { GetMovieGenresUseCase(get()) }
    factory { GetDiscoverMoviesUseCase(get()) }
    factory { GetLatestMoviesUseCase(get()) }
    factory { GetPopularMoviesUseCase(get()) }
    factory { GetMovieDetailUseCase(get()) }
    factory { GetMovieReviewsUseCase(get()) }
    factory { GetMovieTrailerUseCase(get()) }
    factory { SearchMoviesUseCase(get()) }
}

val viewModelModule = module {
    viewModel {
        GenreViewModel(
            getMovieGenresUseCase = get(),
            getDiscoverMoviesUseCase = get(),
            searchMoviesUseCase = get()
        )
    }
    viewModel {
        AllGenresViewModel(
            getMovieGenresUseCase = get()
        )
    }
    viewModel { (genreId: Int) ->
        MovieListViewModel(
            genreId = genreId,
            getDiscoverMoviesUseCase = get()
        )
    }
    viewModel { (movieId: Int) ->
        MovieDetailViewModel(
            movieId = movieId,
            getMovieDetailUseCase = get(),
            getMovieReviewsUseCase = get(),
            getMovieTrailerUseCase = get()
        )
    }
}

val appModules = listOf(
    networkModule,
    databaseModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)
