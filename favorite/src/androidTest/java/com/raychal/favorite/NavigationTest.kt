package com.raychal.favorite

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.raychal.core.R
import com.raychal.submissionandroiddeveloperexpert.MainActivity
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
    fun testNavigateToFavorite() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("submissionandroiddeveloperexpert://favorites")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)

        val favoriteTitle = composeTestRule.activity.getString(R.string.favorite)

        val isFavoriteLoaded = device.wait(Until.hasObject(By.text(favoriteTitle)), 15000)
        assert(isFavoriteLoaded) { "Favorite screen with title '$favoriteTitle' not found" }
    }
}
