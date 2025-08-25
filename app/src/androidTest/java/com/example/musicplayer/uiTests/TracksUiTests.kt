package com.example.musicplayer.uiTests

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.withKeysDown
import androidx.test.platform.app.InstrumentationRegistry
import com.example.musicplayer.R
import com.example.musicplayer.utils.app
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.apply

class TracksUiTests : UiTest() {

    @Before
    fun navigateToTracksScreen() {
        composeTestRule.apply {
            onNodeWithContentDescription("Tracks page").performClick()
            waitForIdle()
        }
    }

    @Test
    fun isInTracksScreenTest() = runTest {
        composeTestRule.apply {
            onNodeWithTag("PageTitle").assertIsDisplayed()
            onNodeWithTag("TrackList").assertIsDisplayed()
        }
    }

    @Test
    fun trackListTest() = runTest {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val count = app(ctx).container.musicRepository.getAllTracks().size

        composeTestRule
            .onNodeWithTag("TrackList")
            .onChildren()
            .assertCountEquals(count)
    }

    @Test
    fun selectionModeActivatesTest() = runTest {
        composeTestRule.apply {
            onNodeWithTag("TrackList")
                .onChildren()
                .onFirst()
                .performTouchInput { longClick() }
            waitForIdle()
            onNodeWithTag("SelectionToolbar").assertIsDisplayed()
        }
    }

    @Test
    fun songInfoDialogOpenedTest() = runTest {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        composeTestRule.apply {
            onAllNodesWithContentDescription("Track options")
                .onFirst()
                .performClick()
            onNodeWithTag(ctx.getString(R.string.song_info)).performClick()
            waitForIdle()
            onNodeWithTag("SongInfo").assertIsDisplayed()
        }
    }

    @Test
    fun addToPlaylistDialogDisplayedTest() = runTest {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        composeTestRule.apply {
            onAllNodesWithContentDescription("Track options")
                .onFirst()
                .performClick()
            onNodeWithTag(ctx.getString(R.string.playlist_add_label)).performClick()
            waitForIdle()
            onNodeWithTag("AddToPlDialog").assertIsDisplayed()
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun searchTracksTest() = runTest {
        composeTestRule.apply {
            onNodeWithTag("SearchBar")
                .requestFocus()
                .performKeyInput {
                    keyDown(Key.C)
                    keyDown(Key.I)
                    keyDown(Key.A)
                    keyDown(Key.O)
                }
            waitForIdle()
            onNodeWithTag("TrackList")
                .onChildren()
                .assertCountEquals(0)
        }
    }

}