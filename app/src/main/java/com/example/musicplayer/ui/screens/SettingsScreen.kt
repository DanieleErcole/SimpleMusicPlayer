package com.example.musicplayer.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val ctx = LocalContext.current
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
                text = "Settings",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 16.sp
            )
        }
        Divider()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Song library",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
            //TODO (maybe) implement blacklist
            //TODO: implement automatic scan toggle with checkbox checking
            SettingsItem(
                onClick = { vm.rescan(ctx) },
                painter = painterResource(R.drawable.scan),
                text = "Scan",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Theming",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
            //TODO: implement accent color
        }
    }
}

@Composable
fun SettingsItem(
    onClick: () -> Unit,
    painter: Painter? = null,
    text: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        painter?.let {
            Icon(
                painter = it,
                contentDescription = text,
                tint = tint
            )
        }
        Text(
            text = text,
            color = tint,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}