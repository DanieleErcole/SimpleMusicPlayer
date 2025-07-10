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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicplayer.R
import com.example.musicplayer.ui.components.CustomSlider
import com.example.musicplayer.ui.state.CurrentPlayingVM

@Composable
fun CurrentPlayingScreen(
    vm: CurrentPlayingVM,
    modifier: Modifier
) {
    val cur = vm.curTrack.collectAsState()
    //cur.value?.let {
    //} ?: NothingPlaying()
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
                    .data(null)
                    .crossfade(true)
                    .build(),
                contentDescription = "Mhanz",
                error = painterResource(R.drawable.unknown_thumb),
                placeholder = painterResource(R.drawable.unknown_thumb),
                contentScale = ContentScale.FillBounds
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
                text = "Pleasing Jesus (Salvation)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding()
            )
            Text(
                text = "Eric Cartman",
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Welcome Jesus in your Heart",
                overflow = TextOverflow.Ellipsis
            )
            UpperToolbar()
            Spacer(modifier.fillMaxWidth().height(30.dp))
            SliderToolbar()
            Spacer(modifier.fillMaxWidth().height(30.dp))
            PlayerControls()
            Spacer(modifier.fillMaxWidth().height(40.dp))
        }
    }
}

// Toolbar for favorite, info, add to playlist and shuffle
@Composable
fun UpperToolbar(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {

    }
}

// Slider toolbar, with timestamps
@Composable
fun SliderToolbar(
    //current: QueuedTrack,
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
            text = "01:00",
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )//formatTimestamp(current.track.internal.durationMs))
    }
}

// Main toolbar, with skip next/previous, fast forward/rewind, play/pause, volume and loop
@Composable
fun PlayerControls(
    //current: QueuedTrack,
    modifier: Modifier = Modifier
) { //TODO: finish button dimensions
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = {  }
        ) {
            Icon(
                //TODO: implement volume
                painter = painterResource(R.drawable.volume_full),
                contentDescription = "Current queue"
            )
        }
        IconButton(
            onClick = {  },
            modifier = Modifier.height(45.dp).width(45.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.skip_prev_big),
                contentDescription = "Current queue",
                modifier = Modifier.fillMaxSize()
            )
        }
        IconButton(
            onClick = {  },
            modifier = Modifier.height(35.dp).width(35.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.fast_rewind),
                contentDescription = "Current queue",
                modifier = Modifier.fillMaxSize()
            )
        }
        IconButton(
            onClick = {  },
            modifier = Modifier.height(50.dp).width(50.dp)
        ) {
            Icon(
                //TODO: implement play/pause
                painter = painterResource(R.drawable.play_big),
                contentDescription = "Current queue",
                modifier = Modifier.fillMaxSize()
            )
        }
        IconButton(
            onClick = {  },
            modifier = Modifier.height(35.dp).width(35.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.fast_forward),
                contentDescription = "Current queue",
                modifier = Modifier.fillMaxSize()
            )
        }
        IconButton(
            onClick = {  },
            modifier = Modifier.height(45.dp).width(45.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.skip_next_big),
                contentDescription = "Current queue",
                modifier = Modifier.fillMaxSize()
            )
        }
        IconButton(
            onClick = {  }
        ) {
            Icon(
                //TODO: implement loop
                painter = painterResource(R.drawable.repeat_one),
                contentDescription = "Current queue",
                tint = Color.Red,
            )
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