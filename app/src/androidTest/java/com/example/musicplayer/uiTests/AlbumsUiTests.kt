package com.example.musicplayer.uiTests

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.example.musicplayer.utils.app
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AlbumsUiTests : UiTest() {

    @Before
    fun navigateToAlbumsScreen() {
        composeTestRule.apply {
            onNodeWithContentDescription("Albums page").performClick()
            waitForIdle()
        }
    }

    @Test
    fun isAlbumsListDisplayed() = runTest {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val count = app(ctx).container.musicRepository.getAllAlbums().size

        composeTestRule
            .onNodeWithTag("AlbumsList")
            .assertIsDisplayed()
            .onChildren()
            .assertCountEquals(count)
    }

    @Test
    fun viewAlbumTracks() {
        composeTestRule.apply {
            onNodeWithTag("AlbumsList")
                .onChildren()
                .onFirst()
                .performClick()
            waitForIdle()
            onNodeWithTag("PageTitle").assertIsDisplayed()
            onNodeWithTag("TrackList").onChildren()
        }
    }

}