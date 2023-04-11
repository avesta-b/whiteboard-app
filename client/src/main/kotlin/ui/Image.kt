/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client.ui

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import cs346.whiteboard.client.helpers.CustomIcon

@Composable
fun ImageIcon(icon: CustomIcon, modifier: Modifier) {
    Image(
        painter = painterResource(icon.path()),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}