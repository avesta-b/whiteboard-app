package cs346.whiteboard.client.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize

@Composable
fun Logo(modifier: Modifier) {
    Image(painterResource("logo.svg"), "logo", modifier)
}
