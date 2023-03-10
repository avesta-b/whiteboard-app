package cs346.whiteboard.client.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cs346.whiteboard.client.whiteboard.Whiteboard
import cs346.whiteboard.client.whiteboard.WhiteboardController

@Composable
fun WhiteboardView(modifier: Modifier, roomId: String, onExit: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val whiteboardController = remember { WhiteboardController(roomId, coroutineScope, onExit) }
    Whiteboard(
        whiteboardController = whiteboardController,
        modifier = modifier,
    )
}