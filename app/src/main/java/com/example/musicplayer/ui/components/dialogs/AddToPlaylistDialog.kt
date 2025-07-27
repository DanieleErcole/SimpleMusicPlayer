package com.example.musicplayer.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicplayer.R
import com.example.musicplayer.ui.components.CustomContextMenuCheckboxBtn
import com.example.musicplayer.ui.components.TransparentButton
import com.example.musicplayer.ui.components.Divider
import com.example.musicplayer.ui.state.DialogsVM
import com.example.musicplayer.ui.state.PlaylistsVM

@Composable
fun AddToPlaylistDialog(
    onEndAction: (() -> Unit)? = null,
    plVm: PlaylistsVM,
    dialogsVm: DialogsVM,
    modifier: Modifier = Modifier
) {
    val state = dialogsVm.addDialog.collectAsStateWithLifecycle()
    state.value?.let {
        val tracks by remember { derivedStateOf { it.tracks } }

        BaseDialog(onDismissRequest = { dialogsVm.setAddDialog() }) {
            val playlists = plVm.playlists.collectAsStateWithLifecycle()
            val itemsState = remember {
                mutableStateMapOf<Long, Boolean>().apply {
                    playlists.value.forEach { pl ->
                        put(pl.playlistId, false)
                    }
                }
            }

            Surface {
                Column(
                    modifier = modifier
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(top = 12.dp, start = 12.dp, end = 12.dp)
                ) {
                    Text(
                        text = if (tracks.size == 1) tracks.first().internal.title else "${tracks.size} songs",
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
                    )
                    Divider()
                    LazyColumn(
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        items(playlists.value) { pl ->
                            //TODO: decide whether to search if the tracks are already present or not in the playlists
                            CustomContextMenuCheckboxBtn(
                                onClick = { itemsState[pl.playlistId]?.let { itemsState[pl.playlistId] = !it } },
                                text = pl.name,
                                isChecked = false,
                                tint = MaterialTheme.colorScheme.outline,
                                Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = { /*TODO: open new playlist dialog*/ },
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.add),
                                contentDescription = "New playlist",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                text = "New playlist",
                                fontSize = 14.sp,
                                lineHeight = 14.sp,
                            )
                        }
                    }
                    Divider()
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        TransparentButton(
                            onClick = {
                                //TODO: add the tracks to the checked playlists
                                dialogsVm.setAddDialog()
                                onEndAction?.invoke()
                            },
                            text = "Add",
                            fontSize = 14.sp,
                            lineHeight = TextUnit.Unspecified,
                            enabled = itemsState.containsValue(true),
                        )
                    }
                }
            }
        }
    }
}