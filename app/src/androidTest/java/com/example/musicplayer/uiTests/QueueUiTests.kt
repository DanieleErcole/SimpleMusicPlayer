package com.example.musicplayer.uiTests

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.test.platform.app.InstrumentationRegistry
import com.example.musicplayer.R
import com.example.musicplayer.ui.AppScreen
import com.example.musicplayer.utils.app
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class QueueUiTests : UiTest() {

    @Before
    fun queueTracks() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        composeTestRule.apply {
            navigateTo(AppScreen.Albums, this)
            onNodeWithTag(testAlbumName).performClick()
            waitForIdle()
            onNodeWithTag("AlbumOptions").performClick()
            onNodeWithTag(ctx.getString(R.string.play_label)).performClick()
            waitForIdle()
            navigateTo(AppScreen.Queue, this)
        }
    }

    @Test
    fun tracksInQueue() = runTest {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val count = app(ctx).container.musicRepository.getQueueTracks().size
        composeTestRule
            .onNodeWithTag("QueueList")
            .assertIsDisplayed()
            .onChildren()
            .assertCountEquals(count)
    }

    @Test
    fun skipToOtherTrack() {
        composeTestRule.apply {
            onNodeWithTag("QueueList")
                .onChildren()
                .onLast()
                .performClick()
            waitForIdle()
            onNodeWithTag("QueueList")
                .onChildren()
                .filterToOne(hasTestTag("Current"))
                .assertIsDisplayed()
        }
    }

    @Test
    fun reorderCurrentDragAndDrop() = runTest {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val currentTrackName = app(ctx).container.musicRepository
            .getQueueTracks()
            .first { it.queuedItem.isCurrent }
            .track.internal.title

        composeTestRule.apply {
            onNodeWithTag("Current")
                .onChildren()
                .filterToOne(hasTestTag("DragHandle"))
                .performTouchInput {
                    swipeDown(endY = bottom + 200)
                }
            waitForIdle()
            onNodeWithTag("QueueList")
                .onChildren()
                .onLast()
                .assert(hasText(currentTrackName))
        }
    }

    @Test
    fun removeAnElementWithSelection() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        composeTestRule.apply {
            onNodeWithTag("QueueList")
                .onChildren()
                .onFirst()
                .performTouchInput { longClick() }
            waitForIdle()
            onNodeWithContentDescription("Selection options").performClick()
            waitForIdle()
            onNodeWithTag(ctx.getString(R.string.dequeue_label)).performClick()
            waitForIdle()
            onNodeWithTag("QueueList")
                .onChildren()
                .assertCountEquals(1)
        }
    }

    @Test
    fun clearQueue() {
        composeTestRule.apply {
            onNodeWithContentDescription("Clear queue").performClick()
            waitForIdle()
            onNodeWithTag("Yes").performClick()
            waitForIdle()
            onNodeWithTag("QueueList")
                .onChildren()
                .assertCountEquals(0)
        }
    }

}