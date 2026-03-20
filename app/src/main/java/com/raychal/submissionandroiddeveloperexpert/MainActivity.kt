package com.raychal.submissionandroiddeveloperexpert

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.raychal.core.R
import com.raychal.core.navigation.NavigationCommand
import com.raychal.core.navigation.NavigationManager
import com.raychal.core.navigation.Screen
import com.raychal.core.ui.theme.Background
import com.raychal.core.ui.theme.SubmissionAndroidDeveloperExpertTheme
import com.raychal.submissionandroiddeveloperexpert.ui.detail.DetailScreen
import com.raychal.submissionandroiddeveloperexpert.ui.home.HomeIntent
import com.raychal.submissionandroiddeveloperexpert.ui.home.HomeScreen
import com.raychal.submissionandroiddeveloperexpert.ui.home.HomeViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    private val navigationManager: NavigationManager by inject()

    @RequiresApi(Build.VERSION_CODES.S)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.WHITE))

        setContent {
            SubmissionAndroidDeveloperExpertTheme {
                val navController = rememberNavController()
                val homeViewModel: HomeViewModel = koinViewModel()
                var searchQuery by remember { mutableStateOf("") }

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val isHome = currentRoute?.contains("Screen.Home") == true || currentRoute == null

                LaunchedEffect(Unit) {
                    navigationManager.navigationCommands.collect { command ->
                        when (command) {
                            is NavigationCommand.Navigate -> {
                                if (command.route is Screen.Favorite) {
                                    try {
                                        val uri =
                                            "submissionandroiddeveloperexpert://favorites".toUri()
                                        startActivity(Intent(Intent.ACTION_VIEW, uri))
                                    } catch (e: ClassNotFoundException) {
                                        e.printStackTrace()
                                    }
                                } else {
                                    navController.navigate(command.route)
                                }
                            }
                            NavigationCommand.NavigateUp -> {
                                navController.navigateUp()
                            }
                        }
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (isHome) {
                            TopAppBar(
                                title = {
                                    OutlinedTextField(
                                        value = searchQuery,
                                        onValueChange = {
                                            searchQuery = it
                                            homeViewModel.sendIntent(HomeIntent.SearchGames(it))
                                        },
                                        placeholder = { Text(stringResource(R.string.search_text)) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(end = 16.dp),
                                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                        singleLine = true,
                                        shape = MaterialTheme.shapes.medium,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                                            unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f)
                                        )
                                    )
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Background
                                )
                            )
                        }
                    },
                    floatingActionButton = {
                        if (isHome) {
                            FloatingActionButton(
                                onClick = { navigationManager.navigate(Screen.Favorite) },
                                containerColor = Background,
                                contentColor = Color.White
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = stringResource(R.string.favorite)
                                )
                            }
                        }
                    },
                    containerColor = Background
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<Screen.Home> {
                            HomeScreen(viewModel = homeViewModel)
                        }
                        composable<Screen.Detail> { backStackEntry ->
                            val detail = backStackEntry.toRoute<Screen.Detail>()
                            DetailScreen(gameId = detail.gameId)
                        }
                    }
                }
            }
        }
    }
}
