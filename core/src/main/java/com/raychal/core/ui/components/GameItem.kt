package com.raychal.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.raychal.core.R
import com.raychal.core.domain.model.Game
import com.raychal.core.ui.theme.backgroundCard

@Composable
fun GameItem(game: Game, modifier: Modifier = Modifier) {

    val platforms = mutableListOf<Pair<String, Int>>()

    game.platforms.forEach {
        when (it.lowercase()) {
            "pc" -> platforms += Pair("PC", R.drawable.pc)
            "playstation", "playstation 5", "playstation 4", "playstation 3", "playstation 2", "psp", "ps vita" -> platforms += Pair("Play Station", R.drawable.play_station)
            "xbox", "xbox one", "xbox series s/x", "xbox 360" -> platforms += Pair("Xbox", R.drawable.xbox)
            "nintendo", "nintendo switch", "nintendo 3ds", "nintendo ds", "nintendo dsi" -> platforms += Pair("Nintendo", R.drawable.nintendo)
            "linux" -> platforms += Pair("Linux", R.drawable.linux)
            "macos" -> platforms += Pair("Mac", R.drawable.mac)
            "ios" -> platforms += Pair("ios", R.drawable.ios)
            "android" -> platforms += Pair("Android", R.drawable.android)
            "web" -> platforms += Pair("Web", R.drawable.browser)
        }
    }

    val colorFontMetaScore = when (game.metaScore) {
        in 0..50 -> Color.Red
        in 51..74 -> Color(0xFFF0C04F)
        else -> Color(0xFF6DC849)
    }

    val colorBorderMetaScore = when (game.metaScore) {
        in 0..50 -> Color.Red.copy(alpha = 0.5f)
        in 51..74 -> Color(0xFF786434)
        else -> Color(0xFF3F6330)
    }

    val shape = RoundedCornerShape(8.dp)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundCard,
            contentColor = Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(game.backgroundImage)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .crossfade(true)
                    .build(),
                contentDescription = game.name,
                modifier = Modifier.clip(shape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val maxVisible = 3
                    val displayPlatforms = platforms.distinctBy { it.second }.take(maxVisible)
                    val remainingCount = platforms.distinctBy { it.second }.size - maxVisible

                    displayPlatforms.forEach {
                        Icon(
                            painter = painterResource(id = it.second),
                            contentDescription = it.first,
                            tint = Color.White
                        )
                    }

                    if (remainingCount > 0) {
                        Text(
                            text = "+$remainingCount",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.Transparent, shape)
                            .border(1.dp, colorBorderMetaScore,shape)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = game.metaScore.toString(),
                            style = MaterialTheme.typography.bodySmall.merge(color = colorFontMetaScore)
                        )
                    }
                    if (game.tba) {
                        Text(
                            text = stringResource(R.string.tba).uppercase(),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = game.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.rating_with_colon), style = MaterialTheme.typography.bodySmall)
                StarRatingBar(rating = game.rating.toFloat())
                Text(text = "(${game.rating})", style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = stringResource(R.string.released_with_colon, game.released ?: "N/A"),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun StarRatingBar(
    maxStars: Int = 5,
    rating: Float
) {
    val density = LocalDensity.current.density
    val starSize = (5f * density).dp
    val starSpacing = (0.5f * density).dp

    Row(
        modifier = Modifier.selectableGroup(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isSelected = i <= rating
            val icon = if (isSelected) Icons.Filled.Star else Icons.Default.Star
            val iconTintColor = if (isSelected) Color(0xFFFFC700) else Color(0x20FFFFFF)
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTintColor,
                modifier = Modifier.width(starSize).height(starSize)
            )

            if (i < maxStars) {
                Spacer(modifier = Modifier.width(starSpacing))
            }
        }
    }
}
