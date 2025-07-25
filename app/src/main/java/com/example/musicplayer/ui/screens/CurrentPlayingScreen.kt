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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicplayer.R
import com.example.musicplayer.data.Loop
import com.example.musicplayer.data.TrackWithAlbum
import com.example.musicplayer.ui.components.CustomSlider
import com.example.musicplayer.ui.components.TransparentBtnWithContextMenu
import com.example.musicplayer.ui.components.TransparentButton
import com.example.musicplayer.ui.components.dialogs.AddToPlaylistDialog
import com.example.musicplayer.ui.components.dialogs.LoopDialog
import com.example.musicplayer.ui.components.dialogs.SongInfoDialog
import com.example.musicplayer.ui.state.CurrentPlayingVM
import com.example.musicplayer.ui.state.DialogsVM
import com.example.musicplayer.utils.formatTimestamp
import com.example.musicplayer.utils.toPosition
import kotlinx.coroutines.Dispatchers
import kotlin.math.floor

@Composable
fun CurrentPlayingScreen(
    modifier: Modifier = Modifier,
    vm: CurrentPlayingVM,
    dialogsVm: DialogsVM
) {
    val cur = vm.curTrack.collectAsStateWithLifecycle()

    cur.value?.track?.let { current ->
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
                        .data(current.album.thumbnail)
                        .dispatcher(Dispatchers.IO)
                        .crossfade(true)
                        .build(),
                    contentDescription = current.internal.title,
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
                    text = current.internal.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = current.internal.artist,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 6.dp, bottom = 6.dp)
                )
                Text(
                    text = current.album.name,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier.fillMaxWidth().height(10.dp))
                UpperToolbar(
                    onInfoClick = { dialogsVm.toggleInfoDialog(track = current) },
                    onAddClick = { dialogsVm.toggleAddDialog(tracks = listOf(current)) },
                    vm = vm
                )
                Spacer(modifier.fillMaxWidth().height(10.dp))
                SliderToolbar(
                    vm = vm,
                    current = current
                )
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
    onInfoClick: () -> Unit,
    onAddClick: () -> Unit,
    vm: CurrentPlayingVM,
    modifier: Modifier = Modifier
) {
    val shuffle = vm.shuffle.collectAsStateWithLifecycle()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TransparentButton(
                onClick = onInfoClick,
                painter = painterResource(R.drawable.info),
                contentDescription = "Song info",
                tint = MaterialTheme.colorScheme.outline,
            )
            TransparentButton(
                onClick = onAddClick,
                painter = painterResource(R.drawable.playlist_add),
                contentDescription = "Playlist add",
                tint = MaterialTheme.colorScheme.outline,
            )
            TransparentButton(
                onClick = { vm.toggleShuffleMode() },
                painter = painterResource(if (shuffle.value) R.drawable.shuffle else R.drawable.shuffle_off),
                contentDescription = "Shuffle",
                tint = MaterialTheme.colorScheme.outline,
            )
        }
    }
}

// Slider toolbar, with timestamps
@Composable
fun SliderToolbar(
    vm: CurrentPlayingVM,
    current: TrackWithAlbum,
    modifier: Modifier = Modifier
) {
    val position = vm.position.collectAsStateWithLifecycle()
    val sliderValue = vm.sliderValue.collectAsStateWithLifecycle()
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isChangingValue by remember { mutableStateOf(false) }

    LaunchedEffect(sliderValue.value) {
        if (!isChangingValue)
            sliderPosition = sliderValue.value
    }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = formatTimestamp(position.value),
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        CustomSlider(
            value = sliderPosition,
            valueRange = 0f..100f,
            onValueChange = {
                isChangingValue = true
                sliderPosition = it
            },
            onValueChangeFinished = {
                vm.seekTo(toPosition(sliderPosition, current.internal.durationMs))
                isChangingValue = false
            },
            modifier = Modifier
                .fillMaxWidth(.8f)
                .height(10.dp)
        )
        Text(
            text = formatTimestamp(current.internal.durationMs),
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
    val volume = vm.volume.collectAsStateWithLifecycle()
    val paused = vm.paused.collectAsStateWithLifecycle()
    val loop = vm.loop.collectAsStateWithLifecycle()

    val intVolume = floor(volume.value).toInt()
    var sliderPosition by remember { mutableFloatStateOf(volume.value) }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        TransparentBtnWithContextMenu(
            painter = painterResource(when {
                intVolume == 0 -> R.drawable.volume_mute
                intVolume < 50 -> R.drawable.volume_low
                else -> R.drawable.volume_full
            }),
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
                    text = "$intVolume",
                    fontSize = 14.sp,
                    lineHeight = 14.sp
                )
                CustomSlider(
                    value = sliderPosition,
                    valueRange = 0f..100f,
                    onValueChange = { sliderPosition = it },
                    onValueChangeFinished = { vm.setVolume(sliderPosition) },
                    trackSize = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        TransparentButton(
            onClick = { vm.skipPrev() },
            painter = painterResource(R.drawable.skip_prev_big),
            contentDescription = "Skip previous",
            tint = MaterialTheme.colorScheme.primary,
            fullSizeIcon = true,
            modifier = Modifier.height(45.dp).width(45.dp)
        )
        TransparentButton(
            onClick = { vm.seekRewind() },
            painter = painterResource(R.drawable.fast_rewind),
            contentDescription = "Fast rewind",
            tint = MaterialTheme.colorScheme.outline,
            fullSizeIcon = true,
            modifier = Modifier.height(35.dp).width(35.dp)
        )
        TransparentButton(
            onClick = { vm.togglePauseResume() },
            painter = painterResource(if (paused.value) R.drawable.play_big else R.drawable.pause_big),
            contentDescription = "Play/pause",
            tint = MaterialTheme.colorScheme.primary,
            fullSizeIcon = true,
            modifier = Modifier.height(50.dp).width(50.dp)
        )
        TransparentButton(
            onClick = { vm.seekForward() },
            painter = painterResource(R.drawable.fast_forward),
            contentDescription = "Fast forward",
            tint = MaterialTheme.colorScheme.outline,
            fullSizeIcon = true,
            modifier = Modifier.height(35.dp).width(35.dp)
        )
        TransparentButton(
            onClick = { vm.skipNext() },
            painter = painterResource(R.drawable.skip_next_big),
            contentDescription = "Skip next",
            tint = MaterialTheme.colorScheme.primary,
            fullSizeIcon = true,
            modifier = Modifier.height(45.dp).width(45.dp)
        )
        TransparentBtnWithContextMenu(
            painter = painterResource(when(loop.value) {
                Loop.None -> R.drawable.next_song
                Loop.Queue -> R.drawable.repeat_queue
                Loop.Track -> R.drawable.repeat_one
            }),
            contentDescription = "Loop dialog",
            tint = MaterialTheme.colorScheme.outline
        ) {
            LoopDialog(vm = vm, currentMode = loop.value)
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