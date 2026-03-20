package com.raychal.submissionandroiddeveloperexpert.ui.home

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.raychal.core.R
import com.raychal.core.navigation.NavigationManager
import com.raychal.core.navigation.Screen
import com.raychal.core.ui.components.GameItem
import com.raychal.core.ui.components.LineScaleProgressIndicator
import com.raychal.core.ui.components.LoadingCard
import com.raychal.core.ui.components.LottieNotFoundAnimation
import com.raychal.core.ui.components.Width
import com.raychal.core.ui.theme.Background
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.io.IOException
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    navigationManager: NavigationManager = koinInject()
) {
    val context = LocalContext.current
    val games = viewModel.gamesPagingData.collectAsLazyPagingItems()
    val isRefreshing = games.loadState.refresh is LoadState.Loading && games.itemCount > 0

    val lazyListState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()

    val youAreOfflineText = stringResource(R.string.you_are_offline)
    val serverErrorText = stringResource(R.string.server_error)

    LaunchedEffect(games.loadState) {
        val errorState = games.loadState.append as? LoadState.Error
            ?: games.loadState.prepend as? LoadState.Error
        
        if (errorState?.error is IOException) {
            Toast.makeText(context, youAreOfflineText, Toast.LENGTH_SHORT).show()
        }
    }

    val isInitialLoad = games.loadState.refresh is LoadState.Loading && games.itemCount == 0

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { games.refresh() },
        modifier = Modifier.fillMaxSize().testTag("PullToRefreshBox"),
        indicator = {
            PullToRefreshIndicator(
                progress = pullToRefreshState.distanceFraction,
                isRefreshing = isRefreshing,
                modifier = Modifier.testTag("PullToRefreshIndicator")
            )
        },
        state = pullToRefreshState
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().testTag("GameList"),
            state = lazyListState,
            contentPadding = PaddingValues(16.dp)
        ) {
            items(
                count = games.itemCount,
                key = { index -> games[index]?.id ?: index }
            ) { index ->
                val game = games[index] ?: return@items
                GameItem(
                    game = game,
                    modifier = Modifier
                        .testTag("GameItem")
                        .clickable {
                            navigationManager.navigate(Screen.Detail(game.id))
                        }
                )
            }

            when (games.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LineScaleProgressIndicator()
                        }
                    }
                }
                else -> {}
            }
        }

        when (val state = games.loadState.refresh) {
            is LoadState.Loading -> {
                if (isInitialLoad) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().testTag("LoadingList"),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(4) {
                            LoadingCard(
                                heightForPicture = 200
                            )
                        }
                    }
                }
            }
            is LoadState.Error -> {
                if (games.itemCount == 0) {
                    LottieNotFoundAnimation(
                        lottieFile = R.raw.no_connection,
                        message = if (state.error is IOException) youAreOfflineText else serverErrorText,
                        modifier = Modifier.testTag("ErrorAnimation")
                    )
                }
            }
            is LoadState.NotLoading -> {
                if (games.itemCount == 0) {
                    LottieNotFoundAnimation(modifier = Modifier.testTag("EmptyAnimation"))
                }
            }
        }
    }
}

@Composable
fun PullToRefreshIndicator(progress: Float, isRefreshing: Boolean, modifier: Modifier = Modifier) {
    val rowHeight = if (isRefreshing) { 60.dp } else {
        (progress * 50).roundToInt().dp
    }
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(rowHeight)
            .background(Background)
            .padding(top = 8.dp)
    ) {
        if (progress >= 1 || isRefreshing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                LineScaleProgressIndicator()
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Width(8)
                Text(
                    "Pull to Refresh",
                    color = Color.White,
                )
            }
        }

    }
}
