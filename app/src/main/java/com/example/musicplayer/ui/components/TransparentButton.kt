package com.example.musicplayer.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.TextUnit

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
    text: String,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    enabled: Boolean = true,
    tint: Color = Color.Unspecified,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            lineHeight = lineHeight,
            color = tint,
        )
    }
}

@Composable
fun TransparentBtnWithContextMenu(
    painter: Painter,
    contentDescription: String,
    enabled: Boolean = true,
    tint: Color,
    modifier: Modifier = Modifier,
    fullSizeIcon: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    var isContextMenuVisible by remember { mutableStateOf(false) }

    Box(contentAlignment = Alignment.Center) {
        TransparentButton(
            onClick = { isContextMenuVisible = true },
            painter = painter,
            contentDescription = contentDescription,
            enabled = enabled,
            tint = tint,
            fullSizeIcon = fullSizeIcon,
            modifier = modifier
        )
        DropdownMenu(
            expanded = isContextMenuVisible,
            onDismissRequest = { isContextMenuVisible = false },
            content = content
        )
    }
}