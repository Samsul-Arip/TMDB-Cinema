package com.samsul.moviedb.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.samsul.moviedb.R
import com.samsul.moviedb.domain.model.Movie
import com.samsul.moviedb.ui.theme.CinemaAmberStart
import com.samsul.moviedb.ui.theme.CinemaCardBackground
import com.samsul.moviedb.ui.theme.CinemaCardBorderNew
import com.samsul.moviedb.ui.theme.CinemaMutedSubtitle
import com.samsul.moviedb.ui.theme.CinemaRatingStar
import com.samsul.moviedb.ui.theme.CinemaSurfaceVariant
import com.samsul.moviedb.ui.theme.CinemaTextPrimary

@Composable
fun CinemaMovieCard(
    movie: Movie,
    genreName: String?,
    onClick: () -> Unit,
    onPlayTrailerClick: () -> Unit = onClick,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CinemaCardBackground),
        border = BorderStroke(1.dp, CinemaCardBorderNew),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Poster Container with 0.72f aspect ratio
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.72f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(CinemaSurfaceVariant)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(movie.fullPosterUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.content_desc_movie_poster, movie.title),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Vignette gradient overlay matching SVG
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.35f),
                                    Color(0xF20F1118)
                                )
                            )
                        )
                )

                // Rating Badge on top right: ★ 8.4
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.rating_star_format, movie.voteAverage),
                        color = CinemaRatingStar,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            // Text Info Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    color = CinemaTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                val year = movie.releaseDate?.take(4) ?: ""
                val subtitle = if (!genreName.isNullOrBlank() && year.isNotEmpty()) {
                    stringResource(R.string.year_genre_format, year, genreName)
                } else year.ifEmpty { genreName ?: "" }

                if (subtitle.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = CinemaMutedSubtitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Action: ▶ Play Trailer
                Text(
                    text = stringResource(R.string.play_trailer),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = CinemaAmberStart,
                    modifier = Modifier.clickable(onClick = onPlayTrailerClick)
                )
            }
        }
    }
}
