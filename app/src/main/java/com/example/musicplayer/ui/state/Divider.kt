package com.example.musicplayer.ui.state

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Divider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider (
        thickness = 1.dp,
        modifier = modifier
            .height(1.dp)
            .fillMaxWidth()
    )
}