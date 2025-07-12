package com.example.musicplayer.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun MenuItem(
    onClick: () -> Unit,
    painter: Painter,
    text: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                painter = painter,
                contentDescription = text,
                tint = tint
            )
            Text(
                text = text,
                color = tint
            )
        }
    }
}

@Composable
fun CustomContextMenuBtn(
    onClick: () -> Unit,
    painter: Painter,
    text: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    DropdownMenuItem(
        onClick = onClick,
        text = {
            Text(
                text = text,
                color = tint
            )
        },
        leadingIcon = {
            Icon(
                painter = painter,
                contentDescription = text,
                tint = tint
            )
        },
        modifier = modifier
    )
}

@Composable
fun CustomContextMenuRadioBtn(
    onClick: () -> Unit,
    painter: Painter,
    text: String,
    isSelected: Boolean,
    tint: Color,
    modifier: Modifier = Modifier
) {
    DropdownMenuItem(
        onClick = onClick,
        text = {
            Text(
                text = text,
                color = tint
            )
        },
        leadingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = null
                )
                Spacer(Modifier.fillMaxHeight().width(8.dp))
                Icon(
                    painter = painter,
                    contentDescription = text,
                    tint = tint
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun CustomContextMenuCheckboxBtn(
    onClick: () -> Unit,
    text: String,
    isChecked: Boolean,
    tint: Color,
    modifier: Modifier = Modifier
) {
    DropdownMenuItem(
        onClick = onClick,
        text = {
            Text(
                text = text,
                color = tint
            )
        },
        leadingIcon = {
            Checkbox(
                checked = isChecked,
                onCheckedChange = null
            )
        },
        modifier = modifier
    )
}