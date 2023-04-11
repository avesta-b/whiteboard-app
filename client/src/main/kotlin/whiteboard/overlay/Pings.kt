/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client.whiteboard.overlay

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.zIndex
import cs346.whiteboard.client.helpers.toDp
import cs346.whiteboard.client.helpers.toIntOffset
import cs346.whiteboard.client.ui.EmojiPingText
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.WhiteboardLayerZIndices

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Pings(controller: WhiteboardController) {
    controller.pingController.pings.forEach { (_, data) ->
        val coordinate = controller.whiteboardToViewCoordinate(data.coordinate)
        val size = controller.whiteboardToViewSize(Size(150f, 150f))
        Box(
            modifier = Modifier
                        .offset((coordinate.x - size.width / 2).toDp(), (coordinate.y - size.height / 2).toDp())
                        .size(size.width.toDp(), size.height.toDp())
                        .zIndex(WhiteboardLayerZIndices.pings),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visibleState = data.isVisible,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                EmojiPingText(data.ping, 3f * controller.whiteboardZoom)
            }
        }
    }
}