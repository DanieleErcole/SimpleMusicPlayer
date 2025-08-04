package com.example.musicplayer.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicplayer.R
import com.example.musicplayer.ui.components.CustomTextField
import com.example.musicplayer.ui.components.TransparentButton
import com.example.musicplayer.ui.state.DialogsVM
import com.example.musicplayer.ui.state.PlaylistsVM

@Composable
private fun TextInputDialog(
    title: String,
    placeholder: String,
    btnLabel: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }
    val notEmpty by remember { derivedStateOf { text.isNotEmpty() } }

    BaseDialog(onDismissRequest = onDismiss) {
        Surface {
            Column(
                modifier = modifier
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(top = 12.dp, start = 16.dp, end = 12.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
                )
                Box(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    CustomTextField(
                        text = text,
                        onChange = { text = it },
                        placeholderText = placeholder,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(bottom = 1.dp)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                    )
                }
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    TransparentButton(
                        onClick = onDismiss,
                        text = stringResource(R.string.cancel_label),
                        fontSize = 14.sp,
                        lineHeight = TextUnit.Unspecified,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    TransparentButton(
                        onClick = {
                            onConfirm(text)
                            onDismiss()
                        },
                        text = btnLabel,
                        fontSize = 14.sp,
                        lineHeight = TextUnit.Unspecified,
                        enabled = notEmpty,
                    )
                }
            }
        }
    }
}

@Composable
fun NewPlaylistDialog(
    plVm: PlaylistsVM,
    dialogsVM: DialogsVM,
    modifier: Modifier = Modifier
) {
    val isOpen = dialogsVM.openNewDialog.collectAsStateWithLifecycle()
    if (isOpen.value) {
        TextInputDialog(
            title = stringResource(R.string.new_pl_btn),
            placeholder = stringResource(R.string.plholder_name),
            btnLabel = stringResource(R.string.create_btn_label),
            onDismiss = { dialogsVM.setNewDialog() },
            onConfirm = { plVm.newPlaylist(it) },
            modifier = modifier
        )
    }
}

@Composable
fun RenamePlaylistDialog(
    plVm: PlaylistsVM,
    dialogsVM: DialogsVM,
    modifier: Modifier = Modifier
) {
    val state = dialogsVM.renameDialog.collectAsStateWithLifecycle()
    state.value?.let { pl ->
        TextInputDialog(
            title = stringResource(R.string.rename_pl_label),
            placeholder = stringResource(R.string.plholder_name),
            btnLabel = stringResource(R.string.rename_btn_label),
            onDismiss = { dialogsVM.setRenameDialog() },
            onConfirm = {
                plVm.renamePlaylist(pl.playlist, it)
                pl.endAction(it)
            },
            modifier = modifier
        )
    }
}