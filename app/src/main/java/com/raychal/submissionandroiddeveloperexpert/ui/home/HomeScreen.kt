package com.raychal.submissionandroiddeveloperexpert.ui.home

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.raychal.core.R
import com.raychal.core.navigation.NavigationManager
import com.raychal.core.navigation.Screen
import com.raychal.core.ui.components.FilterChips
import com.raychal.core.ui.components.GameItem
import com.raychal.core.ui.components.LineScaleProgressIndicator
import com.raychal.core.ui.components.LoadingCard
import com.raychal.core.ui.components.LottieNotFoundAnimation
import com.raychal.core.ui.components.Width
import com.raychal.core.ui.theme.Background
import kotlinx.coroutines.launch
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
    val state by viewModel.state.collectAsStateWithLifecycle()
    val games = viewModel.gamesPagingData.collectAsLazyPagingItems()
    val isRefreshing = games.loadState.refresh is LoadState.Loading && games.itemCount > 0

    val lazyListState = rememberLazyListState()
    val lazyVerticalStaggeredState = rememberLazyStaggeredGridState()
    val pullToRefreshState = rememberPullToRefreshState()

    val youAreOfflineText = stringResource(R.string.you_are_offline)
    val serverErrorText = stringResource(R.string.server_error)

    val showButton by remember {
        derivedStateOf {
            lazyVerticalStaggeredState.firstVisibleItemIndex > 20
        }
    }

    val isButtonVisible = showButton && !lazyVerticalStaggeredState.isScrollInProgress

    val scope = rememberCoroutineScope()

    val verticalOffset = if (isRefreshing) {
        60.dp
    } else {
        (pullToRefreshState.distanceFraction * 50).dp
    }

    LaunchedEffect(games.loadState) {
        val errorState = games.loadState.append as? LoadState.Error
            ?: games.loadState.prepend as? LoadState.Error
        
        if (errorState?.error is IOException) {
            Toast.makeText(context, youAreOfflineText, Toast.LENGTH_SHORT).show()
        }
    }

    val isInitialLoad = games.loadState.refresh is LoadState.Loading && games.itemCount == 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (state.genres.isNotEmpty()) {
            val genrePairs = state.genres.map { it.name to it.id.toString() }

            FilterChips(
                modifier = Modifier
                    .fillMaxWidth(),
                dataList = genrePairs,
                activeState = state.selectedGenre,
                onStateChanged = { selectedSlug ->
                    viewModel.sendIntent(HomeIntent.SelectGenre(selectedSlug))
                }
            )
        }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                viewModel.sendIntent(HomeIntent.Refresh())
                games.refresh()
            },
            modifier = Modifier
                .fillMaxSize()
                .testTag("PullToRefreshBox"),
            indicator = {
                PullToRefreshIndicator(
                    progress = pullToRefreshState.distanceFraction,
                    isRefreshing = isRefreshing,
                    modifier = Modifier.testTag("PullToRefreshIndicator")
                )
            },
            state = pullToRefreshState
        ) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = verticalOffset.toPx()
                    }
                    .testTag("GameList"),
                state = lazyVerticalStaggeredState
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

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd).padding(bottom = 60.dp)
            ) {
                AnimatedVisibility(
                    visible = isButtonVisible,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                ) {
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                lazyVerticalStaggeredState.animateScrollToItem(0)
                            }
                        },
                        containerColor = Background,
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = stringResource(R.string.scroll_to_top)
                        )
                    }
                }
            }

            when (val state = games.loadState.refresh) {
                is LoadState.Loading -> {
                    if (isInitialLoad) {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    translationY = verticalOffset.toPx()
                                }
                                .testTag("GameList"),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(20) {
                                LoadingCard(
                                    heightForPicture = 100
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
