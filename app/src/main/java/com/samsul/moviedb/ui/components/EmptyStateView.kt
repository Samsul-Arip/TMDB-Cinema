package com.samsul.moviedb.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Theaters
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samsul.moviedb.R
import com.samsul.moviedb.ui.preview.PreviewConstants
import com.samsul.moviedb.ui.theme.CinemaGold
import com.samsul.moviedb.ui.theme.CinemaTextPrimary
import com.samsul.moviedb.ui.theme.CinemaTextTertiary
import com.samsul.moviedb.ui.theme.TechnicalTestAndroidTheme

@Composable
fun EmptyStateView(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Theaters,
            contentDescription = null,
            tint = CinemaTextTertiary,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = CinemaTextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (onRetry != null) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CinemaGold,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(44.dp)
            ) {
                Text(
                    text = stringResource(R.string.retry),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview(name = PreviewConstants.PREVIEW_EMPTY_STATE, showBackground = true)
@Composable
private fun EmptyStateViewPreview() {
    TechnicalTestAndroidTheme {
        EmptyStateView(
            message = stringResource(R.string.empty_movies),
            onRetry = {}
        )
    }
}

