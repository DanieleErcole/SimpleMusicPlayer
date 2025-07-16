package com.example.musicplayer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicplayer.R
import com.example.musicplayer.data.QueuedTrack
import com.example.musicplayer.ui.components.CustomSlider
import com.example.musicplayer.ui.components.TransparentBtnWithContextMenu
import com.example.musicplayer.ui.components.TransparentButton
import com.example.musicplayer.ui.components.dialogs.AddToPlaylistDialog
import com.example.musicplayer.ui.components.dialogs.LoopDialog
import com.example.musicplayer.ui.state.CurrentPlayingVM
import com.example.musicplayer.ui.state.PlaylistsVM
import com.example.musicplayer.utils.formatTimestamp

@Composable
fun CurrentPlayingScreen(
    modifier: Modifier = Modifier,
    vm: CurrentPlayingVM = viewModel(factory = CurrentPlayingVM.Factory),
    plVm: PlaylistsVM = viewModel(factory = PlaylistsVM.Factory),
) {
    val cur = vm.curTrack.collectAsState()
    cur.value?.let { current ->
        AddToPlaylistDialog(
            track = current.track,
            plVm = plVm
        )

        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(.6f)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(40.dp),
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(current.track.album.thumbnail)
                        .crossfade(true)
                        .build(),
                    contentDescription = current.track.internal.title,
                    error = painterResource(R.drawable.unknown_thumb),
                    placeholder = painterResource(R.drawable.unknown_thumb),
                    contentScale = ContentScale.Crop
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(.4f)
            ) {
                Text(
                    text = current.track.internal.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = current.track.internal.artist,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 6.dp, bottom = 6.dp)
                )
                Text(
                    text = current.track.album.name,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier.fillMaxWidth().height(10.dp))
                UpperToolbar(
                    current = current,
                    plVm = plVm
                )
                Spacer(modifier.fillMaxWidth().height(10.dp))
                SliderToolbar(current = current)
                Spacer(modifier.fillMaxWidth().height(30.dp))
                PlayerControls(vm = vm)
                Spacer(modifier.fillMaxWidth().height(40.dp))
            }
        }
    } ?: NothingPlaying()
}

// Toolbar for info, add to playlist and shuffle
@Composable
fun UpperToolbar(
    current: QueuedTrack,
    plVm: PlaylistsVM,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TransparentButton(
                onClick = {  },
                painter = painterResource(R.drawable.info),
                contentDescription = "Song info",
                tint = MaterialTheme.colorScheme.outline,
            )
            TransparentButton(
                onClick = { plVm.toggleAddDialog() },
                painter = painterResource(R.drawable.playlist_add),
                contentDescription = "Playlist add",
                tint = MaterialTheme.colorScheme.outline,
            )
            TransparentButton(
                onClick = {  },
                painter = painterResource(R.drawable.shuffle),
                contentDescription = "Shuffle",
                tint = MaterialTheme.colorScheme.outline,
            )
        }
    }
}

// Slider toolbar, with timestamps
@Composable
fun SliderToolbar(
    current: QueuedTrack,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "00:00",
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        CustomSlider(
            value = 50f,
            valueRange = 0f..100f,
            onValueChange = {

            },
            modifier = Modifier
                .fillMaxWidth(.8f)
                .height(10.dp)
        )
        Text(
            text = formatTimestamp(current.track.internal.durationMs),
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

// Main toolbar, with skip next/previous, fast forward/rewind, play/pause, volume and loop
@Composable
fun PlayerControls(
    vm: CurrentPlayingVM,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        //TODO: implement volume
        TransparentBtnWithContextMenu(
            painter = painterResource(R.drawable.volume_full),
            contentDescription = "Volume dialog",
            tint = MaterialTheme.colorScheme.outline
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(8.dp)
                    .width(200.dp)
                    .height(20.dp)
            ) {
                Text(
                    text = "50",
                    fontSize = 14.sp,
                    lineHeight = 14.sp
                )
                CustomSlider(
                    value = 50f,
                    valueRange = 0f..100f,
                    onValueChange = {

                    },
                    trackSize = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        TransparentButton(
            onClick = {  },
            painter = painterResource(R.drawable.skip_prev_big),
            contentDescription = "Skip previous",
            tint = MaterialTheme.colorScheme.primary,
            fullSizeIcon = true,
            modifier = Modifier.height(45.dp).width(45.dp)
        )
        TransparentButton(
            onClick = {  },
            painter = painterResource(R.drawable.fast_rewind),
            contentDescription = "Fast rewind",
            tint = MaterialTheme.colorScheme.outline,
            fullSizeIcon = true,
            modifier = Modifier.height(35.dp).width(35.dp)
        )
        //TODO: implement play/pause
        TransparentButton(
            onClick = {  },
            painter = painterResource(R.drawable.play_big),
            contentDescription = "Play/pause",
            tint = MaterialTheme.colorScheme.primary,
            fullSizeIcon = true,
            modifier = Modifier.height(50.dp).width(50.dp)
        )
        TransparentButton(
            onClick = {  },
            painter = painterResource(R.drawable.fast_forward),
            contentDescription = "Fast forward",
            tint = MaterialTheme.colorScheme.outline,
            fullSizeIcon = true,
            modifier = Modifier.height(35.dp).width(35.dp)
        )
        TransparentButton(
            onClick = {  },
            painter = painterResource(R.drawable.skip_next_big),
            contentDescription = "Skip next",
            tint = MaterialTheme.colorScheme.primary,
            fullSizeIcon = true,
            modifier = Modifier.height(45.dp).width(45.dp)
        )
        //TODO: implement loop
        TransparentBtnWithContextMenu(
            painter = painterResource(R.drawable.repeat_one),
            contentDescription = "Loop dialog",
            tint = MaterialTheme.colorScheme.outline
        ) {
            LoopDialog(vm = vm)
        }
    }
}

@Composable
fun NothingPlaying(
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Text(text = stringResource(R.string.NothingPlaying))
    }
}