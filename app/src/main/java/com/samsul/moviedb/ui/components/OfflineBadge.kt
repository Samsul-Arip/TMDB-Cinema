package com.samsul.moviedb.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.samsul.moviedb.ui.preview.PreviewConstants
import androidx.compose.ui.unit.dp
import com.samsul.moviedb.R
import com.samsul.moviedb.ui.theme.CinemaGold
import com.samsul.moviedb.ui.theme.CinemaSurfaceVariant
import com.samsul.moviedb.ui.theme.TechnicalTestAndroidTheme

@Composable
fun OfflineBadge(
    isFromCache: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isFromCache,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CinemaSurfaceVariant)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.CloudOff,
                contentDescription = stringResource(R.string.content_desc_offline),
                tint = CinemaGold,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.offline_cached_data),
                style = MaterialTheme.typography.bodySmall,
                color = CinemaGold
            )
        }
    }
}

@Preview(name = PreviewConstants.PREVIEW_OFFLINE_BADGE, showBackground = true)
@Composable
private fun OfflineBadgePreview() {
    TechnicalTestAndroidTheme {
        OfflineBadge(isFromCache = true)
    }
}

