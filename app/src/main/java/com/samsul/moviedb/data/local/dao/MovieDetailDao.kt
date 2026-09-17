package com.samsul.moviedb.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.samsul.moviedb.data.local.entity.MovieDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDetailDao {

    @Query("SELECT * FROM movie_details WHERE id = :movieId")
    fun getMovieDetail(movieId: Int): Flow<MovieDetailEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieDetail(movieDetail: MovieDetailEntity): Long
}
