package com.raychal.favorite.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.raychal.core.navigation.NavigationManager
import com.raychal.core.navigation.Screen
import com.raychal.core.ui.components.EmptyOrErrorState
import com.raychal.core.ui.components.GameItem
import com.raychal.core.ui.theme.SubmissionAndroidDeveloperExpertTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

val favoriteModule = module {
    factory { FavoriteViewModel(get()) }
}

class FavoriteActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(listOf(favoriteModule))
        enableEdgeToEdge()
        setContent {
            SubmissionAndroidDeveloperExpertTheme {
                val navigationManager: NavigationManager = koinInject()
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Favorite Games") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Text("back")
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
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
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (state.favorites.isEmpty() && !state.isLoading) {
            EmptyOrErrorState(
                message = "No favorite games yet.",
                onClickRetry = { viewModel.sendIntent(FavoriteIntent.GetFavorites) },
                modifier = Modifier.align(Alignment.Center)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
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
