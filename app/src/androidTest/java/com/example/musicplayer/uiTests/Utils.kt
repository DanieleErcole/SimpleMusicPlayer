package com.example.musicplayer.uiTests

import android.os.Environment
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.example.musicplayer.ui.AppScreen
import java.io.File
import java.io.FileOutputStream

fun grantPermission(packageName: String, permission: String) {
    InstrumentationRegistry.getInstrumentation().uiAutomation.grantRuntimePermission(packageName, permission)
}

fun copyTestFileToDevice(fileName: String) {
    val ctx = InstrumentationRegistry.getInstrumentation().context
    val inputStream = ctx.assets.open(fileName)

    val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
    val outputFile = File(musicDir, fileName)
    if (!outputFile.exists()) {
        FileOutputStream(outputFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
}

fun navigateTo(page: AppScreen, rule: ComposeTestRule) {
    rule.apply {
        onNodeWithContentDescription("${page.name} page").performClick()
        waitForIdle()
    }
}

fun playSong(rule: ComposeTestRule) {
    rule.apply {
        navigateTo(AppScreen.Tracks, this)
        onNodeWithTag("TrackList")
            .onChildren()
            .onFirst()
            .performClick()
    }
}