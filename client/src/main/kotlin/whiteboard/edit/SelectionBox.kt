/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client.whiteboard.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cs346.whiteboard.client.constants.WhiteboardColors
import cs346.whiteboard.client.helpers.toDp
import cs346.whiteboard.client.helpers.toList
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.WhiteboardLayerZIndices

@Composable
fun SelectionBox(controller: WhiteboardController, data: SelectionBoxData) {
    val coordinate = controller.whiteboardToViewCoordinate(controller.editController.getCoordinate(data))
    val size = controller.whiteboardToViewSize(controller.editController.getSize(data))
    val resizeNodeSize = controller.whiteboardToViewSize(data.resizeNodeSize)
    val borderWidth = 2.dp * controller.whiteboardZoom
    Box(Modifier
        .wrapContentSize(Alignment.TopStart, true)
        .offset(coordinate.x.toDp(), coordinate.y.toDp())
        .size(size.width.toDp(),size.height.toDp())
        .zIndex(WhiteboardLayerZIndices.selectionBox)
    ) {
        Box(Modifier
            .fillMaxSize()
            .border(2.dp * controller.whiteboardZoom, WhiteboardColors.secondaryVariant)
        )
        if (data.isResizable) {
            controller.editController.getSelectionBoxResizeNodeCoordinates(data).toList().forEach { offset ->
                val resizeNodeCoordinate = controller.whiteboardToViewCoordinate(offset)
                Box(Modifier
                    .offset((resizeNodeCoordinate.x - coordinate.x).toDp(), (resizeNodeCoordinate.y - coordinate.y).toDp())
                    .requiredSize(resizeNodeSize.width.toDp(), resizeNodeSize.height.toDp())
                    .border(borderWidth, WhiteboardColors.secondaryVariant, CircleShape)
                    .background(WhiteboardColors.selectionNodeColor, CircleShape)
                )
            }
        }
    }
}