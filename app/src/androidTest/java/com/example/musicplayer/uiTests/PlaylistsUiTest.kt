package com.example.musicplayer.uiTests

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.AnnotatedString
import androidx.test.platform.app.InstrumentationRegistry
import com.example.musicplayer.R
import com.example.musicplayer.ui.AppScreen
import com.example.musicplayer.utils.app
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class PlaylistsUiTest : UiTest() {

    private val playlistName = "New"

    @OptIn(ExperimentalTestApi::class)
    fun newPlaylist(name: String) {
        composeTestRule.apply {
            onNodeWithContentDescription("New playlist").performClick()
            waitForIdle()
            onNodeWithTag("DialogTextInput")
                .performSemanticsAction(SemanticsActions.SetText) {
                    it(AnnotatedString.Builder(name).toAnnotatedString())
                }
            onNodeWithTag("Confirm").performClick()
            waitForIdle()
        }
    }

    @Before
    fun navigateToPlaylistsScreen() = navigateTo(AppScreen.Playlists, composeTestRule)

    @After
    fun clearPlaylists() = runTest {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val musicRepo = app(ctx).container.musicRepository

        musicRepo.apply {
            getAllPlaylists().first().forEach { deletePlaylist(it) }
        }
    }

    @Test
    fun isInPlaylistPage() {
        composeTestRule.onNodeWithTag("PlaylistList").assertIsDisplayed()
    }

    @Test
    fun createNewPlaylist() {
        newPlaylist(playlistName)
        composeTestRule.apply {
            onNodeWithTag("PlaylistList")
                .onChildren()
                .assertCountEquals(1)
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun addTrackToPlaylist() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        newPlaylist(playlistName)

        composeTestRule.apply {
            navigateTo(AppScreen.Tracks, this)
            onAllNodesWithContentDescription("Track options")
                .onFirst()
                .performClick()
            onNodeWithTag(ctx.getString(R.string.playlist_add_label)).performClick()
            waitForIdle()
            onNodeWithTag("CheckboxCtxMenuBtn").performClick()
            onNodeWithTag("AddBtn").performClick()
            waitForIdle()
            navigateTo(AppScreen.Playlists, this)
            onNodeWithTag("PlaylistList")
                .onChildren()
                .onFirst()
                .performClick()
            waitForIdle()
            onNodeWithTag("TrackList")
                .onChildren()
                .assertCountEquals(1)
        }
    }

    @Test
    fun viewPlaylistTracks() {
        newPlaylist(playlistName)
        composeTestRule.apply {
            onNodeWithTag("PlaylistList")
                .onChildren()
                .onFirst()
                .performClick()
            waitForIdle()
            onNodeWithTag("PageTitle").assertIsDisplayed()
        }
    }

}