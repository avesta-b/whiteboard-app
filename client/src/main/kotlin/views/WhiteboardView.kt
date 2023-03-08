package cs346.whiteboard.client.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.whiteboard.Whiteboard
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.WhiteboardToolbar

@Composable
fun WhiteboardView(modifier: Modifier, roomId: String, onExit: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val whiteboardController = remember { WhiteboardController(roomId, coroutineScope, onExit) }
    Whiteboard(
        whiteboardController = whiteboardController,
        modifier = modifier,
    )
}