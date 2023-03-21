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
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.helpers.toDp
import cs346.whiteboard.client.helpers.toList
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.WhiteboardLayerZIndices

@Composable
fun SelectionBox(controller: WhiteboardController, data: SelectionBoxData) {
    val coordinate = controller.whiteboardToViewCoordinate(data.coordinate)
    val size = controller.whiteboardToViewSize(data.size)
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
            .border(2.dp * controller.whiteboardZoom, Colors.secondaryVariant)
        )
        if (data.isResizable) {
            controller.editController.getSelectionBoxResizeNodeCoordinates(data).toList().forEach { offset ->
                val resizeNodeCoordinate = controller.whiteboardToViewCoordinate(offset)
                Box(Modifier
                    .offset((resizeNodeCoordinate.x - coordinate.x).toDp(), (resizeNodeCoordinate.y - coordinate.y).toDp())
                    .requiredSize(resizeNodeSize.width.toDp(), resizeNodeSize.height.toDp())
                    .border(borderWidth, Colors.secondaryVariant, CircleShape)
                    .background(Colors.background, CircleShape)
                )
            }
        }
    }
}