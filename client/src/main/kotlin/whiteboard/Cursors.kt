package cs346.whiteboard.client.whiteboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cs346.whiteboard.client.components.CursorUserNameText
import cs346.whiteboard.client.helpers.getUserColor
import cs346.whiteboard.client.helpers.toIntOffset
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun Cursors(controller: WhiteboardController) {
    // Draw circles for each item in positions map
    Box(Modifier
        .fillMaxSize()
        .background(Color.Transparent)
        .pointerInput(Unit) {
            coroutineScope {
                while (true) {
                    val position = awaitPointerEventScope {
                        awaitPointerEvent(PointerEventPass.Initial).changes.first().position
                    }
                    launch {
                        controller.cursorsController.updateCursor(controller.viewToWhiteboardCoordinate(position))
                    }
                }
            }
        }
        .pointerHoverIcon(PointerIcon(controller.cursorsController.getCurrentCursor()))
    ) {
        controller.cursorsController.friendCursorPositions.forEach { (id, offset) ->
            val userColor = getUserColor(id)
            Column(
                Modifier
                    .offset{controller.whiteboardToViewCoordinate(offset.value).toIntOffset()}
                    .zIndex(WhiteboardLayerZIndices.cursors)
            ) {
                Icon(
                    imageVector = Icons.Rounded.NearMe,
                    contentDescription = null,
                    modifier = Modifier.size(35.dp).rotate(270f),
                    tint = userColor
                )
                CursorUserNameText(id, userColor, Modifier.offset(30.dp, (-5).dp))
            }
        }
    }
}