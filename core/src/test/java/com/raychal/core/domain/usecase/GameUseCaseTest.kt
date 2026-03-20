package com.raychal.core.domain.usecase

import com.raychal.core.data.Resource
import com.raychal.core.domain.model.Game
import com.raychal.core.domain.repository.IGameRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GameUseCaseTest {

    private lateinit var gameUseCase: GameUseCase

    @Mock
    private lateinit var gameRepository: IGameRepository

    @Before
    fun setUp() {
        gameUseCase = GameInteractor(gameRepository)
    }

    @Test
    fun `getDetailGame should return Success resource when repository returns Success`() = runTest {
        val gameId = 1
        val dummyGame = createDummyGame()
        val expectedResource = Resource.Success(dummyGame)

        `when`(gameRepository.getDetailGame(gameId)).thenReturn(flowOf(expectedResource))

        val resultFlow = gameUseCase.getDetailGame(gameId)

        resultFlow.collect { resource ->
            assertTrue(resource is Resource.Success)
            assertEquals(dummyGame, resource.data)
        }
        verify(gameRepository).getDetailGame(gameId).let { }
        verifyNoMoreInteractions(gameRepository)
    }

    @Test
    fun `getDetailGame should return Error resource when repository returns Error`() = runTest {
        val gameId = 1
        val errorMessage = "Not Found"
        val expectedResource = Resource.Error<Game>(errorMessage)
        `when`(gameRepository.getDetailGame(gameId)).thenReturn(flowOf(expectedResource))

        val resultFlow = gameUseCase.getDetailGame(gameId)

        resultFlow.collect { resource ->
            assertTrue(resource is Resource.Error)
            assertEquals(errorMessage, resource.message)
        }
        verify(gameRepository).getDetailGame(gameId).let { }
        verifyNoMoreInteractions(gameRepository)
    }

    @Test
    fun `getFavoriteGame should return list of games`() = runTest {
        val dummyGames = listOf(createDummyGame(isFavorite = true))

        `when`(gameRepository.getFavoriteGame()).thenReturn(flowOf(dummyGames))

        val resultFlow = gameUseCase.getFavoriteGame()

        resultFlow.collect { games ->
            assertEquals(dummyGames, games)
            assertTrue(games[0].isFavorite)
        }

        verify(gameRepository).getFavoriteGame().let { }
        verifyNoMoreInteractions(gameRepository)
    }

    @Test
    fun `getFavoriteGame should return empty list when no favorites`() = runTest {
        val dummyGames = emptyList<Game>()
        `when`(gameRepository.getFavoriteGame()).thenReturn(flowOf(dummyGames))

        val resultFlow = gameUseCase.getFavoriteGame()

        resultFlow.collect { games ->
            assertTrue(games.isEmpty())
        }
        verify(gameRepository).getFavoriteGame().let { }
        verifyNoMoreInteractions(gameRepository)
    }

    @Test
    fun `setFavoriteGame should call repository update`() = runTest {
        val dummyGame = createDummyGame()
        val newState = true

        gameUseCase.setFavoriteGame(dummyGame, newState)

        verify(gameRepository).setFavoriteGame(dummyGame, newState)
        verifyNoMoreInteractions(gameRepository)
    }

    private fun createDummyGame(isFavorite: Boolean = false) = Game(
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
        movies = emptyList(),
        isFavorite = isFavorite
    )
}
