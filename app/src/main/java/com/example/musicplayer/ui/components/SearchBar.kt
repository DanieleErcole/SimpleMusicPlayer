package com.example.musicplayer.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.musicplayer.R

@Composable
fun SearchInputField(
    text: String,
    placeholder: String? = null,
    onChange: (String) -> Unit,
    modifier: Modifier
) {
    CustomTextField(
        text = text,
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.search),
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.outline,
            )
        },
        onChange = onChange,
        placeholderText = placeholder ?: stringResource(R.string.plholder_search),
        modifier = modifier,
    )
}