package cs346.whiteboard.client.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import cs346.whiteboard.client.constants.WhiteboardColors

@Composable
fun Logo(modifier: Modifier) {
    Image(painterResource(if (WhiteboardColors.isDarkMode) "logo-dark.svg" else "logo.svg"), "logo", modifier)
}
