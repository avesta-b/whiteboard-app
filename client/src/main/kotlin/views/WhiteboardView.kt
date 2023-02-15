package cs346.whiteboard.client.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.whiteboard.Whiteboard
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.WhiteboardToolbar

@Composable
fun WhiteboardView(modifier: Modifier) {
    val whiteboardController = remember { WhiteboardController() }

    Box(modifier = modifier, contentAlignment = Alignment.BottomCenter) {
        Whiteboard(
            whiteboardController = whiteboardController,
            modifier = modifier
        )
        WhiteboardToolbar(
            whiteboardController = whiteboardController,
            modifier = Modifier.size(250.dp, 85.dp).offset(0.dp, (-32).dp).background(Colors.background)
        )
    }
}