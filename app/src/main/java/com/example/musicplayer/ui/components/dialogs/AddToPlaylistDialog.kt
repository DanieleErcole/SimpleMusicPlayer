package com.example.musicplayer.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.data.QueuedTrack
import com.example.musicplayer.ui.components.CustomContextMenuCheckboxBtn
import com.example.musicplayer.ui.components.TransparentButton
import com.example.musicplayer.ui.state.PlaylistsVM

@Composable
fun AddToPlaylistDialog(
    curTrack: QueuedTrack,
    plVm: PlaylistsVM,
    modifier: Modifier = Modifier
) {
    val openAddDialog = plVm.openAddDialog.collectAsState()
    if (openAddDialog.value) {
        BaseDialog(onDismissRequest = { plVm.toggleDialog() }) {
            val track = curTrack.track
            val playlists = plVm.playlists.collectAsState()
            val itemsState = remember {
                mutableStateMapOf<Int, Boolean>().apply {
                    playlists.value.forEach { pl ->
                        put(pl.playlist.playlistId, false)
                    }
                }
            }

            Surface {
                Column(
                    modifier = modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp)
                ) {
                    Text(
                        text = track.internal.title,
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
                    )
                    HorizontalDivider (
                        thickness = 1.dp,
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                    )
                    LazyColumn(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        items(playlists.value) { pl ->
                            if (pl.tracks.find { it.internal.trackId == track.internal.trackId } == null) {
                                CustomContextMenuCheckboxBtn(
                                    onClick = { itemsState[pl.playlist.playlistId]?.let { itemsState[pl.playlist.playlistId] = !it } },
                                    text = pl.playlist.name,
                                    isChecked = false,
                                    tint = MaterialTheme.colorScheme.outline,
                                )
                            }
                        }
                    }
                    HorizontalDivider (
                        thickness = 1.dp,
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        TransparentButton(
                            onClick = {
                                //TODO: add the track to the checked playlists
                                plVm.toggleDialog()
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