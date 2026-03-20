package com.raychal.favorite.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.play.core.splitcompat.SplitCompat
import com.raychal.core.R
import com.raychal.core.navigation.NavigationManager
import com.raychal.core.navigation.Screen
import com.raychal.core.ui.components.GameItem
import com.raychal.core.ui.components.LineScaleProgressIndicator
import com.raychal.core.ui.components.LottieNotFoundAnimation
import com.raychal.core.ui.theme.Background
import com.raychal.core.ui.theme.SubmissionAndroidDeveloperExpertTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.context.loadKoinModules


class FavoriteActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)
        SplitCompat.installActivity(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(listOf(favoriteModule))
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.WHITE))
        setContent {
            SubmissionAndroidDeveloperExpertTheme {
                val navigationManager: NavigationManager = koinInject()
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(R.string.favorite)) },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = stringResource(R.string.back)
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Background,
                                titleContentColor = Color.White,
                                navigationIconContentColor = Color.White
                            )
                        )
                    },
                    containerColor = Background
                ) { innerPadding ->
                    Box(modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()) {
                        FavoriteScreen(
                            onItemClick = { gameId ->
                                navigationManager.navigate(Screen.Detail(gameId))
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModel = koinViewModel(),
    onItemClick: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(FavoriteIntent.GetFavorites)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            LineScaleProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (state.favorites.isEmpty() && !state.isLoading) {
            LottieNotFoundAnimation(
                message = stringResource(R.string.no_favorite_game_yet),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().testTag("FavoriteList"),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(state.favorites, key = { it.id }) { game ->
                GameItem(
                    game = game,
                    modifier = Modifier.clickable {
                        onItemClick(game.id)
                    }
                )
            }
        }
    }
}
