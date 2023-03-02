package cs346.whiteboard.client.whiteboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.helpers.getResource
import cs346.whiteboard.client.helpers.toDp
import cs346.whiteboard.client.helpers.toList
import java.awt.Point
import java.awt.Toolkit
import javax.imageio.ImageIO

@Composable
fun SelectionBox(controller: WhiteboardController, data: SelectionBoxData) {
    val coordinate = controller.whiteboardToViewCoordinate(data.coordinate)
    val size = controller.whiteboardToViewSize(data.size)
    val resizeNodeSize = controller.whiteboardToViewSize(data.resizeNodeSize)
    val borderWidth = 2.dp * controller.whiteboardZoom
    val resizeLeftImage = remember { ImageIO.read(getResource("/cursors/resize-left.png")) }
    val resizeRightImage = remember { ImageIO.read(getResource("/cursors/resize-right.png")) }
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
            controller.selectionBoxController.getSelectionBoxResizeNodeCoordinates(data).toList().forEachIndexed { index, offset ->
                val resizeNodeCoordinate = controller.whiteboardToViewCoordinate(offset)
                val cursorImage = when(index) {
                    0 -> resizeLeftImage
                    1 -> resizeRightImage
                    2 -> resizeRightImage
                    3 -> resizeLeftImage
                    else -> resizeLeftImage
                }
                val cursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, Point(12,12), "resizeCursor")
                Box(Modifier
                    .offset((resizeNodeCoordinate.x - coordinate.x).toDp(), (resizeNodeCoordinate.y - coordinate.y).toDp())
                    .requiredSize(resizeNodeSize.width.toDp(), resizeNodeSize.height.toDp())
                    .border(borderWidth, Colors.secondaryVariant, CircleShape)
                    .background(Colors.background, CircleShape)
                    .pointerHoverIcon(PointerIcon(cursor))
                )
            }
        }
    }
}