package com.example.musicplayer.ui.components.dialogs

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.R
import com.example.musicplayer.data.TrackWithAlbum
import com.example.musicplayer.ui.components.Divider
import com.example.musicplayer.utils.formatInstantToHuman
import com.example.musicplayer.utils.formatTimestamp

@Composable
fun SongInfoDialog(
    track: TrackWithAlbum,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    BaseDialog(
        onDismissRequest = onDismiss,
    ) {
        Surface {
            Column(
                modifier = modifier.heightIn(max = 800.dp)
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
                        text = "Song info",
                        fontSize = 20.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Divider()
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    InfoField(title = "Location", text = track.internal.location)
                    Divider(modifier.padding(vertical = 8.dp))
                    InfoField(title = "Title", text = track.internal.title)
                    InfoField(title = "Artist", text = track.internal.artist)
                    InfoField(title = "Album artist", text = track.album.artist)
                    InfoField(title = "Composer", text = track.internal.composer)
                    InfoField(title = "Genre", text = track.internal.genre)
                    InfoField(title = "Track number", text = "${track.internal.trackNumber}")
                    InfoField(title = "Disc number", text = "${track.internal.discNumber}")
                    InfoField(title = "Year", text = "${track.internal.year}")
                    Divider(modifier.padding(vertical = 8.dp))
                    InfoField(title = "Duration", text = formatTimestamp(track.internal.durationMs))
                    Divider(modifier.padding(vertical = 8.dp))
                    InfoField(title = "Date added to library", text = formatInstantToHuman(track.internal.addedToLibrary))
                    InfoField(title = "Date last played", text = track.internal.lastPlayed?.let { formatInstantToHuman(it) } ?: "Never")
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