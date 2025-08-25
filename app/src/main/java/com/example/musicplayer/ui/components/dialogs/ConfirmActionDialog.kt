package com.example.musicplayer.ui.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicplayer.R
import com.example.musicplayer.ui.state.DialogsVM

@Composable
fun ConfirmActionDialog(
    dialogsVm: DialogsVM,
    modifier: Modifier = Modifier
) {
    val state = dialogsVm.confirmDialog.collectAsStateWithLifecycle()
    state.value?.let {
        AlertDialog(
            onDismissRequest = { dialogsVm.setConfirmDialog() },
            title = {
                Text(text = it.title)
            },
            text = {
                it.text?.let {
                    Text(text = it)
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            ),
            confirmButton = {
                TextButton(
                    onClick = {
                        it.action()
                        dialogsVm.setConfirmDialog()
                    },
                    modifier = Modifier.testTag(stringResource(R.string.yes))
                ) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { dialogsVm.setConfirmDialog() },
                    modifier = Modifier.testTag(stringResource(R.string.no))
                ) {
                    Text(stringResource(R.string.no))
                }
            },
            modifier = modifier
        )
    }
}