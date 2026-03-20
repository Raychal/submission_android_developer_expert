package com.raychal.submissionandroiddeveloperexpert

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.raychal.core.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var device: UiDevice

    @Before
    fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun testSearchFunctionality() {
        val searchPlaceholder = composeTestRule.activity.getString(R.string.search_text)

        composeTestRule.onNodeWithText(searchPlaceholder).performTextInput("The Witcher")

        composeTestRule.waitUntil(30000) {
            composeTestRule.onAllNodes(hasTestTag("GameItem")).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onAllNodes(hasTestTag("GameItem"))[0].assertIsDisplayed()
    }

    @Test
    fun testNavigateToDetailAndBack() {
        composeTestRule.waitUntil(30000) {
            composeTestRule.onAllNodes(hasTestTag("GameItem")).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onAllNodes(hasTestTag("GameItem"))[0].performClick()

        composeTestRule.waitUntil(35000) {
            composeTestRule.onAllNodes(hasTestTag("DetailContent")).fetchSemanticsNodes().isNotEmpty()
        }

        val releaseLabel = composeTestRule.activity.getString(R.string.released)
        composeTestRule.onNodeWithText(releaseLabel, substring = true, useUnmergedTree = true).assertExists()

        repeat(15) {
            composeTestRule.onNodeWithTag("DetailContent").performTouchInput {
                swipeUp(startY = 0.8f, endY = 0.2f, durationMillis = 1500)
            }
            composeTestRule.waitForIdle()
        }

        device.pressBack()
        composeTestRule.waitForIdle()

        val favoriteDesc = composeTestRule.activity.getString(R.string.favorite)
        composeTestRule.onNodeWithContentDescription(favoriteDesc).assertIsDisplayed()
    }

    @Test
    fun testScrollHomeScreen() {
        composeTestRule.waitUntil(30000) {
            composeTestRule.onAllNodes(hasTestTag("GameItem")).fetchSemanticsNodes().isNotEmpty()
        }

        repeat(20) {
            composeTestRule.onNodeWithTag("GameList").performTouchInput {
                swipeUp(startY = 0.8f, endY = 0.2f, durationMillis = 5000)
            }
            composeTestRule.waitForIdle()
        }

        composeTestRule.onAllNodes(hasTestTag("GameItem")).fetchSemanticsNodes().isNotEmpty()
    }

    @Test
    fun testPullToRefresh() {
        composeTestRule.waitUntil(30000) {
            composeTestRule.onAllNodes(hasTestTag("GameItem")).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("PullToRefreshBox").performTouchInput {
            swipeDown(
                startY = 0.5f,
                endY = 0.5f,
                durationMillis = 10000
            )
        }

        composeTestRule.onNodeWithTag("PullToRefreshIndicator").assertExists()

        composeTestRule.waitUntil(30000) {
            composeTestRule.onAllNodes(hasTestTag("GameItem")).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onAllNodes(hasTestTag("GameItem"))[0].assertIsDisplayed()
    }
}
