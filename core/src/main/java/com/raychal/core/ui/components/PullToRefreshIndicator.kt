package com.raychal.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.raychal.core.ui.theme.background
import kotlin.math.roundToInt

@Composable
fun PullToRefreshIndicator(progress: Float, isRefreshing: Boolean) {
    val rowHeight = if (isRefreshing) { 60.dp } else {
        (progress * 70).roundToInt().dp
    }
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight)
            .background(background)
            .padding(top = 8.dp)
    ) {
        if (progress >= 1 || isRefreshing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                LineScaleProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Pull to Refresh",
                    color = Color.White,
                )
                Image(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = "",
                    modifier = Modifier
                        .width(16.dp)
                        .height(16.dp),
                    colorFilter = ColorFilter.tint(Color.Black),
                    alignment = Alignment.Center
                )
            }
        }

    }
}