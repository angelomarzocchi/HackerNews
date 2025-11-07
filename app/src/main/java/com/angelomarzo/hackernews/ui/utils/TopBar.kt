package com.angelomarzo.hackernews.ui.utils

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.angelomarzo.hackernews.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    @StringRes textResource: Int,
    modifier: Modifier = Modifier,
    @DrawableRes appIconResource: Int = R.drawable.ic_launcher_foreground
) {

    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dimen_padding_small))
            ) {
                Box(
                    modifier = Modifier
                        .size(MaterialTheme.typography.headlineLarge.fontSize.value.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = appIconResource),
                        contentDescription = stringResource(id = textResource),
                        modifier = Modifier.size(dimensionResource(R.dimen.dimen_top_bar_icon_size)),
                        contentScale = ContentScale.Fit
                    )
                }
                Text(
                    text = stringResource(id = textResource),
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }
    )
}