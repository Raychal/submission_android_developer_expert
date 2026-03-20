package com.raychal.submissionandroiddeveloperexpert.ui.detail

import com.raychal.core.data.Resource
import com.raychal.core.domain.model.Game
import com.raychal.core.domain.usecase.GameUseCase
import com.raychal.core.utils.network.NetworkObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class DetailViewModelTest {

    @Mock
    private lateinit var gameUseCase: GameUseCase

    @Mock
    private lateinit var networkObserver: NetworkObserver

    private lateinit var viewModel: DetailViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    private val dummyGame = Game(
        id = 1,
        name = "Test Game",
        released = "2023-01-01",
        tba = false,
        metaScore = 90,
        backgroundImage = "url",
        rating = 4.5,
        ratingTop = 5,
        updated = "2023-01-01",
        genres = listOf("Action"),
        platforms = listOf("PC"),
        tags = listOf("Singleplayer"),
        esrbRating = "E",
        screenshots = emptyList(),
        movies = emptyList()
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        `when`(networkObserver.observe()).thenReturn(flowOf(NetworkObserver.Status.Available))
        viewModel = DetailViewModel(gameUseCase, networkObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getDetail should update state with Success when usecase returns Success`() = runTest {
        val gameId = 1
        `when`(gameUseCase.getDetailGame(gameId)).thenReturn(flowOf(Resource.Success(dummyGame)))

        viewModel.sendIntent(DetailIntent.GetDetail(gameId))

        val state = viewModel.state.value
        assertEquals(dummyGame, state.game)
        assertEquals(false, state.isLoading)
        assertNull(state.errorMessage)
    }

    @Test
    fun `getDetail should update state with Error when usecase returns Error`() = runTest {
        val gameId = 1
        val errorMessage = "Not Found"
        `when`(gameUseCase.getDetailGame(gameId)).thenReturn(flowOf(Resource.Error(errorMessage)))

        viewModel.sendIntent(DetailIntent.GetDetail(gameId))

        val state = viewModel.state.value
        assertNull(state.game)
        assertEquals(false, state.isLoading)
        assertEquals(errorMessage, state.errorMessage)
    }

    @Test
    fun `toggleFavorite should update isFavorite state`() = runTest {
        val game = dummyGame.copy(isFavorite = false)

        viewModel.sendIntent(DetailIntent.ToggleFavorite(game))

        val state = viewModel.state.value
        assertEquals(true, state.game?.isFavorite)
    }
}
