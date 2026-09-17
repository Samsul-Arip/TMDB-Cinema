package com.samsul.moviedb.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.samsul.moviedb.data.local.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies WHERE genreId = :genreId AND categoryType = :categoryType ORDER BY page ASC, voteAverage DESC")
    fun getMoviesByGenreAndCategory(genreId: Int, categoryType: String): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE genreId = :genreId AND categoryType = 'discover' ORDER BY page ASC, voteAverage DESC")
    fun getMoviesByGenre(genreId: Int): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>): List<Long>

    @Query("DELETE FROM movies WHERE genreId = :genreId AND categoryType = :categoryType")
    suspend fun deleteMoviesByGenreAndCategory(genreId: Int, categoryType: String): Int

    @Query("DELETE FROM movies WHERE genreId = :genreId AND categoryType = 'discover'")
    suspend fun deleteMoviesByGenre(genreId: Int): Int

    @Query("SELECT COUNT(id) FROM movies WHERE genreId = :genreId AND categoryType = 'discover'")
    suspend fun getMovieCountByGenre(genreId: Int): Int
}
