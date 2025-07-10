package com.example.musicplayer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSlider(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    thumbSize: Dp = 16.dp,
    trackSize: Dp = 4.dp,
    modifier: Modifier
) {
    Slider(
        value = value,
        valueRange = valueRange,
        onValueChange = onValueChange,
        track = { sliderState ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(trackSize)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(trackSize)
                        .background(Color.Gray)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(sliderState.value / valueRange.endInclusive)
                        .height(trackSize)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        },
        thumb = {
            Box(
                modifier = Modifier
                    .padding(0.dp)
                    .size(thumbSize)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        },
        modifier = modifier
    )
}