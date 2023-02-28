package cs346.whiteboard.client.whiteboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.helpers.toDp
import cs346.whiteboard.client.helpers.toList

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
        .zIndex(1f)
    ) {
        Box(Modifier
            .fillMaxSize()
            .border(2.dp * controller.whiteboardZoom, Colors.secondaryVariant)
        )
        if (data.isResizable) {
            controller.selectionBoxController.getSelectionBoxResizeNodeCoordinates(data).toList().forEach {
                val resizeNodeCoordinate = controller.whiteboardToViewCoordinate(it)
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