package com.example.musicplayer.uiTests

import android.Manifest
import android.os.Build
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.musicplayer.MainActivity
import com.example.musicplayer.utils.app
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class UiTest {

    /*val album = testAlbum()

    val tracks = listOf(
        testTrack(0, album),
        testTrack(1, album, "Artist1")
    )*/

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun initEnvironment() = runTest {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val app = app(ctx)

        grantPermission(
            packageName = "com.example.musicplayer",
            permission =
                if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_AUDIO
                else Manifest.permission.READ_EXTERNAL_STORAGE
        )
        copyTestFileToDevice("test1.mp3")
        copyTestFileToDevice("test2.mp3")
        app.scanner.scanDirectories(ctx)

        /*app.userPreferencesRepository.updateAutoScan(false)
        app.container.musicRepository.apply {
            newAlbum(album)
            tracks.forEach { newTrack(it) }
        }*/
    }

    /*@After
    fun clearDb() = runTest {
        val app = app(InstrumentationRegistry.getInstrumentation().targetContext)
        app.container.musicRepository.apply {
            deleteTrackBlk(tracks)
            deleteAlbum(album)
        }
    }*/

    @After
    fun clearPlayer() = runTest {
        val app = app(InstrumentationRegistry.getInstrumentation().targetContext)
        withContext(Dispatchers.Main) {
            app.playerController.clearQueue()
        }
    }

}