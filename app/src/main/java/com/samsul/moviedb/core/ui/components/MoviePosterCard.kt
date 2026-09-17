package com.samsul.moviedb.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.samsul.moviedb.R
import com.samsul.moviedb.domain.model.Movie
import com.samsul.moviedb.ui.theme.CinemaBackground
import com.samsul.moviedb.ui.theme.CinemaCardBorder
import com.samsul.moviedb.ui.theme.CinemaRatingStar
import com.samsul.moviedb.ui.theme.CinemaSurface
import com.samsul.moviedb.ui.theme.CinemaTextPrimary
import com.samsul.moviedb.ui.theme.CinemaTextSecondary

@Composable
fun MoviePosterCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CinemaSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.67f)
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(movie.fullPosterUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.content_desc_movie_poster, movie.title),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                        ShimmerBox(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(0.dp)
                        )
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(CinemaBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = movie.title.take(1),
                                style = MaterialTheme.typography.headlineLarge,
                                color = CinemaCardBorder
                            )
                        }
                    }
                )

                // Top rating badge
                if (movie.voteAverage > 0.0) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xCC121218))
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = stringResource(R.string.content_desc_star),
                            tint = CinemaRatingStar,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = stringResource(R.string.rating_format, movie.voteAverage),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = CinemaTextPrimary
                        )
                    }
                }

                // Bottom gradient scrim
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, CinemaSurface.copy(alpha = 0.95f))
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = CinemaTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (!movie.releaseDate.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.release_date_label, movie.releaseDate.take(4)),
                        style = MaterialTheme.typography.bodySmall,
                        color = CinemaTextSecondary
                    )
                }
            }
        }
    }
}
