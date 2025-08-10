package com.example.musicplayer.ui.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicplayer.R
import com.example.musicplayer.ui.state.DialogsVM

@Composable
fun PermissionDeniedDialog(
    modifier: Modifier = Modifier,
    dialogsVm: DialogsVM,
) {
    val show = dialogsVm.permDialog.collectAsStateWithLifecycle()
    if (show.value) {
        AlertDialog(
            onDismissRequest = { dialogsVm.togglePermDialog() },
            title = { Text(stringResource(R.string.perm_denied_title)) },
            text = { Text(stringResource(R.string.read_storage_denied)) },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            ),
            confirmButton = {},
            dismissButton = {},
            modifier = modifier
        )
    }
}