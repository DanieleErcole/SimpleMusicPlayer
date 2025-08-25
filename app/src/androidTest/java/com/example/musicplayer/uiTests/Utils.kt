package com.example.musicplayer.uiTests

import android.os.Environment
import androidx.test.platform.app.InstrumentationRegistry
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