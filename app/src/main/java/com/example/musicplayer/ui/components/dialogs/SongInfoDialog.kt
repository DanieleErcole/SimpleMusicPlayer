package com.example.musicplayer.ui.components.dialogs

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicplayer.R
import com.example.musicplayer.ui.components.Divider
import com.example.musicplayer.ui.state.DialogsVM
import com.example.musicplayer.utils.formatInstantToHuman
import com.example.musicplayer.utils.formatTimestamp

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SongInfoDialog(
    dialogsVm: DialogsVM,
    horizontalLayout: Boolean,
    modifier: Modifier = Modifier
) {
    val track = dialogsVm.infoTrack.collectAsStateWithLifecycle()
    track.value?.let { track ->
        BaseDialog(onDismissRequest = { dialogsVm.setInfoDialog() }) {
            BoxWithConstraints {
                Surface {
                    Column(
                        modifier = modifier
                            .heightIn(max = maxHeight - if (horizontalLayout) 50.dp else 70.dp)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.info),
                                contentDescription = "Info",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(30.dp, 30.dp)
                            )
                            Text(
                                text = stringResource(R.string.song_info),
                                fontSize = 20.sp,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                        Divider()
                        Column(
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            InfoField(title = stringResource(R.string.location), text = track.internal.location, modifier = Modifier.padding(top = 8.dp))
                            Divider(modifier.padding(vertical = 8.dp))
                            InfoField(title = stringResource(R.string.title), text = track.internal.title)
                            InfoField(title = stringResource(R.string.artist), text = track.internal.artist)
                            InfoField(title = stringResource(R.string.album_artist), text = track.album.artist)
                            InfoField(title = stringResource(R.string.composer), text = track.internal.composer)
                            InfoField(title = stringResource(R.string.genre), text = track.internal.genre)
                            InfoField(title = stringResource(R.string.track_number), text = "${track.internal.trackNumber}")
                            InfoField(title = stringResource(R.string.disc_number), text = "${track.internal.discNumber}")
                            InfoField(title = stringResource(R.string.year), text = "${track.internal.year}")
                            Divider(modifier.padding(vertical = 8.dp))
                            InfoField(title = stringResource(R.string.duration), text = formatTimestamp(track.internal.durationMs))
                            Divider(modifier.padding(vertical = 8.dp))
                            InfoField(title = stringResource(R.string.added_date), text = formatInstantToHuman(track.internal.addedToLibrary))
                            InfoField(title = stringResource(R.string.last_played_date), text = track.internal.lastPlayed?.let { formatInstantToHuman(it) }
                                ?: stringResource(R.string.never_date))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoField(
    title: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = text,
            fontSize = 14.sp,
            lineHeight = 14.sp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}