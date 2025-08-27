package com.example.musicplayer.uiTests

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.example.musicplayer.R
import com.example.musicplayer.data.Loop
import com.example.musicplayer.ui.AppScreen
import com.example.musicplayer.utils.app
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import kotlin.apply

@OptIn(ExperimentalTestApi::class)
class CurrentPlayingUiTests : UiTest() {

    fun playSong() {
        composeTestRule.apply {
            playSong(this)
            waitUntilExactlyOneExists(hasContentDescription("Song artwork"), 10000)
        }
    }

    @After
    fun resetPlayerState() = runTest {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        app(ctx).playerStateRepository.updateLoop(Loop.None)
        app(ctx).playerStateRepository.updateVolume(100f)
        app(ctx).playerStateRepository.updatePaused(false)
    }

    @Test
    fun isInitiallyEmpty() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        composeTestRule.onNodeWithText(ctx.getString(R.string.nothing_playing))
            .assertIsDisplayed()
    }

    @Test
    fun selectAndPlaySong() {
        playSong()
        composeTestRule.apply {
            waitUntilExactlyOneExists(hasContentDescription("Song artwork"), 10000)
            onNodeWithTag("SongTitle").assertIsDisplayed()
        }
    }

    @Test
    fun pauseAndResume() {
        playSong()
        composeTestRule.apply {
            waitUntilExactlyOneExists(hasContentDescription("Song artwork"), 10000)
            onNodeWithContentDescription("Pause").performClick()
            waitUntilExactlyOneExists(hasContentDescription("Play"), 10000)
            onNodeWithContentDescription("Play")
                .assertIsDisplayed()
                .performClick()
            waitUntilExactlyOneExists(hasContentDescription("Pause"), 10000)
            onNodeWithContentDescription("Pause").assertIsDisplayed()
        }
    }

    @Test
    fun changeLoopMode() {
        playSong()
        composeTestRule.apply {
            waitUntilExactlyOneExists(hasContentDescription("Song artwork"), 10000)
            onNodeWithContentDescription("Loop disabled").performClick()
            onNodeWithTag("ContextMenu")
                .onChildren()[1]
                .performClick()
            waitForIdle()
            onNodeWithContentDescription("Loop queue").assertIsDisplayed()
        }
    }

    @Test
    fun queueAnotherTrackAndSkip() = runTest {
        playSong()

        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val current = app(ctx).container.musicRepository
            .getQueueTracks()
            .first { it.queuedItem.isCurrent }
            .track
        val otherTrackName = app(ctx).container.musicRepository
            .getAllTracks()
            .first { it.album.name == current.album.name && it.internal.title != current.internal.title }
            .internal.title

        composeTestRule.apply {
            navigateTo(AppScreen.Tracks, this)
            onNodeWithTag("TrackList")
                .onChildren()
                .onLast()
                .performClick()
            waitForIdle()
            onNodeWithContentDescription("Skip next").performClick()
            waitForIdle()
            onNodeWithTag("SongTitle").assertTextEquals(otherTrackName)
        }
    }

}