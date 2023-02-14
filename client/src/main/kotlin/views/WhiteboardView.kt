package cs346.whiteboard.client.views

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cs346.whiteboard.client.components.TitleText

@Composable
fun WhiteboardView(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        TitleText("Draw")
    }
}