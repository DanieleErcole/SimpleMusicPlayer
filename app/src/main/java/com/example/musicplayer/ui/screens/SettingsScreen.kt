package com.example.musicplayer.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.musicplayer.R
import com.example.musicplayer.ui.components.Divider
import com.example.musicplayer.ui.components.TransparentButton
import com.example.musicplayer.ui.state.DialogsVM
import com.example.musicplayer.ui.state.SettingsVM

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    vm: SettingsVM,
    dialogsVm: DialogsVM
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            TransparentButton(
                onClick = { navController.popBackStack() },
                painter = painterResource(R.drawable.back),
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.settings_page),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 16.sp
            )
        }
        Divider()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            LibrarySection(
                vm = vm,
                modifier = Modifier
                    .padding(top = 16.dp)
            )
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            PlaybackSection(vm = vm)
        }
    }
}

@Composable
fun LibrarySection(
    modifier: Modifier = Modifier,
    vm: SettingsVM
) {
    val ctx = LocalContext.current
    val autoScan = vm.autoScan.collectAsStateWithLifecycle()
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.settings_library_section),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 32.dp)
        )
        SettingsItem(
            onClick = { vm.rescan(ctx) },
            painter = painterResource(R.drawable.scan),
            title = stringResource(R.string.scan_btn_label),
            tint = MaterialTheme.colorScheme.primary
        )
        CheckboxSettingsItem(
            onClick = { vm.toggleAutoScan() },
            title = stringResource(R.string.toggle_auto_scan_label),
            text = stringResource(R.string.toggle_auto_scan_text),
            checked = autoScan.value,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun PlaybackSection(
    modifier: Modifier = Modifier,
    vm: SettingsVM
) {
    val autoPlay = vm.autoPlay.collectAsStateWithLifecycle()
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.settings_playback_section),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 32.dp)
        )
        SwitchSettingsItem(
            onClick = { vm.toggleAutoPlay() },
            title = stringResource(R.string.auto_play_text),
            isActive = autoPlay.value,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun SettingsItem(
    onClick: () -> Unit,
    painter: Painter? = null,
    title: String,
    text: String? = null,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = 16.dp, start = 32.dp, end = 32.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            painter?.let {
                Icon(
                    painter = it,
                    contentDescription = title,
                    tint = tint
                )
            }
            Text(
                text = title,
                color = tint,
                modifier = painter?.let { Modifier.padding(start = 16.dp) } ?: Modifier
            )
        }
        text?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CheckboxSettingsItem(
    onClick: () -> Unit,
    title: String,
    text: String? = null,
    tint: Color,
    checked: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = 16.dp, start = 32.dp, end = 32.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                color = tint
            )
            Checkbox(
                checked = checked,
                onCheckedChange = null
            )
        }
        text?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SwitchSettingsItem(
    onClick: () -> Unit,
    title: String,
    text: String? = null,
    tint: Color,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = 16.dp, start = 32.dp, end = 32.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                color = tint
            )
            Switch(
                checked = isActive,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                    checkedBorderColor = Color.Transparent,
                    uncheckedBorderColor = Color.Transparent
                ),
                onCheckedChange = null
            )
        }
        text?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}