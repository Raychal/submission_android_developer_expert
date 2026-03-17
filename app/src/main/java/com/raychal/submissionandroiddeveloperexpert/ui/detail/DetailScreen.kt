package com.raychal.submissionandroiddeveloperexpert.ui.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.RetainedEffect
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.ContentFrame
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.raychal.core.R
import com.raychal.core.domain.model.Game
import com.raychal.core.navigation.NavigationManager
import com.raychal.core.ui.components.EmptyOrErrorState
import com.raychal.core.ui.components.Height
import com.raychal.core.ui.components.Label
import com.raychal.core.ui.components.LoadingCard
import com.raychal.core.ui.components.StarRatingBar
import com.raychal.core.ui.components.Width
import com.raychal.core.ui.theme.background
import com.raychal.core.utils.network.NetworkObserver
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    gameId: Int,
    viewModel: DetailViewModel = koinViewModel(),
    navigationManager: NavigationManager = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val networkStatus by viewModel.networkStatus.collectAsStateWithLifecycle()

    LaunchedEffect(gameId) {
        viewModel.sendIntent(DetailIntent.GetDetail(gameId))
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { Text(stringResource(R.string.detail), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                navigationIcon = {
                    IconButton(onClick = { navigationManager.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    state.game?.let { game ->
                        IconButton(onClick = { viewModel.sendIntent(DetailIntent.ToggleFavorite(game)) }) {
                            Icon(
                                imageVector = if (game.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = stringResource(R.string.favorite),
                                tint = if (game.isFavorite) Color.Red else LocalContentColor.current
                            )
                        }
                    }
                },
                windowInsets = WindowInsets(0,0,0,0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = background,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = background,
        contentColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.isLoading) {
                LoadingCard(
                    heightForPicture = 250,
                    repeat = 50,
                    containerColor = Color.Transparent
                )
            }

            state.errorMessage?.let { message ->
                EmptyOrErrorState(
                    message = message,
                    onClickRetry = { viewModel.sendIntent(DetailIntent.GetDetail(gameId)) },
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            state.game?.let { game ->
                GameDetailContent(game = game, networkObserver = networkStatus)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailContent(
    game: Game,
    networkObserver: NetworkObserver.Status
) {

    val context = LocalContext.current
    val player = retain {
        ExoPlayer
            .Builder(context.applicationContext)
            .build()
    }

    var isPlaying by retain { mutableStateOf(false) }
    var currentPosition by retain { mutableLongStateOf(0L) }
    var duration by retain { mutableLongStateOf(0L) }
    var isSeeking by retain { mutableStateOf(false) }
    var isBuffering by retain { mutableStateOf(false) }
    var isPlayerUiVisible by retain { mutableStateOf(false) }

    var currentVideoIndex by retain { mutableIntStateOf(0) }

    RetainedEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) { isPlaying = playing }
            override fun onPlaybackStateChanged(state: Int) {
                isBuffering = state == Player.STATE_BUFFERING
                if (state == Player.STATE_READY) duration = player.duration.coerceAtLeast(0)
                if (state == Player.STATE_ENDED) {
                    goToNextVideo()
                }
            }

            private fun goToNextVideo() {
                if (game.movies.isNotEmpty()) {
                    currentVideoIndex = (currentVideoIndex + 1) % game.movies.size
                    val nextMediaItem = MediaItem.fromUri(game.movies[currentVideoIndex].url)
                    player.setMediaItem(nextMediaItem)
                    player.prepare()
                    if (isPlaying) {
                        player.play()
                    }
                }
            }
        }

        player.addListener(listener)

        onRetire {
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(player, isPlaying, isSeeking) {
        while(isPlaying) {
            if(!isSeeking) {
                currentPosition = player.currentPosition.coerceAtLeast(0)
            }
            delay(16L)
        }
    }

    LaunchedEffect(game.movies, networkObserver) {
        if (game.movies.isNotEmpty()) {
            player.setMediaItem(MediaItem.fromUri(game.movies[currentVideoIndex].url))
            if (networkObserver == NetworkObserver.Status.Available) {
                player.prepare()
                player.play()
            }
        }
    }

    LaunchedEffect(isPlayerUiVisible, isPlaying) {
        if (isPlayerUiVisible && isPlaying) {
            delay(3000)
            isPlayerUiVisible = false
        }
    }

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

    val ratingTopImage = when (game.ratingTop) {
        5 -> R.drawable.exceptional
        else -> R.drawable.recommended
    }

    val shape = RoundedCornerShape(8.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = game.backgroundImage,
            contentDescription = game.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Height(8)
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
                        .border(1.dp, colorBorderMetaScore, shape)
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
        
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = game.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 3,
                    modifier = Modifier.weight(1f)
                )

                if (game.ratingTop in listOf(5,4)) {
                    Image(
                        painter = painterResource(id = ratingTopImage),
                        contentDescription = stringResource(R.string.review),
                        modifier = Modifier.size(30.dp),
                        alignment = Alignment.Center,
                        contentScale = ContentScale.Crop
                    )
                }
            }

            game.descriptionRaw?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = stringResource(R.string.review), style = MaterialTheme.typography.labelLarge)
                    Row {
                        StarRatingBar(rating = game.rating.toFloat())
                        Width(4)
                        Text(text = "(${game.rating})", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(text = stringResource(R.string.released), style = MaterialTheme.typography.labelLarge)
                    Text(text = "${game.released}", style = MaterialTheme.typography.bodySmall)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = stringResource(R.string.age_rating), style = MaterialTheme.typography.labelLarge)
                    Text(text = "${game.esrbRating}", style = MaterialTheme.typography.bodySmall)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(text = stringResource(R.string.last_modified), style = MaterialTheme.typography.labelLarge)
                    Text(text = "${game.updated}", style = MaterialTheme.typography.bodySmall)
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = stringResource(R.string.genre), style = MaterialTheme.typography.labelLarge)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    game.genres.forEach {
                        Label(text = it)
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = stringResource(R.string.tags), style = MaterialTheme.typography.labelLarge)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    game.tags.forEach {
                        Label(text = it)
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = stringResource(R.string.screenshots), style = MaterialTheme.typography.labelLarge)
                InfiniteHorizontalCarousel(
                    items = game.screenshots,
                    itemWidth = 350.dp,
                    itemSpacing = 10.dp,
                    contentPadding = PaddingValues(32.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(vertical = 12.dp)
                ) { imageUrl ->
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageUrl)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .height(180.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            }
            if (game.movies.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = stringResource(R.string.trailer), style = MaterialTheme.typography.labelLarge)
                    if (networkObserver == NetworkObserver.Status.Available) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black)
                        ) {
                            ContentFrame(
                                player = player,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable(
                                        interactionSource = null,
                                        indication = null
                                    ) {
                                        isPlayerUiVisible = !isPlayerUiVisible
                                    },
                                contentScale = ContentScale.FillBounds
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                AnimatedVisibility(
                                    visible = isPlayerUiVisible,
                                    enter = fadeIn(),
                                    exit = fadeOut(),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    PlayerUi(
                                        isPlaying = isPlaying,
                                        isBuffering = isBuffering,
                                        currentPosition = currentPosition,
                                        duration = duration,
                                        onSeekBarPositionChange = {
                                            isSeeking = true
                                            currentPosition = it
                                        },
                                        onSeekBarPositionChangeFinished = {
                                            player.seekTo(it)
                                            isSeeking = false
                                        },
                                        onPlayPauseClick = {
                                            when {
                                                !isPlaying && player.playbackState == Player.STATE_ENDED -> {
                                                    player.seekTo(0)
                                                    player.play()
                                                }
                                                !isPlaying -> player.play()
                                                isPlaying -> player.pause()
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(game.movies[0].preview)
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.placeholder)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .height(180.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                    Height(16)
                }
            }
        }
    }
}

@Composable
fun <T> InfiniteHorizontalCarousel(
    items: List<T>,
    itemWidth: Dp,
    itemSpacing: Dp,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 32.dp),
    itemContent: @Composable (T) -> Unit
) {
    if (items.isEmpty()) return

    val repeatedCount = 1000
    val startIndex = (repeatedCount / 2) - ((repeatedCount / 2) % items.size)

    val state = rememberPagerState(initialPage = startIndex) { repeatedCount }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            state.animateScrollToPage(state.currentPage + 1)
        }
    }

    Box(modifier = modifier) {
        HorizontalPager(
            state = state,
            pageSize = PageSize.Fill,
            contentPadding = contentPadding,
            pageSpacing = itemSpacing,
            modifier = Modifier.fillMaxWidth()
        ) { index ->
            val actualIndex = index % items.size
            val item = items[actualIndex]

            Box(
                modifier = Modifier
                    .width(itemWidth)
                    .graphicsLayer {
                        val pageOffset =
                            ((state.currentPage - index) + state.currentPageOffsetFraction).absoluteValue

                        scaleY = lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
            ) {
                itemContent(item)
            }
        }
    }
}
