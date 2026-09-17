package com.samsul.moviedb.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.samsul.moviedb.data.local.entity.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    @Query("SELECT * FROM reviews WHERE movieId = :movieId ORDER BY page ASC, createdAt DESC")
    fun getReviewsByMovie(movieId: Int): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>): List<Long>

    @Query("DELETE FROM reviews WHERE movieId = :movieId")
    suspend fun deleteReviewsByMovie(movieId: Int): Int
}
