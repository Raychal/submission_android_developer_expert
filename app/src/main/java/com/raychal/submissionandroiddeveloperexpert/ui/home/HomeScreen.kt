package com.raychal.submissionandroiddeveloperexpert.ui.home

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.raychal.core.R
import com.raychal.core.navigation.NavigationManager
import com.raychal.core.navigation.Screen
import com.raychal.core.ui.components.GameItem
import com.raychal.core.ui.components.LottieNotFoundAnimation
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    navigationManager: NavigationManager = koinInject()
) {
    val context = LocalContext.current
    val games = viewModel.gamesPagingData.collectAsLazyPagingItems()
    val isRefreshing = games.loadState.refresh is LoadState.Loading

    val youAreOfflineText = stringResource(R.string.you_are_offline)
    val serverErrorText = stringResource(R.string.server_error)

    LaunchedEffect(games.loadState) {
        val errorState = games.loadState.append as? LoadState.Error
            ?: games.loadState.prepend as? LoadState.Error
        
        if (errorState?.error is IOException) {
            Toast.makeText(context, youAreOfflineText, Toast.LENGTH_SHORT).show()
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { games.refresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                verticalItemSpacing = 4.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(
                    count = games.itemCount,
                    key = games.itemKey { it.id }
                ) { index ->
                    games[index]?.let { game ->
                        GameItem(
                            game = game,
                            modifier = Modifier.clickable {
                                navigationManager.navigate(Screen.Detail(game.id))
                            }
                        )
                    }
                }

                when (val state = games.loadState.append) {
                    is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                    else -> {}
                }
            }

            when (val state = games.loadState.refresh) {
                is LoadState.Loading -> {
                    if (games.itemCount == 0) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
                is LoadState.Error -> {
                    if (games.itemCount == 0) {
                        LottieNotFoundAnimation(
                            lottieFile = R.raw.no_connection,
                            message = if (state.error is IOException) youAreOfflineText else serverErrorText
                        )
                    }
                }
                is LoadState.NotLoading -> {
                    if (games.itemCount == 0) {
                        LottieNotFoundAnimation()
                    }
                }
            }
        }
    }
}
