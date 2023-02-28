package cs346.whiteboard.client.whiteboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.constants.queryBoxColor
import cs346.whiteboard.client.helpers.toDp

@Composable
fun QueryBox(controller: WhiteboardController, data: QueryBoxData) {
    val coordinate = controller.whiteboardToViewCoordinate(data.coordinate)
    val size = controller.whiteboardToViewSize(data.size)
    Box(
        Modifier
            .wrapContentSize(Alignment.TopStart, true)
            .offset(coordinate.x.toDp(), coordinate.y.toDp())
            .size(size.width.toDp(),size.height.toDp())
            .background(Colors.queryBoxColor)
            .zIndex(2f)
    )
}