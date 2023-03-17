package cs346.whiteboard.client.whiteboard.overlay

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cs346.whiteboard.client.ui.CursorUserNameText
import cs346.whiteboard.client.helpers.getUserColor
import cs346.whiteboard.client.helpers.toIntOffset
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.WhiteboardLayerZIndices

@Composable
fun Cursors(controller: WhiteboardController) {
    // Draw circles for each item in positions map
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
                modifier = Modifier.size((35 * controller.whiteboardZoom).dp).rotate(270f),
                tint = userColor
            )
            CursorUserNameText(
                id,
                userColor,
                Modifier.offset(
                    (30 * controller.whiteboardZoom).dp,
                    (-5 * controller.whiteboardZoom).dp
                ),
                controller.whiteboardZoom
            )
        }
    }
}