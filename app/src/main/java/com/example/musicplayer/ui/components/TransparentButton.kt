package com.example.musicplayer.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.example.musicplayer.R
import com.example.musicplayer.ui.AppScreen

@Composable
fun TransparentButton(
    onClick: () -> Unit,
    painter: Painter,
    contentDescription: String,
    enabled: Boolean = true,
    tint: Color,
    modifier: Modifier = Modifier,
    fullSizeIcon: Boolean = false
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            tint = tint,
            modifier = if (fullSizeIcon) Modifier.fillMaxSize() else Modifier
        )
    }
}

@Composable
fun TransparentButton(
    onClick: () -> Unit,
    imageVector: ImageVector,
    contentDescription: String,
    enabled: Boolean = true,
    tint: Color,
    modifier: Modifier = Modifier,
    fullSizeIcon: Boolean = false
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint,
            modifier = if (fullSizeIcon) Modifier.fillMaxSize() else Modifier
        )
    }
}